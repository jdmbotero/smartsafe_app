package com.smartsafe.smartsafe_app.presentation.main.boxNew

import com.smartsafe.smartsafe_app.domain.entity.Box

sealed class BoxNewIntent {
    data class AddOrUpdateBox(val box: Box) : BoxNewIntent()
}