package com.smartsafe.smartsafe_app.presentation.main.boxDetail

import com.smartsafe.smartsafe_app.domain.entity.Box

sealed class BoxDetailIntent {
    data class FetchBox(val box: Box) : BoxDetailIntent()
}