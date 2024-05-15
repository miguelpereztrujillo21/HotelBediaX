package com.mpt.hotelbediax.ui.home

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.InvalidatingPagingSourceFactory
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import com.mpt.hotelbediax.dao.DestinationDao
import com.mpt.hotelbediax.models.Destination
import com.mpt.hotelbediax.network.DestinationRepository
import com.mpt.hotelbediax.pagin.DestinationPagingSource
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

    private val pagingSourceFactory = InvalidatingPagingSourceFactory { DestinationPagingSource(destinationDao) }

    val destinationsPaged: LiveData<PagingData<Destination>> = Pager(
        config = PagingConfig(
            pageSize = 20,  // Define el tamaño de página
            enablePlaceholders = true
        ),
        pagingSourceFactory = pagingSourceFactory
    ).flow.cachedIn(viewModelScope).asLiveData()


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
                pagingSourceFactory.invalidate()
            }
        }
    }

    fun updateDestination(destination: Destination){
        viewModelScope.launch {
            try {
                destinationRepository.update(destination)
                destination.isSyncPending = false
                destinationDao.updateDestination(destination)
                pagingSourceFactory.invalidate()
            } catch (e: Exception) {
                e.printStackTrace()
                destination.isSyncPending = true
            }finally {
            }
        }
    }

    private fun syncDestinations() {
        viewModelScope.launch {
            try {
                val backendDestinations = destinationRepository.getAllDestinations().results
                val localDestinations = destinationDao.getAllDestinations()

                // Paso 3: Comparar los datos
                val newDestinations = backendDestinations?.filter { backendDestination ->
                    localDestinations.none { it.id == backendDestination.id}
                }
                // Paso 4: Actualizar la base de datos local si hay nuevos destinos
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
                _destinations.postValue(destinationDao.getAllDestinations())
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

}