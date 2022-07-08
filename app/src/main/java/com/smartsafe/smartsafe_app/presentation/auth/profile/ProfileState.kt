package com.smartsafe.smartsafe_app.presentation.auth.profile

sealed class ProfileState {
    object Idle : ProfileState()
    object Loading : ProfileState()
    object LogoutSuccess : ProfileState()
    object UpdateUserSuccess : ProfileState()
    data class Error(val message: String?) : ProfileState()
}