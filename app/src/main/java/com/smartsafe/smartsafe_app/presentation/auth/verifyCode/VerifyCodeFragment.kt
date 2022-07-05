package com.smartsafe.smartsafe_app.presentation.auth.verifyCode

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.smartsafe.smartsafe_app.databinding.FragmentVerifyCodeBinding
import com.smartsafe.smartsafe_app.presentation.auth.AuthActivity
import com.wynsbin.vciv.VerificationCodeInputView.OnInputListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class VerifyCodeFragment : Fragment() {

    private lateinit var binding: FragmentVerifyCodeBinding
    private val verifyCodeViewModel: VerifyCodeViewModel by activityViewModels()
    lateinit var verificationId: String

    companion object {
        const val VERIFICATION_ID_PARAM = "verificationId"
    }

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            verificationId = it.getString(VERIFICATION_ID_PARAM, "")
        }

        setUpView()
    }

    private fun setUpView() {
        binding.verifyCodeText.setOnInputListener(object : OnInputListener {
            override fun onComplete(code: String) {
                verifyPhoneNumber(code)
            }

            override fun onInput() {}
        })
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            verifyCodeViewModel.state.collect { state ->
                when (state) {
                    is VerifyCodeState.Loading -> showLoading()
                    is VerifyCodeState.Success -> gotToMain()
                    is VerifyCodeState.Error -> showError(state.message)
                    is VerifyCodeState.Idle -> {}
                }
            }
        }
    }

    private fun showLoading() {
        binding.loading.show()
    }

    private fun showError(message: String?) {
        binding.loading.hide()
    }

    private fun verifyPhoneNumber(code: String) {
        lifecycleScope.launch {
            verifyCodeViewModel.userIntent.send(
                VerifyCodeIntent.VerifyCodeNumber(
                    verificationId,
                    code
                )
            )
        }
    }

    private fun gotToMain() {
        startActivity(Intent(activity, AuthActivity::class.java))
        activity?.finish()
    }
}