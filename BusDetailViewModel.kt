package com.vidyarthibus.app.ui

import android.app.Application
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.vidyarthibus.app.data.FirebaseRepository
import com.vidyarthibus.app.model.CrowdStatus
import com.vidyarthibus.app.utils.LocationHelper
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import android.Manifest

class BusDetailViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = FirebaseRepository()
    private val locationHelper = LocationHelper(app)

    private val _crowdStatus = MutableLiveData(CrowdStatus.EMPTY)
    val crowdStatus: LiveData<CrowdStatus> = _crowdStatus

    private val _submitting = MutableLiveData(false)
    val submitting: LiveData<Boolean> = _submitting

    /** Pair<success, message> */
    private val _toast = MutableLiveData<String?>(null)
    val toast: LiveData<String?> = _toast

    fun observeStatus(routeId: String) {
        viewModelScope.launch {
            repo.getCrowdStatus(routeId)
                .catch { /* keep default */ }
                .collect { status -> _crowdStatus.value = status }
        }
    }

    fun submitReport(routeId: String, userId: String, status: CrowdStatus) {
        if (_submitting.value == true) return
        viewModelScope.launch {
            _submitting.value = true

            // Location check
            val hasPermission = ContextCompat.checkSelfPermission(
                getApplication(), Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

            val lat: Double
            val lon: Double

            if (hasPermission) {
                val loc = locationHelper.getCurrentLocation()
                if (loc != null && !locationHelper.isNearRoute(loc, routeId)) {
                    _toast.value = "⚠️ You must be near the bus route to report!"
                    _submitting.value = false
                    return@launch
                }
                lat = loc?.latitude ?: 0.0
                lon = loc?.longitude ?: 0.0
            } else {
                lat = 0.0
                lon = 0.0
            }

            repo.submitReport(routeId, userId, status, lat, lon) { ok, msg ->
                _toast.value = msg
                _submitting.value = false
            }
        }
    }

    fun clearToast() { _toast.value = null }
}
