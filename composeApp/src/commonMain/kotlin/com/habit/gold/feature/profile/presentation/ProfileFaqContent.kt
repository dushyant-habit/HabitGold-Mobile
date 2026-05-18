package com.habit.gold.feature.profile.presentation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HeadsetMic
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Sell
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import habitgoldmobile.composeapp.generated.resources.Res
import habitgoldmobile.composeapp.generated.resources.profile_help_buy_a1
import habitgoldmobile.composeapp.generated.resources.profile_help_buy_a2
import habitgoldmobile.composeapp.generated.resources.profile_help_buy_a3
import habitgoldmobile.composeapp.generated.resources.profile_help_buy_q1
import habitgoldmobile.composeapp.generated.resources.profile_help_buy_q2
import habitgoldmobile.composeapp.generated.resources.profile_help_buy_q3
import habitgoldmobile.composeapp.generated.resources.profile_help_delivery_a1
import habitgoldmobile.composeapp.generated.resources.profile_help_delivery_a2
import habitgoldmobile.composeapp.generated.resources.profile_help_delivery_a3
import habitgoldmobile.composeapp.generated.resources.profile_help_delivery_q1
import habitgoldmobile.composeapp.generated.resources.profile_help_delivery_q2
import habitgoldmobile.composeapp.generated.resources.profile_help_delivery_q3
import habitgoldmobile.composeapp.generated.resources.profile_help_rewards_a1
import habitgoldmobile.composeapp.generated.resources.profile_help_rewards_a2
import habitgoldmobile.composeapp.generated.resources.profile_help_rewards_a3
import habitgoldmobile.composeapp.generated.resources.profile_help_rewards_q1
import habitgoldmobile.composeapp.generated.resources.profile_help_rewards_q2
import habitgoldmobile.composeapp.generated.resources.profile_help_rewards_q3
import habitgoldmobile.composeapp.generated.resources.profile_help_save_a1
import habitgoldmobile.composeapp.generated.resources.profile_help_save_a2
import habitgoldmobile.composeapp.generated.resources.profile_help_save_a3
import habitgoldmobile.composeapp.generated.resources.profile_help_save_q1
import habitgoldmobile.composeapp.generated.resources.profile_help_save_q2
import habitgoldmobile.composeapp.generated.resources.profile_help_save_q3
import habitgoldmobile.composeapp.generated.resources.profile_help_section_buying_digital_gold
import habitgoldmobile.composeapp.generated.resources.profile_help_section_delivery
import habitgoldmobile.composeapp.generated.resources.profile_help_section_getting_started
import habitgoldmobile.composeapp.generated.resources.profile_help_section_habit_saving
import habitgoldmobile.composeapp.generated.resources.profile_help_section_rewards
import habitgoldmobile.composeapp.generated.resources.profile_help_section_selling_gold
import habitgoldmobile.composeapp.generated.resources.profile_help_section_top_faqs
import habitgoldmobile.composeapp.generated.resources.profile_help_section_trust
import habitgoldmobile.composeapp.generated.resources.profile_help_sell_a1
import habitgoldmobile.composeapp.generated.resources.profile_help_sell_a2
import habitgoldmobile.composeapp.generated.resources.profile_help_sell_a3
import habitgoldmobile.composeapp.generated.resources.profile_help_sell_q1
import habitgoldmobile.composeapp.generated.resources.profile_help_sell_q2
import habitgoldmobile.composeapp.generated.resources.profile_help_sell_q3
import habitgoldmobile.composeapp.generated.resources.profile_help_started_a1
import habitgoldmobile.composeapp.generated.resources.profile_help_started_a2
import habitgoldmobile.composeapp.generated.resources.profile_help_started_a3
import habitgoldmobile.composeapp.generated.resources.profile_help_started_q1
import habitgoldmobile.composeapp.generated.resources.profile_help_started_q2
import habitgoldmobile.composeapp.generated.resources.profile_help_started_q3
import habitgoldmobile.composeapp.generated.resources.profile_help_top_a1
import habitgoldmobile.composeapp.generated.resources.profile_help_top_a2
import habitgoldmobile.composeapp.generated.resources.profile_help_top_a3
import habitgoldmobile.composeapp.generated.resources.profile_help_top_a4
import habitgoldmobile.composeapp.generated.resources.profile_help_top_a5
import habitgoldmobile.composeapp.generated.resources.profile_help_top_q1
import habitgoldmobile.composeapp.generated.resources.profile_help_top_q2
import habitgoldmobile.composeapp.generated.resources.profile_help_top_q3
import habitgoldmobile.composeapp.generated.resources.profile_help_top_q4
import habitgoldmobile.composeapp.generated.resources.profile_help_top_q5
import habitgoldmobile.composeapp.generated.resources.profile_help_trust_a1
import habitgoldmobile.composeapp.generated.resources.profile_help_trust_a2
import habitgoldmobile.composeapp.generated.resources.profile_help_trust_a3
import habitgoldmobile.composeapp.generated.resources.profile_help_trust_q1
import habitgoldmobile.composeapp.generated.resources.profile_help_trust_q2
import habitgoldmobile.composeapp.generated.resources.profile_help_trust_q3
import org.jetbrains.compose.resources.stringResource

internal data class ProfileFaqSection(
    val title: String,
    val icon: ImageVector,
    val items: List<ProfileFaqEntry>,
)

internal data class ProfileFaqEntry(
    val question: String,
    val answer: String,
)

