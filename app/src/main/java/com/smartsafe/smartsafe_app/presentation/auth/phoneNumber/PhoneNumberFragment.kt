package com.smartsafe.smartsafe_app.presentation.auth.phoneNumber

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.smartsafe.smartsafe_app.R
import com.smartsafe.smartsafe_app.databinding.FragmentPhoneNumberBinding
import com.smartsafe.smartsafe_app.presentation.auth.AuthActivity
import com.smartsafe.smartsafe_app.presentation.auth.verifyCode.VerifyCodeFragment.Companion.VERIFICATION_ID_PARAM
import com.smartsafe.smartsafe_app.presentation.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PhoneNumberFragment : Fragment() {

    private lateinit var binding: FragmentPhoneNumberBinding
    private val phoneNumberViewModel: PhoneNumberViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPhoneNumberBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
        setUpView()
    }

    private fun setUpView() {
        binding.phoneNumberButtonSend.isEnabled = false

        binding.phoneNumberText.editText?.doOnTextChanged { inputText, _, _, _ ->
            binding.phoneNumberButtonSend.isEnabled = (inputText?.length ?: 0) > 0
        }

        binding.phoneNumberButtonSend.setOnClickListener {
            verifyPhoneNumber(
                binding.phoneNumberText.prefixText.toString(),
                binding.phoneNumberText.editText?.text.toString()
            )
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            phoneNumberViewModel.state.collect { state ->
                when (state) {
                    is PhoneNumberState.Loading -> showLoading()
                    is PhoneNumberState.Success -> goToVerifyCode(state.verificationId)
                    is PhoneNumberState.SuccessVerification -> gotToMain()
                    is PhoneNumberState.Error -> showError(state.message)
                    is PhoneNumberState.Idle -> {}
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

    private fun verifyPhoneNumber(prefix: String, phoneNumber: String) {
        lifecycleScope.launch {
            phoneNumberViewModel.userIntent.send(
                PhoneNumberIntent.VerifyPhoneNumber(
                    prefix,
                    phoneNumber
                )
            )
        }
    }

    private fun goToVerifyCode(verificationId: String) {
        binding.loading.hide()
        findNavController().navigate(
            R.id.action_phoneNumberFragment_to_verifyCodeFragment,
            bundleOf(VERIFICATION_ID_PARAM to verificationId)
        )
    }

    private fun gotToMain() {
        startActivity(Intent(activity, MainActivity::class.java))
        activity?.finish()
    }
}