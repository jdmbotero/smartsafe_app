package com.smartsafe.smartsafe_app.presentation.main.boxDetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.smartsafe.smartsafe_app.R
import com.smartsafe.smartsafe_app.databinding.FragmentBoxDetailBinding
import com.smartsafe.smartsafe_app.domain.entity.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.DecimalFormat

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
        fetchHistory()
    }

    private fun setUpView() {
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        binding.boxDetailButtonOpenOrClose.setOnClickListener { openOrCloseBox() }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            boxDetailViewModel.state.collectLatest { state ->
                when (state) {
                    is BoxDetailState.Loading -> showLoading()
                    is BoxDetailState.SuccessOpenOrClose -> refreshBoxInfo(state.box)
                    is BoxDetailState.Error -> showError(state.message)
                    is BoxDetailState.Idle -> {}
                }
            }
        }

        lifecycleScope.launch {
            boxDetailViewModel.stateFetch.collectLatest { state ->
                when (state) {
                    is BoxDetailFetchState.Loading -> showLoading()
                    is BoxDetailFetchState.SuccessFetch -> refreshBoxInfo(state.box)
                    is BoxDetailFetchState.Error -> showError(state.message)
                    is BoxDetailFetchState.Idle -> {}
                }
            }
        }

        lifecycleScope.launch {
            boxDetailViewModel.stateHistory.collectLatest { state ->
                when (state) {
                    is BoxDetailHistoryState.Loading -> showLoading()
                    is BoxDetailHistoryState.SuccessFetchHistory -> refreshHistory(state.history)
                    is BoxDetailHistoryState.Error -> showError(state.message)
                    is BoxDetailHistoryState.Idle -> {}
                }
            }
        }
    }

    private fun showLoading() {
        binding.loading.show()
    }

    private fun showError(message: String?) {
        // binding.loading.hide()
    }

    private fun fetchBox() {
        lifecycleScope.launch {
            boxDetailViewModel.userIntentFetch.send(BoxDetailFetchIntent.FetchBox(box))
        }
    }

    private fun fetchHistory() {
        lifecycleScope.launch {
            boxDetailViewModel.userIntentHistory.send(BoxDetailHistoryIntent.FetchHistory(box.id))
        }
    }

    private fun refreshBoxInfo(box: Box?) {
        if (box?.doorStatus != this.box.doorStatus) binding.loading.hide()
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

            val decimalFormat = DecimalFormat("#")

            binding.boxDetailNearStatusText.text =
                if ((box.distanceObject ?: 0f) > 0 && (box.distanceObject ?: 0f) < 200f) {
                    getString(R.string.near_state_box, decimalFormat.format(box.distanceObject))
                } else if ((box.distanceObject ?: 0f) > 200f) {
                    getString(R.string.near_state_box, decimalFormat.format(box.distanceObject))
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

            binding.boxDetailLightStatusText.text = when (box.lightStatus) {
                LightStatus.ON -> getString(R.string.light_state_box_on)
                LightStatus.OFF -> getString(R.string.light_state_box_off)
            }

            binding.boxDetailLightStatusIndicator.backgroundTintList =
                when (box.lightStatus) {
                    LightStatus.ON -> ContextCompat.getColorStateList(
                        requireContext(),
                        R.color.yellow
                    )
                    LightStatus.OFF -> ContextCompat.getColorStateList(
                        requireContext(),
                        R.color.black
                    )
                }

            binding.boxDetailButtonOpenOrClose.text = when (box.doorStatus) {
                DoorStatus.OPEN -> getString(R.string.close_box)
                DoorStatus.CLOSED -> getString(R.string.open_box)
            }
        }
    }

    private fun refreshHistory(history: List<History>?) {
        if (history?.isNotEmpty() == true) {
            binding.historyList.visibility = View.VISIBLE
            binding.historyList.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = HistoryListAdapter(history)
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