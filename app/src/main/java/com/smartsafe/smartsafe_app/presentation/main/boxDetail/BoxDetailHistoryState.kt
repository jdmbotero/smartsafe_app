package com.smartsafe.smartsafe_app.presentation.main.boxDetail

import com.smartsafe.smartsafe_app.domain.entity.History

sealed class BoxDetailHistoryState {
    object Idle : BoxDetailHistoryState()
    object Loading : BoxDetailHistoryState()
    data class SuccessFetchHistory(val history: List<History>?) : BoxDetailHistoryState()
    data class Error(val message: String?) : BoxDetailHistoryState()
}