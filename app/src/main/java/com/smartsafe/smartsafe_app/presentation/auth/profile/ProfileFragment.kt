package com.smartsafe.smartsafe_app.presentation.auth.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.smartsafe.smartsafe_app.databinding.FragmentProfileBinding
import com.smartsafe.smartsafe_app.presentation.auth.AuthActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private val profileViewModel: ProfileViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
        setUpView()
    }

    private fun setUpView() {
        binding.profileButtonLogout.setOnClickListener {
            logout()
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            profileViewModel.state.collect { state ->
                when (state) {
                    is ProfileState.Loading -> showLoading()
                    is ProfileState.LogoutSuccess -> goToLogin()
                    is ProfileState.UpdateUserSuccess -> {}
                    is ProfileState.Error -> showError(state.message)
                    is ProfileState.Idle -> {}
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

    private fun logout() {
        lifecycleScope.launch {
            profileViewModel.userIntent.send(ProfileIntent.Logout)
        }
    }

    private fun goToLogin() {
        binding.loading.hide()
        startActivity(Intent(activity, AuthActivity::class.java))
        activity?.finish()
    }
}