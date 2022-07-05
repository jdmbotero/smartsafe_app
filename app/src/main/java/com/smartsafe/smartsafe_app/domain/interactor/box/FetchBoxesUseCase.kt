package com.smartsafe.smartsafe_app.domain.interactor.box

import com.smartsafe.smartsafe_app.data.repository.box.BoxRepository
import com.smartsafe.smartsafe_app.data.repository.box.FetchBoxesState
import com.smartsafe.smartsafe_app.domain.interactor.FlowUseCase
import javax.inject.Inject

class FetchBoxesUseCase @Inject constructor(
    private val repository: BoxRepository
) : FlowUseCase<FetchBoxesState, String>() {
    override suspend fun performAction(request: String) = repository.fetchBoxes(request)
}