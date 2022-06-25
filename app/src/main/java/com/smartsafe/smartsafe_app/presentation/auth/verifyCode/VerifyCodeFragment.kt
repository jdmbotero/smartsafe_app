package com.smartsafe.smartsafe_app.presentation.auth.verifyCode

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.smartsafe.smartsafe_app.databinding.FragmentVerifyCodeBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class VerifyCodeFragment : Fragment() {

    lateinit var binding: FragmentVerifyCodeBinding
    private val verifyCodeViewModel: VerifyCodeViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        observeViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentVerifyCodeBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            verifyCodeViewModel.state.collect { state ->
                when (state) {
                    is VerifyCodeState.Loading -> showLoading()
                    is VerifyCodeState.Success -> TODO()
                    is VerifyCodeState.Error -> showError(state.message)
                    is VerifyCodeState.Idle -> TODO()
                }
            }
        }
    }

    private fun showLoading() {
        // TODO
    }

    private fun showError(message: String?) {
        // TODO
    }

    private fun verifyPhoneNumber() {
        lifecycleScope.launch {
            verifyCodeViewModel.userIntent.send(VerifyCodeIntent.VerifyCodeNumber("", ""))
        }
    }
}