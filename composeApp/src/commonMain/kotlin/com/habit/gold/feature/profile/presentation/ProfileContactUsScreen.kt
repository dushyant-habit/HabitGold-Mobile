@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.habit.gold.feature.profile.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.habit.gold.core.network.skipAuthentication
import com.habit.gold.core.presentation.PlatformBackHandler
import com.habit.gold.core.presentation.clearFocusOnTapOutside
import com.habit.gold.feature.home.presentation.ChildPrimaryText
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import habitgoldmobile.composeapp.generated.resources.Res
import habitgoldmobile.composeapp.generated.resources.common_back
import habitgoldmobile.composeapp.generated.resources.common_contact_us
import habitgoldmobile.composeapp.generated.resources.profile_contact_call_label
import habitgoldmobile.composeapp.generated.resources.profile_contact_dialer_unavailable
import habitgoldmobile.composeapp.generated.resources.profile_contact_email_label
import habitgoldmobile.composeapp.generated.resources.profile_contact_issue_label
import habitgoldmobile.composeapp.generated.resources.profile_contact_issue_placeholder
import habitgoldmobile.composeapp.generated.resources.profile_contact_message_button
import habitgoldmobile.composeapp.generated.resources.profile_contact_quick_support
import habitgoldmobile.composeapp.generated.resources.profile_contact_send_failure
import habitgoldmobile.composeapp.generated.resources.profile_contact_send_message
import habitgoldmobile.composeapp.generated.resources.profile_contact_send_success
import habitgoldmobile.composeapp.generated.resources.profile_contact_subject_label
import habitgoldmobile.composeapp.generated.resources.profile_contact_subject_placeholder
import habitgoldmobile.composeapp.generated.resources.profile_contact_support_phone_uri
import habitgoldmobile.composeapp.generated.resources.profile_contact_support_whatsapp_uri
import habitgoldmobile.composeapp.generated.resources.profile_contact_whatsapp_label
import habitgoldmobile.composeapp.generated.resources.profile_contact_whatsapp_unavailable
import org.jetbrains.compose.resources.stringResource

private const val ProfileSupportEmail = "support@habitgold.com"
private const val ProfileSupportWebhookUrl = "https://chat.googleapis.com/v1/spaces/AAQAbzGkvmo/messages?key=AIzaSyDdI0hCZtE6vySjMm-WEfRq3CPzqKqqsHI&token=HnzvV9xt96VAWaypn8_37o3c5sVWxq0Yw1FxCUQ6HW8"

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
    val sendSuccessMessage = stringResource(Res.string.profile_contact_send_success)
    val sendFailureMessage = stringResource(Res.string.profile_contact_send_failure)
    val dialerUnavailableMessage = stringResource(Res.string.profile_contact_dialer_unavailable)
    val whatsAppUnavailableMessage = stringResource(Res.string.profile_contact_whatsapp_unavailable)

    PlatformBackHandler(
        enabled = true,
        onBack = onBackClick,
    )

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
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
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
                            snackbarHostState.showSnackbar(if (success) sendSuccessMessage else sendFailureMessage)
                            if (success) onBackClick()
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
                .verticalScroll(androidx.compose.foundation.rememberScrollState()),
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
                                    scope.launch { snackbarHostState.showSnackbar(dialerUnavailableMessage) }
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
                                    scope.launch { snackbarHostState.showSnackbar(whatsAppUnavailableMessage) }
                                }
                        },
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(Res.string.profile_contact_send_message),
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

private fun String.wordCount(): Int {
    return trim().split(Regex("\\s+")).filter { it.isNotBlank() }.size
}
