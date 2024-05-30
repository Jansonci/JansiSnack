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

package com.example.jetsnack.ui.home

import LoginScreen
import android.os.Build
import androidx.annotation.FloatRange
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.core.os.ConfigurationCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.jetsnack.R
import com.example.jetsnack.data.PostDao
import com.example.jetsnack.data.PostRepository
import com.example.jetsnack.model.Kygo
import com.example.jetsnack.ui.components.JetsnackSurface
import com.example.jetsnack.ui.home.Cart.Cart
import com.example.jetsnack.ui.home.Feed.Feed
import com.example.jetsnack.ui.home.Feed.FeedViewModel
import com.example.jetsnack.ui.home.News.HomeViewModel
import com.example.jetsnack.ui.home.Profile.Profile
import com.example.jetsnack.ui.home.Profile.ProfileViewModel
import com.example.jetsnack.ui.home.Search.Search
import com.example.jetsnack.ui.navigation.MainDestinations
import com.example.jetsnack.ui.theme.JetsnackTheme
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
fun NavGraphBuilder.addHomeGraph(
    onSnackSelected: (Long, NavBackStackEntry) -> Unit,
    onPostSelected: (String, NavBackStackEntry) -> Unit,
    onCategorySelected: (String, NavBackStackEntry) -> Unit,
    onNavigateToRoute: (String) -> Unit,
    onNavigateToEdit: ()-> Unit,
    onNavigateToOrder: (Long, Boolean) -> Unit,
    feedViewModel: FeedViewModel,
    postDao: PostDao,
    modifier: Modifier = Modifier
) {
    composable(MainDestinations.LOGIN_ROUTE){
        LoginScreen { onNavigateToRoute(HomeSections.FEED.route + "/") }
    }

    composable(HomeSections.FEED.route+"/") { from ->
        val homeViewModel: HomeViewModel = viewModel(
            factory = HomeViewModel.provideFactory(
                postRepository = PostRepository.getInstance(postDao)            )
        )
        Feed(onSnackClick = { id -> onSnackSelected(id, from) },
            onPostSelected = { id -> onPostSelected(id, from) },
            onNavigateToRoute = onNavigateToRoute,  modifier = modifier, prevSelection = "",
            feedViewModel = feedViewModel, homeViewModel = homeViewModel,
        )
    }

    composable(HomeSections.FEED.route + "/{prevSelection}") { from ->
        val homeViewModel: HomeViewModel = viewModel(
            factory = HomeViewModel.provideFactory(postRepository = PostRepository.getInstance(postDao))
        )
        Feed(onSnackClick = { id -> onSnackSelected(id, from) },
            onPostSelected = { id -> onPostSelected(id, from) },
            onNavigateToRoute = onNavigateToRoute,  modifier = modifier, prevSelection =  from.arguments?.getString("prevSelection").toString(),
            feedViewModel = feedViewModel, homeViewModel = homeViewModel
        )
    }

    composable(HomeSections.CART.route + "/{prevSelection}") { from ->
        Cart(
            onSnackClick = { id -> onSnackSelected(id, from) },
            onNavigateToRoute = onNavigateToRoute,
            onNavigateToOrder = onNavigateToOrder,
            prevSelection =  from.arguments?.getString("prevSelection").toString(),
            modifier
        )
    }

    composable(HomeSections.SEARCH.route + "/{prevSelection}") { from ->
        Search(feedViewModel, onSnackClick = { id -> onSnackSelected(id, from) }, onCategoryClick = { name -> onCategorySelected(name, from)} ,
            onNavigateToRoute, from.arguments?.getString("prevSelection").toString(), modifier)
    }

    composable(HomeSections.PROFILE.route + "/{prevSelection}") {from ->
        val profileViewModel:ProfileViewModel = viewModel(
            from,
            factory = ProfileViewModel.provideFactory(
            )
        )
        Profile(Kygo, profileViewModel, onNavigateToRoute, onNavigateToOrder, { id -> onPostSelected(id, from) }, from.arguments?.getString("prevSelection").toString(),
            onNavigateToEdit, modifier)
    }
}

enum class HomeSections(
    @StringRes val title: Int,
    val icon: ImageVector,
    val route: String
) {
    FEED(R.string.home_feed, Icons.Outlined.Home, "home/feed"),
    SEARCH(R.string.home_search, Icons.Outlined.Search, "home/search"),
    CART(R.string.home_cart, Icons.Outlined.ShoppingCart, "home/cart"),
    PROFILE(R.string.home_profile, Icons.Outlined.AccountCircle, "home/profile")
}

