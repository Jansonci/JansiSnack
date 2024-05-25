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

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.jetsnack.model.Category
import com.example.jetsnack.model.Dessert
import com.example.jetsnack.model.Lifestyle
import com.example.jetsnack.model.Post


/**
 * The Room database for this app
 */
@Database(entities = [Dessert::class, Category::class, Lifestyle::class, Post::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dessertIngredientsDao(): DessertIngredientsDao
    abstract fun dessertDao(): DessertDao
    abstract fun postDao(): PostDao

    companion object {

        // For Singleton instantiation
        @Volatile private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }
        private fun buildDatabase(context: Context): AppDatabase {
            return Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
                .addCallback(
                    object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // 由于数据库不持久化，可能不需要在这里填充初始数据，
                            // 除非你希望每次应用启动时都重新填充内存数据库。
                            // 种子数据填充逻辑可以在这里初始化。
//                            val request = OneTimeWorkRequestBuilder<SeedDatabaseWorker1>()
//                                    .setInputData(workDataOf(KEY_FILENAME to PLANT_DATA_FILENAME))
//                                    .build()
////                            WorkManager.getInstance(context).enqueue(request)
//                            val request = OneTimeWorkRequestBuilder<SeedDatabaseWorker1>().build()
//                            WorkManager.getInstance(context).enqueue(request)
                        }
                    }
                )
                .build()
        }
    }
}
