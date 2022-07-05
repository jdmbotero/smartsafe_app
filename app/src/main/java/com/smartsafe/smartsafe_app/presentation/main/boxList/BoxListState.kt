package com.smartsafe.smartsafe_app.presentation.main.boxList

import com.smartsafe.smartsafe_app.domain.entity.Box

sealed class BoxListState {
    object Idle : BoxListState()
    object Loading : BoxListState()
    data class Success(val boxes: List<Box>) : BoxListState()
    data class Error(val message: String?) : BoxListState()
}