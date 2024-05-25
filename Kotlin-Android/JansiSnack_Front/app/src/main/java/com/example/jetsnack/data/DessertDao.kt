package com.example.jetsnack.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.jetsnack.model.Category
import com.example.jetsnack.model.Dessert
import com.example.jetsnack.model.Lifestyle

@Dao
interface DessertDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAllDesserts(desserts: List<Dessert>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAllCategories(categories: List<Category>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAllLifestyles(lifestyles: List<Lifestyle>)

    @Query("SELECT * FROM desserts ORDER BY id")
    suspend fun getDesserts(): List<Dessert>?

    @Query("SELECT * FROM desserts WHERE id = :plantId")
    suspend fun getDessert(plantId: Long): Dessert?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateDessert(dessert: Dessert)

    @Query("SELECT * FROM desserts WHERE categoryName = :categoryName or lifestyle = :categoryName")
    suspend fun findDessertsByCategory(categoryName: String): List<Dessert>?
}