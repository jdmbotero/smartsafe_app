package com.smartsafe.smartsafe_app.domain.interactor

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest

@OptIn(ExperimentalCoroutinesApi::class)
abstract class FlowUseCase<Response, Request> {

    /**
     * Trigger for the action which can be done in this request
     */
    private val _trigger = MutableStateFlow<Request?>(null)

    /**
     * Exposes result of this use case
     */
    val resultFlow: Flow<Response> = _trigger.flatMapLatest { trigger ->
        trigger?.let { performAction(trigger) } ?: performAction()
    }

    /**
     * Triggers the execution of this use case
     */
    suspend fun launch(request: Request) {
        _trigger.emit(request)
    }

    suspend fun launch() {
        _trigger.emit(null)
    }

    open suspend fun performAction(request: Request): Flow<Response> {
        TODO("This method is not implemented")
    }

    open suspend fun performAction(): Flow<Response> {
        TODO("This method is not implemented")
    }
}