package com.smartsafe.smartsafe_app.presentation.auth.profile

import com.google.firebase.firestore.auth.User

sealed class ProfileIntent {
    data class UpdateUser(val user: User) : ProfileIntent()
    object Logout : ProfileIntent()
}