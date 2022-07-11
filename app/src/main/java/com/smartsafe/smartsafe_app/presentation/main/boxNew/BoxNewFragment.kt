package com.smartsafe.smartsafe_app.presentation.main.boxNew

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.DefaultDecoderFactory
import com.smartsafe.smartsafe_app.R
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

    private val formats =
        listOf(BarcodeFormat.QR_CODE)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBoxNewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
        setUpView()
        configureBiometric()
        configureScanner()
    }

    override fun onResume() {
        super.onResume()
        binding.barcodeScannerView.resume()
    }

    override fun onPause() {
        super.onPause()
        binding.barcodeScannerView.pause()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if ((grantResults.isNotEmpty() &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED)
            ) {
                scanQr()
            } else {
                stopScanner()
            }
            return
        }
    }

    private fun configureScanner() {
        binding.barcodeScannerView.barcodeView.decoderFactory = DefaultDecoderFactory(formats)
        binding.barcodeScannerView.cameraSettings.isAutoFocusEnabled = true

        binding.buttonScanner.setOnClickListener {
            if (!checkPermissions()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requireActivity().requestPermissions(arrayOf(Manifest.permission.CAMERA), 1)
                }
            } else {
                scanQr()
            }
        }
    }

    private fun checkPermissions():Boolean{
        return ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_DENIED
    }

    private fun scanQr() {
        binding.barcodeScannerView.resume()
        binding.barcodeScannerView.barcodeView.decoderFactory = DefaultDecoderFactory(formats)
        binding.barcodeScannerView.cameraSettings.isAutoFocusEnabled = true

        binding.imgScanner.visibility = View.GONE
        binding.barcodeScannerView.visibility = View.VISIBLE
        binding.barcodeScannerView.initializeFromIntent(requireActivity().intent)
        binding.barcodeScannerView.setStatusText("")

        binding.barcodeScannerView.decodeSingle {
            if (it.text != null) {
                binding.barcodeScannerView.pause()
                binding.boxIdText.editText?.setText(it.text)

                Handler(Looper.getMainLooper()).postDelayed({
                    stopScanner()
                }, 1000)
            }
        }
    }

    private fun stopScanner() {
        binding.barcodeScannerView.pause()
        binding.imgScanner.visibility = View.VISIBLE
        binding.barcodeScannerView.visibility = View.INVISIBLE
    }

    private fun setUpView() {
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }

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
            biometricManager = BiometricManager(
                this,
                it,
                title = getString(R.string.biometric_title),
                subtitle = getString(R.string.biometric_subtitle),
                onSuccess = {
                    saveBox(
                        binding.boxIdText.editText?.text.toString(),
                        binding.boxNameText.editText?.text.toString()
                    )
                },
                onError = { message ->
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
                    is BoxNewState.Success -> goBack()
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

    private fun goBack() {
        binding.loading.hide()
        findNavController().popBackStack()
    }
}