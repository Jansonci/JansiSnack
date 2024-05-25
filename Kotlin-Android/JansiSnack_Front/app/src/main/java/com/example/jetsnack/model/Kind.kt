package com.example.jetsnack.model

import androidx.room.PrimaryKey

open class Kind(
    @PrimaryKey val id: Long,
    val imageUrl: String,
    val name: String
)