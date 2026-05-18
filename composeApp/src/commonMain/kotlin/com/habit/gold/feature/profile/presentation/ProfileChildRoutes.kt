@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.habit.gold.feature.profile.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habit.gold.core.designsystem.HabitGoldPalette
import com.habit.gold.core.network.ApiResult
import com.habit.gold.feature.profile.domain.model.ProfileNominee
import com.habit.gold.feature.profile.domain.model.ProfileSummary
import com.habit.gold.feature.profile.domain.usecase.UpdateProfileUseCase
import com.habit.gold.feature.profile.domain.usecase.VerifyProfileKycUseCase
import habitgoldmobile.composeapp.generated.resources.Res
import habitgoldmobile.composeapp.generated.resources.common_back
import habitgoldmobile.composeapp.generated.resources.profile_kyc_error_title
import habitgoldmobile.composeapp.generated.resources.profile_kyc_invalid_name
import habitgoldmobile.composeapp.generated.resources.profile_kyc_invalid_pan
import habitgoldmobile.composeapp.generated.resources.profile_kyc_name_label
import habitgoldmobile.composeapp.generated.resources.profile_kyc_name_placeholder
import habitgoldmobile.composeapp.generated.resources.profile_kyc_pan_label
import habitgoldmobile.composeapp.generated.resources.profile_kyc_pan_placeholder
import habitgoldmobile.composeapp.generated.resources.profile_kyc_title
import habitgoldmobile.composeapp.generated.resources.profile_kyc_verified_button
import habitgoldmobile.composeapp.generated.resources.profile_kyc_verify
import habitgoldmobile.composeapp.generated.resources.profile_nominee_invalid_mobile
import habitgoldmobile.composeapp.generated.resources.profile_nominee_invalid_name
import habitgoldmobile.composeapp.generated.resources.profile_nominee_invalid_relation
import habitgoldmobile.composeapp.generated.resources.profile_nominee_mobile_label
import habitgoldmobile.composeapp.generated.resources.profile_nominee_mobile_placeholder
import habitgoldmobile.composeapp.generated.resources.profile_nominee_name_label
import habitgoldmobile.composeapp.generated.resources.profile_nominee_name_placeholder
import habitgoldmobile.composeapp.generated.resources.profile_nominee_relation_brother
import habitgoldmobile.composeapp.generated.resources.profile_nominee_relation_daughter
import habitgoldmobile.composeapp.generated.resources.profile_nominee_relation_father
import habitgoldmobile.composeapp.generated.resources.profile_nominee_relation_husband
import habitgoldmobile.composeapp.generated.resources.profile_nominee_relation_label
import habitgoldmobile.composeapp.generated.resources.profile_nominee_relation_mother
import habitgoldmobile.composeapp.generated.resources.profile_nominee_relation_other
import habitgoldmobile.composeapp.generated.resources.profile_nominee_relation_placeholder
import habitgoldmobile.composeapp.generated.resources.profile_nominee_relation_sister
import habitgoldmobile.composeapp.generated.resources.profile_nominee_relation_son
import habitgoldmobile.composeapp.generated.resources.profile_nominee_relation_wife
import habitgoldmobile.composeapp.generated.resources.profile_nominee_save
import habitgoldmobile.composeapp.generated.resources.profile_nominee_title
import habitgoldmobile.composeapp.generated.resources.profile_personal_info_dob_label
import habitgoldmobile.composeapp.generated.resources.profile_personal_info_dob_placeholder
import habitgoldmobile.composeapp.generated.resources.profile_personal_info_email_label
import habitgoldmobile.composeapp.generated.resources.profile_personal_info_email_placeholder
import habitgoldmobile.composeapp.generated.resources.profile_personal_info_gender_female
import habitgoldmobile.composeapp.generated.resources.profile_personal_info_gender_label
import habitgoldmobile.composeapp.generated.resources.profile_personal_info_gender_male
import habitgoldmobile.composeapp.generated.resources.profile_personal_info_gender_other
import habitgoldmobile.composeapp.generated.resources.profile_personal_info_gender_placeholder
import habitgoldmobile.composeapp.generated.resources.profile_personal_info_gender_prefer_not
import habitgoldmobile.composeapp.generated.resources.profile_personal_info_invalid_email
import habitgoldmobile.composeapp.generated.resources.profile_personal_info_invalid_name
import habitgoldmobile.composeapp.generated.resources.profile_personal_info_name_helper_locked
import habitgoldmobile.composeapp.generated.resources.profile_personal_info_name_label
import habitgoldmobile.composeapp.generated.resources.profile_personal_info_name_placeholder
import habitgoldmobile.composeapp.generated.resources.profile_personal_info_save
import habitgoldmobile.composeapp.generated.resources.profile_personal_info_title
import habitgoldmobile.composeapp.generated.resources.common_cancel
import org.jetbrains.compose.resources.stringResource
import kotlinx.coroutines.launch

