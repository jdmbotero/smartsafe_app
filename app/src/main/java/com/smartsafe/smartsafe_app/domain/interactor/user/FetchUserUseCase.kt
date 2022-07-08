package com.smartsafe.smartsafe_app.domain.interactor.user

import com.smartsafe.smartsafe_app.data.repository.user.FetchUserState
import com.smartsafe.smartsafe_app.data.repository.user.UserRepository
import com.smartsafe.smartsafe_app.domain.interactor.FlowUseCase
import javax.inject.Inject

class FetchUserUseCase @Inject constructor(
    private val repository: UserRepository
) : FlowUseCase<FetchUserState, String>() {
    override suspend fun performAction(request: String) = repository.fetchUser(request)
}