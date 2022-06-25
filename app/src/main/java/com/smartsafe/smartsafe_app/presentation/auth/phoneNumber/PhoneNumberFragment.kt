package com.smartsafe.smartsafe_app.presentation.auth.phoneNumber

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.smartsafe.smartsafe_app.R
import com.smartsafe.smartsafe_app.databinding.FragmentPhoneNumberBinding
import com.smartsafe.smartsafe_app.presentation.auth.verifyCode.VerifyCodeFragment.Companion.VERIFICATION_ID_PARAM
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PhoneNumberFragment : Fragment() {

    lateinit var binding: FragmentPhoneNumberBinding
    private val phoneNumberViewModel: PhoneNumberViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        observeViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPhoneNumberBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpView()
    }

    private fun setUpView() {
        binding.phoneNumberButtonSend.setOnClickListener {
            verifyPhoneNumber("")
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            phoneNumberViewModel.state.collect { state ->
                when (state) {
                    is PhoneNumberState.Loading -> showLoading()
                    is PhoneNumberState.Success -> goToVerifyCode(state.verificationId)
                    is PhoneNumberState.Error -> showError(state.message)
                    is PhoneNumberState.Idle -> TODO()
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

    private fun verifyPhoneNumber(phoneNumber: String) {
        lifecycleScope.launch {
            phoneNumberViewModel.userIntent.send(PhoneNumberIntent.VerifyPhoneNumber(phoneNumber))
        }
    }

    private fun goToVerifyCode(verificationId: String) {
        findNavController().navigate(
            R.id.action_phoneNumberFragment_to_verifyCodeFragment,
            bundleOf(VERIFICATION_ID_PARAM to verificationId)
        )
    }
}