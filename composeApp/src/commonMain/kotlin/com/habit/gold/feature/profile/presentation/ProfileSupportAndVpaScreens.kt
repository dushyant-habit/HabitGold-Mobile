@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.habit.gold.feature.profile.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.HeadsetMic
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sell
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habit.gold.core.designsystem.HabitGoldPalette
import com.habit.gold.core.network.ApiResult
import com.habit.gold.core.network.skipAuthentication
import com.habit.gold.core.presentation.clearFocusOnTapOutside
import com.habit.gold.feature.home.presentation.ChildCardBorder
import com.habit.gold.feature.home.presentation.ChildMutedText
import com.habit.gold.feature.home.presentation.ChildPrimaryText
import com.habit.gold.feature.home.presentation.HomeChildEmptyState
import com.habit.gold.feature.trade.domain.model.TradeUserVpa
import com.habit.gold.feature.trade.domain.usecase.GetTradeUserVpasUseCase
import com.habit.gold.feature.trade.domain.usecase.SetDefaultTradeVpaUseCase
import com.habit.gold.feature.trade.domain.usecase.VerifyTradeVpaUseCase
import habitgoldmobile.composeapp.generated.resources.Res
import habitgoldmobile.composeapp.generated.resources.common_back
import habitgoldmobile.composeapp.generated.resources.common_contact_us
import habitgoldmobile.composeapp.generated.resources.common_retry
import habitgoldmobile.composeapp.generated.resources.profile_contact_call_label
import habitgoldmobile.composeapp.generated.resources.profile_contact_email_label
import habitgoldmobile.composeapp.generated.resources.profile_contact_issue_label
import habitgoldmobile.composeapp.generated.resources.profile_contact_issue_placeholder
import habitgoldmobile.composeapp.generated.resources.profile_contact_message_button
import habitgoldmobile.composeapp.generated.resources.profile_contact_quick_support
import habitgoldmobile.composeapp.generated.resources.profile_contact_subject_label
import habitgoldmobile.composeapp.generated.resources.profile_contact_subject_placeholder
import habitgoldmobile.composeapp.generated.resources.profile_contact_support_email
import habitgoldmobile.composeapp.generated.resources.profile_contact_support_phone_display
import habitgoldmobile.composeapp.generated.resources.profile_contact_support_phone_uri
import habitgoldmobile.composeapp.generated.resources.profile_contact_support_whatsapp_display
import habitgoldmobile.composeapp.generated.resources.profile_contact_support_whatsapp_uri
import habitgoldmobile.composeapp.generated.resources.profile_contact_subtitle
import habitgoldmobile.composeapp.generated.resources.profile_contact_whatsapp_label
import habitgoldmobile.composeapp.generated.resources.profile_help_subtitle
import habitgoldmobile.composeapp.generated.resources.profile_hub_help_center
import habitgoldmobile.composeapp.generated.resources.profile_vpa_default
import habitgoldmobile.composeapp.generated.resources.profile_vpa_empty
import habitgoldmobile.composeapp.generated.resources.profile_vpa_info_note
import habitgoldmobile.composeapp.generated.resources.profile_vpa_not_verified
import habitgoldmobile.composeapp.generated.resources.profile_vpa_verified
import habitgoldmobile.composeapp.generated.resources.trade_route_vpa_title
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource

private const val ProfileSupportEmail = "support@habitgold.com"
private const val ProfileSupportWebhookUrl = "https://chat.googleapis.com/v1/spaces/AAQAbzGkvmo/messages?key=AIzaSyDdI0hCZtE6vySjMm-WEfRq3CPzqKqqsHI&token=HnzvV9xt96VAWaypn8_37o3c5sVWxq0Yw1FxCUQ6HW8"

