package com.example.jetsnack.di

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.jetsnack.api.DessertService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class NetworkModule {

    @RequiresApi(Build.VERSION_CODES.O)
    @Singleton
    @Provides
    fun provideUnsplashService(): DessertService {
        return DessertService.create()
    }
}
