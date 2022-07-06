package com.smartsafe.smartsafe_app.domain.interactor.box

import com.smartsafe.smartsafe_app.data.repository.box.AddOrUpdateBoxState
import com.smartsafe.smartsafe_app.data.repository.box.BoxRepository
import com.smartsafe.smartsafe_app.domain.entity.Box
import com.smartsafe.smartsafe_app.domain.interactor.FlowUseCase
import javax.inject.Inject

class AddOrUpdateBoxUseCase @Inject constructor(
    private val repository: BoxRepository
) : FlowUseCase<AddOrUpdateBoxState, Box>() {
    override suspend fun performAction(request: Box) = repository.addOrUpdateBox(request)
}