package com.smartsafe.smartsafe_app.domain.interactor.auth

import com.smartsafe.smartsafe_app.data.repository.AuthWithPhoneRepository
import com.smartsafe.smartsafe_app.data.repository.VerifyPhoneNumberState
import com.smartsafe.smartsafe_app.domain.interactor.FlowUseCase
import javax.inject.Inject

class VerifyPhoneNumberUseCase @Inject constructor(
    private val repository: AuthWithPhoneRepository
) : FlowUseCase<VerifyPhoneNumberState, Pair<String, String>>() {
    override suspend fun performAction(request: Pair<String, String>) =
        repository.verifyPhoneNumber(request.first, request.second)
}