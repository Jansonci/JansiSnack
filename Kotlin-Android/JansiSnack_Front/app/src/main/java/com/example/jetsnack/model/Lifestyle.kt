package com.example.jetsnack.model;

import androidx.room.Entity

@Entity(tableName = "lifestyles")
class Lifestyle(
        id: Long,
        imageUrl: String,
        name: String,
        // 你可以在这里添加 Category 特有的属性
) : Kind(id, imageUrl, name) // 正确地调用 Kind 的构造器