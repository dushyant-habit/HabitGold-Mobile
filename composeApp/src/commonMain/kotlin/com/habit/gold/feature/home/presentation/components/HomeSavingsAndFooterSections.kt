package com.habit.gold.feature.home.presentation.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habit.gold.feature.home.domain.model.HomeSipMandate
import com.habit.gold.feature.home.presentation.formatCreatedAt
import com.habit.gold.feature.home.presentation.formatInr
import habitgoldmobile.composeapp.generated.resources.Res
import habitgoldmobile.composeapp.generated.resources.common_need_help_with_something
import habitgoldmobile.composeapp.generated.resources.common_safegold
import habitgoldmobile.composeapp.generated.resources.common_support_is_available_for_payments_shipping_and_orders
import habitgoldmobile.composeapp.generated.resources.common_your_gold_is_100percent_secured
import habitgoldmobile.composeapp.generated.resources.home_screen_auto_invest
import habitgoldmobile.composeapp.generated.resources.home_screen_bank_confirmation_pending
import habitgoldmobile.composeapp.generated.resources.home_screen_footer_badge_insured
import habitgoldmobile.composeapp.generated.resources.home_screen_footer_badge_physical
import habitgoldmobile.composeapp.generated.resources.home_screen_footer_badge_vault
import habitgoldmobile.composeapp.generated.resources.home_screen_gold_savings_habit
import habitgoldmobile.composeapp.generated.resources.home_screen_invests_every_day
import habitgoldmobile.composeapp.generated.resources.home_screen_invests_every_month
import habitgoldmobile.composeapp.generated.resources.home_screen_invests_every_week
import habitgoldmobile.composeapp.generated.resources.home_screen_next_debit
import habitgoldmobile.composeapp.generated.resources.home_screen_powered_by
import habitgoldmobile.composeapp.generated.resources.home_screen_resume
import habitgoldmobile.composeapp.generated.resources.home_screen_start_daily_savings
import habitgoldmobile.composeapp.generated.resources.home_screen_start_monthly_savings
import habitgoldmobile.composeapp.generated.resources.home_screen_start_weekly_savings
import habitgoldmobile.composeapp.generated.resources.home_screen_starts_from_daily
import habitgoldmobile.composeapp.generated.resources.home_screen_starts_from_monthly
import habitgoldmobile.composeapp.generated.resources.home_screen_starts_from_weekly
import habitgoldmobile.composeapp.generated.resources.home_screen_started
import habitgoldmobile.composeapp.generated.resources.home_screen_track_all_savings
import habitgoldmobile.composeapp.generated.resources.home_screen_view_all
import habitgoldmobile.composeapp.generated.resources.home_screen_your_savings
import habitgoldmobile.composeapp.generated.resources.safegold_image
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun GoldSavingsPlansCard(
    mandates: List<HomeSipMandate>,
    onOpenSavingsScreen: () -> Unit,
) {
    val dailyTitle = stringResource(Res.string.home_screen_start_daily_savings)
    val weeklyTitle = stringResource(Res.string.home_screen_start_weekly_savings)
    val monthlyTitle = stringResource(Res.string.home_screen_start_monthly_savings)
    val dailySubtitle = stringResource(Res.string.home_screen_starts_from_daily)
    val weeklySubtitle = stringResource(Res.string.home_screen_starts_from_weekly)
    val monthlySubtitle = stringResource(Res.string.home_screen_starts_from_monthly)
    val mandateByFrequency = remember(mandates) {
        mandates.fold(mutableMapOf<String, HomeSipMandate>()) { mandatesByFrequency, mandate ->
            val frequencyKey = mandate.frequency.trim().uppercase()
            val statusKind = homeSipMandateStatusKind(mandate.status)
            val existingMandate = mandatesByFrequency[frequencyKey]
            val existingStatusKind = existingMandate?.let { homeSipMandateStatusKind(it.status) }
            if (
                existingStatusKind == null ||
                (existingStatusKind != HomeSipMandateStatusKind.Active && statusKind == HomeSipMandateStatusKind.Active) ||
                (existingStatusKind !in setOf(HomeSipMandateStatusKind.Active, HomeSipMandateStatusKind.Paused) &&
                    statusKind == HomeSipMandateStatusKind.Paused)
            ) {
                mandatesByFrequency[frequencyKey] = mandate
            }
            mandatesByFrequency
        }
    }
    val planItems = remember(
        mandateByFrequency,
        dailyTitle,
        weeklyTitle,
        monthlyTitle,
        dailySubtitle,
        weeklySubtitle,
        monthlySubtitle,
    ) {
        listOf(
            HomeSavingsPlanItem("Daily", dailyTitle, dailySubtitle),
            HomeSavingsPlanItem("Weekly", weeklyTitle, weeklySubtitle),
            HomeSavingsPlanItem("Monthly", monthlyTitle, monthlySubtitle),
        ).map { defaultItem ->
            val matchingMandate = mandateByFrequency[defaultItem.frequency.uppercase()]
            val mandateStatusKind = matchingMandate?.let { homeSipMandateStatusKind(it.status) }
            val actionableMandate = matchingMandate?.takeIf {
                mandateStatusKind == HomeSipMandateStatusKind.Active || mandateStatusKind == HomeSipMandateStatusKind.Paused
            }
            val actionTitle = when (mandateStatusKind) {
                HomeSipMandateStatusKind.Active -> "Upgrade ${defaultItem.frequency} Savings"
                HomeSipMandateStatusKind.Paused -> "Resume ${defaultItem.frequency} Savings"
                else -> defaultItem.defaultTitle
            }
            defaultItem.copy(
                title = actionTitle,
                subtitle = actionableMandate?.let(::currentSavingsAmountSubtitle) ?: defaultItem.defaultSubtitle,
                mandate = actionableMandate,
                statusKind = mandateStatusKind ?: HomeSipMandateStatusKind.Neutral,
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = stringResource(Res.string.home_screen_gold_savings_habit),
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF1F2937),
            )
            Box(
                modifier = Modifier
                    .width(120.dp)
                    .height(3.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFFFDE68A).copy(alpha = 0.24f),
                                Gold500.copy(alpha = 0.72f),
                                Gold650.copy(alpha = 0.42f),
                            )
                        )
                    ),
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            planItems.forEach { planItem ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(if (planItem.frequency == "Daily") Modifier.homeAnimatedSavingsBorder() else Modifier)
                        .clickable(onClick = onOpenSavingsScreen),
                    color = HomeWhite,
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, Slate200.copy(alpha = 0.7f)),
                ) {
                    GoldSavingsRowItem(item = planItem)
                }
            }
        }
    }
}

