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

package com.example.jetsnack.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "desserts",indices = [Index(value = ["categoryName", "lifestyle"])])
data class Dessert(
    @PrimaryKey val id: Long,
    val name: String,
    val categoryName: String, // 确保注解或数据处理反映了正确的字段名
    val lifestyle: String,
    val flavor: String,
    val price: Double,
    val difficulty: String,
    val prepTime: Int,
    val cookTime: Int, // 类型更改为Int
    val nutritionInfo: String,
    val occasion: String,
    val storageInfo: String,
    val shelfLife: Int,
    val rating: Double,
    val imageUrl: String,
    val region: String,
    val ingredients: List<String> // 显示指定类型转换器（如果不是全局的）
) {
    override fun toString() = name
}