@Composable
internal fun ProfileHelpCenterScreen(
    onBackClick: () -> Unit,
    onOpenContactUs: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var searchQuery by rememberSaveable { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    val normalizedQuery = searchQuery.trim()
    val filteredSections = remember(normalizedQuery) {
        if (normalizedQuery.isBlank()) {
            ProfileFaqData.sections
        } else {
            ProfileFaqData.sections.mapNotNull { section ->
                val matchingItems = section.items.filter { item ->
                    section.title.contains(normalizedQuery, ignoreCase = true) ||
                        item.question.contains(normalizedQuery, ignoreCase = true) ||
                        item.answer.contains(normalizedQuery, ignoreCase = true)
                }
                if (matchingItems.isEmpty()) null else section.copy(items = matchingItems)
            }
        }
    }

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
                .clearFocusOnTapOutside { focusManager.clearFocus(force = true) }
                .verticalScroll(rememberScrollState()),
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Find answers quickly",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = HabitGoldPalette.plum,
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Browse common questions across gold buying, selling, delivery, rewards, and auto save.",
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
                        text = "Search questions",
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
                text = if (normalizedQuery.isBlank()) "FAQ CATEGORIES" else "SEARCH RESULTS",
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
                        text = "No FAQs matched \"$normalizedQuery\".",
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
                        expandedByDefault = normalizedQuery.isNotBlank() || index == 0,
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
internal fun ProfileContactUsScreen(
    httpClient: HttpClient,
    userName: String,
    userPhone: String,
    userEmail: String,
    userGender: String,
    userDob: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val uriHandler = LocalUriHandler.current
    val snackbarHostState = remember { SnackbarHostState() }
    var subject by rememberSaveable { mutableStateOf("") }
    var issue by rememberSaveable { mutableStateOf("") }
    var isSending by rememberSaveable { mutableStateOf(false) }

    val subjectWordCount = remember(subject) { subject.wordCount() }
    val issueWordCount = remember(issue) { issue.wordCount() }
    val subjectValid = subject.isNotBlank() && subjectWordCount <= 100
    val issueValid = issue.isNotBlank() && issueWordCount <= 2000
    val canSubmit = subjectValid && issueValid && !isSending

    val supportPhoneUri = stringResource(Res.string.profile_contact_support_phone_uri)
    val supportWhatsAppUri = stringResource(Res.string.profile_contact_support_whatsapp_uri)
    val supportPhoneDisplay = stringResource(Res.string.profile_contact_support_phone_display)
    val supportWhatsAppDisplay = stringResource(Res.string.profile_contact_support_whatsapp_display)
    val supportEmailDisplay = stringResource(Res.string.profile_contact_support_email)

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = ProfileScreenBackground,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(Res.string.common_contact_us),
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
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White),
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .imePadding()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                color = Color.Transparent,
            ) {
                Button(
                    onClick = {
                        scope.launch {
                            isSending = true
                            val success = sendToGoogleChatWebhook(
                                httpClient = httpClient,
                                subject = subject,
                                issue = issue,
                                userName = userName,
                                userPhone = userPhone,
                                userEmail = userEmail,
                                userGender = userGender,
                                userDob = userDob,
                            )
                            isSending = false
                            if (success) {
                                snackbarHostState.showSnackbar("Message sent successfully!")
                                onBackClick()
                            } else {
                                snackbarHostState.showSnackbar("Failed to send message. Please try again.")
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    enabled = canSubmit,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = HabitGoldPalette.plum,
                        disabledContainerColor = HabitGoldPalette.plum.copy(alpha = 0.5f),
                    ),
                ) {
                    if (isSending) {
                        androidx.compose.material3.CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            color = Color.White,
                            strokeWidth = 2.dp,
                        )
                    } else {
                        Text(
                            text = stringResource(Res.string.profile_contact_message_button),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                }
            }
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .clearFocusOnTapOutside { focusManager.clearFocus(force = true) }
                .verticalScroll(rememberScrollState()),
        ) {
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = stringResource(Res.string.profile_contact_quick_support),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = ChildPrimaryText,
            )
            Spacer(modifier = Modifier.height(10.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = HabitGoldPalette.plum.copy(alpha = 0.12f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    ProfileSupportActionTile(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Email,
                        title = stringResource(Res.string.profile_contact_email_label),
                        accentColor = Color(0xFF2563EB),
                        onClick = { uriHandler.openUri("mailto:$ProfileSupportEmail") },
                    )
                    ProfileSupportActionTile(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Call,
                        title = stringResource(Res.string.profile_contact_call_label),
                        accentColor = Color(0xFF059669),
                        onClick = {
                            runCatching { uriHandler.openUri("tel:$supportPhoneUri") }
                                .onFailure {
                                    scope.launch { snackbarHostState.showSnackbar("Unable to open the dialer right now.") }
                                }
                        },
                    )
                    ProfileSupportActionTile(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.ChatBubble,
                        title = stringResource(Res.string.profile_contact_whatsapp_label),
                        accentColor = Color(0xFF16A34A),
                        onClick = {
                            runCatching { uriHandler.openUri("https://wa.me/$supportWhatsAppUri") }
                                .onFailure {
                                    scope.launch { snackbarHostState.showSnackbar("WhatsApp is not available on this device.") }
                                }
                        },
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Send us a message",
                modifier = Modifier.fillMaxWidth(),
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = ChildPrimaryText,
                textAlign = TextAlign.Start,
            )
            Spacer(modifier = Modifier.height(12.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(Res.string.profile_contact_subject_label),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = ProfileFieldLabel,
                    )
                    Text(
                        text = "$subjectWordCount/100 words",
                        fontSize = 11.sp,
                        color = if (subjectWordCount > 100) ProfileError else ProfileMutedText,
                    )
                }
                ProfileEditorTextField(
                    value = subject,
                    onValueChange = { subject = it },
                    placeholder = stringResource(Res.string.profile_contact_subject_placeholder),
                    keyboardCapitalization = KeyboardCapitalization.Sentences,
                    imeAction = androidx.compose.ui.text.input.ImeAction.Next,
                    keyboardActions = androidx.compose.foundation.text.KeyboardActions(
                        onDone = { focusManager.clearFocus(force = true) },
                    ),
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(Res.string.profile_contact_issue_label),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = ProfileFieldLabel,
                    )
                    Text(
                        text = "$issueWordCount/2000 words",
                        fontSize = 11.sp,
                        color = if (issueWordCount > 2000) ProfileError else ProfileMutedText,
                    )
                }
                ProfileEditorTextField(
                    value = issue,
                    onValueChange = { issue = it },
                    placeholder = stringResource(Res.string.profile_contact_issue_placeholder),
                    keyboardCapitalization = KeyboardCapitalization.Sentences,
                    keyboardType = KeyboardType.Text,
                    singleLine = false,
                    minLines = 7,
                    imeAction = androidx.compose.ui.text.input.ImeAction.Default,
                    modifier = Modifier.height(188.dp),
                    keyboardActions = androidx.compose.foundation.text.KeyboardActions(
                        onDone = { focusManager.clearFocus(force = true) },
                    ),
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

private suspend fun sendToGoogleChatWebhook(
    httpClient: HttpClient,
    subject: String,
    issue: String,
    userName: String,
    userPhone: String,
    userEmail: String,
    userGender: String,
    userDob: String,
): Boolean {
    return runCatching {
        val formattedMessage = buildString {
            append("*NEW SUPPORT TICKET RECEIVED*")
            append("\n\n*From:* ")
            append(userName.ifBlank { "N/A" })
            append(" (")
            append(userPhone.ifBlank { "N/A" })
            append(")")
            append("\n*Email:* ")
            append(userEmail.ifBlank { "Not Provided" })
            append("\n*Profile:* ")
            append(userGender.ifBlank { "N/A" })
            append(" | DOB: ")
            append(userDob.ifBlank { "N/A" })
            append("\n\n*Subject:* ")
            append(subject.trim())
            append("\n\n*Issue Description:*\n")
            append(issue.trim())
        }

        val response: HttpResponse = httpClient.post(ProfileSupportWebhookUrl) {
            skipAuthentication()
            contentType(ContentType.Application.Json)
            setBody(ProfileGoogleChatPayload(text = formattedMessage))
        }
        response.status.value in 200..299
    }.getOrDefault(false)
}

@Serializable
private data class ProfileGoogleChatPayload(
    val text: String,
)

@Composable
internal fun ProfileVpaListRoute(
    getTradeUserVpasUseCase: GetTradeUserVpasUseCase,
    setDefaultTradeVpaUseCase: SetDefaultTradeVpaUseCase,
    verifyTradeVpaUseCase: VerifyTradeVpaUseCase,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var refreshKey by rememberSaveable { mutableStateOf(0) }
    val uiState by produceState<ProfileVpaUiState>(
        initialValue = ProfileVpaUiState.Loading,
        key1 = refreshKey,
        key2 = getTradeUserVpasUseCase,
    ) {
        value = ProfileVpaUiState.Loading
        value = when (val result = getTradeUserVpasUseCase()) {
            is ApiResult.Success -> ProfileVpaUiState.Success(result.value)
            is ApiResult.Failure -> ProfileVpaUiState.Error(result.error.message)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.White,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(Res.string.trade_route_vpa_title),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
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
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White),
            )
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            when (val state = uiState) {
                ProfileVpaUiState.Loading -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(top = 20.dp, bottom = 124.dp),
                    ) {
                        items(3) {
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                color = Color(0xFFF8FAFC),
                                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(0.62f)
                                            .height(18.dp)
                                            .profileShimmerPlaceholder(RoundedCornerShape(999.dp)),
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(0.38f)
                                            .height(14.dp)
                                            .profileShimmerPlaceholder(RoundedCornerShape(999.dp)),
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    HorizontalDivider(color = Color(0xFFE2E8F0))
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Box(
                                        modifier = Modifier
                                            .width(104.dp)
                                            .height(14.dp)
                                            .profileShimmerPlaceholder(RoundedCornerShape(999.dp)),
                                    )
                                }
                            }
                        }
                    }
                }

                is ProfileVpaUiState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            Text(
                                text = state.message,
                                color = ProfileError,
                                textAlign = TextAlign.Center,
                                lineHeight = 20.sp,
                            )
                            Button(
                                onClick = { refreshKey += 1 },
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = HabitGoldPalette.plum),
                            ) {
                                Text(
                                    text = stringResource(Res.string.common_retry),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                        }
                    }
                }

                is ProfileVpaUiState.Success -> {
                    if (state.vpas.isEmpty()) {
                        HomeChildEmptyState(
                            paddingValues = PaddingValues(24.dp),
                            message = stringResource(Res.string.profile_vpa_empty),
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 20.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            contentPadding = PaddingValues(top = 20.dp, bottom = 124.dp),
                        ) {
                            items(state.vpas, key = { it.id }) { vpa ->
                                ProfileVpaCard(vpa = vpa)
                            }
                        }
                    }
                }
            }

            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 20.dp, vertical = 24.dp),
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFFF8FAFC),
                border = BorderStroke(1.dp, ChildCardBorder),
            ) {
                Text(
                    text = stringResource(Res.string.profile_vpa_info_note),
                    fontSize = 13.sp,
                    color = ChildMutedText,
                    lineHeight = 18.sp,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                )
            }
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
                        text = "${section.items.size} questions",
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

