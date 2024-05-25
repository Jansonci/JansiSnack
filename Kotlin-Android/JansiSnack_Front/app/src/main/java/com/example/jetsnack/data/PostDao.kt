package com.example.jetsnack.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.jetsnack.model.Post

@Dao
interface PostDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAllPosts(posts: List<Post>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdatePost(post: Post)
    
    @Query("SELECT * FROM posts ORDER BY id")
    suspend fun getPosts(): List<Post>?

    @Query("SELECT * FROM posts WHERE id = :plantId")
    suspend fun getPost(plantId: String): Post?

}