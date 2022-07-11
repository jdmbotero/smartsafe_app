package com.smartsafe.smartsafe_app.presentation.auth.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.smartsafe.smartsafe_app.databinding.FragmentProfileBinding
import com.smartsafe.smartsafe_app.domain.entity.User
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
        fetchUser()
    }

    private fun setUpView() {
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }

        binding.profileButtonSave.isEnabled =
            (binding.profileNameText.editText?.text?.length ?: 0) > 0
        binding.profileNameText.editText?.doOnTextChanged { inputText, _, _, _ ->
            binding.profileButtonSave.isEnabled = (inputText?.length ?: 0) > 0
        }

        binding.profileButtonSave.setOnClickListener {
            updateUser(binding.profileNameText.editText?.text.toString())
        }

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
                    is ProfileState.FetchUserSuccess -> refreshUserInfo(state.user)
                    is ProfileState.UpdateUserSuccess -> binding.loading.hide()
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

    private fun fetchUser() {
        lifecycleScope.launch {
            profileViewModel.userIntent.send(ProfileIntent.FetchUser)
        }
    }

    private fun refreshUserInfo(user: User?) {
        binding.loading.hide()
        binding.profileNameText.editText?.setText(user?.name)
    }

    private fun updateUser(name: String) {
        lifecycleScope.launch {
            profileViewModel.userIntent.send(ProfileIntent.UpdateUser(User(name = name)))
        }
    }

    private fun logout() {
        lifecycleScope.launch {
            profileViewModel.userIntent.send(ProfileIntent.Logout)
        }
    }

    private fun goToLogin() {
        binding.loading.hide()
        val intent = Intent(activity, AuthActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        activity?.finish()
    }
}