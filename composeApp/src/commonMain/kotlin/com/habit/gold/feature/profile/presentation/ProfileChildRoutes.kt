@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.habit.gold.feature.profile.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.habit.gold.core.storage.SecureStorage
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
import habitgoldmobile.composeapp.generated.resources.profile_security_biometric_body
import habitgoldmobile.composeapp.generated.resources.profile_security_biometric_note
import habitgoldmobile.composeapp.generated.resources.profile_security_biometric_title
import habitgoldmobile.composeapp.generated.resources.profile_security_continue
import habitgoldmobile.composeapp.generated.resources.profile_security_disable
import habitgoldmobile.composeapp.generated.resources.profile_security_disable_body
import habitgoldmobile.composeapp.generated.resources.profile_security_pin_requirement
import habitgoldmobile.composeapp.generated.resources.profile_security_reset
import habitgoldmobile.composeapp.generated.resources.profile_security_save
import habitgoldmobile.composeapp.generated.resources.profile_security_setup_body
import habitgoldmobile.composeapp.generated.resources.profile_security_setup_title
import habitgoldmobile.composeapp.generated.resources.profile_security_skip
import habitgoldmobile.composeapp.generated.resources.profile_security_title
import habitgoldmobile.composeapp.generated.resources.profile_security_verify_body
import habitgoldmobile.composeapp.generated.resources.profile_security_verify_title
import habitgoldmobile.composeapp.generated.resources.profile_security_wrong_pin
import habitgoldmobile.composeapp.generated.resources.common_cancel
import org.jetbrains.compose.resources.stringResource
import kotlinx.coroutines.delay
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

@Composable
internal fun ProfileSecurityRoute(
    secureStorage: SecureStorage,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val coroutineScope = rememberCoroutineScope()
    val securityStore = remember(secureStorage) { ProfileSecurityStore(secureStorage) }
    val settings by produceState<ProfileSecuritySettings?>(initialValue = null, securityStore) {
        value = securityStore.read()
    }
    var biometricEnabled by rememberSaveable { mutableStateOf(false) }
    var isSaving by rememberSaveable { mutableStateOf(false) }
    var showDisableConfirm by rememberSaveable { mutableStateOf(false) }
    val disableLabel = stringResource(Res.string.profile_security_disable)
    val disableBody = stringResource(Res.string.profile_security_disable_body)

    LaunchedEffect(settings) {
        val current = settings ?: return@LaunchedEffect
        biometricEnabled = current.biometricEnabled
    }

    if (settings == null) {
        ProfileSecurityLoadingScreen(onBackClick = onBackClick, modifier = modifier)
        return
    }

    SecurityScaffold(
        title = stringResource(Res.string.profile_security_title),
        onBackClick = onBackClick,
        showSkip = false,
        modifier = modifier,
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = stringResource(Res.string.profile_security_setup_title),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = ProfilePrimaryText,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(Res.string.profile_security_setup_body),
            fontSize = 14.sp,
            color = ProfileMutedText,
            lineHeight = 22.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(28.dp))
        SecuritySettingRow(
            title = stringResource(Res.string.profile_security_biometric_title),
            body = stringResource(Res.string.profile_security_biometric_body),
            checked = biometricEnabled,
            onCheckedChange = { biometricEnabled = it },
        )
        Spacer(modifier = Modifier.height(18.dp))
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            color = Color(0xFFF8FAFC),
            border = androidx.compose.foundation.BorderStroke(1.dp, ProfileCardBorder),
        ) {
            Text(
                text = stringResource(Res.string.profile_security_biometric_note),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                fontSize = 13.sp,
                lineHeight = 19.sp,
                color = ProfileMutedText,
            )
        }
        Spacer(modifier = Modifier.height(28.dp))
        ButtonLikeFooter(
            label = stringResource(Res.string.profile_security_save),
            loading = isSaving,
            onClick = {
                coroutineScope.launch {
                    isSaving = true
                    securityStore.save(biometricEnabled)
                    isSaving = false
                    onBackClick()
                }
            },
        )
        if (settings?.biometricEnabled == true) {
            Spacer(modifier = Modifier.height(12.dp))
            TextButton(
                onClick = { showDisableConfirm = true },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = disableLabel,
                    color = ProfileMutedText,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
    }

    if (showDisableConfirm) {
        AlertDialog(
            onDismissRequest = { showDisableConfirm = false },
            title = { Text(disableLabel) },
            text = { Text(disableBody) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDisableConfirm = false
                        coroutineScope.launch {
                            securityStore.clear()
                            onBackClick()
                        }
                    },
                ) {
                    Text(disableLabel)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDisableConfirm = false }) {
                    Text(stringResource(Res.string.common_cancel))
                }
            },
        )
    }
}

@Composable
private fun ProfileSecurityLoadingScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(Res.string.profile_security_title),
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
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator(color = HabitGoldPalette.plum, strokeWidth = 2.dp)
        }
    }
}

