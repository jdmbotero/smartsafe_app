package com.smartsafe.smartsafe_app.domain.entity

import java.io.Serializable

data class Box(
    var id: String? = null,
    var name: String? = null,
    var userId: String? = null
) : Serializable