@Composable
private fun Modifier.homeAnimatedSavingsBorder(): Modifier {
    val infiniteTransition = rememberInfiniteTransition(label = "dailySavingsBorder")
    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(animation = tween(durationMillis = 2200, easing = LinearEasing)),
        label = "dailySavingsBorderProgress",
    )
    val animatedBrush by rememberUpdatedState(
        Brush.linearGradient(
            colors = listOf(
                Gold500.copy(alpha = 0.18f),
                HomeWhite.copy(alpha = 0.92f),
                HomePrimary.copy(alpha = 0.34f),
                Gold500.copy(alpha = 0.60f),
            ),
            start = Offset(0f, 0f),
            end = Offset(600f * progress.coerceAtLeast(0.2f), 180f),
        ),
    )

    return this.drawWithContent {
        drawContent()
        drawRoundRect(
            color = Gold500.copy(alpha = 0.14f),
            cornerRadius = CornerRadius(16.dp.toPx(), 16.dp.toPx()),
            style = Stroke(width = 1.dp.toPx()),
        )
        drawRoundRect(
            brush = animatedBrush,
            cornerRadius = CornerRadius(16.dp.toPx(), 16.dp.toPx()),
            style = Stroke(width = 1.5.dp.toPx()),
            alpha = 0.92f,
        )
    }
}