@Composable
fun JetsnackBottomBar(
    tabs: Array<HomeSections>,
    currentRoute: String,
    navigateToRoute: (String) -> Unit,
    color: Color = JetsnackTheme.colors.iconPrimary,
    contentColor: Color = JetsnackTheme.colors.iconInteractive,
    prevSelection : String
    ) {
    val routes = remember { tabs.map { it.route } }
    val currentSection = tabs.first { it.route == currentRoute }

    JetsnackSurface(
        color = color,
        contentColor = contentColor
    ) {
        val springSpec = SpringSpec<Float>(
            // Determined experimentally
            stiffness = 50f,
            dampingRatio = 0.8f
        )
        JetsnackBottomNavLayout(
            selectedIndex = currentSection.ordinal,
            itemCount = routes.size,
            indicator = { JetsnackBottomNavIndicator() },
            animSpec = springSpec,
            modifier = Modifier.navigationBarsPadding(),
            prevSelection = prevSelection
        ) {
            val configuration = LocalConfiguration.current
            val currentLocale: Locale =
                ConfigurationCompat.getLocales(configuration).get(0) ?: Locale.getDefault()

            tabs.forEach { section ->
                val selected = section == currentSection
                val tint by animateColorAsState(
                    if (selected) {
                        JetsnackTheme.colors.iconInteractive
                    } else {
                        JetsnackTheme.colors.iconInteractiveInactive
                    }
                )

                val text = stringResource(section.title).uppercase(currentLocale)

                JetsnackBottomNavigationItem(
                    icon = {
                        Icon(
                            imageVector = section.icon,
                            tint = tint,
                            contentDescription = text
                        )
                    },
                    text = {
                        Text(
                            text = text,
                            color = tint,
                            style = MaterialTheme.typography.button,
                            maxLines = 1
                        )
                    },
                    selected = selected,
                    onSelected = { navigateToRoute(section.route+"/from"+currentRoute.substringAfter("/")) },
                    animSpec = springSpec,
                    modifier = BottomNavigationItemPadding
                        .clip(BottomNavIndicatorShape)
                )
            }
        }
    }
}

@Composable
private fun JetsnackBottomNavLayout(
    selectedIndex: Int,
    itemCount: Int,
    animSpec: AnimationSpec<Float>,
    indicator: @Composable BoxScope.() -> Unit,
    modifier: Modifier = Modifier,
    prevSelection: String,
    content: @Composable () -> Unit
) {
    // Track how "selected" each item is [0, 1]
    val selectionFractions = remember(itemCount) {
        List(itemCount) { i ->
            Animatable(if (i == selectedIndex) 1f else 0f)
        }
    }
    selectionFractions.forEachIndexed { index, selectionFraction ->
        val target = if (index == selectedIndex) 1f else 0f
        LaunchedEffect(target, animSpec) {
            selectionFraction.animateTo(target, animSpec)
        }
    }

    val prevSelectionIndex = when (prevSelection) {
        "fromfeed" -> 0f
        "fromsearch" -> 1f
        "fromcart" -> 2f
        "fromprofile" -> 3f
        else -> 0f
    }
    // Animate the position of the indicator
    val indicatorIndex = remember { Animatable(prevSelectionIndex) }
    val targetIndicatorIndex = selectedIndex.toFloat()
    LaunchedEffect(targetIndicatorIndex) {
        indicatorIndex.animateTo(targetIndicatorIndex, animSpec)
    }

    Layout(
        modifier = modifier.height(BottomNavHeight),
        content = {
            content()
            Box(Modifier.layoutId("indicator"), content = indicator)
        }
    ) { measurables, constraints ->
        check(itemCount == (measurables.size - 1)) // account for indicator

        // Divide the width into n+1 slots and give the selected item 2 slots
        val unselectedWidth = constraints.maxWidth / (itemCount + 1)
        val selectedWidth = 2 * unselectedWidth
        val indicatorMeasurable = measurables.first { it.layoutId == "indicator" }

        val itemPlaceables = measurables
            .filterNot { it == indicatorMeasurable }
            .mapIndexed { index, measurable ->
                // Animate item's width based upon the selection amount
                val width2 = if ( index == selectedIndex) selectedWidth else unselectedWidth
                measurable.measure(
                    constraints.copy(
                        minWidth = width2,
                        maxWidth = width2
                    )
                )
            }
        val indicatorPlaceable = indicatorMeasurable.measure(
            constraints.copy(
                minWidth = selectedWidth,
                maxWidth = selectedWidth
            )
        )

        layout(
            width = constraints.maxWidth,
            height = itemPlaceables.maxByOrNull { it.height }?.height ?: 0
        ) {
            val indicatorLeft = indicatorIndex.value * unselectedWidth
            indicatorPlaceable.placeRelative(x = indicatorLeft.toInt(), y = 0)
            var x = 0
            itemPlaceables.forEach { placeable ->
                placeable.placeRelative(x = x, y = 0)
                x += placeable.width
            }
        }
    }
}

