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

package com.example.jetsnack.ui.home.Cart

import GeoCodeResultListener
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.SnackbarHost
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.LastBaseline
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.jetsnack.R
import com.example.jetsnack.api.DessertService
import com.example.jetsnack.model.CartDetail
import com.example.jetsnack.model.SnackCollection
import com.example.jetsnack.model.SnackRepo
import com.example.jetsnack.ui.components.JetsnackButton
import com.example.jetsnack.ui.components.JetsnackDivider
import com.example.jetsnack.ui.components.JetsnackScaffold
import com.example.jetsnack.ui.components.JetsnackSnackbar
import com.example.jetsnack.ui.components.JetsnackSurface
import com.example.jetsnack.ui.components.QuantitySelector
import com.example.jetsnack.ui.components.SnackCollection
import com.example.jetsnack.ui.components.SnackImage
import com.example.jetsnack.ui.components.offsetGradientBackground
import com.example.jetsnack.ui.components.rememberJetsnackScaffoldState
import com.example.jetsnack.ui.home.Feed.DestinationBar
import com.example.jetsnack.ui.home.HomeSections
import com.example.jetsnack.ui.home.JetsnackBottomBar
import com.example.jetsnack.ui.home.Profile.loggedInUser
import com.example.jetsnack.ui.theme.AlphaNearOpaque
import com.example.jetsnack.ui.theme.JetnewsShapes
import com.example.jetsnack.ui.theme.JetsnackTheme
import com.example.jetsnack.ui.theme.Shadow3
import com.example.jetsnack.ui.theme.Shadow6
import com.example.jetsnack.ui.utils.formatPrice
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import reverseGeoCode

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Cart(
    onSnackClick: (Long) -> Unit,
    onNavigateToRoute: (String) -> Unit,
    onNavigateToOrder: (Long, Boolean) -> Unit,
    prevSelection : String,
    modifier: Modifier = Modifier,
    viewModel: CartViewModel = viewModel(factory = CartViewModel.provideFactory())
) {
    LaunchedEffect(Unit){
        viewModel.refreshData()
    }
    val cartDetails = viewModel.cartDetails
    val inspiredByCart = remember { SnackRepo.getInspiredByCart() }
    val jetsnackScaffoldState = rememberJetsnackScaffoldState()

    val sheetState = rememberModalBottomSheetState()
    val coroutineScope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }

    val expand = {
        showBottomSheet = true
    }
    val close = { coroutineScope.launch { sheetState.hide() }.invokeOnCompletion {
        if (!sheetState.isVisible) {
            showBottomSheet = false
        }
    } }
    val destination = remember { mutableStateOf("Choose your destination") }
    val temporaryDestination = remember { mutableStateOf("") }
    val temporarilyChoose = { option: String -> temporaryDestination.value = option }
    val geocodeViewModel: GeocodeViewModel = viewModel()
    val location by geocodeViewModel.location.collectAsState()

    LaunchedEffect(location) {
        if (location.first != 0.0 && location.second != 0.0) {
            reverseGeoCode(location.first, location.second, object : GeoCodeResultListener {
                override fun onResult(city: String) {
                    println("Retrieved city: $city")
                    destination.value = city
                }
                override fun onError(error: String) {
                    println("Error: $error")
                }
            })
        }
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = { showBottomSheet = false },
            scrimColor = Color(0x99000000), // 透明遮罩颜色
            content = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp,4.dp, )
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 30.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TextButton(onClick = { close() },
                            modifier = Modifier.offset(y = -10.dp)) {
                            Text(text = "取消")
                        }
                        JetsnackSurface(
                            modifier = modifier,
                            color = Shadow3,
                            contentColor = Shadow6,
                            shape = JetnewsShapes.small,
                            elevation = 2.dp
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
                                    .clickable {
                                        geocodeViewModel.fetchLocation()
                                        close()
                                    }
                                    .then(backgroundPressed)
                            ) {
                                Text(
                                    text = "获取定位",
                                    style = MaterialTheme.typography.caption,
                                    maxLines = 1,
                                    modifier = Modifier.padding(
                                        horizontal = 20.dp,
                                        vertical = 6.dp
                                    )
                                )
                            }
                        }
                        TextButton(
                            onClick = {
                                close()
                                destination.value = temporaryDestination.value
                            },
                            modifier = Modifier.offset(y = -10.dp)) {
                            Text(text = "确定")
                        }
                    }
                    Box(modifier = Modifier) {
                        MyFlowLayout(temporarilyChoose)
                    }

                }
            }
        )
    }
    JetsnackScaffold(
        bottomBar = {
            JetsnackBottomBar(
                tabs = HomeSections.entries.toTypedArray(),
                currentRoute = HomeSections.CART.route,
                navigateToRoute = onNavigateToRoute,
                prevSelection= prevSelection
            )
        },
        snackbarHost = {
            SnackbarHost(
                hostState = it,
                modifier = Modifier.systemBarsPadding(),
                snackbar = { snackbarData -> JetsnackSnackbar(snackbarData) }
            )
        },
        scaffoldState = jetsnackScaffoldState.scaffoldState,
        modifier = modifier
    ) { paddingValues ->
        Cartt(
            cartDetails = cartDetails.value?: listOf(),
            removeSnack = viewModel::removeSnack,
            increaseItemCount = viewModel::increaseSnackCount,
            decreaseItemCount = viewModel::decreaseSnackCount,
            inspiredByCart = inspiredByCart,
            onSnackClick = onSnackClick,
            onNavigateToOrder = onNavigateToOrder,
            expand = expand,
            viewModel = viewModel,
            destination = destination,
            location = location,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Cartt(
    cartDetails: List<CartDetail>,
    removeSnack: (Long) -> Unit,
    increaseItemCount: (Long) -> Unit,
    decreaseItemCount: (Long) -> Unit,
    inspiredByCart: SnackCollection,
    onSnackClick: (Long) -> Unit,
    onNavigateToOrder: (Long, Boolean) -> Unit,
    expand: () -> Unit,
    viewModel: CartViewModel,
    destination: State<String>,
    location: Pair<Double, Double>,
    modifier: Modifier = Modifier
) {

    JetsnackSurface(modifier = modifier.fillMaxSize()) {
        Box(modifier = Modifier
            .fillMaxWidth()
            .height(1000.dp)) {
            val shippingCosts = remember {
                mutableStateOf(0.00)
            }
            LaunchedEffect(destination,location ) {
                if (location.first != 0.0 && location.second != 0.0) {
                    shippingCosts.value =
                        (calculateDistance(destination.value, Pair(31.82, 117.23), location)).times(
                            0.3
                        )
                }
            }
            CartContent(
                cartDetails = cartDetails,
                removeSnack = removeSnack,
                increaseItemCount = increaseItemCount,
                decreaseItemCount = decreaseItemCount,
                inspiredByCart = inspiredByCart,
                onSnackClick = onSnackClick,
                viewModel = viewModel,
                shippingCosts,
                modifier = Modifier.align(Alignment.TopCenter)
            )
            DestinationBar(
                destination.value,
                expanded = { expand() }
            )
            CheckoutBar(onNavigateToOrder = onNavigateToOrder, destination, location, shippingCosts, modifier = Modifier.align(Alignment.BottomCenter))
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun CartContent(
    cartDetails: List<CartDetail>,
    removeSnack: (Long) -> Unit,
    increaseItemCount: (Long) -> Unit,
    decreaseItemCount: (Long) -> Unit,
    inspiredByCart: SnackCollection,
    onSnackClick: (Long) -> Unit,
    viewModel: CartViewModel,
    shippingCosts: State<Double>,
    modifier: Modifier = Modifier
) {
    val resources = LocalContext.current.resources
    val snackCountFormattedString = remember(cartDetails.size, resources) {
        resources.getQuantityString(
            R.plurals.cart_order_count,
            cartDetails.size, cartDetails.size
        )
    }
    LazyColumn(modifier) {
        item {
            Spacer(
                Modifier.windowInsetsTopHeight(
                    WindowInsets.statusBars.add(WindowInsets(top = 56.dp))
                )
            )
            Text(
                text = stringResource(R.string.cart_order_header, snackCountFormattedString),
                style = MaterialTheme.typography.h6,
                color = JetsnackTheme.colors.brand,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .heightIn(min = 56.dp)
                    .padding(horizontal = 24.dp, vertical = 4.dp)
                    .wrapContentHeight()
            )
        }
        items(cartDetails) { cartDetail ->
            SwipeDismissItem(
                background = { offsetX ->
                    /*Background color changes from light gray to red when the
                    swipe to delete with exceeds 160.dp*/
                    val backgroundColor = if (offsetX < -160.dp) {
                        JetsnackTheme.colors.error
                    } else {
                        JetsnackTheme.colors.uiFloated
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .background(backgroundColor),
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.Center
                    ) {
                        // Set 4.dp padding only if offset is bigger than 160.dp
                        val padding: Dp by animateDpAsState(
                            if (offsetX > -160.dp) 4.dp else 0.dp
                        )
                        Box(
                            Modifier
                                .width(offsetX * -1)
                                .padding(padding)
                        ) {
                            // Height equals to width removing padding
                            val height = (offsetX + 8.dp) * -1
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(height)
                                    .align(Alignment.Center),
                                shape = CircleShape,
                                color = JetsnackTheme.colors.error
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    // Icon must be visible while in this width range
                                    if (offsetX < -40.dp && offsetX > -152.dp) {
                                        // Icon alpha decreases as it is about to disappear
                                        val iconAlpha: Float by animateFloatAsState(
                                            if (offsetX < -120.dp) 0.5f else 1f
                                        )

                                        Icon(
                                            imageVector = Icons.Filled.DeleteForever,
                                            modifier = Modifier
                                                .size(16.dp)
                                                .graphicsLayer(alpha = iconAlpha),
                                            tint = JetsnackTheme.colors.uiBackground,
                                            contentDescription = null,
                                        )
                                    }
                                    /*Text opacity increases as the text is supposed to appear in
                                    the screen*/
                                    val textAlpha by animateFloatAsState(
                                        if (offsetX > -144.dp) 0.5f else 1f
                                    )
                                    if (offsetX < -120.dp) {
                                        Text(
                                            text = stringResource(id = R.string.remove_item),
                                            style = MaterialTheme.typography.subtitle1,
                                            color = JetsnackTheme.colors.uiBackground,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier
                                                .graphicsLayer(
                                                    alpha = textAlpha
                                                )
                                        )
                                    }
                                }
                            }
                        }
                    }
                },
            ) {
                CartItemm(
                    cartDetail = cartDetail,
                    removeSnack = removeSnack,
                    increaseItemCount = increaseItemCount,
                    decreaseItemCount = decreaseItemCount,
                    onSnackClick = onSnackClick
                )
            }
        }
        item {
            SummaryItemm(
                subtotal = viewModel.subtotal.value.toLong() ,
                shippingCosts = shippingCosts.value.toLong()
            )
        }
        item {
            SnackCollection(
                snackCollection = inspiredByCart,
                onSnackClick = onSnackClick,
                highlight = false
            )
            Spacer(Modifier.height(56.dp))
        }
    }
}

@Composable
fun SummaryItem(
    subtotal: Long,
    shippingCosts: Long,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        Text(
            text = stringResource(R.string.cart_summary_header),
            style = MaterialTheme.typography.h6,
            color = JetsnackTheme.colors.brand,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .heightIn(min = 56.dp)
                .wrapContentHeight()
        )
        Row(modifier = Modifier.padding(horizontal = 24.dp)) {
            Text(
                text = stringResource(R.string.cart_subtotal_label),
                style = MaterialTheme.typography.body1,
                modifier = Modifier
                    .weight(1f)
                    .wrapContentWidth(Alignment.Start)
                    .alignBy(LastBaseline)
            )
            Text(
                text = formatPrice(subtotal),
                style = MaterialTheme.typography.body1,
                modifier = Modifier.alignBy(LastBaseline)
            )
        }
        Row(modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)) {
            Text(
                text = stringResource(R.string.cart_shipping_label),
                style = MaterialTheme.typography.body1,
                modifier = Modifier
                    .weight(1f)
                    .wrapContentWidth(Alignment.Start)
                    .alignBy(LastBaseline)
            )
            Text(
                text = formatPrice(shippingCosts),
                style = MaterialTheme.typography.body1,
                modifier = Modifier.alignBy(LastBaseline)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        JetsnackDivider()
        Row(modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)) {
            Text(
                text = stringResource(R.string.cart_total_label),
                style = MaterialTheme.typography.body1,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 16.dp)
                    .wrapContentWidth(Alignment.End)
                    .alignBy(LastBaseline)
            )
            Text(
                text = formatPrice(subtotal + shippingCosts),
                style = MaterialTheme.typography.subtitle1,
                modifier = Modifier.alignBy(LastBaseline)
            )
        }
        JetsnackDivider()
    }
}

@Composable
fun CartItemm(
    cartDetail: CartDetail,
    removeSnack: (Long) -> Unit,
    increaseItemCount: (Long) -> Unit,
    decreaseItemCount: (Long) -> Unit,
    onSnackClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val snack = cartDetail.dessert
    ConstraintLayout(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onSnackClick(snack.id) }
            .background(JetsnackTheme.colors.uiBackground)
            .padding(horizontal = 24.dp)

    ) {
        val (divider, image, name, tag, priceSpacer, price, remove, quantity) = createRefs()
        createVerticalChain(name, tag, priceSpacer, price, chainStyle = ChainStyle.Packed)
        SnackImage(
            imageUrl = snack.imageUrl,
            contentDescription = null,
            modifier = Modifier
                .size(100.dp)
                .constrainAs(image) {
                    top.linkTo(parent.top, margin = 16.dp)
                    bottom.linkTo(parent.bottom, margin = 16.dp)
                    start.linkTo(parent.start)
                }
        )
        Text(
            text = snack.name,
            style = MaterialTheme.typography.subtitle1,
            color = JetsnackTheme.colors.textSecondary,
            modifier = Modifier.constrainAs(name) {
                linkTo(
                    start = image.end,
                    startMargin = 16.dp,
                    end = remove.start,
                    endMargin = 16.dp,
                    bias = 0f
                )
            }
        )
        IconButton(
            onClick = { removeSnack(snack.id) },
            modifier = Modifier
                .constrainAs(remove) {
                    top.linkTo(parent.top)
                    end.linkTo(parent.end)
                }
                .padding(top = 12.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Close,
                tint = JetsnackTheme.colors.iconSecondary,
                contentDescription = stringResource(R.string.label_remove)
            )
        }
        Text(
            text = "",
            style = MaterialTheme.typography.body1,
            color = JetsnackTheme.colors.textHelp,
            modifier = Modifier.constrainAs(tag) {
                linkTo(
                    start = image.end,
                    startMargin = 16.dp,
                    end = parent.end,
                    endMargin = 16.dp,
                    bias = 0f
                )
            }
        )
        Spacer(
            Modifier
                .height(8.dp)
                .constrainAs(priceSpacer) {
                    linkTo(top = tag.bottom, bottom = price.top)
                }
        )
        Text(
            text = formatPrice(snack.price),
            style = MaterialTheme.typography.subtitle1,
            color = JetsnackTheme.colors.textPrimary,
            modifier = Modifier.constrainAs(price) {
                linkTo(
                    start = image.end,
                    end = quantity.start,
                    startMargin = 16.dp,
                    endMargin = 16.dp,
                    bias = 0f
                )
            }
        )
        QuantitySelector(
            count = cartDetail.amount,
            decreaseItemCount = { decreaseItemCount(snack.id) },
            increaseItemCount = { increaseItemCount(snack.id) },
            modifier = Modifier.constrainAs(quantity) {
                baseline.linkTo(price.baseline)
                end.linkTo(parent.end)
            }
        )
        JetsnackDivider(
            Modifier.constrainAs(divider) {
                linkTo(start = parent.start, end = parent.end)
                top.linkTo(parent.bottom)
            }
        )
    }
}

