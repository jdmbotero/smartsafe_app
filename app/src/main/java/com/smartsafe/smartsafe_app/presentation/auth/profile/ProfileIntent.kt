package com.smartsafe.smartsafe_app.presentation.auth.profile

import com.smartsafe.smartsafe_app.domain.entity.User

sealed class ProfileIntent {
    object FetchUser : ProfileIntent()
    data class UpdateUser(val user: User) : ProfileIntent()
    object Logout : ProfileIntent()
}