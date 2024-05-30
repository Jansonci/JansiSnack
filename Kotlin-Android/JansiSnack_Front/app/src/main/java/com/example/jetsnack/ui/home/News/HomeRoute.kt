package com.example.jetnews.ui.home

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.jetsnack.ui.home.HomeFeedScreen
import com.example.jetsnack.ui.home.News.HomeUiState
import com.example.jetsnack.ui.home.News.HomeViewModel
// 有时同一个屏幕可能会拥有不同的状态，而不同状态所呈现的ui内容可能存在较大差异，但本质上在不同状态之间切换不属于导航
// 这时在NavGraph和Screen之间引入Route就显得十分合理，注意：状态本身是由viewmodel管理的，所以viewmodel
// 只会注入进Route而不是Screen，在Route中会根据viewmodel中的ui状态来决定到底展现怎样的“Screen”。
@Composable
fun HomeRoute(
    homeViewModel: HomeViewModel,
    onPostSelected: (String) -> Unit,
    openDrawer: () -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    // UiState of the HomeScreen
    val uiState by homeViewModel.uiState.collectAsStateWithLifecycle()
    HomeRoute(
        uiState = uiState,
        onSelectPost = onPostSelected,
        onRefreshPosts = { homeViewModel.refreshPosts(true) },
        onErrorDismiss = { homeViewModel.errorShown(it) },
        openDrawer = openDrawer,
        snackbarHostState = snackbarHostState,
    )
}

/**
 * Displays the Home route.
 *
 * This composable is not coupled to any specific state management.
 *
 * @param uiState (state) the data to show on the screen
 * @param isExpandedScreen (state) whether the screen is expanded
 * @param onToggleFavorite (event) toggles favorite for a post
 * @param onSelectPost (event) indicate that a post was selected
 * @param onRefreshPosts (event) request a refresh of posts
 * @param onErrorDismiss (event) error message was shown
 * @param onInteractWithFeed (event) indicate that the feed was interacted with
 * @param onInteractWithArticleDetails (event) indicate that the article details were interacted
 * with
 * @param openDrawer (event) request opening the app drawer
 * @param snackbarHostState (state) state for the [Scaffold] component on this screen
 */
@Composable
fun HomeRoute(
    uiState: HomeUiState,
    onSelectPost: (String) -> Unit,
    onRefreshPosts: () -> Unit,
    onErrorDismiss: (Long) -> Unit,
    openDrawer: () -> Unit,
    snackbarHostState: SnackbarHostState,

    ) {
    // Construct the lazy list states for the list and the details outside of deciding which one to
    // show. This allows the associated state to survive beyond that decision, and therefore
    // we get to preserve the scroll throughout any changes to the content.
    when (uiState) {
        is HomeUiState.HasPosts -> uiState.postsFeed.allPosts
        is HomeUiState.NoPosts -> emptyList()
    }.associate { post ->
        key(post.id) {
            post.id to rememberLazyListState()
        }
    }
    val showTopAppBar by remember { mutableStateOf(true) }
            HomeFeedScreen(
                uiState = uiState,
                showTopAppBar = showTopAppBar,
                onSelectPost = onSelectPost,
                onRefreshPosts = onRefreshPosts,
                onErrorDismiss = onErrorDismiss,
                openDrawer = openDrawer,
                snackbarHostState = snackbarHostState,
            )
        }