@Composable
internal fun profileFaqSections(): List<ProfileFaqSection> {
    return listOf(
        ProfileFaqSection(
            title = stringResource(Res.string.profile_help_section_top_faqs),
            icon = Icons.Default.Star,
            items = listOf(
                ProfileFaqEntry(
                    stringResource(Res.string.profile_help_top_q1),
                    stringResource(Res.string.profile_help_top_a1),
                ),
                ProfileFaqEntry(
                    stringResource(Res.string.profile_help_top_q2),
                    stringResource(Res.string.profile_help_top_a2),
                ),
                ProfileFaqEntry(
                    stringResource(Res.string.profile_help_top_q3),
                    stringResource(Res.string.profile_help_top_a3),
                ),
                ProfileFaqEntry(
                    stringResource(Res.string.profile_help_top_q4),
                    stringResource(Res.string.profile_help_top_a4),
                ),
                ProfileFaqEntry(
                    stringResource(Res.string.profile_help_top_q5),
                    stringResource(Res.string.profile_help_top_a5),
                ),
            ),
        ),
        ProfileFaqSection(
            title = stringResource(Res.string.profile_help_section_getting_started),
            icon = Icons.Default.HeadsetMic,
            items = listOf(
                ProfileFaqEntry(
                    stringResource(Res.string.profile_help_started_q1),
                    stringResource(Res.string.profile_help_started_a1),
                ),
                ProfileFaqEntry(
                    stringResource(Res.string.profile_help_started_q2),
                    stringResource(Res.string.profile_help_started_a2),
                ),
                ProfileFaqEntry(
                    stringResource(Res.string.profile_help_started_q3),
                    stringResource(Res.string.profile_help_started_a3),
                ),
            ),
        ),
        ProfileFaqSection(
            title = stringResource(Res.string.profile_help_section_buying_digital_gold),
            icon = Icons.Default.ShoppingCart,
            items = listOf(
                ProfileFaqEntry(
                    stringResource(Res.string.profile_help_buy_q1),
                    stringResource(Res.string.profile_help_buy_a1),
                ),
                ProfileFaqEntry(
                    stringResource(Res.string.profile_help_buy_q2),
                    stringResource(Res.string.profile_help_buy_a2),
                ),
                ProfileFaqEntry(
                    stringResource(Res.string.profile_help_buy_q3),
                    stringResource(Res.string.profile_help_buy_a3),
                ),
            ),
        ),
        ProfileFaqSection(
            title = stringResource(Res.string.profile_help_section_habit_saving),
            icon = Icons.Default.Payments,
            items = listOf(
                ProfileFaqEntry(
                    stringResource(Res.string.profile_help_save_q1),
                    stringResource(Res.string.profile_help_save_a1),
                ),
                ProfileFaqEntry(
                    stringResource(Res.string.profile_help_save_q2),
                    stringResource(Res.string.profile_help_save_a2),
                ),
                ProfileFaqEntry(
                    stringResource(Res.string.profile_help_save_q3),
                    stringResource(Res.string.profile_help_save_a3),
                ),
            ),
        ),
        ProfileFaqSection(
            title = stringResource(Res.string.profile_help_section_selling_gold),
            icon = Icons.Default.Sell,
            items = listOf(
                ProfileFaqEntry(
                    stringResource(Res.string.profile_help_sell_q1),
                    stringResource(Res.string.profile_help_sell_a1),
                ),
                ProfileFaqEntry(
                    stringResource(Res.string.profile_help_sell_q2),
                    stringResource(Res.string.profile_help_sell_a2),
                ),
                ProfileFaqEntry(
                    stringResource(Res.string.profile_help_sell_q3),
                    stringResource(Res.string.profile_help_sell_a3),
                ),
            ),
        ),
        ProfileFaqSection(
            title = stringResource(Res.string.profile_help_section_delivery),
            icon = Icons.Default.LocalShipping,
            items = listOf(
                ProfileFaqEntry(
                    stringResource(Res.string.profile_help_delivery_q1),
                    stringResource(Res.string.profile_help_delivery_a1),
                ),
                ProfileFaqEntry(
                    stringResource(Res.string.profile_help_delivery_q2),
                    stringResource(Res.string.profile_help_delivery_a2),
                ),
                ProfileFaqEntry(
                    stringResource(Res.string.profile_help_delivery_q3),
                    stringResource(Res.string.profile_help_delivery_a3),
                ),
            ),
        ),
        ProfileFaqSection(
            title = stringResource(Res.string.profile_help_section_trust),
            icon = Icons.Default.Lock,
            items = listOf(
                ProfileFaqEntry(
                    stringResource(Res.string.profile_help_trust_q1),
                    stringResource(Res.string.profile_help_trust_a1),
                ),
                ProfileFaqEntry(
                    stringResource(Res.string.profile_help_trust_q2),
                    stringResource(Res.string.profile_help_trust_a2),
                ),
                ProfileFaqEntry(
                    stringResource(Res.string.profile_help_trust_q3),
                    stringResource(Res.string.profile_help_trust_a3),
                ),
            ),
        ),
        ProfileFaqSection(
            title = stringResource(Res.string.profile_help_section_rewards),
            icon = Icons.Default.People,
            items = listOf(
                ProfileFaqEntry(
                    stringResource(Res.string.profile_help_rewards_q1),
                    stringResource(Res.string.profile_help_rewards_a1),
                ),
                ProfileFaqEntry(
                    stringResource(Res.string.profile_help_rewards_q2),
                    stringResource(Res.string.profile_help_rewards_a2),
                ),
                ProfileFaqEntry(
                    stringResource(Res.string.profile_help_rewards_q3),
                    stringResource(Res.string.profile_help_rewards_a3),
                ),
            ),
        ),
    )
}
