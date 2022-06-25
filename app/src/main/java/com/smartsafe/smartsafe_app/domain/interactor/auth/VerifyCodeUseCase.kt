package com.smartsafe.smartsafe_app.domain.interactor.auth

import com.smartsafe.smartsafe_app.data.repository.AuthWithPhoneRepository
import com.smartsafe.smartsafe_app.data.repository.SignInWithPhoneState
import com.smartsafe.smartsafe_app.domain.interactor.FlowUseCase
import javax.inject.Inject

class VerifyCodeUseCase @Inject constructor(
    private val repository: AuthWithPhoneRepository
) : FlowUseCase<SignInWithPhoneState, Pair<String, String>>() {
    override suspend fun performAction(request: Pair<String, String>) =
        repository.verifyCode(request.first, request.second)
}