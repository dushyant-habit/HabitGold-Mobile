package com.habit.gold.feature.profile.presentation

sealed interface ProfileDestination {
    data object Hub : ProfileDestination
    data class PersonalInfo(val seedSummary: com.habit.gold.feature.profile.domain.model.ProfileSummary?) : ProfileDestination
    data class Kyc(val seedSummary: com.habit.gold.feature.profile.domain.model.ProfileSummary?) : ProfileDestination
    data class Nominee(val seedSummary: com.habit.gold.feature.profile.domain.model.ProfileSummary?) : ProfileDestination
    data class HelpCenter(val returnDestination: ProfileDestination? = null) : ProfileDestination
    data object VpaList : ProfileDestination
    data class ContactUs(
        val seedSummary: com.habit.gold.feature.profile.domain.model.ProfileSummary? = null,
        val returnDestination: ProfileDestination? = null,
    ) : ProfileDestination
    data object TrackOrder : ProfileDestination
    data object SavedAddresses : ProfileDestination
}