@Composable
internal fun ProfilePersonalInfoRoute(
    seedSummary: ProfileSummary?,
    updateProfileUseCase: UpdateProfileUseCase,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()
    val user = seedSummary?.user
    var name by rememberSaveable { mutableStateOf(user?.name.orEmpty()) }
    var email by rememberSaveable { mutableStateOf(user?.email.orEmpty()) }
    val initialDob = remember(user?.dateOfBirth) { formatDateOfBirthForDisplay(user?.dateOfBirth) }
    var dateOfBirth by rememberSaveable { mutableStateOf(initialDob) }
    var gender by rememberSaveable { mutableStateOf(user?.gender.orEmpty()) }
    var isSaving by rememberSaveable { mutableStateOf(false) }
    var errorMessage by rememberSaveable { mutableStateOf<String?>(null) }
    var showGenderSheet by rememberSaveable { mutableStateOf(false) }
    var showDatePicker by rememberSaveable { mutableStateOf(false) }
    val legalNameLocked = remember(user?.name) { !user?.name.isNullOrBlank() }

    val normalizedName = name.trim()
    val normalizedEmail = email.trim()
    val normalizedDob = dateOfBirth.trim()
    val normalizedStoredDob = initialDob.trim()
    val normalizedGender = gender.trim()
    val hasChanges = normalizedName != user?.name.orEmpty().trim() ||
        normalizedEmail != user?.email.orEmpty().trim() ||
        normalizedDob != normalizedStoredDob ||
        normalizedGender != user?.gender.orEmpty().trim()
    val nameError = if (ProfileInputRules.isLegalNameValid(normalizedName)) null
        else stringResource(Res.string.profile_personal_info_invalid_name)
    val emailError = if (ProfileInputRules.isEmailValid(normalizedEmail)) null
        else stringResource(Res.string.profile_personal_info_invalid_email)

    ProfileDetailScaffold(
        title = stringResource(Res.string.profile_personal_info_title),
        onBackClick = onBackClick,
        bottomButtonLabel = stringResource(Res.string.profile_personal_info_save),
        bottomButtonEnabled = hasChanges && nameError == null && emailError == null && !isSaving,
        bottomButtonLoading = isSaving,
        onBottomButtonClick = {
            if (nameError != null || emailError != null || !hasChanges) return@ProfileDetailScaffold
            focusManager.clearFocus(force = true)
            coroutineScope.launch {
                isSaving = true
                when (
                    val result = updateProfileUseCase(
                        name = normalizedName,
                        email = normalizedEmail,
                        dateOfBirth = normalizedDob.ifBlank { null }?.let(::toIsoDateOrNull),
                        gender = normalizedGender.ifBlank { null },
                        nominee = user?.nominee,
                    )
                ) {
                    is ApiResult.Success -> {
                        isSaving = false
                        onBackClick()
                    }
                    is ApiResult.Failure -> {
                        isSaving = false
                        errorMessage = result.error.message
                    }
                }
            }
        },
        modifier = modifier,
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        ProfileFieldBlock(label = stringResource(Res.string.profile_personal_info_name_label)) {
            ProfileEditorTextField(
                value = name,
                onValueChange = {
                    if (!legalNameLocked) {
                        name = ProfileInputRules.normalizeLegalName(it)
                    }
                },
                placeholder = stringResource(Res.string.profile_personal_info_name_placeholder),
                readOnly = legalNameLocked,
                errorText = if (!legalNameLocked) nameError else null,
                supportingText = if (legalNameLocked) {
                    stringResource(Res.string.profile_personal_info_name_helper_locked)
                } else null,
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus(force = true) }),
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        ProfileFieldBlock(label = stringResource(Res.string.profile_personal_info_email_label)) {
            ProfileEditorTextField(
                value = email,
                onValueChange = { email = ProfileInputRules.normalizeEmail(it) },
                placeholder = stringResource(Res.string.profile_personal_info_email_placeholder),
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next,
                errorText = emailError,
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus(force = true) }),
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        ProfileFieldBlock(label = stringResource(Res.string.profile_personal_info_dob_label)) {
            Box {
                ProfileEditorTextField(
                    value = dateOfBirth,
                    onValueChange = {},
                    readOnly = true,
                    placeholder = stringResource(Res.string.profile_personal_info_dob_placeholder),
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next,
                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus(force = true) }),
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.DateRange,
                            contentDescription = null,
                            tint = ProfileMutedText,
                        )
                    },
                )
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable {
                            focusManager.clearFocus(force = true)
                            showDatePicker = true
                        },
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        ProfileFieldBlock(label = stringResource(Res.string.profile_personal_info_gender_label)) {
            ProfileSelectionField(
                value = gender,
                placeholder = stringResource(Res.string.profile_personal_info_gender_placeholder),
                onClick = {
                    focusManager.clearFocus(force = true)
                    showGenderSheet = true
                },
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
    }

    if (showGenderSheet) {
        val options = listOf(
            stringResource(Res.string.profile_personal_info_gender_male),
            stringResource(Res.string.profile_personal_info_gender_female),
            stringResource(Res.string.profile_personal_info_gender_other),
            stringResource(Res.string.profile_personal_info_gender_prefer_not),
        )
        ProfileSelectionSheet(
            title = stringResource(Res.string.profile_personal_info_gender_label),
            options = options,
            selectedValue = gender,
            onDismiss = { showGenderSheet = false },
            onSelect = { gender = it },
        )
    }

    if (showDatePicker) {
        ProfileDatePickerDialog(
            selectedDate = dateOfBirth,
            onDismiss = { showDatePicker = false },
            onDateSelected = { dateOfBirth = it },
        )
    }

    errorMessage?.let { message ->
        ProfileErrorDialog(
            title = stringResource(Res.string.profile_personal_info_save),
            message = message,
            onDismiss = { errorMessage = null },
        )
    }
}