@Composable
private fun SecurityVerifyScreen(
    biometricEnabled: Boolean,
    pin: String,
    errorText: String?,
    onDigit: (String) -> Unit,
    onBackspace: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SecurityScaffold(
        title = stringResource(Res.string.profile_security_title),
        onBackClick = onBackClick,
        showSkip = false,
        modifier = modifier,
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = stringResource(Res.string.profile_security_verify_title),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = ProfilePrimaryText,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(Res.string.profile_security_verify_body),
            fontSize = 14.sp,
            color = ProfileMutedText,
            lineHeight = 22.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
        if (biometricEnabled) {
            Spacer(modifier = Modifier.height(24.dp))
            Surface(
                shape = RoundedCornerShape(999.dp),
                color = Color(0xFFF6F1FB),
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.Fingerprint,
                        contentDescription = null,
                        tint = HabitGoldPalette.plum,
                    )
                    Text(
                        text = stringResource(Res.string.profile_security_biometric_title),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = HabitGoldPalette.plum,
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
        SecurityPinDots(pin = pin)
        if (errorText != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = errorText,
                color = ProfileError,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        Spacer(modifier = Modifier.height(32.dp))
        SecurityKeypad(
            onDigit = onDigit,
            onBackspace = onBackspace,
        )
    }
}

@Composable
private fun SecuritySetupScreen(
    existingPin: String?,
    biometricEnabled: Boolean,
    pin: String,
    errorText: String?,
    isSaving: Boolean,
    onToggleBiometric: (Boolean) -> Unit,
    onDigit: (String) -> Unit,
    onBackspace: () -> Unit,
    onSkip: () -> Unit,
    onDisable: () -> Unit,
    onSave: () -> Unit,
    onReset: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val requiresPinEntry = existingPin == null || pin.isNotBlank()
    SecurityScaffold(
        title = stringResource(Res.string.profile_security_title),
        onBackClick = onBackClick,
        showSkip = existingPin == null,
        onSkip = onSkip,
        modifier = modifier,
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = stringResource(Res.string.profile_security_setup_title),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = ProfilePrimaryText,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(Res.string.profile_security_setup_body),
            fontSize = 14.sp,
            color = ProfileMutedText,
            lineHeight = 22.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(28.dp))
        SecuritySettingRow(
            title = stringResource(Res.string.profile_security_biometric_title),
            body = stringResource(Res.string.profile_security_biometric_body),
            checked = biometricEnabled,
            onCheckedChange = onToggleBiometric,
        )
        Spacer(modifier = Modifier.height(24.dp))
        if (existingPin != null && pin.isBlank()) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = Color.White,
                border = androidx.compose.foundation.BorderStroke(1.dp, ProfileCardBorder),
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = stringResource(Res.string.profile_security_verify_title),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ProfilePrimaryText,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(Res.string.profile_security_biometric_body),
                        fontSize = 13.sp,
                        color = ProfileMutedText,
                        lineHeight = 20.sp,
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        TextButton(onClick = onReset) {
                            Text(stringResource(Res.string.profile_security_reset))
                        }
                        TextButton(onClick = onDisable) {
                            Text(stringResource(Res.string.profile_security_disable), color = ProfileError)
                        }
                    }
                }
            }
        } else {
            SecurityPinDots(pin = pin)
            if (errorText != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = errorText,
                    color = ProfileError,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            SecurityKeypad(
                onDigit = onDigit,
                onBackspace = onBackspace,
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        ButtonLikeFooter(
            label = if (existingPin == null || requiresPinEntry) {
                stringResource(Res.string.profile_security_save)
            } else {
                stringResource(Res.string.profile_security_continue)
            },
            loading = isSaving,
            onClick = onSave,
        )
    }
}

@Composable
private fun SecurityScaffold(
    title: String,
    onBackClick: () -> Unit,
    showSkip: Boolean,
    modifier: Modifier = Modifier,
    onSkip: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = title,
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
                    if (showSkip && onSkip != null) {
                        TextButton(onClick = onSkip) {
                            Text(
                                text = stringResource(Res.string.profile_security_skip),
                                color = HabitGoldPalette.plum,
                                fontWeight = FontWeight.SemiBold,
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
                .imePadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            content = content,
        )
    }
}

@Composable
private fun SecuritySettingRow(
    title: String,
    body: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, ProfileCardBorder),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(ProfileNeutralIconBackground),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.Fingerprint,
                    contentDescription = null,
                    tint = HabitGoldPalette.plum,
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ProfilePrimaryText,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = body,
                    fontSize = 13.sp,
                    lineHeight = 19.sp,
                    color = ProfileMutedText,
                )
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
            )
        }
    }
}

@Composable
private fun ButtonLikeFooter(
    label: String,
    loading: Boolean,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !loading, onClick = onClick),
        shape = RoundedCornerShape(999.dp),
        color = HabitGoldPalette.plum,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            contentAlignment = Alignment.Center,
        ) {
            if (loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color.White,
                    strokeWidth = 2.dp,
                )
            } else {
                Text(
                    text = label,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

@Composable
private fun SecurityPinDots(
    pin: String,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.wrapContentHeight(),
    ) {
        repeat(4) { index ->
            Box(
                modifier = Modifier
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(
                        if (index < pin.length) HabitGoldPalette.plum else Color(0xFFE2E8F0),
                    ),
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SecurityKeypad(
    onDigit: (String) -> Unit,
    onBackspace: () -> Unit,
) {
    FlowRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp),
        maxItemsInEachRow = 3,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        (1..9).forEach { digit ->
            SecurityKeypadButton(
                label = digit.toString(),
                onClick = { onDigit(digit.toString()) },
            )
        }
        Spacer(modifier = Modifier.size(76.dp))
        SecurityKeypadButton(
            label = "0",
            onClick = { onDigit("0") },
        )
        SecurityKeypadButton(
            label = "",
            icon = {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Backspace,
                    contentDescription = null,
                    tint = ProfilePrimaryText,
                )
            },
            onClick = onBackspace,
        )
    }
}

@Composable
private fun SecurityKeypadButton(
    label: String,
    onClick: () -> Unit,
    icon: @Composable (() -> Unit)? = null,
) {
    Surface(
        modifier = Modifier
            .size(76.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        color = Color(0xFFF8FAFC),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (icon != null) {
                icon()
            } else {
                Text(
                    text = label,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ProfilePrimaryText,
                )
            }
        }
    }
}

private enum class ProfileSecurityMode {
    Loading,
    Verify,
    Setup,
}
