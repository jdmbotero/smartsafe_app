package com.smartsafe.smartsafe_app.presentation.main.boxList

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.smartsafe.smartsafe_app.R
import com.smartsafe.smartsafe_app.databinding.FragmentBoxListBinding
import com.smartsafe.smartsafe_app.domain.entity.Box
import com.smartsafe.smartsafe_app.presentation.main.boxDetail.BoxDetailFragment
import com.smartsafe.smartsafe_app.util.BiometricManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BoxListFragment : Fragment() {

    private lateinit var binding: FragmentBoxListBinding
    private val boxListViewModel: BoxListViewModel by activityViewModels()
    private lateinit var biometricManager: BiometricManager
    private lateinit var selectedBox: Box

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBoxListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
        setUpView()
        fetchBoxes()
        configureBiometric()
    }

    private fun setUpView() {
        binding.toolbarProfileButton.setOnClickListener { goToProfile() }
        binding.boxListButtonNew.setOnClickListener { goToBoxNew() }
    }

    private fun configureBiometric() {
        context?.let {
            biometricManager = BiometricManager(
                this,
                it,
                title = getString(R.string.biometric_title),
                subtitle = getString(R.string.biometric_subtitle),
                onSuccess = {
                    if (this::selectedBox.isInitialized) goToBoxDetail(selectedBox)
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
        boxListViewModel.handleIntent()
        lifecycleScope.launch {
            boxListViewModel.state.collect { state ->
                when (state) {
                    is BoxListState.Loading -> showLoading()
                    is BoxListState.Success -> setUpBoxesList(state.boxes)
                    is BoxListState.Error -> showError(state.message)
                    is BoxListState.Idle -> {}
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

    private fun fetchBoxes() {
        lifecycleScope.launch {
            boxListViewModel.userIntent.send(BoxListIntent.FetchBoxes)
        }
    }

    private fun setUpBoxesList(boxes: List<Box>) {
        binding.loading.hide()
        if (boxes.isNotEmpty()) {
            binding.contentEmptyState.visibility = View.GONE
            binding.boxList.visibility = View.VISIBLE
            binding.boxList.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = BoxListAdapter(boxes) {
                    selectedBox = it
                    biometricManager.tryAuthenticateBiometric()
                }
            }
        } else {
            binding.contentEmptyState.visibility = View.VISIBLE
            binding.boxList.visibility = View.GONE
        }
    }

    private fun goToBoxNew() {
        findNavController().navigate(R.id.action_boxListFragment_to_boxNewFragment)
    }

    private fun goToBoxDetail(box: Box) {
        findNavController().navigate(
            R.id.action_boxListFragment_to_boxDetailFragment,
            bundleOf(BoxDetailFragment.BOX_PARAM to box)
        )
    }

    private fun goToProfile() {
        findNavController().navigate(R.id.action_boxListFragment_to_profileFragment)
    }
}