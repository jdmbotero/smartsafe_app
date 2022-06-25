package com.smartsafe.smartsafe_app.presentation.auth.phoneNumber

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.smartsafe.smartsafe_app.databinding.FragmentPhoneNumberBinding
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

    private fun observeViewModel() {
        lifecycleScope.launch {
            phoneNumberViewModel.phoneNumberState.collect { phoneNumberState ->
                when (phoneNumberState) {
                    is PhoneNumberState.Loading -> showLoading()
                    is PhoneNumberState.Success -> TODO()
                    is PhoneNumberState.Error -> showError(phoneNumberState.message)
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

    private fun login() {
        lifecycleScope.launch {
            phoneNumberViewModel.userIntent.send(PhoneNumberIntent.VerifyPhoneNumber(""))
        }
    }
}