@Composable
internal fun HomeSipMandatesSection(
    mandates: List<HomeSipMandate>,
    onViewAllClick: () -> Unit,
) {
    val activeMandates = remember(mandates) { mandates.filter(::isHomeVisibleSipMandate) }
    val previewMandates = remember(activeMandates) { activeMandates.take(5) }
    if (previewMandates.isEmpty()) return

    val pagerState = rememberPagerState(pageCount = { previewMandates.size })
    val shouldShowViewAll = activeMandates.size > 5

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = stringResource(Res.string.home_screen_your_savings),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = Slate900,
                )
                Text(
                    text = stringResource(Res.string.home_screen_track_all_savings),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Slate500,
                )
            }

            if (shouldShowViewAll) {
                Text(
                    text = stringResource(Res.string.home_screen_view_all),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = HomePrimary,
                    modifier = Modifier.clickable(onClick = onViewAllClick),
                )
            }
        }

        HorizontalPager(
            state = pagerState,
            pageSpacing = 12.dp,
            modifier = Modifier.fillMaxWidth(),
        ) { pageIndex ->
            HomeSipMandateCard(
                mandate = previewMandates[pageIndex],
                modifier = Modifier.fillMaxWidth(),
            )
        }

        if (previewMandates.size > 1) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
            ) {
                repeat(previewMandates.size) { indicatorIndex ->
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 3.dp)
                            .size(if (pagerState.currentPage == indicatorIndex) 8.dp else 6.dp)
                            .clip(CircleShape)
                            .background(if (pagerState.currentPage == indicatorIndex) HomePrimary else Slate200),
                    )
                }
            }
        }
    }
}

@Composable
internal fun HomeSupportFooter(onSupportClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, FooterBorder, RoundedCornerShape(20.dp)),
            shape = RoundedCornerShape(20.dp),
            color = FooterCard,
            tonalElevation = 0.dp,
            onClick = onSupportClick,
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(FooterIconTile),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalShipping,
                            contentDescription = null,
                            tint = HomePrimary,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = stringResource(Res.string.common_need_help_with_something),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = FooterPrimaryText,
                        )
                        Text(
                            text = stringResource(Res.string.common_support_is_available_for_payments_shipping_and_orders),
                            fontSize = 12.sp,
                            lineHeight = 18.sp,
                            color = FooterSecondaryText,
                        )
                    }
                }
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = FooterSecondaryText,
                    modifier = Modifier.size(20.dp),
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(FooterBackground)
                .padding(horizontal = 18.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(Res.string.common_your_gold_is_100percent_secured),
                modifier = Modifier.fillMaxWidth(),
                fontSize = 42.sp,
                lineHeight = 42.sp,
                fontWeight = FontWeight.ExtraBold,
                color = FooterHeading,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(16.dp))

            HorizontalDivider(
                color = FooterDivider,
                thickness = 1.dp,
                modifier = Modifier.padding(horizontal = 32.dp),
            )

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.Top,
            ) {
                FooterBadge(Icons.Default.WorkspacePremium, stringResource(Res.string.home_screen_footer_badge_physical), Modifier.weight(1f))
                FooterBadge(Icons.Default.Verified, stringResource(Res.string.home_screen_footer_badge_insured), Modifier.weight(1f))
                FooterBadge(Icons.Default.AccountBalance, stringResource(Res.string.home_screen_footer_badge_vault), Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(32.dp))
            PoweredBySafeGoldRow()
        }
    }
}

private data class HomeSavingsPlanItem(
    val frequency: String,
    val defaultTitle: String,
    val defaultSubtitle: String,
    val title: String = defaultTitle,
    val subtitle: String = defaultSubtitle,
    val mandate: HomeSipMandate? = null,
    val statusKind: HomeSipMandateStatusKind = HomeSipMandateStatusKind.Neutral,
)

