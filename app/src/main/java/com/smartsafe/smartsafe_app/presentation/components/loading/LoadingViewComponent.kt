package com.smartsafe.smartsafe_app.presentation.components.loading

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.smartsafe.smartsafe_app.R
import com.smartsafe.smartsafe_app.databinding.ViewLoadingBinding

class LoadingViewComponent : ConstraintLayout {

    private lateinit var binding: ViewLoadingBinding

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context)
    }

    private fun init(context: Context) {
        if (!isInEditMode) {
            binding = ViewLoadingBinding.inflate(LayoutInflater.from(context), this, true)
        } else {
            LayoutInflater.from(context).inflate(R.layout.view_loading, this, true)
        }
    }

    fun show() {
        this.visibility = View.VISIBLE
    }

    fun hide() {
        this.visibility = View.GONE
    }
}