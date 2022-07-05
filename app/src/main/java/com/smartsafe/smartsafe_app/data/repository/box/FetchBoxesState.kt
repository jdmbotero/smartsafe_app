package com.smartsafe.smartsafe_app.data.repository.box

import com.smartsafe.smartsafe_app.domain.entity.Box

sealed class FetchBoxesState {
    data class Success(val boxes: List<Box>? = null) : FetchBoxesState()
    data class Failure(val message: String? = null, val code: String? = null) :
        FetchBoxesState()
}
