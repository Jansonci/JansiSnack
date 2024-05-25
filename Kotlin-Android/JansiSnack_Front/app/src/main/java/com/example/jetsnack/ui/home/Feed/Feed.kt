/*
 * Copyright 2020 The Android Open Source Project
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

package com.example.jetsnack.ui.home.Feed

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.jetnews.ui.home.HomeRoute
import com.example.jetsnack.R
import com.example.jetsnack.States
import com.example.jetsnack.model.DessertCollection
import com.example.jetsnack.model.Filter
import com.example.jetsnack.model.SnackRepo
import com.example.jetsnack.ui.components.FilterBar
import com.example.jetsnack.ui.components.JetsnackDivider
import com.example.jetsnack.ui.components.JetsnackScaffold
import com.example.jetsnack.ui.components.JetsnackSurface
import com.example.jetsnack.ui.components.SnackCollection1
import com.example.jetsnack.ui.home.HomeSections
import com.example.jetsnack.ui.home.JetsnackBottomBar
import com.example.jetsnack.ui.home.News.HomeViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState


enum class FeedSections(@StringRes val titleResId: Int) {
    Topics(R.string.feed_section),
    People(R.string.news_section),
    Publications(R.string.explore_section)
}

class FeedTabContent(val section: FeedSections, val content: @Composable () -> Unit)

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Feed(
    feedViewModel: FeedViewModel,
    homeViewModel: HomeViewModel,
    onSnackClick: (Long) -> Unit,
    onPostSelected: (String) -> Unit,
    onNavigateToRoute: (String) -> Unit,
    modifier: Modifier,
    prevSelection : String,
    ) {
    Log.i(
        "Dessertsees",
        feedViewModel.snackCollections.value.mapNotNull{ it.value }.size.toString()
    )
    val snackCollections = remember {
        feedViewModel.snackCollections.value.mapNotNull { it.value }
    }
    val filters = remember { SnackRepo.getFilters() }
    JetsnackScaffold(
        bottomBar = {
            JetsnackBottomBar(
                tabs = HomeSections.values(),
                currentRoute = HomeSections.FEED.route,
                navigateToRoute = onNavigateToRoute,
                prevSelection = prevSelection
            )
        },
        modifier = modifier
    ) { paddingValues ->
        Feed(
            feedViewModel,
            homeViewModel,
            snackCollections,
            filters,
            onSnackClick,
            onPostSelected,
            Modifier.padding(paddingValues)
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun Feed(
    feedViewModel: FeedViewModel,
    homeViewModel: HomeViewModel,
    snackCollections: List<DessertCollection>,
    filters: List<Filter>,
    onSnackClick: (Long) -> Unit,
    onPostSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    JetsnackSurface(modifier = modifier.fillMaxSize()) {
        Box {
            val tabContent = rememberFeedTabContent(feedViewModel, homeViewModel, onSnackClick, onPostSelected)
            val (currentSection, updateSection) = rememberSaveable {
                mutableStateOf(States.previousTab)
            }
            FeedScreenContent(
                currentSection,
                false,
                updateSection,
                tabContent,
            )
        }
    }
}

@Composable
private fun FeedScreenContent(
    currentProfileSections: FeedSections,
    isExpandedScreen: Boolean,
    updateProfileSections: (FeedSections) -> Unit,
    tabContent: List<FeedTabContent>,
    modifier: Modifier = Modifier
) {
    val selectedTabIndex = tabContent.indexOfFirst { it.section == currentProfileSections }
    Column(modifier.padding(top=20.dp)) {
        FeedTabRow(selectedTabIndex, updateProfileSections, tabContent, isExpandedScreen)
        Divider(
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
        )
        Box {
            // display the current tab content which is a @Composable () -> Unit
            tabContent[selectedTabIndex].content()
        }
    }
}

//private val tabContainerModifier = Modifier
//    .fillMaxWidth()
//    .wrapContentWidth(Alignment.CenterHorizontally)

@Composable
private fun FeedTabRow(
    selectedTabIndex: Int,
    updateProfileSections: (FeedSections) -> Unit,
    tabContent: List<FeedTabContent>,
    isExpandedScreen: Boolean
) {
    when (isExpandedScreen) {
        false -> {
            TabRow(
                selectedTabIndex = selectedTabIndex,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                FeedTabRowContent(selectedTabIndex, updateProfileSections, tabContent)
            }
        }
        true -> {
            ScrollableTabRow(
                selectedTabIndex = selectedTabIndex,
                contentColor = MaterialTheme.colorScheme.primary,
                edgePadding = 0.dp
            ) {
                FeedTabRowContent(
                    selectedTabIndex = selectedTabIndex,
                    updateProfileSections = updateProfileSections,
                    tabContent = tabContent,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun FeedTabRowContent(
    selectedTabIndex: Int,
    updateProfileSections: (FeedSections) -> Unit,
    tabContent: List<FeedTabContent>,
    modifier: Modifier = Modifier
) {
    tabContent.forEachIndexed { index, content ->
        val colorText = if (selectedTabIndex == index) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
        }
        Tab(
            selected = selectedTabIndex == index,
            onClick = { updateProfileSections(content.section)
                      States.previousTab = content.section},
            modifier = Modifier.heightIn(min = 48.dp)
        ) {
            Text(
                text = stringResource(id = content.section.titleResId),
                color = colorText,
                style = MaterialTheme.typography.titleMedium,
                modifier = modifier.paddingFromBaseline(top = 20.dp)
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun rememberFeedTabContent(
    feedViewModel: FeedViewModel,
    homeViewModel: HomeViewModel,
    onSnackClick: (Long) -> Unit,
    onPostSelected: (String) -> Unit,
    ): List<FeedTabContent> {
    val topicsProfileSections = FeedTabContent(FeedSections.Topics) {
        SnackCollectionList(feedViewModel.snackCollections.value.mapNotNull { it.value }, feedViewModel.filters, onSnackClick, feedViewModel)
    }

    val peopleProfileSections = FeedTabContent(FeedSections.People) {
        HomeRoute(homeViewModel = homeViewModel, onPostSelected, isExpandedScreen = false, openDrawer = { /*TODO*/ })
    }

    val publicationProfileSections = FeedTabContent(FeedSections.Publications) {

    }

    return listOf(topicsProfileSections, peopleProfileSections, publicationProfileSections)
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun SnackCollectionList(
    snackCollections: List<DessertCollection>,
    filters: List<Filter>,
    onSnackClick: (Long) -> Unit,
    feedViewModel: FeedViewModel,
    modifier: Modifier = Modifier,
    ) {
    var filtersVisible by rememberSaveable { mutableStateOf(false) }
    val listState = rememberLazyListState()
    // 当组件首次加载时，尝试恢复滑动位置
    LaunchedEffect(key1 = "restoreScrollPosition") {
        listState.scrollToItem(States.mainScrollIndex, States.mainScrollOffset)
    }

        // 如果dessertDetail不是null，显示甜品详情
        SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshing = false),
            onRefresh = { feedViewModel.refreshData(isRefresh = true) },
        ) {
            Box(modifier) {
                LazyColumn(state = listState) {

                    item {
                        Spacer(
                            Modifier.windowInsetsTopHeight(
                                WindowInsets.statusBars.add(WindowInsets(top = -15.dp))
                            )
                        )
                        FilterBar(filters, onShowFilters = { filtersVisible = true })
                    }
                    itemsIndexed(snackCollections) { index, snackCollection ->
                        if (index > 0) {
                            JetsnackDivider(thickness = 2.dp)
                        }

                        SnackCollection1(
                            snackCollection = snackCollection,
                            onSnackClick = onSnackClick,
                            index = index
                        )
                    }
                }
            }
        }
    AnimatedVisibility(
        visible = filtersVisible,
        enter = slideInVertically() + expandVertically(
            expandFrom = Alignment.Top
        ) + fadeIn(initialAlpha = 0.3f),
        exit = slideOutVertically() + shrinkVertically() + fadeOut()
    ) {
        FilterScreen(
            onDismiss = { filtersVisible = false }
        )
    }

    DisposableEffect(key1 = "saveScrollPosition") {
        onDispose {
            States.mainScrollIndex = listState.firstVisibleItemIndex
            States.mainScrollOffset = listState.firstVisibleItemScrollOffset
        }
    }
}

