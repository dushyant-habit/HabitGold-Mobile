package com.habit.gold.feature.rewards.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habit.gold.core.network.ApiResult
import com.habit.gold.feature.home.presentation.formatInr
import com.habit.gold.feature.rewards.domain.model.RewardMilestone
import com.habit.gold.feature.rewards.domain.model.RewardsMilestonesSummary
import com.habit.gold.feature.rewards.domain.usecase.GetRewardsMilestonesUseCase
import com.habit.gold.feature.rewards.domain.usecase.GetRewardsUserFeaturesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import kotlin.math.roundToInt
import kotlin.time.Clock

class RewardsViewModel(
    private val getRewardsMilestonesUseCase: GetRewardsMilestonesUseCase,
    private val getRewardsUserFeaturesUseCase: GetRewardsUserFeaturesUseCase,
    private val nowMillis: () -> Long = { Clock.System.now().toEpochMilliseconds() },
) : ViewModel() {

    private val _state = MutableStateFlow(RewardsHomeState())
    val state: StateFlow<RewardsHomeState> = _state.asStateFlow()

    private var milestonesFetchInFlight = false
    private var userFeaturesFetchInFlight = false
    private var lastMilestonesLoadedAtMillis: Long? = null
    private var lastUserFeaturesLoadedAtMillis: Long? = null

    init {
        onIntent(RewardsHomeIntent.LoadIfNeeded)
    }

    fun onIntent(intent: RewardsHomeIntent) {
        when (intent) {
            RewardsHomeIntent.LoadIfNeeded -> loadIfNeeded()
            RewardsHomeIntent.Visible -> refreshWhenRewardsVisible()
            RewardsHomeIntent.Refresh -> refresh(force = true)
        }
    }

    private fun loadIfNeeded() {
        refreshUserFeaturesIfNeeded()
        if (_state.value.homeUi == null) {
            refresh(force = false)
        }
    }

    private fun refreshWhenRewardsVisible() {
        refreshUserFeaturesIfNeeded()
        val now = nowMillis()
        if (milestonesFetchInFlight || _state.value.isLoading) return
        if (lastMilestonesLoadedAtMillis?.let { now - it < REWARDS_CACHE_TTL_MS } == true) return
        refresh(force = false)
    }

    private fun refresh(force: Boolean) {
        refreshUserFeaturesIfNeeded(force = force)
        if (milestonesFetchInFlight) return

        val previousUi = _state.value.homeUi
        val now = nowMillis()
        if (!force &&
            previousUi != null &&
            lastMilestonesLoadedAtMillis?.let { now - it < REWARDS_CACHE_TTL_MS } == true
        ) {
            return
        }

        milestonesFetchInFlight = true
        _state.value = if (previousUi != null) {
            _state.value.copy(
                isLoading = false,
                isRefreshing = true,
                errorMessage = null,
            )
        } else {
            _state.value.copy(
                isLoading = true,
                isRefreshing = false,
                errorMessage = null,
            )
        }

        viewModelScope.launch {
            when (val result = getRewardsMilestonesUseCase()) {
                is ApiResult.Success -> {
                    lastMilestonesLoadedAtMillis = nowMillis()
                    _state.value = _state.value.copy(
                        isLoading = false,
                        isRefreshing = false,
                        homeUi = mapRewardsHomeUi(result.value),
                        errorMessage = null,
                    )
                }

                is ApiResult.Failure -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        isRefreshing = false,
                        errorMessage = if (previousUi == null) result.error.message else null,
                    )
                }
            }
            milestonesFetchInFlight = false
        }
    }

    private fun refreshUserFeaturesIfNeeded(force: Boolean = false) {
        if (userFeaturesFetchInFlight) return
        val now = nowMillis()
        if (!force &&
            lastUserFeaturesLoadedAtMillis?.let { now - it < USER_FEATURES_CACHE_TTL_MS } == true
        ) {
            return
        }

        userFeaturesFetchInFlight = true
        viewModelScope.launch {
            when (val result = getRewardsUserFeaturesUseCase()) {
                is ApiResult.Success -> {
                    lastUserFeaturesLoadedAtMillis = nowMillis()
                    _state.value = _state.value.copy(rewardsFeatureActive = result.value.rewardsActive)
                }

                is ApiResult.Failure -> Unit
            }
            userFeaturesFetchInFlight = false
        }
    }
}

