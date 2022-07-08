package com.smartsafe.smartsafe_app.domain.interactor.auth

import com.smartsafe.smartsafe_app.data.repository.authWithPhone.AuthWithPhoneRepository
import com.smartsafe.smartsafe_app.domain.interactor.FlowUseCase
import com.smartsafe.smartsafe_app.presentation.auth.profile.ProfileState
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val repository: AuthWithPhoneRepository
) : FlowUseCase<ProfileState, Any>() {
    override suspend fun performAction() = flow {
        repository.logout()
        emit(ProfileState.LogoutSuccess)
    }
}