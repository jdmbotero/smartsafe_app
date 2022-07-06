package com.smartsafe.smartsafe_app.presentation.main.boxNew

import com.smartsafe.smartsafe_app.domain.entity.Box

sealed class BoxNewState {
    object Idle : BoxNewState()
    object Loading : BoxNewState()
    data class Success(val box: Box) : BoxNewState()
    data class Error(val message: String?) : BoxNewState()
}