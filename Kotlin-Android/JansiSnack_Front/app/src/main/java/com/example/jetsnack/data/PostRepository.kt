package com.example.jetsnack.data

import com.example.jetsnack.model.Post
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PostRepository@Inject constructor(private val postDao: PostDao) {

    suspend fun getPosts()= withContext(Dispatchers.IO) {
        postDao.getPosts()
    }

    suspend fun getPost(postId: String) = withContext(Dispatchers.IO) {
        postDao.getPost(postId)
    }
    
    suspend fun insertPost(post: Post) = withContext(Dispatchers.IO) {
        postDao.insertOrUpdatePost(post)
    }

    suspend fun upsertAllPosts(posts: List<Post>) = withContext(Dispatchers.IO) {
        postDao.upsertAllPosts(posts)
    }

    companion object {
        // For Singleton instantiation
        @Volatile private var instance: PostRepository? = null

        fun getInstance(postDao: PostDao) =
            instance ?: synchronized(this) {
                instance ?: PostRepository(postDao).also { instance = it }
            }
    }
}