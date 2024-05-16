package com.mpt.hotelbediax.ui.home


import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.adapters.ImageViewBindingAdapter.setImageDrawable
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mpt.hotelbediax.R
import com.mpt.hotelbediax.adapters.DestinationAdapter
import com.mpt.hotelbediax.databinding.FragmentHomeBinding
import com.mpt.hotelbediax.helpers.Constants
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
        setUpComponents()

        return root
    }

    private fun initObservers() {
        homeViewModel.destinations.observe(viewLifecycleOwner) { destinations ->
            val visibleDestinations =  destinations.filter { !it.isLocalDeleted }
            destinationAdapter?.submitList(visibleDestinations)
        }
        homeViewModel.filterText.observe(viewLifecycleOwner) {
            homeViewModel.filterByDestinationName(it ?: "")
        }
    }

    private fun initListeners(){
        binding.homeSwipeRefresh.setOnRefreshListener {

            homeViewModel.syncDestinations()
            resetFilters()

            // Recuerda llamar a setRefreshing(false) para indicar que el gesto de actualización ha terminado.
            binding.homeSwipeRefresh.isRefreshing = false
        }
        binding.homeSearchBar.doAfterTextChanged { text ->
            homeViewModel.updateFilterText(text.toString())
        }
        binding.homeAddButton.setOnClickListener {
            val dialog = DestinationDialogFragment(object : DestinationDialogFragment.OnAddClickListener {
                override fun onPositiveClick(destination: Destination) {
                    homeViewModel.addDestination(destination)
                }
            },false)
            dialog.show(childFragmentManager, "DestinationDialogFragment")
        }
        binding.homeFilters.spinnerType.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedItem = parent.getItemAtPosition(position).toString()
                    if (selectedItem == Constants.TYPE_CITY) {
                        homeViewModel.filterByDestinationType(Constants.TYPE_CITY)
                        binding.homeFilters.spinnerData.setSelection(0)
                    } else if (selectedItem == Constants.TYPE_COUNTRY) {
                        homeViewModel.filterByDestinationType(Constants.TYPE_COUNTRY)
                        binding.homeFilters.spinnerData.setSelection(0)
                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    TODO("Not yet implemented")
                }
            }
        binding.homeFilters.spinnerData.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {

                    val selectedItem = parent.getItemAtPosition(position).toString()
                    if (selectedItem == Constants.ORDER_BY_DATE_ASC) {
                        homeViewModel.filterByDestinationDate(Constants.ORDER_BY_DATE_ASC)
                        binding.homeFilters.spinnerType.setSelection(0)
                    } else if (selectedItem == Constants.ORDER_BY_DATE_DESC) {
                        homeViewModel.filterByDestinationDate(Constants.ORDER_BY_DATE_DESC)
                        binding.homeFilters.spinnerType.setSelection(0)
                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    TODO("Not yet implemented")
                }
            }
    }

    private fun setUpComponents() {
        seUpRecyclerView()
        setUpSpinner(
            arrayOf(Constants.NONE, Constants.TYPE_CITY, getString(R.string.simple_country)),
            binding.homeFilters.spinnerType
        )
        setUpSpinner(
            arrayOf(
                Constants.NONE,
                Constants.ORDER_BY_DATE_ASC,
                Constants.ORDER_BY_DATE_DESC
            ), binding.homeFilters.spinnerData
        )
    }

    private fun setUpSpinner(array: Array<String>, spinner: Spinner) {
        val adapter = ArrayAdapter(requireContext(), R.layout.item_spinner, array)
        spinner.adapter = adapter
    }

    private fun seUpRecyclerView() {
        destinationAdapter =
            DestinationAdapter(object : DestinationAdapter.ClickListener {
                override fun onClickDelete(destination: Destination) {
                    homeViewModel.deleteDestination(destination)
                }
                override fun onClickEdit(destination: Destination) {
                    val dialog = DestinationDialogFragment(object : DestinationDialogFragment.OnAddClickListener {
                        override fun onPositiveClick(destination: Destination) {
                            homeViewModel.updateDestination(destination)
                        }
                    }, true)
                    dialog.show(childFragmentManager, "DestinationDialogFragment")
                    dialog.setDestination(destination)
                }
            })

        binding.homeRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                if (filtersAreEmpty() && dy > 0 && !homeViewModel.isLoading && layoutManager.findLastVisibleItemPosition() >= layoutManager.itemCount - 10) {
                    homeViewModel.loadNextPage()
                }
            }
        })
        binding.homeRecycler.adapter = destinationAdapter
    }

    private fun resetFilters() {
        binding.homeFilters.spinnerType.setSelection(0)
        binding.homeFilters.spinnerData.setSelection(0)
        homeViewModel.updateFilterText("")
    }

    fun filtersAreEmpty(): Boolean {
        return binding.homeFilters.spinnerType.selectedItemPosition == 0 && binding.homeFilters.spinnerData.selectedItemPosition == 0
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}