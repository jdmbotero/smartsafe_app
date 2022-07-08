package com.smartsafe.smartsafe_app.domain.interactor.box

import com.smartsafe.smartsafe_app.data.repository.box.BoxRepository
import com.smartsafe.smartsafe_app.data.repository.box.FetchBoxState
import com.smartsafe.smartsafe_app.domain.interactor.FlowUseCase
import javax.inject.Inject

class FetchBoxUseCase @Inject constructor(
    private val repository: BoxRepository
) : FlowUseCase<FetchBoxState, String>() {
    override suspend fun performAction(request: String) = repository.fetchBox(request)
}