package com.example.jetnews.data.collections

import com.example.jetnews.data.Result
import com.example.jetnews.data.interests.TopicSelection
import com.example.jetnews.data.posts.impl.posts
import com.example.jetsnack.model.Post
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

interface CollectionsRepository {

    /**
     * Get JetNews posts.
     */
    fun getCollections(): Result<List<Post>>

    fun observeCollectionsSelected(): Flow<Set<Post>>

    suspend fun toggleFavorite(postId: String)

}