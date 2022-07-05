package com.smartsafe.smartsafe_app.domain.interactor.user

import com.smartsafe.smartsafe_app.data.repository.user.UserRepository
import com.smartsafe.smartsafe_app.domain.entity.User
import com.smartsafe.smartsafe_app.domain.interactor.FlowUseCase
import com.smartsafe.smartsafe_app.domain.interactor.GenericResponseState
import javax.inject.Inject

class AddOrUpdateUserUseCase @Inject constructor(
    private val repository: UserRepository
) : FlowUseCase<GenericResponseState, String>() {
    override suspend fun performAction(request: String) =
        repository.addOrUpdateUser(request, null)
}