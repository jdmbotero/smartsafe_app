package com.smartsafe.smartsafe_app.util

import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import java.util.concurrent.Executor

class BiometricManager constructor(
    fragment: Fragment,
    context: Context,
    title: String = "Example biometric authentication",
    subtitle: String = "Please authenticate yourself first.",
    private val onSuccess: (() -> Unit)? = null,
    private val onError: ((message: String) -> Unit)? = null
) {

    companion object {
        const val LOG_TAG = "BiometricManager"
    }

    private var biometricManager: BiometricManager = BiometricManager.from(context)
    private var executor: Executor = ContextCompat.getMainExecutor(context)
    private var biometricPrompt: BiometricPrompt = BiometricPrompt(
        fragment, executor,
        object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(
                errorCode: Int,
                errString: CharSequence
            ) {
                super.onAuthenticationError(errorCode, errString)
                onError?.invoke("Authentication error: Code: $errorCode ($errString)")
                Log.e(LOG_TAG, "Authentication error: Code: $errorCode ($errString)")
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                onError?.invoke("Failed to authenticate. Please try again.")
                Log.e(LOG_TAG, "Failed to authenticate. Please try again.")
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                onSuccess?.invoke()
                Log.e(LOG_TAG, "Authentication successful!")
            }
        },
    )
    private lateinit var promptInfo: BiometricPrompt.PromptInfo
    private val authenticators =
        BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL

    lateinit var enrollBiometricRequestLauncher: ActivityResultLauncher<Intent>

    init {
        try {
            promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle(title)
                .setSubtitle(subtitle)
                .setAllowedAuthenticators(authenticators)
                .build()
        } catch (e: Exception) {
            onError?.invoke(e.message ?: "Unable to initialize PromptInfo")
            Log.e(LOG_TAG, e.message ?: "Unable to initialize PromptInfo")
        }
    }

    fun tryAuthenticateBiometric() {
        checkDeviceCapability {
            biometricPrompt.authenticate(promptInfo)
        }
    }

    private fun checkDeviceCapability(onSuccess: () -> Unit) {
        when (biometricManager.canAuthenticate(authenticators)) {
            BiometricManager.BIOMETRIC_SUCCESS, BiometricManager.BIOMETRIC_STATUS_UNKNOWN -> {
                onSuccess()
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                onError?.invoke("No biometric features available on this device")
                Log.e(LOG_TAG, "No biometric features available on this device")
            }
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                onError?.invoke("Biometric features are currently unavailable")
                Log.e(LOG_TAG, "Biometric features are currently unavailable")
            }
            BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED -> {
                onError?.invoke("Biometric options are incompatible with the current Android version")
                Log.e(
                    LOG_TAG,
                    "Biometric options are incompatible with the current Android version"
                )
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    if (this::enrollBiometricRequestLauncher.isInitialized) {
                        enrollBiometricRequestLauncher.launch(
                            Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                                putExtra(
                                    Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                                    authenticators
                                )
                            }
                        )
                    } else {
                        onError?.invoke("enrollBiometricRequestLauncher is not initialized")
                        Log.e(LOG_TAG, "enrollBiometricRequestLauncher is not initialized")
                    }
                } else {
                    onError?.invoke("Could not request biometric enrollment in API level < 30")
                    Log.e(LOG_TAG, "Could not request biometric enrollment in API level < 30")
                }
            }
            BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED -> {
                onError?.invoke("Biometric features are unavailable because security vulnerabilities has been discovered in one or more hardware sensors")
                Log.e(
                    LOG_TAG,
                    "Biometric features are unavailable because security vulnerabilities has been discovered in one or more hardware sensors"
                )
            }
            else -> {
                throw IllegalStateException()
            }
        }
    }
}