@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.habit.gold.feature.profile.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.HeadsetMic
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habit.gold.core.designsystem.HabitGoldPalette
import com.habit.gold.core.presentation.PlatformBackHandler
import com.habit.gold.core.presentation.clearFocusOnTapOutside
import com.habit.gold.feature.home.presentation.ChildMutedText
import com.habit.gold.feature.home.presentation.ChildPrimaryText
import habitgoldmobile.composeapp.generated.resources.Res
import habitgoldmobile.composeapp.generated.resources.common_back
import habitgoldmobile.composeapp.generated.resources.common_contact_us
import habitgoldmobile.composeapp.generated.resources.profile_help_categories_label
import habitgoldmobile.composeapp.generated.resources.profile_help_header_body
import habitgoldmobile.composeapp.generated.resources.profile_help_header_title
import habitgoldmobile.composeapp.generated.resources.profile_help_no_results
import habitgoldmobile.composeapp.generated.resources.profile_help_question_count
import habitgoldmobile.composeapp.generated.resources.profile_help_results_label
import habitgoldmobile.composeapp.generated.resources.profile_help_search_placeholder
import habitgoldmobile.composeapp.generated.resources.profile_hub_help_center
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun ProfileHelpCenterScreen(
    onBackClick: () -> Unit,
    onOpenContactUs: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var searchQuery by rememberSaveable { mutableStateOf("") }
    val filteredSections = profileFaqSections().filterForQuery(searchQuery)

    PlatformBackHandler(
        enabled = true,
        onBack = onBackClick,
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(Res.string.profile_hub_help_center),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = HabitGoldPalette.plum,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(Res.string.common_back),
                            tint = HabitGoldPalette.plum,
                        )
                    }
                },
                actions = {
                    Surface(
                        modifier = Modifier.padding(end = 8.dp),
                        shape = RoundedCornerShape(999.dp),
                        color = HabitGoldPalette.plum.copy(alpha = 0.1f),
                    ) {
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(999.dp))
                                .clickable(onClick = onOpenContactUs)
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                imageVector = Icons.Default.HeadsetMic,
                                contentDescription = null,
                                tint = HabitGoldPalette.plum,
                                modifier = Modifier.size(16.dp),
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = stringResource(Res.string.common_contact_us),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = HabitGoldPalette.plum,
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .clearFocusOnTapOutside {}
                .verticalScroll(rememberScrollState()),
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(Res.string.profile_help_header_title),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = HabitGoldPalette.plum,
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = stringResource(Res.string.profile_help_header_body),
                fontSize = 13.sp,
                lineHeight = 20.sp,
                color = Color(0xFF667085),
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        text = stringResource(Res.string.profile_help_search_placeholder),
                        color = Color(0xFF98A2B3),
                    )
                },
                singleLine = true,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = HabitGoldPalette.plum.copy(alpha = 0.7f),
                    )
                },
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFF7F4FF),
                    unfocusedContainerColor = Color(0xFFF7F4FF),
                    focusedBorderColor = Color(0xFFDCCEFF),
                    unfocusedBorderColor = Color(0xFFDCCEFF),
                    focusedTextColor = ChildPrimaryText,
                    unfocusedTextColor = ChildPrimaryText,
                ),
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = stringResource(
                    if (searchQuery.trim().isBlank()) {
                        Res.string.profile_help_categories_label
                    } else {
                        Res.string.profile_help_results_label
                    },
                ),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = HabitGoldPalette.plum,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(bottom = 16.dp),
            )
            if (filteredSections.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE9D8FD)),
                ) {
                    Text(
                        text = stringResource(Res.string.profile_help_no_results, searchQuery.trim()),
                        modifier = Modifier.padding(18.dp),
                        fontSize = 13.sp,
                        lineHeight = 20.sp,
                        color = ChildMutedText,
                    )
                }
            } else {
                filteredSections.forEachIndexed { index, section ->
                    ProfileFaqCategoryCard(
                        section = section,
                        expandedByDefault = searchQuery.trim().isNotBlank() || index == 0,
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ProfileFaqCategoryCard(
    section: ProfileFaqSection,
    expandedByDefault: Boolean,
) {
    var expanded by remember(section.title) { mutableStateOf(expandedByDefault) }
    val rotation by animateFloatAsState(targetValue = if (expanded) 180f else 0f)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) { expanded = !expanded },
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, Color(0xFFE9D8FD)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF6F1FB)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = section.icon,
                        contentDescription = null,
                        tint = if (expanded) HabitGoldPalette.plum else Color(0xFF6B7280),
                        modifier = Modifier.size(20.dp),
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = section.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (expanded) HabitGoldPalette.plum else ChildPrimaryText,
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = stringResource(Res.string.profile_help_question_count, section.items.size),
                        fontSize = 12.sp,
                        color = Color(0xFF94A3B8),
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(Color(0xFFF6F1FB))
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        modifier = Modifier.rotate(rotation),
                        tint = HabitGoldPalette.plum,
                    )
                }
            }

            AnimatedVisibility(visible = expanded) {
                Column {
                    HorizontalDivider(
                        modifier = Modifier.padding(top = 14.dp),
                        color = Color(0xFFE9D8FD),
                    )
                    Column(modifier = Modifier.padding(top = 16.dp)) {
                        section.items.forEachIndexed { index, item ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Color(0xFFF9F5FF))
                                    .padding(14.dp),
                            ) {
                                Text(
                                    text = item.question,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = HabitGoldPalette.plum,
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = item.answer,
                                    fontSize = 13.sp,
                                    lineHeight = 20.sp,
                                    color = Color(0xFF667085),
                                )
                            }
                            if (index != section.items.lastIndex) {
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun List<ProfileFaqSection>.filterForQuery(query: String): List<ProfileFaqSection> {
    val normalized = query.trim()
    if (normalized.isBlank()) return this
    return mapNotNull { section ->
        val matchingItems = section.items.filter { item ->
            section.title.contains(normalized, ignoreCase = true) ||
                item.question.contains(normalized, ignoreCase = true) ||
                item.answer.contains(normalized, ignoreCase = true)
        }
        if (matchingItems.isEmpty()) null else section.copy(items = matchingItems)
    }
}
