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

package com.example.jetsnack.ui.home.Profile

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.constrainWidth
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.jetnews.ui.collects.TabWithCollections
import com.example.jetnews.ui.collects.TabWithOrders
import com.example.jetsnack.R
import com.example.jetsnack.api.WebSocketViewModel
import com.example.jetsnack.model.Dessert
import com.example.jetsnack.model.Filter
import com.example.jetsnack.model.Snack
import com.example.jetsnack.model.User
import com.example.jetsnack.ui.components.HighlightSnackItem
import com.example.jetsnack.ui.components.JetsnackScaffold
import com.example.jetsnack.ui.home.HomeSections
import com.example.jetsnack.ui.home.JetsnackBottomBar
import com.example.jetsnack.ui.theme.JetsnackTheme
import com.example.jetsnack.ui.theme.Shapes
import com.example.jetsnack.ui.theme.Typography1
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.google.gson.JsonNull
import kotlin.math.max

enum class ProfileSections(@StringRes val titleResId: Int) {
    Topics(R.string.interests_section),
    People(R.string.collect_section_topics),
    Publications(R.string.order_section)
}

class ProfileTabContent(val section: ProfileSections, val content: @Composable () -> Unit)

private val HighlightCardWidth = 170.dp
private val HighlightCardPadding = 16.dp
private val Density.cardWidthWithPaddingPx
    get() = (HighlightCardWidth + HighlightCardPadding).toPx()

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Profile(
    user: User,
    profileViewModel: ProfileViewModel,
    onNavigateToRoute: (String) -> Unit,
    onNavigateToOrder: (Long, Boolean) -> Unit,
    onPostSelected: (String) -> Unit,
    prevSelection : String,
    onNavigateToEdit:() ->Unit,
    modifier: Modifier = Modifier,
    ) {
    val webSocketViewModel1: WebSocketViewModel = viewModel()
    var showDialog by remember { mutableStateOf(false) }

    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing = false),
        onRefresh = { profileViewModel.refreshData() },
    ) {
        JetsnackScaffold(
            bottomBar = {
                JetsnackBottomBar(
                    tabs = HomeSections.entries.toTypedArray(),
                    currentRoute = HomeSections.PROFILE.route,
                    navigateToRoute = onNavigateToRoute,
                    prevSelection = prevSelection
                )
            },
            modifier = modifier
        ) { paddingValues ->
            LazyColumn(modifier = Modifier.height(1200.dp), state = rememberLazyListState()) {
                item {
                    // 背景图片
                    Image(
                        painter = painterResource(id = R.drawable.mancity),
                        contentDescription = "背景图片",
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1080f / 607f)
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onLongPress = {
                                        showDialog = true // 长按时显示放大图片
                                    }
                                )
                            }, // 设置宽高比为16:9
                        contentScale = ContentScale.Crop
                    )
                    if (showDialog) {
                        Dialog(onDismissRequest = { showDialog = false }) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(1200.dp)
                                    .background(Color.Black.copy(alpha = 1f))
                                    .clickable { showDialog = false }, // 点击背景退出放大视图
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.mancity),
                                    contentDescription = "背景图片",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(1080f / 607f)
                                        .pointerInput(Unit) {
                                            detectTapGestures(
                                                onLongPress = {
                                                    showDialog = true // 长按时显示放大图片
                                                }
                                            )
                                        }, // 设置宽高比为16:9
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }
                    Text(
                        profileViewModel.userInfo.value?.asJsonObject?.get("username")?.asString ?: "",
                        style = Typography1.h4,
                        modifier = Modifier.offset(x = 190.dp, y = 20.dp)
                    )
                    Text(
                        profileViewModel.userInfo.value?.asJsonObject?.get("motto")
                            ?.takeIf { it !is JsonNull }?.asString ?: "介绍一下自己吧！",
                        style = Typography1.h1,
                        modifier = Modifier
                            .offset(x = 190.dp, y = 35.dp)
                            .width(170.dp),
                        maxLines = 1,           // 限制为一行显示
                        overflow = TextOverflow.Ellipsis
                    )

                    // 头像
                    Photo(profileViewModel, modifier = Modifier.offset(x = 25.dp, y = -90.dp))
                    Edit(onNavigateToEdit, modifier = Modifier.offset(x = 235.dp, y = -85.dp))
                    Row(
                        modifier = Modifier
                            .size(width = 240.dp, height = 60.dp)
                            .offset(y = -140.dp)
                            .padding(horizontal = 16.dp)
                    ) {
                        // Row的内容
                        Column(
                            modifier = Modifier
                                .weight(0.7f)
                                .fillMaxHeight()
                        ) {
                            Text(
                                text = profileViewModel.userInfo.value?.asJsonObject?.get("liked")
                                    ?.takeIf { it !is JsonNull }?.asString ?: "0",
                                modifier = Modifier
                                    .weight(0.5f)
                                    .align(Alignment.CenterHorizontally)
                            )
                            Text(
                                text = "获赞",
                                style = TextStyle(fontSize = 15.sp),
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(5.dp)
                                    .offset(x = 5.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp)) // 文本之间的间隔
                        Divider(
                            color = Color.Gray,
                            modifier = Modifier
                                .height(30.dp) // 设置分割线的高度
                                .width(1.dp) // 设置分割线的宽度，实现垂直效果
                                .offset(y = 10.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp)) // 文本之间的间隔
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                        ) {
                            Text(
                                text = profileViewModel.userInfo.value?.asJsonObject?.get("follower")
                                    ?.takeIf { it !is JsonNull }?.asString ?: "0",
                                modifier = Modifier
                                    .weight(0.5f)
                                    .align(Alignment.CenterHorizontally)
                            )
                            Text(
                                text = "粉丝",
                                style = TextStyle(fontSize = 15.sp),
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(5.dp)
                                    .offset(x = 13.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp)) // 文本之间的间隔
                        Divider(
                            color = Color.Gray,
                            modifier = Modifier
                                .height(30.dp) // 设置分割线的高度
                                .width(1.dp) // 设置分割线的宽度，实现垂直效果
                                .offset(y = 10.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp)) // 文本之间的间隔
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                        ) {
                            Text(
                                text = profileViewModel.userInfo.value?.asJsonObject?.get("following")
                                    ?.takeIf { it !is JsonNull }?.asString ?: "0",
                                modifier = Modifier
                                    .weight(0.5f)
                                    .align(Alignment.CenterHorizontally)
                            )
                            Text(
                                text = "关注",
                                style = TextStyle(fontSize = 15.sp),
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(5.dp)
                                    .offset(x = 13.dp)
                            )
                        }
                    }
                    LazyRow(modifier = Modifier.offset(x = 20.dp, y = -135.dp)) {
                        items(listOf("gender", "age", "locale", "job").map { info ->
                            profileViewModel.userInfo.value?.asJsonObject?.get(info)
                                ?.takeIf { it !is JsonNull }?.asString ?: ""
                        }
                        ) { info ->
                            if(info != "") {
                            Info(text = info)
                        }
                        }
                    }
                    Divider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .offset(y = (-120).dp),
                        thickness = 2.dp

                    )
                    FilterBar(filters = user.preferences,
                        {},
                        modifier = Modifier.offset(y = -120.dp))

                    Divider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .offset(y = -120.dp),
                        thickness = 2.dp

                    )
                    val tabContent = rememberTabContent(
                        user,
                        onPostSelected,
                        onNavigateToOrder,
                        profileViewModel,
                        webSocketViewModel1
                    )
                    val (currentSection, updateSection) = rememberSaveable {
                        mutableStateOf(tabContent.get(1).section)
                    }
                    // 下部内容区域
                    InterestScreenContent(
                        currentSection,
                        false,
                        updateSection,
                        tabContent,
                        onPostSelected,
                        modifier = Modifier.offset(y = -120.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun InterestScreenContent(
    currentFeedSections: ProfileSections,
    isExpandedScreen: Boolean,
    updateFeedSections: (ProfileSections) -> Unit,
    tabContent: List<ProfileTabContent>,
    onPostSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedTabIndex = tabContent.indexOfFirst { it.section == currentFeedSections }
    Column(modifier) {
        InterestsTabRow(selectedTabIndex, updateFeedSections, tabContent, isExpandedScreen, onPostSelected)
        androidx.compose.material3.Divider(
            color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
        )
        Box {
            // display the current tab content which is a @Composable () -> Unit
            tabContent[selectedTabIndex].content()
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun rememberTabContent(user: User, onPostSelected: (String) -> Unit, onNavigateToOrder: (Long, Boolean) -> Unit,
                       profileViewModel: ProfileViewModel, webSocketViewModel: WebSocketViewModel): List<ProfileTabContent> {
    val topicsFeedSections = ProfileTabContent(ProfileSections.Topics) {
        DesertsCollections(gardenPlants = user.collections, onPlantClick = {})
    }

    val peopleFeedSections = ProfileTabContent(ProfileSections.People) {
        TabWithCollections(posts = profileViewModel.collections.value?:listOf(), onPostSelected, webSocketViewModel)
    }

    val publicationFeedSections = ProfileTabContent(ProfileSections.Publications) {
        TabWithOrders(onNavigateToOrder, orderForChecks = profileViewModel.orderForChecks.value?: listOf())
    }
    return listOf(topicsFeedSections, peopleFeedSections, publicationFeedSections)
}

@Composable
fun FilterBar(
    filters: List<Filter>,
    onShowFilters: () -> Unit,
    modifier: Modifier
) {
    val listState = rememberLazyListState()
    LazyRow(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(start = 12.dp, end = 8.dp),
        modifier = modifier.heightIn(min = 56.dp),
        state = listState
    ) {
        item { Text(text = "Preference:") }
        items(filters) { filter ->
            com.example.jetsnack.ui.components.FilterChip(
                filter = filter,
                shape = Shapes.small
            )
        }
    }
}


@Composable
private fun DesertsCollections(
    gardenPlants: List<Snack>,
    onPlantClick: (Dessert) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(modifier = Modifier.padding(start = 15.dp, top = 15.dp)) {
        items(
            items = gardenPlants,
        ) {snack ->
            HighlightSnackItem(snack = snack,
                onSnackClick = {  },
                index = 1,
                gradient = JetsnackTheme.colors.gradient6_1,
                scrollProvider = {1f})
            Spacer(modifier = Modifier.width(20.dp))
        }
    }
}
/**
 * TabRow for the InterestsScreen
 */
@Composable
private fun InterestsTabRow(
    selectedTabIndex: Int,
    updateFeedSections: (ProfileSections) -> Unit,
    tabContent: List<ProfileTabContent>,
    isExpandedScreen: Boolean,
    onPostSelected: (String) -> Unit,
    ) {
    when (isExpandedScreen) {
        false -> {
            TabRow(
                selectedTabIndex = selectedTabIndex,
                contentColor = androidx.compose.material3.MaterialTheme.colorScheme.primary
            ) {
                InterestsTabRowContent(selectedTabIndex, updateFeedSections, tabContent)
            }
        }
        true -> {
            ScrollableTabRow(
                selectedTabIndex = selectedTabIndex,
                contentColor = androidx.compose.material3.MaterialTheme.colorScheme.primary,
                edgePadding = 0.dp
            ) {
                InterestsTabRowContent(
                    selectedTabIndex = selectedTabIndex,
                    updateFeedSections = updateFeedSections,
                    tabContent = tabContent,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun InterestsTabRowContent(
    selectedTabIndex: Int,
    updateFeedSections: (ProfileSections) -> Unit,
    tabContent: List<ProfileTabContent>,
    modifier: Modifier = Modifier
) {
    tabContent.forEachIndexed { index, content ->
        val colorText = if (selectedTabIndex == index) {
            androidx.compose.material3.MaterialTheme.colorScheme.primary
        } else {
            androidx.compose.material3.MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
        }
        Tab(
            selected = selectedTabIndex == index,
            onClick = { updateFeedSections(content.section) },
            modifier = Modifier.heightIn(min = 48.dp)
        ) {
            androidx.compose.material3.Text(
                text = stringResource(id = content.section.titleResId),
                color = colorText,
                style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
                modifier = modifier.paddingFromBaseline(top = 20.dp)
            )
        }
    }
}

/**
 * Custom layout for the Interests screen that places items on the screen given the available size.
 *
 * For example: Given a list of items (A, B, C, D, E) and a screen size that allows 2 columns,
 * the items will be displayed on the screen as follows:
 *     A B
 *     C D
 *     E
 */
@Composable
private fun InterestsAdaptiveContentLayout(
    modifier: Modifier = Modifier,
    topPadding: Dp = 0.dp,
    itemSpacing: Dp = 4.dp,
    itemMaxWidth: Dp = 450.dp,
    multipleColumnsBreakPoint: Dp = 600.dp,
    content: @Composable () -> Unit,
) {
    Layout(modifier = modifier, content = content) { measurables, outerConstraints ->
        // Convert parameters to Px. Safe to do as `Layout` measure block runs in a `Density` scope
        val multipleColumnsBreakPointPx = multipleColumnsBreakPoint.roundToPx()
        val topPaddingPx = topPadding.roundToPx()
        val itemSpacingPx = itemSpacing.roundToPx()
        val itemMaxWidthPx = itemMaxWidth.roundToPx()

        // Number of columns to display on the screen. This is hardcoded to 2 due to
        // the design mocks, but this logic could change in the future.
        val columns = if (outerConstraints.maxWidth < multipleColumnsBreakPointPx) 1 else 2
        // Max width for each item taking into account available space, spacing and `itemMaxWidth`
        val itemWidth = if (columns == 1) {
            outerConstraints.maxWidth
        } else {
            val maxWidthWithSpaces = outerConstraints.maxWidth - (columns - 1) * itemSpacingPx
            (maxWidthWithSpaces / columns).coerceIn(0, itemMaxWidthPx)
        }
        val itemConstraints = outerConstraints.copy(maxWidth = itemWidth)

        // Keep track of the height of each row to calculate the layout's final size
        val rowHeights = IntArray(measurables.size / columns + 1)
        // Measure elements with their maximum width and keep track of the height
        val placeables = measurables.mapIndexed { index, measureable ->
            val placeable = measureable.measure(itemConstraints)
            // Update the height for each row
            val row = index.floorDiv(columns)
            rowHeights[row] = max(rowHeights[row], placeable.height)
            placeable
        }

        // Calculate maxHeight of the Interests layout. Heights of the row + top padding
        val layoutHeight = topPaddingPx + rowHeights.sum()
        // Calculate maxWidth of the Interests layout
        val layoutWidth = itemWidth * columns + (itemSpacingPx * (columns - 1))

        // Lay out given the max width and height
        layout(
            width = outerConstraints.constrainWidth(layoutWidth),
            height = outerConstraints.constrainHeight(layoutHeight)
        ) {
            // Track the y co-ord we have placed children up to
            var yPosition = topPaddingPx
            // Split placeables in lists that don't exceed the number of columns
            // and place them taking into account their width and spacing
            placeables.chunked(columns).forEachIndexed { rowIndex, row ->
                var xPosition = 0
                row.forEach { placeable ->
                    placeable.placeRelative(x = xPosition, y = yPosition)
                    xPosition += placeable.width + itemSpacingPx
                }
                yPosition += rowHeights[rowIndex]
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Photo( profileViewModel: ProfileViewModel,
    modifier: Modifier = Modifier) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(profileViewModel.userInfo.value?.asJsonObject?.get("avatar")?.takeIf { it !is JsonNull }?.asString ?:"",)
                .crossfade(true)
                .build(),
            contentDescription = null, // decorative
            placeholder = painterResource(R.drawable.placeholder),
            modifier = modifier
                .size(140.dp, 140.dp)
                .clip(CircleShape)
                .clip(androidx.compose.material3.MaterialTheme.shapes.small)
        )
}

@Composable
fun Edit(
    onNavigateToEdit: () -> Unit = {},
    modifier: Modifier = Modifier
) {
        Button(onClick =onNavigateToEdit, modifier = modifier
            .offset(y = 6.dp)
            .height(70.dp)
            .width(135.dp)
            .padding(10.dp),
            shape = Shapes.small,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White, // 按钮的背景色
                contentColor = JetsnackTheme.colors.textSecondary // 文本和图标等内容的颜色
            ),            border = BorderStroke(2.dp, Color.DarkGray)
        ) {
                        Text(text = "编辑资料",
                            fontFamily = FontFamily(Font(R.font.montserrat_semibold)),
                            style = MaterialTheme.typography.subtitle1,
                            color = JetsnackTheme.colors.textSecondary,
                            modifier = Modifier.align(Alignment.CenterVertically))
        }
}

@Composable
fun Info(
    text: String,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        Row {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(color = Color.LightGray.copy(alpha = 0.3f))
                    .padding(horizontal = 5.dp, vertical = 2.dp)
            ) {
                val correctedFontSize = if(text.toIntOrNull() != null) 14.sp else 13.sp
                Text(
                    text = text,
                    fontSize = correctedFontSize,
                    color = Color.DarkGray.copy(alpha = 0.5f),
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
        }
    }
}


