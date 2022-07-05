package com.smartsafe.smartsafe_app.presentation.main.boxList

sealed class BoxListIntent {
    object FetchBoxes : BoxListIntent()
}