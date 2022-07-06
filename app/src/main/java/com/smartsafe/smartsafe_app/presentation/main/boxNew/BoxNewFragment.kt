package com.smartsafe.smartsafe_app.presentation.main.boxNew

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.smartsafe.smartsafe_app.databinding.FragmentBoxNewBinding
import com.smartsafe.smartsafe_app.domain.entity.Box
import com.smartsafe.smartsafe_app.util.BiometricManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BoxNewFragment : Fragment() {

    private lateinit var binding: FragmentBoxNewBinding
    private val boxNewViewModel: BoxNewViewModel by activityViewModels()
    private lateinit var biometricManager: BiometricManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        observeViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBoxNewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpView()
        configureBiometric()
    }

    private fun setUpView() {
        binding.boxButtonSave.isEnabled = false

        binding.boxIdText.editText?.doOnTextChanged { inputText, _, _, _ ->
            binding.boxButtonSave.isEnabled =
                (binding.boxNameText.editText?.text?.length ?: 0) > 0
                        && (inputText?.length ?: 0) > 0
        }

        binding.boxNameText.editText?.doOnTextChanged { inputText, _, _, _ ->
            binding.boxButtonSave.isEnabled =
                (binding.boxIdText.editText?.text?.length ?: 0) > 0
                        && (inputText?.length ?: 0) > 0
        }

        binding.boxButtonSave.setOnClickListener {
            biometricManager.tryAuthenticateBiometric()
        }
    }

    private fun configureBiometric() {
        context?.let {
            biometricManager = BiometricManager(this, it, onSuccess = {
                saveBox(
                    binding.boxIdText.editText?.text.toString(),
                    binding.boxNameText.editText?.text.toString()
                )
            }, onError = { message ->
                showError(message)
            })
            biometricManager.enrollBiometricRequestLauncher =
                registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
                    if (activityResult.resultCode == Activity.RESULT_OK) {
                        biometricManager.tryAuthenticateBiometric()
                    } else {
                        Log.e(BiometricManager.LOG_TAG, "Failed to enroll in biometric")
                    }
                }
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            boxNewViewModel.state.collect { state ->
                when (state) {
                    is BoxNewState.Loading -> showLoading()
                    is BoxNewState.Success -> goToBoxDetail(state.box)
                    is BoxNewState.Error -> showError(state.message)
                    is BoxNewState.Idle -> {}
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

    private fun saveBox(id: String, name: String) {
        lifecycleScope.launch {
            boxNewViewModel.userIntent.send(BoxNewIntent.AddOrUpdateBox(Box(id, name)))
        }
    }

    private fun goToBoxDetail(box: Box) {

    }
}