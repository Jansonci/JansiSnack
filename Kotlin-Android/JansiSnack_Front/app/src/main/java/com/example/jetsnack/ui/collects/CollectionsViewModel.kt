//package com.example.jetnews.ui.collects
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.ViewModelProvider
//import androidx.lifecycle.viewModelScope
//import com.example.jetnews.data.Result
//import com.example.jetnews.data.collections.CollectionsRepository
//import com.example.jetnews.data.collections.impl.postsList
//import com.example.jetnews.data.successOr
//import com.example.jetsnack.R
//import com.example.jetsnack.model.Post
//import com.example.jetsnack.ui.utils.ErrorMessage
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.SharingStarted
//import kotlinx.coroutines.flow.map
//import kotlinx.coroutines.flow.stateIn
//import kotlinx.coroutines.flow.update
//import kotlinx.coroutines.launch
//import java.util.UUID
//
//data class CollectionsUiState1 (
//    val collections: List<Post> = emptyList(),
//    val loading: Boolean = false,
//    val isArticleOpen: Boolean = false,
//    val selectedPost: Post,
//    val favorites: Set<Post> = emptySet(),
//    )
//
//private data class CollectionsViewModelState1(
//    val collections: List<Post> = emptyList(),
//    val selectedPostId: String? = null, // TODO back selectedPostId in a SavedStateHandle
//    val isArticleOpen: Boolean = false,
//    val favorites: Set<Post> = emptySet(),
//    val isLoading: Boolean = false,
//    val errorMessages: List<ErrorMessage> = emptyList(),
//    val searchInput: String = "",
//) {
//
//    fun toUiState(): CollectionsUiState1 = CollectionsUiState1 (
//        collections,
//        isLoading,
//        isArticleOpen,
//        postsList.find {
//            it.id == selectedPostId
//        }?:postsList[0],
//        favorites
//    )
//}
//
//class CollectionsViewModel (
//    private val collectionsRepository: CollectionsRepository
//):ViewModel() {
////    private val _uiState = MutableStateFlow(CollectionsUiState1(loading = true))
//
//    val collections = collectionsRepository.getCollections()
//
//    private val viewModelState = MutableStateFlow(
//        CollectionsViewModelState1(
//            isLoading = true,
//            collections = collections.successOr(emptyList())
////            selectedPostId = preSelectedPostId,
////            isArticleOpen = preSelectedPostId != null
//        )
//    )
////    val uiState: StateFlow<CollectionsUiState1> = _uiState.asStateFlow()
//
//    val uiState = viewModelState
//    .map(CollectionsViewModelState1::toUiState)
//    .stateIn(
//        viewModelScope,
//        SharingStarted.Eagerly,
//        viewModelState.value.toUiState()
//    )
//
//
//    val selectedCollections = collectionsRepository.observeCollectionsSelected().stateIn(
//        viewModelScope,
//        SharingStarted.WhileSubscribed(5000),
//        emptySet()
//    ).value
//
//    init {
//        refreshPosts()
//        viewModelScope.launch {
//            collectionsRepository.observeCollectionsSelected().collect { collections ->
//                viewModelState.update { it.copy(collections = collections.toList()) }
//            }
//        }
//    }
//
//    fun toggleFavourite(postId: String) {
//        viewModelScope.launch {
//            collectionsRepository.toggleFavorite(postId)
//        }
//    }
//
//    fun interactedWithFeed() {
//        viewModelState.update {
//            it.copy(isArticleOpen = false)
//        }
//    }
//
//    fun selectArticle(postId: String) {
//        // Treat selecting a detail as simply interacting with it
//        interactedWithArticleDetails(postId)
//    }
//
//    fun interactedWithArticleDetails(postId: String) {
//        viewModelState.update {
//            it.copy(
//                selectedPostId = postId,
//                isArticleOpen = true
//            )
//        }
//    }
//
//    private fun refreshPosts() {
//        // Ui state is refreshing
//        viewModelState.update { it.copy(isLoading = true) }
//
//        viewModelScope.launch {
//            val result = collectionsRepository.getCollections()
//            viewModelState.update {
//                when (result) {
//                    is Result.Success -> it.copy(collections = result.data, isLoading = false)
//                    is Result.Error -> {
//                        println("Error")
//                        val errorMessages = it.errorMessages + ErrorMessage(
//                            id = UUID.randomUUID().mostSignificantBits,
//                            messageId = R.string.load_error
//                        )
//                        it.copy(errorMessages = errorMessages, isLoading = false)
//                    }
//                }
//            }
//        }
//    }
//
//    companion object {
//        fun provideFactory(
//            collectionsRepository: CollectionsRepository,
//        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
//            @Suppress("UNCHECKED_CAST")
//            override fun <T : ViewModel> create(modelClass: Class<T>): T {
//                return CollectionsViewModel(collectionsRepository) as T
//            }
//        }
//    }
//}