@Composable
fun JetsnackBottomNavigationItem(
    icon: @Composable BoxScope.() -> Unit,
    text: @Composable BoxScope.() -> Unit,
    selected: Boolean,
    onSelected: () -> Unit,
    animSpec: AnimationSpec<Float>,
    modifier: Modifier = Modifier
) {
    // Animate the icon/text positions within the item based on selection
    val animationProgress by animateFloatAsState(if (selected) 1f else 0f, animSpec)
    JetsnackBottomNavItemLayout(
        icon = icon,
        text = text,
        animationProgress = animationProgress,
        modifier = modifier
            .selectable(selected = selected, onClick = onSelected)
            .wrapContentSize()
    )
}

@Composable
private fun JetsnackBottomNavItemLayout(
    icon: @Composable BoxScope.() -> Unit,
    text: @Composable BoxScope.() -> Unit,
    @FloatRange(from = 0.0, to = 1.0) animationProgress: Float,
    modifier: Modifier = Modifier
) {
    Layout(
        modifier = modifier,
        content = {
            Box(
                modifier = Modifier
                    .layoutId("icon")
                    .padding(horizontal = TextIconSpacing),
                content = icon
            )
            val scale = lerp(0.6f, 1f, animationProgress)
            Box(
                modifier = Modifier
                    .layoutId("text")
                    .padding(horizontal = TextIconSpacing)
                    .graphicsLayer {
                        alpha = animationProgress
                        scaleX = scale
                        scaleY = scale
                        transformOrigin = BottomNavLabelTransformOrigin
                    }
                ,
                content = text
            )
        }
    ) { measurables, constraints ->
        val iconPlaceable = measurables.first { it.layoutId == "icon" }.measure(constraints)
        val textPlaceable = measurables.first { it.layoutId == "text" }.measure(constraints)

        placeTextAndIcon(
            textPlaceable,
            iconPlaceable,
            constraints.maxWidth,
            constraints.maxHeight,
            animationProgress
        )
    }
}

private fun MeasureScope.placeTextAndIcon(
    textPlaceable: Placeable,
    iconPlaceable: Placeable,
    width: Int,
    height: Int,
    @FloatRange(from = 0.0, to = 1.0) animationProgress: Float
): MeasureResult {
    val iconY = (height - iconPlaceable.height) / 2
    val textY = (height - textPlaceable.height) / 2

    val textWidth = textPlaceable.width * animationProgress
    val iconX = (width - textWidth - iconPlaceable.width) / 2
    val textX = iconX + iconPlaceable.width

    return layout(width, height) {
        iconPlaceable.placeRelative(iconX.toInt(), iconY)
        if (animationProgress != 0f) {
            textPlaceable.placeRelative(textX.toInt(), textY)// 解释了未被选中的栏为什么看不到text
        }
    }
}

@Composable
private fun JetsnackBottomNavIndicator(
    strokeWidth: Dp = 2.dp,
    color: Color = JetsnackTheme.colors.iconInteractive,
    shape: Shape = BottomNavIndicatorShape
) {
    Spacer(
        modifier = Modifier
            .fillMaxSize()
            .then(BottomNavigationItemPadding)
            .border(strokeWidth, color, shape)
    )
}

private val TextIconSpacing = 2.dp
private val BottomNavHeight = 56.dp
private val BottomNavLabelTransformOrigin = TransformOrigin(0f, 0.5f)
private val BottomNavIndicatorShape = RoundedCornerShape(percent = 50)
private val BottomNavigationItemPadding = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)

@Preview
@Composable
private fun JetsnackBottomNavPreview() {
    JetsnackTheme {
        JetsnackBottomBar(
            tabs = HomeSections.values(),
            currentRoute = "home/feed",
            navigateToRoute = {  String() },
            prevSelection = ""
        )
    }
}