private enum class HomeSipMandateStatusKind {
    Active,
    Paused,
    Failed,
    Neutral,
}

private data class HomeSipMandateStatusStyle(
    val label: String,
    val bg: Color,
    val fg: Color,
    val kind: HomeSipMandateStatusKind,
)

private fun homeSipMandateStatusKind(status: String): HomeSipMandateStatusKind = homeSipMandateStatusStyle(status).kind

private fun homeSipMandateStatusStyle(status: String): HomeSipMandateStatusStyle {
    val normalizedStatus = status.trim().uppercase()
    return when (normalizedStatus) {
        "ACTIVE", "COMPLETED", "SUCCESS", "REGISTERED" -> HomeSipMandateStatusStyle("Active", Green25, Green500Soft, HomeSipMandateStatusKind.Active)
        "PAUSED" -> HomeSipMandateStatusStyle("Paused", GoldSurface, Gold650, HomeSipMandateStatusKind.Paused)
        "FAILED_REGISTRATION", "FAILED", "CANCELLED" -> HomeSipMandateStatusStyle(
            status.lowercase().replaceFirstChar { it.uppercase() },
            Red25,
            RedTint,
            HomeSipMandateStatusKind.Failed,
        )
        else -> HomeSipMandateStatusStyle(
            status.lowercase().replaceFirstChar { it.uppercase() },
            Blue50Alt,
            Slate700,
            HomeSipMandateStatusKind.Neutral,
        )
    }
}

private fun isHomeVisibleSipMandate(mandate: HomeSipMandate): Boolean =
    homeSipMandateStatusKind(mandate.status) == HomeSipMandateStatusKind.Active

@Composable
private fun GoldSavingsRowItem(item: HomeSavingsPlanItem) {
    val accentColor = when (item.frequency) {
        "Daily" -> HomePrimary
        "Weekly" -> Purple700
        else -> Purple500
    }
    val cadenceIcon = when (item.frequency) {
        "Daily" -> Icons.Default.CalendarToday
        "Weekly" -> Icons.Default.DateRange
        else -> Icons.Default.Event
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(HomeWhite)
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(accentColor.copy(alpha = 0.12f))
                .border(1.dp, accentColor.copy(alpha = 0.10f), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = cadenceIcon,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(18.dp),
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(text = item.title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Slate900)
            Text(text = item.subtitle, fontSize = 11.sp, color = Slate600)
        }

        if (item.statusKind == HomeSipMandateStatusKind.Paused) {
            Button(
                onClick = {},
                modifier = Modifier.height(36.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = accentColor.copy(alpha = 0.10f),
                    contentColor = accentColor,
                ),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
            ) {
                Text(
                    text = stringResource(Res.string.home_screen_resume),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.10f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(16.dp),
                )
            }
        }
    }
}

