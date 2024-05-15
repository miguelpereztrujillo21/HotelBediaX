package com.mpt.hotelbediax.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mpt.hotelbediax.dao.DestinationDao
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
    fun addDestination(destination: Destination){
        viewModelScope.launch {
            destinationDao.insertDestination(destination)
            try {
                destinationRepository.create(destination)
                destination.isSyncPending = false
                destinationDao.updateDestination(destination)
            } catch (e: Exception) {
                e.printStackTrace()
                syncDestinations()
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
                // Reemplaza el destino en ese índice con el destino actualizado
                if (index != null && index >= 0) {
                    _destinations.value =
                        _destinations.value?.toMutableList().apply { this?.set(index, destination) }
                }
            }
        }
    }

    private fun syncDestinations() {
        viewModelScope.launch {
            try {
                val backendDestinations = destinationRepository.getAllDestinations().results
                val localDestinations = destinationDao.getAllDestinations()

                // Comparar los datos de back con los locales
                val newDestinations = backendDestinations?.filter { backendDestination ->
                    localDestinations.none { it.id == backendDestination.id}
                }
                // Actualizar la base de datos local si hay nuevos destinos
                newDestinations?.forEach { destination ->
                    destinationDao.insertDestination(destination)
                }
                //Obtiene las no sincronizadas y los intenta sincronizar con back
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
                // Paso 5: Eliminar los destinos locales que no existen en el backend
                val deletedDestinations = localDestinations.filter { localDestination ->
                    backendDestinations?.none { it.id == localDestination.id && !localDestination.isSyncPending }
                        ?: false
                }
                deletedDestinations.forEach { destination ->
                    destinationDao.deleteDestination(destination.id)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }finally {
                _destinations.postValue(
                    destinationDao.getDestinationsInRange(
                        currentPage * itemsPerPage,
                        itemsPerPage
                    )
                )
            }
        }
    }

    fun filterByDestinationName(name: String) {
        viewModelScope.launch {
            _destinations.postValue(destinationDao.getDestinationsByName(name))
        }
    }

    fun updateFilterText(newText: String) {
        _filterText.value = newText
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