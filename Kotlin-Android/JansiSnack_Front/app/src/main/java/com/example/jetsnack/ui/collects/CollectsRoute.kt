package com.example.jetnews.ui.collects

//@Composable
//fun CollectionsRoute(
//    collectsViewModel: CollectionsViewModel,
//    isExpandedScreen: Boolean,
//    openDrawer: () -> Unit,
//    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
//) {
////    val uiState by collectsViewModel.uiState.collectAsStateWithLifecycle()
//
//    CollectionsRoute(
////        uiState = uiState,
//        collectsViewModel = collectsViewModel,
////        isExpandedScreen = isExpandedScreen,
////        onToggleFavorite = { collectsViewModel.toggleFavourite(it) },
////        openDrawer = openDrawer,
////        onInteractWithFeed = { collectsViewModel.interactedWithFeed() },
//        snackbarHostState = snackbarHostState
//    )
//}
//
//@Composable
//fun CollectionsRoute(
////    uiState: CollectionsUiState1,
//    collectsViewModel: CollectionsViewModel,
////    isExpandedScreen: Boolean,
////    onToggleFavorite: (String) -> Unit,
////    openDrawer: () -> Unit,
////    onInteractWithFeed: () -> Unit,
//    snackbarHostState: SnackbarHostState
//) {
//    // UiState of the HomeScreen
////    val uiState by collectsViewModel.uiState.collectAsStateWithLifecycle()
//    val collectionContent = rememberCollections(collectsViewModel = collectsViewModel)
//
////    val homeScreenType = com.example.jetnews.ui.collects.getCollectionsScreenType(isExpandedScreen, uiState)
//
////    val articleDetailLazyListStates = postsList.associate { post ->
////        key(post.id) {
////            post.id to rememberLazyListState()
////        }
////    }
//
////    when (homeScreenType) {
////        CollectionsScreenType.List->
//    CollectsScreen(
//    collectionContent = collectionContent,
////    isExpandedScreen = isExpandedScreen,
////    openDrawer = openDrawer,
//    snackbarHostState = snackbarHostState
//)
//
////        CollectionsScreenType.ArticleDetails ->
////            ArticleScreen(
////                postId = "",
////                isExpandedScreen = isExpandedScreen,
////                onBack = onInteractWithFeed,
////                isFavorite = uiState.favorites.contains(uiState.selectedPost.id),
//////                onToggleFavorite = {
//////                    onToggleFavorite(uiState.selectedPost.id)
//////                },
////                lazyListState = articleDetailLazyListStates.getValue(
////                    uiState.selectedPost.id
////                )
////            )
//
//}

//private enum class CollectionsScreenType {
//    List,
////    ArticleDetails
//}

//@Composable
//private fun getCollectionsScreenType(
//    isExpandedScreen: Boolean,
//    uiState: CollectionsUiState1
//): CollectionsScreenType = when (uiState.isArticleOpen) {
//
//    false -> CollectionsScreenType.List
//
//    true -> CollectionsScreenType.ArticleDetails
//}

