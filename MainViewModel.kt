package com.vidyarthibus.app.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vidyarthibus.app.data.FirebaseRepository
import com.vidyarthibus.app.model.BusRoute
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val repo = FirebaseRepository()

    private val _routes = MutableLiveData<List<BusRoute>>(emptyList())
    val routes: LiveData<List<BusRoute>> = _routes

    private val _loading = MutableLiveData(true)
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    init {
        repo.seedSampleData()
        fetchRoutes()
    }

    private fun fetchRoutes() {
        viewModelScope.launch {
            repo.getBusRoutes()
                .catch { e ->
                    _error.value = e.message ?: "Unknown error"
                    _loading.value = false
                }
                .collect { list ->
                    _routes.value = list
                    _loading.value = false
                }
        }
    }
}