@Composable
private fun ProfileSupportActionTile(
    modifier: Modifier,
    icon: ImageVector,
    title: String,
    accentColor: Color,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .background(Color.White)
            .padding(horizontal = 8.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = accentColor,
            modifier = Modifier.size(16.dp),
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = title,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = ChildPrimaryText,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun ProfileVpaCard(
    vpa: TradeUserVpa,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFFF8FAFC),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = vpa.address,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = ChildPrimaryText,
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = vpa.holderName?.takeIf { it.isNotBlank() } ?: "-",
                        fontSize = 13.sp,
                        color = ChildMutedText,
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Icon(
                    imageVector = if (vpa.isVerified) Icons.Default.CheckCircle else Icons.Default.Warning,
                    contentDescription = null,
                    tint = if (vpa.isVerified) Color(0xFF15803D) else Color(0xFFB45309),
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = Color(0xFFE2E8F0))
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Surface(
                    shape = RoundedCornerShape(999.dp),
                    color = if (vpa.isVerified) Color(0xFFF0FDF4) else Color(0xFFFFFBEB),
                    border = BorderStroke(
                        1.dp,
                        if (vpa.isVerified) Color(0xFFBBF7D0) else Color(0xFFFDE68A),
                    ),
                ) {
                    Text(
                        text = if (vpa.isVerified) {
                            stringResource(Res.string.profile_vpa_verified)
                        } else {
                            stringResource(Res.string.profile_vpa_not_verified)
                        },
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (vpa.isVerified) Color(0xFF15803D) else Color(0xFFB45309),
                    )
                }

                if (vpa.isDefault) {
                    Text(
                        text = stringResource(Res.string.profile_vpa_default),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = HabitGoldPalette.plum,
                    )
                }
            }
        }
    }
}

