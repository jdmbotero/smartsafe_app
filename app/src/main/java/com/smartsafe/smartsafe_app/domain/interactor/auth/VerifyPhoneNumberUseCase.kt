package com.smartsafe.smartsafe_app.domain.interactor.auth

import com.smartsafe.smartsafe_app.data.repository.AuthWithPhoneRepository
import com.smartsafe.smartsafe_app.data.repository.VerifyPhoneNumberState
import com.smartsafe.smartsafe_app.domain.interactor.FlowUseCase
import javax.inject.Inject

class VerifyPhoneNumberUseCase @Inject constructor(
    private val repository: AuthWithPhoneRepository
) : FlowUseCase<VerifyPhoneNumberState, String>() {
    override suspend fun performAction(request: String) = repository.verifyPhoneNumber(request)
}