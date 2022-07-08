package com.smartsafe.smartsafe_app.domain.interactor.user

import com.smartsafe.smartsafe_app.data.repository.user.AddOrUpdateUserState
import com.smartsafe.smartsafe_app.data.repository.user.UserRepository
import com.smartsafe.smartsafe_app.domain.entity.User
import com.smartsafe.smartsafe_app.domain.interactor.FlowUseCase
import javax.inject.Inject

class AddOrUpdateUserUseCase @Inject constructor(
    private val repository: UserRepository
) : FlowUseCase<AddOrUpdateUserState, Pair<String, User?>>() {
    override suspend fun performAction(request: Pair<String, User?>) =
        repository.addOrUpdateUser(request.first, request.second)
}