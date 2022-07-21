package com.smartsafe.smartsafe_app.presentation.main.boxDetail

import com.smartsafe.smartsafe_app.domain.entity.Box
import com.smartsafe.smartsafe_app.domain.entity.DoorAction

sealed class BoxDetailFetchIntent {
    data class FetchBox(val box: Box) : BoxDetailFetchIntent()
}