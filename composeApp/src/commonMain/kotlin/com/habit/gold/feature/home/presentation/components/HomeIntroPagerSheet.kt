package com.habit.gold.feature.home.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import habitgoldmobile.composeapp.generated.resources.Res
import habitgoldmobile.composeapp.generated.resources.home_screen_intro_button_buy_now
import habitgoldmobile.composeapp.generated.resources.home_screen_intro_button_learn_more
import habitgoldmobile.composeapp.generated.resources.home_screen_intro_button_next
import habitgoldmobile.composeapp.generated.resources.home_screen_intro_button_start_saving
import habitgoldmobile.composeapp.generated.resources.home_screen_intro_description_gold
import habitgoldmobile.composeapp.generated.resources.home_screen_intro_description_growth
import habitgoldmobile.composeapp.generated.resources.home_screen_intro_description_liquidity
import habitgoldmobile.composeapp.generated.resources.home_screen_intro_description_secure
import habitgoldmobile.composeapp.generated.resources.home_screen_intro_title_gold
import habitgoldmobile.composeapp.generated.resources.home_screen_intro_title_growth
import habitgoldmobile.composeapp.generated.resources.home_screen_intro_title_liquidity
import habitgoldmobile.composeapp.generated.resources.home_screen_intro_title_secure
import habitgoldmobile.composeapp.generated.resources.img_bis_safety
import habitgoldmobile.composeapp.generated.resources.img_habitgold_intro
import habitgoldmobile.composeapp.generated.resources.img_liquid_accessible
import habitgoldmobile.composeapp.generated.resources.img_proven_growth
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

private data class HomeIntroPagerItem(
    val title: StringResource,
    val description: StringResource,
    val image: DrawableResource,
    val buttonText: StringResource,
)

@Composable
internal fun HomeIntroPagerSheet(
    initialPage: Int,
    onPrimaryAction: () -> Unit,
) {
    val pagerItems = remember {
        listOf(
            HomeIntroPagerItem(
                title = Res.string.home_screen_intro_title_gold,
                description = Res.string.home_screen_intro_description_gold,
                image = Res.drawable.img_habitgold_intro,
                buttonText = Res.string.home_screen_intro_button_next,
            ),
            HomeIntroPagerItem(
                title = Res.string.home_screen_intro_title_secure,
                description = Res.string.home_screen_intro_description_secure,
                image = Res.drawable.img_bis_safety,
                buttonText = Res.string.home_screen_intro_button_learn_more,
            ),
            HomeIntroPagerItem(
                title = Res.string.home_screen_intro_title_liquidity,
                description = Res.string.home_screen_intro_description_liquidity,
                image = Res.drawable.img_liquid_accessible,
                buttonText = Res.string.home_screen_intro_button_start_saving,
            ),
            HomeIntroPagerItem(
                title = Res.string.home_screen_intro_title_growth,
                description = Res.string.home_screen_intro_description_growth,
                image = Res.drawable.img_proven_growth,
                buttonText = Res.string.home_screen_intro_button_buy_now,
            ),
        )
    }
    val pagerState = rememberPagerState(initialPage = initialPage, pageCount = { pagerItems.size })
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 32.dp, start = 20.dp, end = 20.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(440.dp),
            contentAlignment = Alignment.TopCenter,
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
            ) { page ->
                val pagerItem = pagerItems[page]
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = stringResource(pagerItem.title).uppercase(),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF111827),
                        textAlign = TextAlign.Center,
                        lineHeight = 32.sp,
                    )
                    Spacer(modifier = Modifier.height(28.dp))
                    Box(
                        modifier = Modifier
                            .size(240.dp)
                            .clip(RoundedCornerShape(40.dp))
                            .background(Color(0xFFF7F2FA)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Image(
                            painter = painterResource(pagerItem.image),
                            contentDescription = null,
                            modifier = Modifier.size(180.dp),
                        )
                    }
                    Spacer(modifier = Modifier.height(36.dp))
                    Text(
                        text = stringResource(pagerItem.description),
                        fontSize = 16.sp,
                        color = Color(0xFF4A4A4A),
                        textAlign = TextAlign.Center,
                        lineHeight = 24.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            repeat(pagerItems.size) { index ->
                val isSelected = pagerState.currentPage == index
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) Color(0xFF7B2CBF) else Color(0x337B2CBF))
                        .size(height = 6.dp, width = if (isSelected) 24.dp else 6.dp),
                )
            }
        }

        val currentPagerItem = pagerItems[pagerState.currentPage]
        Button(
            onClick = {
                if (pagerState.currentPage < pagerItems.lastIndex) {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    }
                } else {
                    onPrimaryAction()
                }
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF7B2CBF),
                contentColor = Color.White,
            ),
            shape = RoundedCornerShape(16.dp),
        ) {
            Text(
                text = stringResource(currentPagerItem.buttonText),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}
