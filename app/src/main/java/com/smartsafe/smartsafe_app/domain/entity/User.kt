package com.smartsafe.smartsafe_app.domain.entity

import java.io.Serializable

data class User(
    var id: String? = null,
    var name: String? = null
) : Serializable