@Composable
internal fun ProfileKycRoute(
    seedSummary: ProfileSummary?,
    verifyProfileKycUseCase: VerifyProfileKycUseCase,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()
    val user = seedSummary?.user
    val isVerified = remember(user?.kycStatus, user?.kyc?.panMasked) {
        user?.kycStatus?.trim()?.uppercase() in setOf("VERIFIED", "ACTIVE") ||
            !user?.kyc?.panMasked.isNullOrBlank()
    }
    var pan by rememberSaveable { mutableStateOf(user?.kyc?.panMasked.orEmpty()) }
    var fullName by rememberSaveable { mutableStateOf(user?.name.orEmpty()) }
    var isSubmitting by rememberSaveable { mutableStateOf(false) }
    var blockingError by rememberSaveable { mutableStateOf<String?>(null) }

    val normalizedPan = pan.trim().uppercase()
    val normalizedName = fullName.trim()
    val panError = if (normalizedPan.isEmpty() || ProfileInputRules.isPanValid(normalizedPan)) null
        else stringResource(Res.string.profile_kyc_invalid_pan)
    val nameError = if (normalizedName.isEmpty() || ProfileInputRules.isLegalNameValid(normalizedName)) null
        else stringResource(Res.string.profile_kyc_invalid_name)

    ProfileDetailScaffold(
        title = stringResource(Res.string.profile_kyc_title),
        onBackClick = onBackClick,
        bottomButtonLabel = if (isVerified) {
            stringResource(Res.string.profile_kyc_verified_button)
        } else {
            stringResource(Res.string.profile_kyc_verify)
        },
        bottomButtonEnabled = !isVerified && panError == null && nameError == null &&
            normalizedPan.isNotBlank() && normalizedName.isNotBlank(),
        bottomButtonLoading = isSubmitting,
        onBottomButtonClick = {
            if (isVerified || panError != null || nameError != null) return@ProfileDetailScaffold
            focusManager.clearFocus(force = true)
            coroutineScope.launch {
                isSubmitting = true
                when (val result = verifyProfileKycUseCase(normalizedPan, normalizedName)) {
                    is ApiResult.Success -> {
                        isSubmitting = false
                        onBackClick()
                    }
                    is ApiResult.Failure -> {
                        isSubmitting = false
                        blockingError = result.error.message
                    }
                }
            }
        },
        modifier = modifier,
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        ProfileFieldBlock(label = stringResource(Res.string.profile_kyc_pan_label)) {
            ProfileEditorTextField(
                value = pan,
                onValueChange = { if (!isVerified) pan = ProfileInputRules.normalizePan(it) },
                placeholder = stringResource(Res.string.profile_kyc_pan_placeholder),
                readOnly = isVerified,
                keyboardType = KeyboardType.Ascii,
                imeAction = ImeAction.Next,
                errorText = if (!isVerified) panError else null,
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus(force = true) }),
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        ProfileFieldBlock(label = stringResource(Res.string.profile_kyc_name_label)) {
            ProfileEditorTextField(
                value = fullName,
                onValueChange = { if (!isVerified) fullName = ProfileInputRules.normalizeLegalName(it) },
                placeholder = stringResource(Res.string.profile_kyc_name_placeholder),
                readOnly = isVerified,
                keyboardCapitalization = KeyboardCapitalization.Words,
                errorText = if (!isVerified) nameError else null,
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus(force = true) }),
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
    }

    blockingError?.let { message ->
        AlertDialog(
            onDismissRequest = {},
            title = { Text(stringResource(Res.string.profile_kyc_error_title)) },
            text = { Text(message) },
            confirmButton = {
                TextButton(onClick = { blockingError = null }) {
                    Text(stringResource(Res.string.profile_kyc_verify))
                }
            },
        )
    }
}

