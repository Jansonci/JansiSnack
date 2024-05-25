package com.example.jetsnack

import android.app.Application
import androidx.work.Configuration
import androidx.work.WorkManager
import com.baidu.location.LocationClient
import com.baidu.mapapi.CoordType
import com.baidu.mapapi.SDKInitializer
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class JetsnackApp : Application() {

    override fun onCreate() {
        super.onCreate()
        val configuration = Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .build()
        WorkManager.initialize(this, configuration)
        SDKInitializer.setAgreePrivacy(this,true);
        SDKInitializer.initialize(this);
        SDKInitializer.setCoordType(CoordType.BD09LL);
        LocationClient.setAgreePrivacy(true);
    }
}