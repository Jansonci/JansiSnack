/*
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.jetsnack.data

import com.example.jetsnack.model.Category
import com.example.jetsnack.model.Dessert
import com.example.jetsnack.model.Lifestyle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository module for handling data operations.
 *
 * Collecting from the Flows in [PlantDao] is main-safe.  Room supports Coroutines and moves the
 * query execution off of the main thread.
 */
@Singleton
class DessertRepository @Inject constructor(private val dessertDao: DessertDao) {

    suspend fun getDesserts() = withContext(Dispatchers.IO) {
        dessertDao.getDesserts()
    }

    suspend fun getDessert(dessertId: Long) = withContext(Dispatchers.IO) {
        dessertDao.getDessert(dessertId)
    }

    suspend fun insertDessert(dessert: Dessert) = withContext(Dispatchers.IO) {
        dessertDao.insertOrUpdateDessert(dessert)
    }

    suspend fun upsertAllDesserts(desserts: List<Dessert>) = withContext(Dispatchers.IO) {
        dessertDao.upsertAllDesserts(desserts)
    }

    suspend fun upsertAllCategories(categories: List<Category>) = withContext(Dispatchers.IO) {
        dessertDao.upsertAllCategories(categories)
    }

    suspend fun upsertAllLifestyles(lifestyles: List<Lifestyle>) = withContext(Dispatchers.IO) {
        dessertDao.upsertAllLifestyles(lifestyles)
    }

    suspend fun findDessertsByCategory(categoryName: String) = withContext(Dispatchers.IO) {
        dessertDao.findDessertsByCategory(categoryName)
    }



//    fun getPlantsWithGrowZoneNumber(growZoneNumber: Int) =
//        plantDao.getPlantsWithGrowZoneNumber(growZoneNumber)

//    companion object {
//
//        // For Singleton instantiation
//        @Volatile private var instance: DessertRepository? = null
//
//        fun getInstance(dessertDao: DessertDao) =
//            instance ?: synchronized(this) {
//                instance ?: DessertRepository(dessertDao).also { instance = it }
//            }
//    }
}
