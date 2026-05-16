package com.habit.gold.feature.home.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HeadsetMic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habit.gold.core.designsystem.HabitGoldPalette
import habitgoldmobile.composeapp.generated.resources.Res
import habitgoldmobile.composeapp.generated.resources.common_contact_us
import habitgoldmobile.composeapp.generated.resources.home_help_center_contact_description
import habitgoldmobile.composeapp.generated.resources.home_help_center_contact_title
import habitgoldmobile.composeapp.generated.resources.home_help_center_empty_state
import habitgoldmobile.composeapp.generated.resources.home_help_center_faq_1_answer
import habitgoldmobile.composeapp.generated.resources.home_help_center_faq_1_question
import habitgoldmobile.composeapp.generated.resources.home_help_center_faq_2_answer
import habitgoldmobile.composeapp.generated.resources.home_help_center_faq_2_question
import habitgoldmobile.composeapp.generated.resources.home_help_center_faq_3_answer
import habitgoldmobile.composeapp.generated.resources.home_help_center_faq_3_question
import habitgoldmobile.composeapp.generated.resources.home_help_center_faq_4_answer
import habitgoldmobile.composeapp.generated.resources.home_help_center_faq_4_question
import habitgoldmobile.composeapp.generated.resources.home_help_center_intro_description
import habitgoldmobile.composeapp.generated.resources.home_help_center_intro_title
import habitgoldmobile.composeapp.generated.resources.home_help_center_search_placeholder
import habitgoldmobile.composeapp.generated.resources.home_help_center_title
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun HomeHelpCenterScreen(
    onBackClick: () -> Unit,
) {
    var searchQuery by remember { mutableStateOf("") }
    val faqItems = remember {
        listOf(
            HomeFaqItem(Res.string.home_help_center_faq_1_question, Res.string.home_help_center_faq_1_answer),
            HomeFaqItem(Res.string.home_help_center_faq_2_question, Res.string.home_help_center_faq_2_answer),
            HomeFaqItem(Res.string.home_help_center_faq_3_question, Res.string.home_help_center_faq_3_answer),
            HomeFaqItem(Res.string.home_help_center_faq_4_question, Res.string.home_help_center_faq_4_answer),
        )
    }
    val normalizedQuery = searchQuery.trim()
    val filteredFaqItems = faqItems.filter { faqItem ->
        normalizedQuery.isBlank() ||
            stringResource(faqItem.question).contains(normalizedQuery, ignoreCase = true) ||
            stringResource(faqItem.answer).contains(normalizedQuery, ignoreCase = true)
    }

    HomeChildScaffold(
        title = stringResource(Res.string.home_help_center_title),
        onBackClick = onBackClick,
        backgroundColor = Color.White,
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF6F1FB)),
                border = BorderStroke(1.dp, Color(0xFFE9D8FD)),
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = stringResource(Res.string.home_help_center_intro_title),
                        fontSize = 20.sp,
                        lineHeight = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = HabitGoldPalette.plum,
                    )
                    Text(
                        text = stringResource(Res.string.home_help_center_intro_description),
                        fontSize = 13.sp,
                        lineHeight = 20.sp,
                        color = ChildMutedText,
                    )
                }
            }

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                    )
                },
                placeholder = {
                    Text(text = stringResource(Res.string.home_help_center_search_placeholder))
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = HabitGoldPalette.plum,
                    unfocusedBorderColor = ChildCardBorder,
                ),
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, ChildCardBorder),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    androidx.compose.foundation.layout.Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFF6F1FB)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = Icons.Default.HeadsetMic,
                                contentDescription = null,
                                tint = HabitGoldPalette.plum,
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = stringResource(Res.string.home_help_center_contact_title),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = ChildPrimaryText,
                            )
                            Text(
                                text = stringResource(Res.string.home_help_center_contact_description),
                                fontSize = 12.sp,
                                lineHeight = 18.sp,
                                color = ChildMutedText,
                            )
                        }
                    }
                    Surface(
                        shape = RoundedCornerShape(999.dp),
                        color = HabitGoldPalette.plum.copy(alpha = 0.08f),
                    ) {
                        Text(
                            text = stringResource(Res.string.common_contact_us),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = HabitGoldPalette.plum,
                        )
                    }
                }
            }

            if (filteredFaqItems.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, ChildCardBorder),
                ) {
                    Text(
                        text = stringResource(Res.string.home_help_center_empty_state),
                        modifier = Modifier.padding(16.dp),
                        color = ChildMutedText,
                        fontSize = 13.sp,
                    )
                }
            } else {
                filteredFaqItems.forEach { faqItem ->
                    HomeFaqCard(homeFaqItem = faqItem)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