@Composable
internal fun ProfileNomineeRoute(
    seedSummary: ProfileSummary?,
    updateProfileUseCase: UpdateProfileUseCase,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()
    val user = seedSummary?.user
    val nominee = user?.nominee
    var nomineeName by rememberSaveable { mutableStateOf(nominee?.name.orEmpty()) }
    var relation by rememberSaveable { mutableStateOf(nominee?.relation.orEmpty()) }
    var mobileNumber by rememberSaveable { mutableStateOf(nominee?.mobileNumber.orEmpty()) }
    var isSaving by rememberSaveable { mutableStateOf(false) }
    var errorMessage by rememberSaveable { mutableStateOf<String?>(null) }
    var showRelationSheet by rememberSaveable { mutableStateOf(false) }

    val normalizedName = nomineeName.trim()
    val normalizedRelation = relation.trim()
    val normalizedMobile = mobileNumber.trim()
    val hasChanges = normalizedName != nominee?.name.orEmpty().trim() ||
        normalizedRelation != nominee?.relation.orEmpty().trim() ||
        normalizedMobile != nominee?.mobileNumber.orEmpty().trim()
    val nameError = if (ProfileInputRules.isNomineeNameValid(normalizedName)) null
        else stringResource(Res.string.profile_nominee_invalid_name)
    val relationError = if (normalizedRelation.isBlank()) stringResource(Res.string.profile_nominee_invalid_relation) else null
    val mobileError = if (ProfileInputRules.isNomineePhoneValid(normalizedMobile)) null
        else stringResource(Res.string.profile_nominee_invalid_mobile)

    ProfileDetailScaffold(
        title = stringResource(Res.string.profile_nominee_title),
        onBackClick = onBackClick,
        bottomButtonLabel = stringResource(Res.string.profile_nominee_save),
        bottomButtonEnabled = hasChanges && nameError == null && relationError == null && mobileError == null,
        bottomButtonLoading = isSaving,
        onBottomButtonClick = {
            if (!hasChanges || nameError != null || relationError != null || mobileError != null) return@ProfileDetailScaffold
            focusManager.clearFocus(force = true)
            coroutineScope.launch {
                isSaving = true
                when (
                    val result = updateProfileUseCase(
                        name = user?.name.orEmpty(),
                        email = user?.email.orEmpty(),
                        dateOfBirth = user?.dateOfBirth?.takeIf { it.isNotBlank() },
                        gender = user?.gender?.takeIf { it.isNotBlank() },
                        nominee = ProfileNominee(
                            name = normalizedName,
                            relation = normalizedRelation,
                            dateOfBirth = nominee?.dateOfBirth.orEmpty(),
                            mobileNumber = normalizedMobile,
                        ),
                    )
                ) {
                    is ApiResult.Success -> {
                        isSaving = false
                        onBackClick()
                    }
                    is ApiResult.Failure -> {
                        isSaving = false
                        errorMessage = result.error.message
                    }
                }
            }
        },
        modifier = modifier,
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        ProfileFieldBlock(label = stringResource(Res.string.profile_nominee_name_label)) {
            ProfileEditorTextField(
                value = nomineeName,
                onValueChange = { nomineeName = ProfileInputRules.normalizeNomineeName(it) },
                placeholder = stringResource(Res.string.profile_nominee_name_placeholder),
                errorText = nameError,
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus(force = true) }),
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        ProfileFieldBlock(label = stringResource(Res.string.profile_nominee_relation_label)) {
            ProfileSelectionField(
                value = relation,
                placeholder = stringResource(Res.string.profile_nominee_relation_placeholder),
                onClick = {
                    focusManager.clearFocus(force = true)
                    showRelationSheet = true
                },
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        ProfileFieldBlock(label = stringResource(Res.string.profile_nominee_mobile_label)) {
            ProfileEditorTextField(
                value = mobileNumber,
                onValueChange = { mobileNumber = ProfileInputRules.normalizePhone(it) },
                placeholder = stringResource(Res.string.profile_nominee_mobile_placeholder),
                keyboardType = KeyboardType.Phone,
                imeAction = ImeAction.Done,
                errorText = mobileError,
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus(force = true) }),
                leadingIcon = {
                    Text(
                        text = "+91",
                        fontSize = 14.sp,
                        color = ProfileMutedText,
                        modifier = Modifier.padding(start = 4.dp),
                    )
                },
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
    }

    if (showRelationSheet) {
        val options = listOf(
            stringResource(Res.string.profile_nominee_relation_father),
            stringResource(Res.string.profile_nominee_relation_mother),
            stringResource(Res.string.profile_nominee_relation_husband),
            stringResource(Res.string.profile_nominee_relation_wife),
            stringResource(Res.string.profile_nominee_relation_son),
            stringResource(Res.string.profile_nominee_relation_daughter),
            stringResource(Res.string.profile_nominee_relation_brother),
            stringResource(Res.string.profile_nominee_relation_sister),
            stringResource(Res.string.profile_nominee_relation_other),
        )
        ProfileSelectionSheet(
            title = stringResource(Res.string.profile_nominee_relation_label),
            options = options,
            selectedValue = relation,
            onDismiss = { showRelationSheet = false },
            onSelect = { relation = it },
        )
    }

    errorMessage?.let { message ->
        ProfileErrorDialog(
            title = stringResource(Res.string.profile_nominee_save),
            message = message,
            onDismiss = { errorMessage = null },
        )
    }
}
