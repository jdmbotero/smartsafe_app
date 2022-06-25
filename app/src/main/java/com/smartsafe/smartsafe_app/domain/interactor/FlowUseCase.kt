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
    val resultFlow: Flow<Response> = _trigger.flatMapLatest { trigger -> performAction(trigger) }

    /**
     * Triggers the execution of this use case
     */
    suspend fun launch(request: Request? = null) {
        _trigger.emit(request)
    }

    protected abstract suspend fun performAction(request: Request? = null): Flow<Response>
}