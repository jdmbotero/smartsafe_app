package com.smartsafe.smartsafe_app.presentation.main.boxDetail

import com.smartsafe.smartsafe_app.domain.entity.Box

sealed class BoxDetailState {
    object Idle : BoxDetailState()
    object Loading : BoxDetailState()
    data class SuccessFetch(val box: Box?) : BoxDetailState()
    data class SuccessOpenOrClose(val box: Box?) : BoxDetailState()
    data class Error(val message: String?) : BoxDetailState()
}