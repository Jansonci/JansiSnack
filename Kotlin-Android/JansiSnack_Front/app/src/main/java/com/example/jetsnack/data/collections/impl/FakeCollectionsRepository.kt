package com.example.jetnews.data.collections.impl

import com.example.jetnews.data.Result
import com.example.jetnews.data.collections.CollectionsRepository
import com.example.jetnews.data.posts.impl.post1
import com.example.jetnews.data.posts.impl.post2
import com.example.jetnews.data.posts.impl.post3
import com.example.jetnews.data.posts.impl.post4
import com.example.jetnews.data.posts.impl.post5
import com.example.jetsnack.model.Post
import com.example.jetsnack.ui.utils.addOrRemove
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

val postsList: List<Post> = listOf(post1, post2, post3, post4, post5)

val favorites = MutableStateFlow(postsList.filter { it.isCollected == true }.toSet())

class FakeCollectionsRepository: CollectionsRepository {

//    private val favorites = MutableStateFlow(postsList.filter { it.isCollected == true }.toSet())

    override fun getCollections(): Result<List<Post>> {
        val collections = postsList.filter { it.isCollected == true }
        if (collections == null) {
            return  Result.Error(IllegalArgumentException("No Collections"))
        } else {
            return Result.Success(collections)
        }
    }

    private val selectedCollections = MutableStateFlow(setOf<Post>())

    override fun observeCollectionsSelected(): Flow<Set<Post>> = favorites

    override suspend fun toggleFavorite(postId: String) {
        favorites.update {
            it.addOrRemove(postsList.filter { it.id == postId }.first())
        }
        postsList.filter { it.id == postId }.forEach{
            post -> post.isCollected = !post.isCollected
        }
    }
}