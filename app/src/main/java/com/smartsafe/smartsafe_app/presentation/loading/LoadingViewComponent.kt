package com.smartsafe.smartsafe_app.presentation.loading

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.smartsafe.smartsafe_app.databinding.ViewLoadingBinding

class LoadingViewComponent @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var binding: ViewLoadingBinding

    init {
        binding = ViewLoadingBinding.inflate(LayoutInflater.from(context))
        addView(binding.root)
    }

    fun show() {
        binding.root.visibility = View.VISIBLE
    }

    fun hide() {
        binding.root.visibility = View.GONE
    }
}