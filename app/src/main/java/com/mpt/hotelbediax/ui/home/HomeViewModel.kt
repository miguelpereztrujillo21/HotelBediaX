package com.mpt.hotelbediax.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mpt.hotelbediax.dao.DestinationDao
import com.mpt.hotelbediax.helpers.Constants
import com.mpt.hotelbediax.models.Destination
import com.mpt.hotelbediax.network.DestinationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val destinationRepository:DestinationRepository,
    private val destinationDao: DestinationDao): ViewModel() {

    private val _filterText = MutableLiveData<String?>()
    val filterText: LiveData<String?> get() = _filterText

    private val _destinations = MutableLiveData<List<Destination>>()
    val destinations: LiveData<List<Destination>> get() = _destinations
    private var currentPage = 0
    private val itemsPerPage = 20
    var isLoading = false



    init {
        syncDestinations()
    }

    fun updateFilterText(newText: String) {
        _filterText.value = newText
    }

    fun syncDestinations() {
        viewModelScope.launch {
            try {
                currentPage = 0
                val backendDestinations = destinationRepository.getAllDestinations().results
                val localDestinations = destinationDao.getAllDestinations()

                syncNewDestinations(backendDestinations, localDestinations)
                syncPendingDestinations(localDestinations)
                deleteNonExistentDestinations(backendDestinations, localDestinations)
                _destinations.postValue(
                    destinationDao.getDestinationsInRange(
                        currentPage * itemsPerPage,
                        itemsPerPage
                    )
                )

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

     suspend fun syncNewDestinations(
        backendDestinations: List<Destination>?,
        localDestinations: List<Destination>
    ) {
        val newDestinations = backendDestinations?.filter { backendDestination ->
            localDestinations.none { it.id == backendDestination.id }
        }
        newDestinations?.forEach { destination ->
            destinationDao.insertDestination(destination)
        }
    }

    private suspend fun syncPendingDestinations(localDestinations: List<Destination>) {
        val pendingSyncDestinations = localDestinations.filter { it.isSyncPending }
        pendingSyncDestinations.forEach { destination ->
            try {
                if (destination.isLocalDeleted) {
                    destinationRepository.deleteById(destination.id)
                    destinationDao.deleteDestination(destination.id)
                } else {
                    destinationRepository.create(destination)
                    destination.isSyncPending = false
                    destinationDao.updateDestination(destination)
                }
            } catch (e: Exception) {
                destination.isSyncPending = true
                e.printStackTrace()
            }
        }
    }

    private suspend fun deleteNonExistentDestinations(
        backendDestinations: List<Destination>?,
        localDestinations: List<Destination>
    ) {
        val deletedDestinations = localDestinations.filter { localDestination ->
            backendDestinations?.none { it.id == localDestination.id && !localDestination.isSyncPending }
                ?: false
        }
        deletedDestinations.forEach { destination ->
            destinationDao.deleteDestination(destination.id)
        }
    }

    fun addDestination(destination: Destination){
        viewModelScope.launch {
            destinationDao.insertDestination(destination)
            try {
                destinationRepository.create(destination)
                destination.isSyncPending = false
                destinationDao.updateDestination(destination)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _destinations.value = _destinations.value?.plus(destination)
            }
        }
    }

    fun deleteDestination(destination: Destination){
        viewModelScope.launch {
            try {
                destinationRepository.deleteById(destination.id)
                destinationDao.deleteDestination(destination.id)
            } catch (e: Exception) {
                e.printStackTrace()
                destination.isSyncPending = true
                destination.isLocalDeleted = true
                destinationDao.updateDestination(destination)
            }finally {
                _destinations.value = _destinations.value?.filter { it.id != destination.id }
            }
        }
    }

    fun updateDestination(destination: Destination){
        viewModelScope.launch {
            try {
                destinationRepository.update(destination)
                destination.isSyncPending = false
            } catch (e: Exception) {
                e.printStackTrace()
                destination.isSyncPending = true
            }finally {
                destinationDao.updateDestination(destination)
                val index = _destinations.value?.indexOfFirst { it.id == destination.id }
                if (index != null && index >= 0) {
                    _destinations.value =
                        _destinations.value?.toMutableList().apply { this?.set(index, destination) }
                }
            }
        }
    }

    fun filterByDestinationName(name: String) {
        viewModelScope.launch {
            _destinations.postValue(destinationDao.getDestinationsByName(name))
        }
    }

    fun filterByDestinationType(type: String) {
        viewModelScope.launch {
            _destinations.value = destinationDao.getDestinationsByType(type)
        }
    }
    fun filterByDestinationDate(order: String) {
        viewModelScope.launch {
            if (order == Constants.ORDER_BY_DATE_ASC)
                _destinations.postValue(destinationDao.getDestinationsOrderedByDateASC().toMutableList())
            else if (order == Constants.ORDER_BY_DATE_DESC)
                _destinations.postValue(destinationDao.getDestinationsOrderedByDateDESC().toMutableList())
        }
    }


    fun loadNextPage() {
        if (!isLoading) {
            isLoading = true
            viewModelScope.launch {
                currentPage++
                val pageItems =
                    destinationDao.getDestinationsInRange(currentPage * itemsPerPage, itemsPerPage)
                _destinations.value = _destinations.value?.plus(pageItems)
                isLoading = false
            }
        }
    }

}