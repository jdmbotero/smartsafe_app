package com.smartsafe.smartsafe_app.presentation.main.boxDetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.smartsafe.smartsafe_app.R
import com.smartsafe.smartsafe_app.databinding.FragmentBoxDetailBinding
import com.smartsafe.smartsafe_app.domain.entity.Box
import com.smartsafe.smartsafe_app.domain.entity.DoorStatus
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BoxDetailFragment : Fragment() {

    private lateinit var binding: FragmentBoxDetailBinding
    private val boxDetailViewModel: BoxDetailViewModel by activityViewModels()
    lateinit var box: Box

    companion object {
        const val BOX_PARAM = "box"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBoxDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()

        arguments?.let {
            box = it.getSerializable(BOX_PARAM) as Box
        }

        setUpView()
        fetchBox()
    }

    private fun setUpView() {
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            boxDetailViewModel.state.collect { state ->
                when (state) {
                    is BoxDetailState.Loading -> showLoading()
                    is BoxDetailState.Success -> refreshBoxInfo(state.box)
                    is BoxDetailState.Error -> showError(state.message)
                    is BoxDetailState.Idle -> {}
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

    private fun fetchBox() {
        lifecycleScope.launch {
            boxDetailViewModel.userIntent.send(BoxDetailIntent.FetchBox(box))
        }
    }

    private fun refreshBoxInfo(box: Box?) {
        binding.loading.hide()
        box?.let {
            binding.boxDetailDoorStatusText.text = box.doorStatus?.value

            binding.boxDetailDoorStatusText.compoundDrawables[0].setTint(
                if (box.doorStatus!! == DoorStatus.OPEN)
                    ContextCompat.getColor(requireContext(), R.color.yellow)
                else
                    ContextCompat.getColor(requireContext(), R.color.green)
            )

        }
    }
}