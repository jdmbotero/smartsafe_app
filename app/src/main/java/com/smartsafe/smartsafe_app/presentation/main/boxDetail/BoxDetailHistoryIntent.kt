package com.smartsafe.smartsafe_app.presentation.main.boxDetail

sealed class BoxDetailHistoryIntent {
    data class FetchHistory(val boxId: String?) : BoxDetailHistoryIntent()
}