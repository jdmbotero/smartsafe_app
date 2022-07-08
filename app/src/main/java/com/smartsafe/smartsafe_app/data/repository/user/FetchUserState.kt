package com.smartsafe.smartsafe_app.data.repository.user

import com.smartsafe.smartsafe_app.domain.entity.User

sealed class FetchUserState {
    data class Success(val user: User? = null) : FetchUserState()
    data class Failure(val message: String? = null, val code: String? = null) :
        FetchUserState()
}
