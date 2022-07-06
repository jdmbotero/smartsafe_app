package com.smartsafe.smartsafe_app.presentation.main.boxList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.smartsafe.smartsafe_app.R
import com.smartsafe.smartsafe_app.databinding.FragmentBoxListBinding
import com.smartsafe.smartsafe_app.domain.entity.Box
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BoxListFragment : Fragment() {

    private lateinit var binding: FragmentBoxListBinding
    private val boxListViewModel: BoxListViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        observeViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBoxListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpView()
        fetchBoxes()
    }

    private fun setUpView() {
        binding.containedButton.setOnClickListener { goToBoxNew() }
    }

    private fun observeViewModel() {
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
                    goToBoxDetail(it)
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

    }
}