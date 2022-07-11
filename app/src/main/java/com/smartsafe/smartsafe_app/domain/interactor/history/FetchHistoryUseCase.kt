package com.smartsafe.smartsafe_app.domain.interactor.history

import com.smartsafe.smartsafe_app.data.repository.history.FetchHistoryState
import com.smartsafe.smartsafe_app.data.repository.history.HistoryRepository
import com.smartsafe.smartsafe_app.domain.interactor.FlowUseCase
import javax.inject.Inject

class FetchHistoryUseCase @Inject constructor(
    private val repository: HistoryRepository
) : FlowUseCase<FetchHistoryState, String>() {
    override suspend fun performAction(request: String) = repository.fetchHistory(request)
}