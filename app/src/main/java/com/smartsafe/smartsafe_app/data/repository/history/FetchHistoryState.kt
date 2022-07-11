package com.smartsafe.smartsafe_app.data.repository.history

import com.smartsafe.smartsafe_app.domain.entity.History

sealed class FetchHistoryState {
    data class Success(val history: List<History>? = null) : FetchHistoryState()
    data class Failure(val message: String? = null, val code: String? = null) :
        FetchHistoryState()
}
