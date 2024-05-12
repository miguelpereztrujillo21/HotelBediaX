package com.mpt.hotelbediax.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.mpt.hotelbediax.adapters.DestinationAdapter
import com.mpt.hotelbediax.databinding.FragmentHomeBinding
import com.mpt.hotelbediax.helpers.DestinationDialogFragment
import com.mpt.hotelbediax.models.Destination
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val homeViewModel: HomeViewModel by viewModels()
    private var destinationAdapter: DestinationAdapter? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        initObservers()
        initListeners()
        seUpRecyclerView()
        homeViewModel.getDestinations()

        return root
    }

    private fun initObservers() {
        homeViewModel.destinations.observe(viewLifecycleOwner) {
            destinationAdapter?.submitList(it)
        }
        homeViewModel.filterText.observe(viewLifecycleOwner) {
            homeViewModel.filterByDestinationName(it ?: "")
        }
    }
    private fun initListeners(){
        binding.homeSearchBar.doAfterTextChanged { text ->
            homeViewModel.updateFilterText(text.toString())
        }
        binding.homeAddButton.setOnClickListener {
            val dialog = DestinationDialogFragment(object : DestinationDialogFragment.OnAddClickListener {
                override fun onAddClick(destination: Destination) {
                    homeViewModel.addDestination(destination)
                }
            })
            dialog.show(childFragmentManager, "DestinationDialogFragment")
        }
    }

    private fun seUpRecyclerView() {
        destinationAdapter =
            DestinationAdapter(requireContext(), object : DestinationAdapter.ClickListener {
                override fun onClick(destination: Destination) {
                    homeViewModel.deleteDestination(destination)
                }
            })
        binding.homeRecycler.adapter = destinationAdapter
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}