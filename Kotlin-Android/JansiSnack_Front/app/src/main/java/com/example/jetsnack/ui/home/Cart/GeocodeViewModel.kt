package com.example.jetsnack.ui.home.Cart

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GeocodeViewModel(application: Application) : AndroidViewModel(application) {
    private val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(application)
    private val _location = MutableStateFlow(Pair(0.00, 0.00))
    val location = _location.asStateFlow()

    fun fetchLocation() {
        viewModelScope.launch {
            if (ActivityCompat.checkSelfPermission(getApplication(),
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    getApplication(),
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return@launch
            }

            val cancellationTokenSource = CancellationTokenSource()

            fusedLocationClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, cancellationTokenSource.token).addOnSuccessListener { location ->
                location?.let {
                    _location.value = Pair(it.latitude, it.longitude)
                }
            }.addOnFailureListener {
            }
        }
    }
}