@Composable
private fun HomeSipMandateCard(
    mandate: HomeSipMandate,
    modifier: Modifier = Modifier,
) {
    val statusStyle = homeSipMandateStatusStyle(mandate.status)
    val nextExecutionText = mandate.nextExecutionDate?.let(::formatCreatedAt)
    val startedOnText = mandate.startDate.take(10)
    val promoText = mandate.promoCode?.trim()?.takeIf { it.isNotEmpty() }

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = when (statusStyle.kind) {
                HomeSipMandateStatusKind.Active -> HomeWhite
                HomeSipMandateStatusKind.Paused -> GoldSurface.copy(alpha = 0.35f)
                HomeSipMandateStatusKind.Failed -> Red25.copy(alpha = 0.35f)
                HomeSipMandateStatusKind.Neutral -> HomeWhite
            }
        ),
        border = BorderStroke(
            1.dp,
            when (statusStyle.kind) {
                HomeSipMandateStatusKind.Active -> Slate200
                HomeSipMandateStatusKind.Paused -> Gold500.copy(alpha = 0.45f)
                HomeSipMandateStatusKind.Failed -> RedTint.copy(alpha = 0.25f)
                HomeSipMandateStatusKind.Neutral -> Slate200
            }
        ),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 13.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Surface(
                    shape = RoundedCornerShape(999.dp),
                    color = HomePrimary.copy(alpha = 0.08f),
                ) {
                    Text(
                        text = stringResource(Res.string.home_screen_auto_invest),
                        color = HomePrimary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                    )
                }
                Surface(
                    shape = RoundedCornerShape(999.dp),
                    color = statusStyle.bg,
                ) {
                    Text(
                        text = statusStyle.label,
                        color = statusStyle.fg,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    Text(
                        text = mandate.name.ifBlank { "Gold Savings SIP" },
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Slate700,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(text = "₹${mandate.amount}", fontSize = 24.sp, fontWeight = FontWeight.Black, color = Slate900)
                    Text(
                        text = homeSipCadenceLabel(mandate.frequency),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Slate500,
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = if (nextExecutionText != null) stringResource(Res.string.home_screen_next_debit) else stringResource(Res.string.home_screen_started),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Slate500,
                        )
                        Text(
                            text = nextExecutionText ?: startedOnText,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Slate900,
                        )
                    }

                    promoText?.let {
                        Surface(shape = RoundedCornerShape(999.dp), color = HomePrimary.copy(alpha = 0.1f)) {
                            Text(
                                text = it,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = HomePrimary,
                                modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                }
            }

            if (mandate.billing?.needsAttention == true) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.Savings,
                        contentDescription = null,
                        tint = Gold650,
                        modifier = Modifier.size(14.dp),
                    )
                    Text(
                        text = stringResource(Res.string.home_screen_bank_confirmation_pending),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Gold650,
                    )
                }
            }
        }
    }
}

@Composable
private fun homeSipCadenceLabel(frequency: String): String {
    return when (frequency.trim().uppercase()) {
        "DAILY" -> stringResource(Res.string.home_screen_invests_every_day)
        "WEEKLY" -> stringResource(Res.string.home_screen_invests_every_week)
        "MONTHLY" -> stringResource(Res.string.home_screen_invests_every_month)
        else -> frequency.replaceFirstChar { it.uppercase() }
    }
}

private fun currentSavingsAmountSubtitle(mandate: HomeSipMandate): String {
    val cadenceLabel = when (mandate.frequency.trim().uppercase()) {
        "DAILY" -> "day"
        "WEEKLY" -> "week"
        "MONTHLY" -> "month"
        else -> "cycle"
    }
    val rawAmount = mandate.billing?.currentAmount
        ?: mandate.billingCurrentAmount
        ?: mandate.billing?.nextExecutionAmount
        ?: mandate.billingNextExecutionAmount
        ?: mandate.amount
    return "Current Savings ${formatHomeSavingsPlanAmount(rawAmount)}/$cadenceLabel"
}

@Composable
private fun FooterBadge(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .height(108.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(HomeWhite)
            .border(1.dp, Color(0xFFF1F5F9), RoundedCornerShape(12.dp))
            .padding(vertical = 12.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color(0xFFFFF9E5)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Gold500,
                modifier = Modifier.size(20.dp),
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Slate600,
            textAlign = TextAlign.Center,
            lineHeight = 14.sp,
        )
    }
}

@Composable
private fun PoweredBySafeGoldRow(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Text(
            text = stringResource(Res.string.home_screen_powered_by),
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF94A3B8),
            letterSpacing = 1.2.sp,
        )
        Spacer(modifier = Modifier.width(8.dp))
        Image(
            painter = painterResource(Res.drawable.safegold_image),
            contentDescription = stringResource(Res.string.common_safegold),
            modifier = Modifier
                .height(10.dp)
                .wrapContentWidth(),
        )
    }
}

private fun formatHomeSavingsPlanAmount(rawAmount: String?): String {
    val parsedAmount = rawAmount?.trim()?.toDoubleOrNull() ?: return "₹0"
    return "₹${formatInr(parsedAmount)}"
}
