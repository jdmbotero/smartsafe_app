package com.smartsafe.smartsafe_app.domain.interactor

sealed class GenericResponseState {
    data class Success<out Response>(val result: Response? = null) : GenericResponseState()
    data class Failure(val message: String? = null, val code: String? = null) :
        GenericResponseState()
}