private fun String.wordCount(): Int {
    return trim().split(Regex("\\s+")).filter { it.isNotBlank() }.size
}

private fun String.encodeUriComponent(): String {
    return buildString(length) {
        this@encodeUriComponent.forEach { character ->
            when (character) {
                ' ' -> append("%20")
                '\n' -> append("%0A")
                else -> append(character)
            }
        }
    }
}

private sealed interface ProfileVpaUiState {
    data object Loading : ProfileVpaUiState
    data class Success(val vpas: List<TradeUserVpa>) : ProfileVpaUiState
    data class Error(val message: String) : ProfileVpaUiState
}

private data class ProfileFaqSection(
    val title: String,
    val icon: ImageVector,
    val items: List<ProfileFaqEntry>,
)

private data class ProfileFaqEntry(
    val question: String,
    val answer: String,
)

private object ProfileFaqData {
    val sections = listOf(
        ProfileFaqSection(
            title = "Top FAQs",
            icon = Icons.Default.Star,
            items = listOf(
                ProfileFaqEntry("What is HabitGold?", "HabitGold is a digital platform that helps users build a saving habit by investing small amounts into 24K (99.99% pure) digital gold."),
                ProfileFaqEntry("Is the gold on HabitGold real?", "Yes. Every purchase of digital gold represents real gold of 24K (99.99% purity) that is securely stored on your behalf."),
                ProfileFaqEntry("What is the minimum amount required to start?", "You can start saving in gold on HabitGold with as little as ₹10."),
                ProfileFaqEntry("Where is my gold stored?", "The gold purchased through HabitGold is stored in secure vaults managed by authorized gold storage partner Safegold."),
                ProfileFaqEntry("Can I sell my gold anytime?", "Yes. You can sell your digital gold anytime through the HabitGold app based on the live selling price available on the platform."),
            ),
        ),
        ProfileFaqSection(
            title = "Getting Started",
            icon = Icons.Default.HeadsetMic,
            items = listOf(
                ProfileFaqEntry("How does HabitGold work?", "Users can purchase digital gold through the app. The purchased gold is stored securely in 100% insured vaults and reflected in the user’s HabitGold account."),
                ProfileFaqEntry("Who can use HabitGold?", "Any Indian resident with a valid mobile number and payment method can use HabitGold."),
                ProfileFaqEntry("How do I create a HabitGold account?", "You can create an account using your mobile number and complete a simple onboarding process within the app."),
            ),
        ),
        ProfileFaqSection(
            title = "Buying Digital Gold",
            icon = Icons.Default.ShoppingCart,
            items = listOf(
                ProfileFaqEntry("What is digital gold?", "Digital gold allows users to buy real gold online in small amounts while the gold is stored securely in vaults."),
                ProfileFaqEntry("What is the minimum amount required to buy gold?", "You can buy digital gold starting from ₹10."),
                ProfileFaqEntry("How is the gold price determined?", "Gold prices are based on live market prices provided by authorized gold partners and may change throughout the day."),
            ),
        ),
        ProfileFaqSection(
            title = "Habit Saving (Auto Save)",
            icon = Icons.Default.Payments,
            items = listOf(
                ProfileFaqEntry("What is Habit Saving?", "Habit Saving is a feature that allows users to automatically invest in gold at regular intervals."),
                ProfileFaqEntry("How does auto saving work?", "Users can schedule automatic purchases of gold daily, weekly, or monthly."),
                ProfileFaqEntry("Can I change my auto-save amount?", "Yes. You can modify the saving amount anytime within the app."),
            ),
        ),
        ProfileFaqSection(
            title = "Selling Gold & Withdrawals",
            icon = Icons.Default.Sell,
            items = listOf(
                ProfileFaqEntry("How do I sell my digital gold?", "You can sell your gold through the HabitGold app by selecting the quantity of gold you want to sell."),
                ProfileFaqEntry("What price will I receive when selling gold?", "You will receive the live selling price displayed on the platform at the time of the transaction."),
                ProfileFaqEntry("How long does it take to receive money after selling gold?", "The proceeds are typically transferred to your bank account within the platform’s settlement timeline."),
            ),
        ),
        ProfileFaqSection(
            title = "Physical Gold Delivery",
            icon = Icons.Default.LocalShipping,
            items = listOf(
                ProfileFaqEntry("Can I convert digital gold into physical gold?", "Yes. Users may convert digital gold into physical gold such as coins or bars."),
                ProfileFaqEntry("What purity is the physical gold delivered?", "Physical gold is typically delivered as 24K high-purity gold."),
                ProfileFaqEntry("Are there delivery charges?", "Minting, logistics, and delivery charges may apply when converting digital gold to physical gold. Above 5 gm free delivery."),
            ),
        ),
        ProfileFaqSection(
            title = "Trust & Transparency",
            icon = Icons.Default.Lock,
            items = listOf(
                ProfileFaqEntry("Who provides the gold available on HabitGold?", "The gold is sourced through authorized gold partners who maintain the infrastructure for secure storage and transactions."),
                ProfileFaqEntry("Is my gold backed by physical gold?", "Yes. Every digital gold purchase corresponds to physical gold stored on behalf of users."),
                ProfileFaqEntry("Is the gold 100% insured?", "The stored gold is 100% insured against risks such as theft or loss."),
            ),
        ),
        ProfileFaqSection(
            title = "Rewards & Gold Partner Program",
            icon = Icons.Default.People,
            items = listOf(
                ProfileFaqEntry("What is the HabitGold reward system?", "HabitGold offers rewards or incentives to users for purchasing gold through the platform."),
                ProfileFaqEntry("What is the HabitGold Gold Partner Program?", "The Gold Partner Program allows you to refer HabitGold to others and earn rewards whenever they purchase gold in the future."),
                ProfileFaqEntry("Who can join the Gold Partner Program?", "Anyone can join the Gold Partner Program and earn rewards by bringing new users to the app."),
            ),
        ),
    )
}
