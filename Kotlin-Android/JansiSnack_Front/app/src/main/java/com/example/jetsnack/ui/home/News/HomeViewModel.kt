/*
 * Copyright 2021 The Android Open Source Project
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

package com.example.jetsnack.ui.home.News

import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.jetnews.data.posts.impl.post1
import com.example.jetsnack.R
import com.example.jetsnack.api.DessertService
import com.example.jetsnack.data.PostRepository
import com.example.jetsnack.model.Post
import com.example.jetsnack.model.PostsFeed
import com.example.jetsnack.ui.home.Profile.loggedInUser
import com.example.jetsnack.ui.home.overallMessageMap
import com.example.jetsnack.ui.utils.ErrorMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * UI state for the Home route.
 *
 * This is derived from [HomeViewModelState], but split into two possible subclasses to more
 * precisely represent the state available to render the UI.
 */
sealed interface HomeUiState {

    val isLoading: Boolean
    val errorMessages: List<ErrorMessage>
    val searchInput: String

    /**
     * There are no posts to render.
     *
     * This could either be because they are still loading or they failed to load, and we are
     * waiting to reload them.
     */
    data class NoPosts(
        override val isLoading: Boolean,
        override val errorMessages: List<ErrorMessage>,
        override val searchInput: String
    ) : HomeUiState

    /**
     * There are posts to render, as contained in [postsFeed].
     *
     * There is guaranteed to be a [selectedPost], which is one of the posts from [postsFeed].
     */
    data class HasPosts(
        val postsFeed: PostsFeed,
        val selectedPost: Post,
        val isArticleOpen: Boolean,
        val favorites: Set<String>,
        override val isLoading: Boolean,
        override val errorMessages: List<ErrorMessage>,
        override val searchInput: String
    ) : HomeUiState
}

/**
 * An internal representation of the Home route state, in a raw form
 */
private data class HomeViewModelState(
    val postsFeed: PostsFeed? = null,
    val selectedPostId: String? = null, // TODO back selectedPostId in a SavedStateHandle
    val isArticleOpen: Boolean = false,
    val favorites: Set<String> = emptySet(),
    val isLoading: Boolean = false,
    val errorMessages: List<ErrorMessage> = emptyList(),
    val searchInput: String = "",
) {

    /**
     * Converts this [HomeViewModelState] into a more strongly typed [HomeUiState] for driving
     * the ui.
     */
    fun toUiState(): HomeUiState =
        if (postsFeed == null) {
            HomeUiState.NoPosts(
                isLoading = isLoading,
                errorMessages = errorMessages,
                searchInput = searchInput
            )
        } else {
            HomeUiState.HasPosts(
                postsFeed = postsFeed,
                // Determine the selected post. This will be the post the user last selected.
                // If there is none (or that post isn't in the current feed), default to the
                // highlighted post
                selectedPost = postsFeed.allPosts.find {
                    it.id == selectedPostId
                } ?: postsFeed.highlightedPost,
                isArticleOpen = isArticleOpen,
                favorites = favorites,
                isLoading = isLoading,
                errorMessages = errorMessages,
                searchInput = searchInput
            )
        }
}

/**
 * ViewModel that handles the business logic of the Home screen
 */
class HomeViewModel(
    private val postRepository: PostRepository,
    preSelectedPostId: String?
) : ViewModel() {

    private val _postDetail = mutableStateOf<Post?>(null)
    val postDetail: State<Post?> = _postDetail
    var postt: Post = post1

    private val _posts = mutableStateOf<List<Post>?>(null)
    val posts: State<List<Post>?> = _posts

    private val newPostsFeed = derivedStateOf {
            PostsFeed(
                highlightedPost = posts.value?.get(3) ?: post1,
                recommendedPosts = listOf(posts.value?.get(0) ?: post1, posts.value?.get(1) ?: post1, posts.value?.get(3) ?: post1),
                popularPosts = listOf(
                    posts.value?.get(4) ?: post1,
                    posts.value?.get(0) ?: post1,
                    posts.value?.get(1) ?: post1
                ),
                recentPosts = listOf(
                    posts.value?.get(2) ?: post1,
                    posts.value?.get(3) ?: post1,
                    posts.value?.get(3) ?: post1
                )
            )
        }
    private val viewModelState = MutableStateFlow(
        HomeViewModelState(
            isLoading = true,
            selectedPostId = preSelectedPostId,
            isArticleOpen = preSelectedPostId != null
        )
    )

    // UI state exposed to the UI
    val uiState = viewModelState
        .map(HomeViewModelState::toUiState)
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value.toUiState()
        )

    init {
        refreshPosts()
    }

    /**
     * Refresh posts and update the UI state accordingly
     */
    fun refreshPosts(isRefresh: Boolean = false) {
        // Ui state is refreshing
        viewModelState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            if (isRefresh ||postRepository.getPosts()?.isEmpty() == true) {

                val response1 = DessertService.createFoeArticle().getAllArticles()
                val responsePosts = response1.body() ?: listOf()
                // 修改每个post对象的isCollected属性，并创建一个新的列表
                val updatedPosts = responsePosts.map { post ->
                    post.copy(isCollected = post.likes.contains(loggedInUser))
                }
               // 将更新后的列表赋值给_posts.value
                _posts.value = updatedPosts
                val posts = response1.body() ?: listOf()
                for (post in updatedPosts) {
                    overallMessageMap[post.id] = post.isCollected
                }

                postRepository.upsertAllPosts(posts)
                viewModelState.update {
                    when (response1.code()) {
                        200 -> {
                            it.copy(postsFeed = newPostsFeed.value, isLoading = false)
                        }
                        else -> {
                            val errorMessages = it.errorMessages + ErrorMessage(
                                id = UUID.randomUUID().mostSignificantBits,
                                messageId = R.string.load_error
                            )
                            it.copy(errorMessages = errorMessages, isLoading = false)
                        }
                    }
                }
            } else {
                _posts.value = postRepository.getPosts() ?: listOf()
                viewModelState.update { it.copy(postsFeed = newPostsFeed.value, isLoading = false) }
            }
        }
    }

    fun getPostDetail(postId: String, isRefresh: Boolean = false) {
        viewModelScope.launch {
            if (isRefresh || postRepository.getPost(postId) == null) {
                try {
                    val response = DessertService.createFoeArticle().getAnArticle(postId)
                        val post = response.body()
                    if (post != null) {
                        postRepository.insertPost(post)
                    }
                    if (response.isSuccessful) {
                        // 更新_postDetail状态
                        _postDetail.value = post
                        postt = post?: post1
                    } else {
                        // 处理错误情况，例如显示错误信息
                        // ...
                        val errorMessage = when (response.code()) {
                            404 -> "甜品详情未找到。"
                            500 -> "服务器错误，请稍后再试。"
                            else -> "未知错误，请稍后再试。"
                        }
                    }
                } catch (e: Exception) {
                    // 处理异常，例如网络错误
                    // ...
                }
            }
            else {
                _postDetail.value = postRepository.getPost(postId)
            }
        }
    }
    fun errorShown(errorId: Long) {
        viewModelState.update { currentUiState ->
            val errorMessages = currentUiState.errorMessages.filterNot { it.id == errorId }
            currentUiState.copy(errorMessages = errorMessages)
        }
    }

    /**
     * Factory for HomeViewModel that takes PostsRepository as a dependency
     */
    companion object {
        fun provideFactory(
               postRepository: PostRepository,
               preSelectedPostId: String? = null
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return HomeViewModel(postRepository, preSelectedPostId) as T
            }
        }
    }
}
