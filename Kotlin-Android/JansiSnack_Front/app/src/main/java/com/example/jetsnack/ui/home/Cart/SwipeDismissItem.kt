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

package com.example.jetsnack.ui.home.Cart

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.DismissDirection
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.Text
import androidx.compose.material.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.jetsnack.ui.components.JetsnackSurface
import com.example.jetsnack.ui.components.offsetGradientBackground
import com.example.jetsnack.ui.theme.JetnewsShapes
import com.example.jetsnack.ui.theme.JetsnackTheme
import com.example.jetsnack.ui.theme.Shadow3
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

@OptIn(ExperimentalMaterialApi::class)
@Composable
/**
 * Holds the Swipe to dismiss composable, its animation and the current state
 */
fun SwipeDismissItem(
    modifier: Modifier = Modifier,
    directions: Set<DismissDirection> = setOf(DismissDirection.EndToStart),
    enter: EnterTransition = expandVertically(),
    exit: ExitTransition = shrinkVertically(),
    background: @Composable (offset: Dp) -> Unit,
    content: @Composable (isDismissed: Boolean) -> Unit,
) {
    // Hold the current state from the Swipe to Dismiss composable
    val dismissState = rememberDismissState()
    // Boolean value used for hiding the item if the current state is dismissed
    val isDismissed = dismissState.isDismissed(DismissDirection.EndToStart)
    // Returns the swiped value in dp
    val offset = with(LocalDensity.current) { dismissState.offset.value.toDp() }

    AnimatedVisibility(
        modifier = modifier,
        visible = !isDismissed,
        enter = enter,
        exit = exit
    ) {
        SwipeToDismiss(
            modifier = modifier,
            state = dismissState,
            directions = directions,
            background = { background(offset) },
            dismissContent = { content(isDismissed) }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MyFlowLayout(
    temporarilyChoose: (String) -> Unit,
) {
    Box(modifier = Modifier
        .fillMaxSize()
        .padding(18.dp,4.dp)) {
        FlowRow(
            horizontalArrangement = Arrangement.Start,
            verticalArrangement = Arrangement.Top,
            maxItemsInEachRow = Int.MAX_VALUE  // 设置每行的最大项数
        ) {
            // 这里添加你的组件
            for (city in City.entries) {
                DestinationOption(city, temporarilyChoose,modifier = Modifier.padding(vertical = 5.dp))
            }
        }
    }
}

@Composable
fun DestinationOption (
    city: City,
    temporarilyChoose: (String) -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = JetnewsShapes.small
) {
    val selected = remember {
        mutableStateOf(false)
    }
    val backgroundColor by animateColorAsState(
        if (selected.value) Shadow3 else Color.White,
        label = "background color"
    )
    val textColor by animateColorAsState(
        if (selected.value) Color.Black else JetsnackTheme.colors.textSecondary,
        label = "text color"
    )
    Row {
        JetsnackSurface(
            modifier = modifier,
            color = backgroundColor,
            contentColor = textColor,
            shape = shape,
            elevation = 5.dp
        ) {
            val interactionSource = remember { MutableInteractionSource() }

            val pressed by interactionSource.collectIsPressedAsState()
            val backgroundPressed =
                if (pressed) {
                    Modifier.offsetGradientBackground(
                        JetsnackTheme.colors.interactiveSecondary,
                        200f,
                        0f
                    )
                } else {
                    Modifier.background(Color.Transparent)
                }
            Box(
                modifier = Modifier
                    .toggleable(
                        value = selected.value,
                        onValueChange = {
                            selected.value = !selected.value
                            if (selected.value) temporarilyChoose(city.cityName) else temporarilyChoose(
                                ""
                            )
                        },
                        interactionSource = interactionSource,
                        indication = null
                    )
                    .then(backgroundPressed)
            ) {
                Text(
                    text = city.cityName,
                    style = MaterialTheme.typography.caption,
                    maxLines = 1,
                    modifier = Modifier.padding(
                        horizontal = 20.dp,
                        vertical = 6.dp
                    )
                )
            }
        }
        Spacer(modifier = Modifier.width(10.dp))
    }
}


enum class City(val cityName: String, val latitude: Double, val longitude: Double) {
    BEIJING("北京", 39.9042, 116.4074),
    SHANGHAI("上海", 31.2304, 121.4737),
    HANGZHOU("杭州", 30.2741, 120.1551),
    SHENZHEN("深圳", 22.5428, 114.0596),
    CHENGDU("成都", 30.5728, 104.0668)
}

val cityMap: Map<String, Pair<Double, Double>> = City.entries.associate { city ->
    city.cityName to (city.latitude to city.longitude)
}

fun calculateDistance(destination: String, storeLocation: Pair<Double, Double>, location: Pair<Double, Double>): Double {
    Log.i("locationooo", location.toString())
    var lat1: Double =0.0
    var lon1: Double =0.0
    val earthRadius = 6371e3 // 地球半径，单位：米
    if (location.first != 0.0 && location.second != 0.0) {
        lat1 = Math.toRadians(location.first)
        lon1 = Math.toRadians(location.second)
    }
    else {
        val destinationCity = cityMap[destination]

        if (destinationCity != null) {
            lat1 = Math.toRadians(destinationCity.first)
        }
        if (destinationCity != null) {
            lon1 = Math.toRadians(destinationCity.second)
        }

    }
    val lat2 = Math.toRadians(storeLocation.first)
    val lon2 = Math.toRadians(storeLocation.second)

    val deltaLat = lat2 - lat1
    val deltaLon = lon2 - lon1

    val a = sin(deltaLat / 2).pow(2) + cos(lat1) * cos(lat2) * sin(deltaLon / 2).pow(2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))

    return earthRadius * c
}