@Composable
fun SummaryItemm(
    subtotal: Long,
    shippingCosts: Long,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        Text(
            text = stringResource(R.string.cart_summary_header),
            style = MaterialTheme.typography.h6,
            color = JetsnackTheme.colors.brand,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .heightIn(min = 56.dp)
                .wrapContentHeight()
        )
        Row(modifier = Modifier.padding(horizontal = 24.dp)) {
            Text(
                text = stringResource(R.string.cart_subtotal_label),
                style = MaterialTheme.typography.body1,
                modifier = Modifier
                    .weight(1f)
                    .wrapContentWidth(Alignment.Start)
                    .alignBy(LastBaseline)
            )
            Text(
                text = formatPrice(subtotal),
                style = MaterialTheme.typography.body1,
                modifier = Modifier.alignBy(LastBaseline)
            )
        }
        Row(modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)) {
            Text(
                text = stringResource(R.string.cart_shipping_label),
                style = MaterialTheme.typography.body1,
                modifier = Modifier
                    .weight(1f)
                    .wrapContentWidth(Alignment.Start)
                    .alignBy(LastBaseline)
            )
            Text(
                text = formatPrice(shippingCosts),
                style = MaterialTheme.typography.body1,
                modifier = Modifier.alignBy(LastBaseline)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        JetsnackDivider()
        Row(modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)) {
            Text(
                text = stringResource(R.string.cart_total_label),
                style = MaterialTheme.typography.body1,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 16.dp)
                    .wrapContentWidth(Alignment.End)
                    .alignBy(LastBaseline)
            )
            Text(
                text = formatPrice(subtotal + shippingCosts),
                style = MaterialTheme.typography.subtitle1,
                modifier = Modifier.alignBy(LastBaseline)
            )
        }
        JetsnackDivider()
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun CheckoutBar(onNavigateToOrder: (Long, Boolean) -> Unit,
                        destination: State<String>,
                        location: Pair<Double, Double>,
                        shippingCosts: State<Double>,
                        modifier: Modifier = Modifier) {
    Column(
        modifier.background(
            JetsnackTheme.colors.uiBackground.copy(alpha = AlphaNearOpaque)
        )
    ) {
        val requireTime = calculateDistance(destination.value, Pair(31.82, 117.23), location)/22.22
        JetsnackDivider()
        Row {
            Spacer(Modifier.weight(1f))
            JetsnackButton(
                onClick = {
                    CoroutineScope(Dispatchers.IO).launch {
                        addresss = destination.value
                        val orderId = DessertService.create().createOrderWithKafkaa(loggedInUser, addresss, requireTime.toLong(), shippingCosts.value.toBigDecimal() )
                        withContext(Dispatchers.Main) {
                            onNavigateToOrder(orderId.body() ?: 0, true)
                        }
                      }
                    },
                shape = RectangleShape,
                modifier = Modifier
                    .padding(horizontal = 12.dp, vertical = 8.dp)
                    .weight(1f)
            ) {
                Text(
                    text = stringResource(id = R.string.cart_checkout),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Left,
                    maxLines = 1
                )
            }
        }
    }
}
