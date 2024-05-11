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

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text

    private val _destinations = MutableLiveData<List<Destination>>()
    val destinations: LiveData<List<Destination>> get() = _destinations

    fun getDestinations(){
        viewModelScope.launch {
            try {
                syncDestinations()
                _destinations.postValue(destinationDao.getAllDestinations())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    private fun syncDestinations() {
        viewModelScope.launch {
            try {
                // Paso 1: Obtener datos del backend
                val backendDestinations = destinationRepository.getAllDestinations().results

                // Paso 2: Obtener datos de la base de datos local
                val localDestinations = destinationDao.getAllDestinations()

                // Paso 3: Comparar los datos
                val newDestinations = backendDestinations?.filter { backendDestination ->
                    localDestinations.none { it.id == backendDestination.id }
                }
                // Paso 4: Actualizar la base de datos local si hay nuevos destinos
                newDestinations?.forEach { destination ->
                    destinationDao.insertDestination(destination)
                }
                // Paso 5: Eliminar los destinos locales que no existen en el backend
                val deletedDestinations = localDestinations.filter { localDestination ->
                    backendDestinations?.none { it.id == localDestination.id } ?: false
                }
                deletedDestinations.forEach { destination ->
                    destinationDao.deleteDestination(destination.id)
                }

                // Actualizar la lista de destinos en vivo con los datos más recientes
                _destinations.postValue(destinationDao.getAllDestinations())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}