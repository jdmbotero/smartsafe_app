package com.smartsafe.smartsafe_app.presentation.auth.profile

import com.smartsafe.smartsafe_app.domain.entity.User

sealed class ProfileState {
    object Idle : ProfileState()
    object Loading : ProfileState()
    object LogoutSuccess : ProfileState()
    data class FetchUserSuccess(val user: User?) : ProfileState()
    object UpdateUserSuccess : ProfileState()
    data class Error(val message: String?) : ProfileState()
}