private fun mapRewardsHomeUi(dto: RewardsMilestonesSummary): RewardsHomeUi {
    val earned = dto.totalEarnedInr?.toDoubleOrNull() ?: 0.0
    val usePostJourneyHeader = earned > 0.0

    val totalPaidGrams = dto.totalPaidGoldGrams?.toFloatOrNull() ?: 0f
    val thresholds = dto.milestones.map { parseThresholdGramsToFloat(it.thresholdGrams) }
    val boosterLabel = formatBoosterPercentLabel(dto.ongoingPercent)

    val rows = dto.milestones.mapIndexed { index, milestone ->
        val state = mapMilestoneStatus(milestone.status)
        val data = RewardsMilestoneData(
            level = index + 1,
            name = milestone.name,
            targetGold = formatThresholdGrams(milestone.thresholdGrams),
            rewardAmount = "₹${milestone.rewardInr.trim()}",
            completedSubtitle = "Reward available when you complete this step.",
            key = milestone.key,
        )
        val progress = when (state) {
            RewardsLevelState.Active -> progressTowardMilestoneThreshold(
                totalPaidGrams = totalPaidGrams,
                cumulativeThresholdGrams = thresholds.getOrNull(index) ?: 0f,
            )

            else -> 0f
        }
        RewardsMilestoneRowUi(
            data = data,
            state = state,
            progressFraction = progress,
            totalPaidGoldGramsForUi = if (state == RewardsLevelState.Active) totalPaidGrams else null,
        )
    }

    val allTierMilestonesDone = rows.isNotEmpty() && rows.all { it.state == RewardsLevelState.Completed }
    val lifetimeBooster = RewardsLifetimeBoosterRowUi(
        state = when {
            rows.isEmpty() -> RewardsLevelState.Locked
            allTierMilestonesDone && dto.boosterActive -> RewardsLevelState.Completed
            allTierMilestonesDone -> RewardsLevelState.Active
            else -> RewardsLevelState.Locked
        },
        boosterRateLabel = boosterLabel,
    )

    return RewardsHomeUi(
        rows = rows,
        lifetimeBooster = lifetimeBooster,
        milestonesActive = dto.milestonesActive,
        usePostJourneyHeader = usePostJourneyHeader,
        totalEarnedDisplay = formatTotalEarned(dto.totalEarnedInr),
        goldCashbackDisplay = formatInrCompact(dto.goldCashbackInr),
        referralCashbackDisplay = formatInrCompact(dto.referralCashbackInr),
        redeemableDisplay = formatInrCompact(dto.redeemableInr),
        redeemedDisplay = formatInrCompact(dto.redeemedInr),
    )
}

internal fun defaultRewardsMilestoneRows(): List<RewardsMilestoneRowUi> {
    val steps = listOf(
        RewardsMilestoneData(1, "Genesis", "0.5g", "₹50", "Reward available when you complete this step.", "mock_m1"),
        RewardsMilestoneData(2, "Accumulator", "1g", "—", "Reward available when you complete this step.", "mock_m2"),
        RewardsMilestoneData(3, "Sustainer", "5g", "—", "Reward available when you complete this step.", "mock_m3"),
        RewardsMilestoneData(4, "Level 4", "5g", "₹200", "Reward available when you complete this step.", "mock_m4"),
    )
    return steps.mapIndexed { index, data ->
        RewardsMilestoneRowUi(
            data = data,
            state = when {
                index == 0 -> RewardsLevelState.Active
                else -> RewardsLevelState.Locked
            },
        )
    }
}

internal fun defaultRewardsLifetimeBoosterRowUi(
    milestoneRows: List<RewardsMilestoneRowUi>,
): RewardsLifetimeBoosterRowUi {
    val allDone = milestoneRows.isNotEmpty() && milestoneRows.all { it.state == RewardsLevelState.Completed }
    return RewardsLifetimeBoosterRowUi(
        state = if (allDone) RewardsLevelState.Active else RewardsLevelState.Locked,
        boosterRateLabel = "0.5%",
    )
}

private fun mapMilestoneStatus(status: String): RewardsLevelState {
    return when (normalizeRewardsToken(status)) {
        "COMPLETED", "COMPLETE", "DONE", "ACHIEVED" -> RewardsLevelState.Completed
        "IN_PROGRESS" -> RewardsLevelState.Active
        else -> RewardsLevelState.Locked
    }
}

private fun progressTowardMilestoneThreshold(
    totalPaidGrams: Float,
    cumulativeThresholdGrams: Float,
): Float {
    if (cumulativeThresholdGrams <= 1e-6f) return 0f
    return (totalPaidGrams / cumulativeThresholdGrams).coerceIn(0f, 1f)
}

private fun parseThresholdGramsToFloat(raw: String): Float {
    val normalized = raw.trim().lowercase().replace("g", "").replace("+", "").trim()
    return normalized.toFloatOrNull() ?: 0f
}

private fun formatThresholdGrams(raw: String): String {
    val parsed = parseThresholdGramsToFloat(raw)
    return buildString {
        append(formatGramsQuantityForDisplay(parsed))
        append("g")
        if (raw.contains("+")) append("+")
    }
}

private fun formatBoosterPercentLabel(raw: String?): String {
    val trimmed = raw?.trim().orEmpty()
    return when {
        trimmed.isBlank() -> "0.5%"
        trimmed.endsWith("%") -> trimmed
        else -> "$trimmed%"
    }
}

private fun formatTotalEarned(raw: String?): String {
    val parsed = raw?.toDoubleOrNull() ?: 0.0
    return "₹${formatInr(parsed)}"
}

private fun formatInrCompact(raw: String?): String {
    val parsed = raw?.toDoubleOrNull() ?: 0.0
    return "₹${formatInr(parsed.absoluteValue)}"
}

internal fun formatGramsQuantityForDisplay(grams: Float): String {
    if (grams <= 0f || grams.isNaN()) return "0"
    return com.habit.gold.core.util.formatGramsTruncatePlain(grams.toDouble())
}

private fun normalizeRewardsToken(raw: String): String {
    return raw.trim().uppercase().replace(" ", "_")
}

private fun formatPositiveDecimal(value: Float, decimals: Int): String {
    val factor = decimalFactor(decimals)
    val scaled = (value * factor).roundToInt()
    val whole = scaled / factor
    val fraction = (scaled % factor).toString()
        .padStart(decimals, '0')
        .trimEnd('0')
    return if (fraction.isEmpty()) whole.toString() else "$whole.$fraction"
}

private fun decimalFactor(decimals: Int): Int {
    var factor = 1
    repeat(decimals) { factor *= 10 }
    return factor
}

private const val REWARDS_CACHE_TTL_MS = 60_000L
private const val USER_FEATURES_CACHE_TTL_MS = 5 * 60_000L
