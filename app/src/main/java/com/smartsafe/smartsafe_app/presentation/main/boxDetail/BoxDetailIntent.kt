package com.smartsafe.smartsafe_app.presentation.main.boxDetail

import com.smartsafe.smartsafe_app.domain.entity.Box
import com.smartsafe.smartsafe_app.domain.entity.DoorAction

sealed class BoxDetailIntent {
    data class FetchBox(val box: Box) : BoxDetailIntent()
    data class OpenOrCloseBox(val box: Box, val action: DoorAction) : BoxDetailIntent()
}