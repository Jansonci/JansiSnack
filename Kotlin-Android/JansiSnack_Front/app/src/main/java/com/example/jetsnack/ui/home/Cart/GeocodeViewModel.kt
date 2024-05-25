package com.example.jetsnack.ui.home.Cart

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.util.Log
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

    private val _cityName = MutableStateFlow("未知d")
//    val cityName = _cityName.asStateFlow()

    fun fetchLocation() {
        viewModelScope.launch {
            if (ActivityCompat.checkSelfPermission(getApplication(),
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    getApplication(),
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.i("Unableeee", "Unableeee")
                return@launch
            }

            val cancellationTokenSource = CancellationTokenSource()

            fusedLocationClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, cancellationTokenSource.token).addOnSuccessListener { location ->
                location?.let {
                    _location.value = Pair(it.latitude, it.longitude)
                    Log.i("Locationnnn", Pair(it.latitude, it.longitude).toString())
                }
            }.addOnFailureListener {
                Log.i("LocationError", "Failed to get location")
            }
        }
        }

//    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
//    fun getCityNameFromCoordinates(latitude: Double, longitude: Double) {
//        viewModelScope.launch {
//            Log.i("latitude" ,latitude.toString())
//            val geocoder = Geocoder(getApplication(), Locale.SIMPLIFIED_CHINESE)
//            try {
//                var addresses: List<Address> = listOf()
//                geocoder.getFromLocation(latitude, longitude, 1){
//                     addresses = it
//                    Log.i("addressess", addresses.toString() )
//
//                }
//                Log.i("addresses", addresses.toString() )
//
//                if (addresses.isNotEmpty()) {
//                    _cityName.value = addresses[0].locality ?: "未知"
//                    Log.i("adressss",addresses[0].locality)
//                }
//            } catch (e: Exception){
//                e.printStackTrace()
//                _cityName.value = "未知de"
//            }
//        }
//    }
}