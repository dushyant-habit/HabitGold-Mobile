package com.habit.gold.feature.home.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habit.gold.feature.home.domain.model.HomeRecentTransactionPreview
import com.habit.gold.feature.home.presentation.formatCreatedAt
import com.habit.gold.feature.home.presentation.formatInr
import habitgoldmobile.composeapp.generated.resources.Res
import habitgoldmobile.composeapp.generated.resources.common_gold_unit_short
import habitgoldmobile.composeapp.generated.resources.common_why_habitgold
import habitgoldmobile.composeapp.generated.resources.home_screen_activity_empty_title
import habitgoldmobile.composeapp.generated.resources.home_screen_intro_description_gold
import habitgoldmobile.composeapp.generated.resources.home_screen_intro_description_growth
import habitgoldmobile.composeapp.generated.resources.home_screen_intro_description_liquidity
import habitgoldmobile.composeapp.generated.resources.home_screen_intro_description_secure
import habitgoldmobile.composeapp.generated.resources.home_screen_intro_title_gold
import habitgoldmobile.composeapp.generated.resources.home_screen_intro_title_growth
import habitgoldmobile.composeapp.generated.resources.home_screen_intro_title_liquidity
import habitgoldmobile.composeapp.generated.resources.home_screen_intro_title_secure
import habitgoldmobile.composeapp.generated.resources.home_screen_buy_gold
import habitgoldmobile.composeapp.generated.resources.home_screen_coin_delivery
import habitgoldmobile.composeapp.generated.resources.home_screen_gold_savings_sip
import habitgoldmobile.composeapp.generated.resources.home_screen_recent_activity
import habitgoldmobile.composeapp.generated.resources.home_screen_sell_gold
import habitgoldmobile.composeapp.generated.resources.home_screen_transaction_generic
import habitgoldmobile.composeapp.generated.resources.home_screen_trust_slide_earn_every_time_description
import habitgoldmobile.composeapp.generated.resources.home_screen_trust_slide_earn_every_time_title
import habitgoldmobile.composeapp.generated.resources.home_screen_trust_slide_free_delivery_description
import habitgoldmobile.composeapp.generated.resources.home_screen_trust_slide_free_delivery_title
import habitgoldmobile.composeapp.generated.resources.home_screen_trust_slide_real_gold_description
import habitgoldmobile.composeapp.generated.resources.home_screen_trust_slide_real_gold_title
import habitgoldmobile.composeapp.generated.resources.home_screen_trust_slide_stored_safely_description
import habitgoldmobile.composeapp.generated.resources.home_screen_trust_slide_stored_safely_title
import habitgoldmobile.composeapp.generated.resources.img_bis_safety
import habitgoldmobile.composeapp.generated.resources.img_habitgold_intro
import habitgoldmobile.composeapp.generated.resources.img_liquid_accessible
import habitgoldmobile.composeapp.generated.resources.img_proven_growth
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun HomeTrustHighlightsSection(onOpenIntroSheet: (Int) -> Unit) {
    val trustItems = remember {
        listOf(
            HomeTrustItem(0, Res.string.home_screen_trust_slide_real_gold_title, Res.string.home_screen_trust_slide_real_gold_description, Icons.Default.WorkspacePremium, listOf(Color(0xFFFDE68A), Gold500.copy(alpha = 0.18f))),
            HomeTrustItem(1, Res.string.home_screen_trust_slide_stored_safely_title, Res.string.home_screen_trust_slide_stored_safely_description, Icons.Default.Verified, listOf(Blue50Alt, HomeWhite)),
            HomeTrustItem(2, Res.string.home_screen_trust_slide_free_delivery_title, Res.string.home_screen_trust_slide_free_delivery_description, Icons.Default.LocalShipping, listOf(Color(0xFFE8FFF0), HomeWhite)),
            HomeTrustItem(3, Res.string.home_screen_trust_slide_earn_every_time_title, Res.string.home_screen_trust_slide_earn_every_time_description, Icons.Default.LocalOffer, listOf(Purple50, HomeWhite)),
        )
    }
    val pagerState = rememberPagerState(pageCount = { trustItems.size })

    LaunchedEffect(pagerState) {
        while (true) {
            delay(4500)
            if (!pagerState.isScrollInProgress && pagerState.pageCount > 0) {
                pagerState.animateScrollToPage((pagerState.currentPage + 1) % pagerState.pageCount)
            }
        }
    }

    Column(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        HorizontalPager(
            state = pagerState,
            pageSpacing = 12.dp,
            modifier = Modifier
                .fillMaxWidth()
                .height(84.dp),
        ) { page ->
            val trustItem = trustItems[page]
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenIntroSheet(trustItem.introSheetPage) },
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = HomeWhite),
                border = BorderStroke(1.dp, Slate200.copy(alpha = 0.9f)),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Brush.horizontalGradient(colors = trustItem.background)),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(trustItem.title),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Slate900,
                                maxLines = 1,
                            )
                            Text(
                                text = stringResource(trustItem.description),
                                fontSize = 10.sp,
                                lineHeight = 12.sp,
                                color = Slate600,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(HomeWhite.copy(alpha = 0.88f))
                                .border(1.dp, HomeWhite.copy(alpha = 0.92f), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = trustItem.icon,
                                contentDescription = null,
                                tint = HomePrimary,
                                modifier = Modifier.size(18.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
internal fun RecentActivitySection(
    items: List<HomeRecentTransactionPreview>,
    onOpenTransaction: (HomeRecentTransactionPreview) -> Unit,
) {
    Column {
        Text(
            text = stringResource(Res.string.home_screen_recent_activity),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(horizontal = 16.dp),
        )
        Spacer(modifier = Modifier.height(16.dp))
        if (items.isEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = HomeWhite),
                border = BorderStroke(1.dp, Slate200),
            ) {
                Text(
                    text = stringResource(Res.string.home_screen_activity_empty_title),
                    modifier = Modifier.padding(16.dp),
                    fontSize = 13.sp,
                    color = Slate600,
                )
            }
        } else {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                items(items, key = { it.id }) { recentTransaction ->
                    val config = recentActivityConfigFor(recentTransaction)
                    Card(
                        modifier = Modifier
                            .width(220.dp)
                            .clickable { onOpenTransaction(recentTransaction) },
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = HomeWhite),
                        border = BorderStroke(1.dp, Slate200.copy(alpha = 0.72f)),
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(config.iconBackground),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Icon(
                                        imageVector = config.icon,
                                        contentDescription = null,
                                        tint = config.iconTint,
                                        modifier = Modifier.size(18.dp),
                                    )
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = config.title,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Slate900,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                    Text(
                                        text = formatCreatedAt(recentTransaction.createdAt),
                                        fontSize = 11.sp,
                                        color = Slate500,
                                    )
                                }
                            }

                            Text(
                                text = "₹${recentTransaction.amount.toDoubleOrNull()?.let(::formatInr) ?: recentTransaction.amount}",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Slate900,
                            )

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(999.dp),
                                    color = config.statusBackground,
                                ) {
                                    Text(
                                        text = recentTransaction.status.replaceFirstChar { it.uppercase() },
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = config.statusTint,
                                    )
                                }
                                if (recentTransaction.goldQuantity.isNotBlank()) {
                                    Text(
                                        text = "${recentTransaction.goldQuantity} ${stringResource(Res.string.common_gold_unit_short)}",
                                        fontSize = 11.sp,
                                        color = Slate600,
                                        fontWeight = FontWeight.Medium,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
internal fun HomeWhyHabitGoldSection(onOpenIntroSheet: (Int) -> Unit) {
    val introItems = remember {
        listOf(
            HomeIntroCardItem(Res.string.home_screen_intro_title_gold, Res.string.home_screen_intro_description_gold, Res.drawable.img_habitgold_intro),
            HomeIntroCardItem(Res.string.home_screen_intro_title_secure, Res.string.home_screen_intro_description_secure, Res.drawable.img_bis_safety),
            HomeIntroCardItem(Res.string.home_screen_intro_title_liquidity, Res.string.home_screen_intro_description_liquidity, Res.drawable.img_liquid_accessible),
            HomeIntroCardItem(Res.string.home_screen_intro_title_growth, Res.string.home_screen_intro_description_growth, Res.drawable.img_proven_growth),
        )
    }

    Column {
        Text(
            text = stringResource(Res.string.common_why_habitgold),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
        )

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            items(introItems.withIndex().toList(), key = { it.index }) { indexedItem ->
                HomeWhyIntroCard(
                    item = indexedItem.value,
                    onClick = { onOpenIntroSheet(indexedItem.index) },
                )
            }
        }
    }
}

private data class HomeTrustItem(
    val introSheetPage: Int,
    val title: StringResource,
    val description: StringResource,
    val icon: ImageVector,
    val background: List<Color>,
)

private data class HomeIntroCardItem(
    val title: StringResource,
    val description: StringResource,
    val image: DrawableResource,
)

private data class RecentActivityConfig(
    val title: String,
    val icon: ImageVector,
    val iconBackground: Color,
    val iconTint: Color,
    val statusBackground: Color,
    val statusTint: Color,
)

@Composable
private fun recentActivityConfigFor(item: HomeRecentTransactionPreview): RecentActivityConfig {
    val isBuy = item.type.equals("BUY", ignoreCase = true)
    val isSell = item.type.equals("SELL", ignoreCase = true)
    val isDelivery = item.type.contains("delivery", ignoreCase = true)
    val title = when {
        item.isSip -> item.sipName?.ifBlank { stringResource(Res.string.home_screen_gold_savings_sip) }
            ?: stringResource(Res.string.home_screen_gold_savings_sip)
        isSell -> stringResource(Res.string.home_screen_sell_gold)
        isDelivery -> stringResource(Res.string.home_screen_coin_delivery)
        isBuy -> stringResource(Res.string.home_screen_buy_gold)
        else -> stringResource(Res.string.home_screen_transaction_generic)
    }
    val isFailure = item.status.contains("fail", ignoreCase = true) || item.status.contains("cancel", ignoreCase = true)
    return RecentActivityConfig(
        title = title,
        icon = when {
            isBuy -> Icons.Default.Savings
            isSell -> Icons.AutoMirrored.Filled.TrendingDown
            isDelivery -> Icons.Default.LocalShipping
            else -> Icons.Default.CheckCircle
        },
        iconBackground = when {
            isBuy -> Green25
            isSell -> Red25
            isDelivery -> Blue25
            else -> Blue50Alt
        },
        iconTint = when {
            isBuy -> Green500Soft
            isSell -> RedTint
            isDelivery -> Blue600
            else -> HomePrimary
        },
        statusBackground = if (isFailure) RedSoft else GreenSoft,
        statusTint = if (isFailure) RedTint else Color(0xFF15803D),
    )
}

@Composable
private fun HomeWhyIntroCard(
    item: HomeIntroCardItem,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .height(160.dp)
            .padding(vertical = 4.dp, horizontal = 2.dp),
        onClick = onClick,
        border = BorderStroke(1.dp, Color.Black.copy(alpha = 0.08f)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = HomeWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(HomePrimary.copy(alpha = 0.08f)),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    painter = painterResource(item.image),
                    contentDescription = null,
                    modifier = Modifier.size(26.dp),
                )
            }

            Text(
                text = stringResource(item.title),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 15.sp,
                color = Slate900,
                lineHeight = 20.sp,
                modifier = Modifier.padding(top = 10.dp),
            )
        }
    }
}
