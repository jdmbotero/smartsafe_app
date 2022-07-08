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
import com.smartsafe.smartsafe_app.domain.entity.DoorAction
import com.smartsafe.smartsafe_app.domain.entity.DoorStatus
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
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
        arguments?.let {
            box = it.getSerializable(BOX_PARAM) as Box
        }
        observeViewModel()
        setUpView()
        fetchBox()
    }

    private fun setUpView() {
        binding.boxDetailButtonOpenOrClose.setOnClickListener { openOrCloseBox() }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            boxDetailViewModel.state.collectLatest { state ->
                when (state) {
                    is BoxDetailState.Loading -> showLoading()
                    is BoxDetailState.SuccessFetch -> refreshBoxInfo(state.box)
                    is BoxDetailState.SuccessOpenOrClose -> refreshBoxInfo(state.box)
                    is BoxDetailState.Error -> showError(state.message)
                    is BoxDetailState.Idle -> {}
                }
            }
        }
    }

    private fun showLoading() {
        // binding.loading.show()
    }

    private fun showError(message: String?) {
        // binding.loading.hide()
    }

    private fun fetchBox() {
        lifecycleScope.launch {
            boxDetailViewModel.userIntent.send(BoxDetailIntent.FetchBox(box))
        }
    }

    private fun refreshBoxInfo(box: Box?) {
        binding.loading.hide()
        box?.let {
            this.box = box
            binding.boxDetailDoorStatusText.text = when (box.doorStatus) {
                DoorStatus.OPEN -> getString(R.string.open_state_box)
                DoorStatus.CLOSED -> getString(R.string.closed_state_box)
            }

            binding.boxDetailDoorStatusIndicator.backgroundTintList =
                when (box.doorStatus) {
                    DoorStatus.OPEN -> ContextCompat.getColorStateList(
                        requireContext(),
                        R.color.yellow
                    )
                    DoorStatus.CLOSED -> ContextCompat.getColorStateList(
                        requireContext(),
                        R.color.green
                    )
                }

            binding.boxDetailNearStatusText.text =
                if ((box.distanceObject ?: 0f) > 0 && (box.distanceObject ?: 0f) < 200f) {
                    getString(R.string.near_state_box, box.distanceObject)
                } else if ((box.distanceObject ?: 0f) > 200f) {
                    getString(R.string.near_state_box, box.distanceObject)
                } else {
                    getString(R.string.near_state_box_clear)
                }

            binding.boxDetailNearStatusIndicator.backgroundTintList =
                if ((box.distanceObject ?: 0f) > 0 && (box.distanceObject ?: 0f) < 200f) {
                    ContextCompat.getColorStateList(requireContext(), R.color.yellow)
                } else if ((box.distanceObject ?: 0f) > 200f) {
                    ContextCompat.getColorStateList(requireContext(), R.color.green)
                } else {
                    ContextCompat.getColorStateList(requireContext(), R.color.green)
                }

            binding.boxDetailButtonOpenOrClose.text = when (box.doorStatus) {
                DoorStatus.OPEN -> getString(R.string.close_box)
                DoorStatus.CLOSED -> getString(R.string.open_box)
            }
        }
    }

    private fun openOrCloseBox() {
        lifecycleScope.launch {
            boxDetailViewModel.userIntent.send(
                BoxDetailIntent.OpenOrCloseBox(
                    box, when (box.doorStatus) {
                        DoorStatus.OPEN -> DoorAction.CLOSE
                        DoorStatus.CLOSED -> DoorAction.OPEN
                    }
                )
            )
        }
    }
}