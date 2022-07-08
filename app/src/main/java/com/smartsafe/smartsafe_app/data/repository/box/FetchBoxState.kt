package com.smartsafe.smartsafe_app.data.repository.box

import com.smartsafe.smartsafe_app.domain.entity.Box

sealed class FetchBoxState {
    data class Success(val box: Box? = null) : FetchBoxState()
    data class Failure(val message: String? = null, val code: String? = null) :
        FetchBoxState()
}
