package com.smartsafe.smartsafe_app.presentation.main.boxDetail

import com.smartsafe.smartsafe_app.domain.entity.Box

sealed class BoxDetailFetchState {
    object Idle : BoxDetailFetchState()
    object Loading : BoxDetailFetchState()
    data class SuccessFetch(val box: Box?) : BoxDetailFetchState()
    data class Error(val message: String?) : BoxDetailFetchState()
}