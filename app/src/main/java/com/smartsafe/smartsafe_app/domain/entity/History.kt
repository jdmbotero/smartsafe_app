package com.smartsafe.smartsafe_app.domain.entity

import java.util.*

data class History(
    val boxId: String? = null,
    val date: Date? = null,
    var doorAction: DoorAction? = null
)