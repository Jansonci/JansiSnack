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

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.LastBaseline
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.jetsnack.R
import com.example.jetsnack.ui.components.JetsnackDivider
import com.example.jetsnack.ui.theme.JetsnackTheme
import com.example.jetsnack.ui.utils.formatPrice

//@Composable
//fun Cart(
//    onSnackClick: (Long) -> Unit,
//    onNavigateToRoute: (String) -> Unit,
//    prevSelection : String,
//    modifier: Modifier = Modifier,
//    viewModel: CartViewModel = viewModel(factory = CartViewModel.provideFactory())
//) {
//    val orderLines by viewModel.orderLines.collectAsStateWithLifecycle()
//    val inspiredByCart = remember { SnackRepo.getInspiredByCart() }
//    val jetsnackScaffoldState = rememberJetsnackScaffoldState()
//    JetsnackScaffold(
//        bottomBar = {
//            JetsnackBottomBar(
//                tabs = HomeSections.values(),
//                currentRoute = HomeSections.CART.route,
//                navigateToRoute = onNavigateToRoute,
//                prevSelection= prevSelection
//            )
//        },
//        snackbarHost = {
//            SnackbarHost(
//                hostState = it,
//                modifier = Modifier.systemBarsPadding(),
//                snackbar = { snackbarData -> JetsnackSnackbar(snackbarData) }
//            )
//        },
//        scaffoldState = jetsnackScaffoldState.scaffoldState,
//        modifier = modifier
//    ) { paddingValues ->
//        Cart(
//            orderLines = orderLines,
//            removeSnack = viewModel::removeSnack,
//            increaseItemCount = viewModel::increaseSnackCount,
//            decreaseItemCount = viewModel::decreaseSnackCount,
//            inspiredByCart = inspiredByCart,
//            onSnackClick = onSnackClick,
//            modifier = Modifier.padding(paddingValues)
//        )
//    }
//}

//@Composable
//fun Cart(
//    orderLines: List<OrderLine>,
//    removeSnack: (Long) -> Unit,
//    increaseItemCount: (Long) -> Unit,
//    decreaseItemCount: (Long) -> Unit,
//    inspiredByCart: SnackCollection,
//    onSnackClick: (Long) -> Unit,
//    modifier: Modifier = Modifier
//) {
//    val expanded = remember { mutableStateOf(false) } // 记住下拉菜单的展开状态
//    val expand = { expanded.value = true }
//    val close = { expanded.value = false }
//    val destination = remember { mutableStateOf("Choose your destination") }
//    val choose = { option: String -> destination.value = "Deliver to " + option }
//    JetsnackSurface(modifier = modifier.fillMaxSize()) {
//        Box {
//            CartContent(
//                orderLines = orderLines,
//                removeSnack = removeSnack,
//                increaseItemCount = increaseItemCount,
//                decreaseItemCount = decreaseItemCount,
//                inspiredByCart = inspiredByCart,
//                onSnackClick = onSnackClick,
//                modifier = Modifier.align(Alignment.TopCenter)
//            )
//            DestinationBar(
//                destination.value,
//                modifier = Modifier.align(Alignment.TopCenter),
//                expand
//            )
//            CheckoutBar(modifier = Modifier.align(Alignment.BottomCenter))
//        }
//        Column {
//        DropdownMenu(
//                expanded = expanded.value,
//                onDismissRequest = {
//                    expanded.value = false
//                }, // 点击外部时关闭菜单
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .background(Color.White),
//                offset = DpOffset(x = 0.dp, y = 80.dp), // 控制菜单展开位置/
//            ) {
//                // 下拉菜单的选项
//                DropdownMenuItem(
//                    onClick = {
//                    },
//                    modifier = Modifier.fillMaxWidth()
//                )
//                {
//                    DestinationOptionBar("Seattle", choose, close,)
//
//                }
//                DropdownMenuItem(
//                    onClick = { }, modifier = Modifier.fillMaxWidth()
//                )
//                {
//                    DestinationOptionBar("Boston", choose, close,)
//                }
//            }
//        }
//    }
//}
//
//@Composable
//private fun CartContent(
//    orderLines: List<OrderLine>,
//    removeSnack: (Long) -> Unit,
//    increaseItemCount: (Long) -> Unit,
//    decreaseItemCount: (Long) -> Unit,
//    inspiredByCart: SnackCollection,
//    onSnackClick: (Long) -> Unit,
//    modifier: Modifier = Modifier
//) {
//    val resources = LocalContext.current.resources
//    val snackCountFormattedString = remember(orderLines.size, resources) {
//        resources.getQuantityString(
//            R.plurals.cart_order_count,
//            orderLines.size, orderLines.size
//        )
//    }
//    LazyColumn(modifier) {
//        item {
//            Spacer(
//                Modifier.windowInsetsTopHeight(
//                    WindowInsets.statusBars.add(WindowInsets(top = 56.dp))
//                )
//            )
//            Text(
//                text = stringResource(R.string.cart_order_header, snackCountFormattedString),
//                style = MaterialTheme.typography.h6,
//                color = JetsnackTheme.colors.brand,
//                maxLines = 1,
//                overflow = TextOverflow.Ellipsis,
//                modifier = Modifier
//                    .heightIn(min = 56.dp)
//                    .padding(horizontal = 24.dp, vertical = 4.dp)
//                    .wrapContentHeight()
//            )
//        }
//        items(orderLines) { orderLine ->
//            SwipeDismissItem(
//                background = { offsetX ->
//                    /*Background color changes from light gray to red when the
//                    swipe to delete with exceeds 160.dp*/
//                    val backgroundColor = if (offsetX < -160.dp) {
//                        JetsnackTheme.colors.error
//                    } else {
//                        JetsnackTheme.colors.uiFloated
//                    }
//                    Column(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .fillMaxHeight()
//                            .background(backgroundColor),
//                        horizontalAlignment = Alignment.End,
//                        verticalArrangement = Arrangement.Center
//                    ) {
//                        // Set 4.dp padding only if offset is bigger than 160.dp
//                        val padding: Dp by animateDpAsState(
//                            if (offsetX > -160.dp) 4.dp else 0.dp
//                        )
//                        Box(
//                            Modifier
//                                .width(offsetX * -1)
//                                .padding(padding)
//                        ) {
//                            // Height equals to width removing padding
//                            val height = (offsetX + 8.dp) * -1
//                            Surface(
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .height(height)
//                                    .align(Alignment.Center),
//                                shape = CircleShape,
//                                color = JetsnackTheme.colors.error
//                            ) {
//                                Box(
//                                    modifier = Modifier.fillMaxSize(),
//                                    contentAlignment = Alignment.Center
//                                ) {
//                                    // Icon must be visible while in this width range
//                                    if (offsetX < -40.dp && offsetX > -152.dp) {
//                                        // Icon alpha decreases as it is about to disappear
//                                        val iconAlpha: Float by animateFloatAsState(
//                                            if (offsetX < -120.dp) 0.5f else 1f
//                                        )
//
//                                        Icon(
//                                            imageVector = Icons.Filled.DeleteForever,
//                                            modifier = Modifier
//                                                .size(16.dp)
//                                                .graphicsLayer(alpha = iconAlpha),
//                                            tint = JetsnackTheme.colors.uiBackground,
//                                            contentDescription = null,
//                                        )
//                                    }
//                                    /*Text opacity increases as the text is supposed to appear in
//                                    the screen*/
//                                    val textAlpha by animateFloatAsState(
//                                        if (offsetX > -144.dp) 0.5f else 1f
//                                    )
//                                    if (offsetX < -120.dp) {
//                                        Text(
//                                            text = stringResource(id = R.string.remove_item),
//                                            style = MaterialTheme.typography.subtitle1,
//                                            color = JetsnackTheme.colors.uiBackground,
//                                            textAlign = TextAlign.Center,
//                                            modifier = Modifier
//                                                .graphicsLayer(
//                                                    alpha = textAlpha
//                                                )
//                                        )
//                                    }
//                                }
//                            }
//                        }
//                    }
//                },
//            ) {
//                CartItem(
//                    orderLine = orderLine,
//                    removeSnack = removeSnack,
//                    increaseItemCount = increaseItemCount,
//                    decreaseItemCount = decreaseItemCount,
//                    onSnackClick = onSnackClick
//                )
//            }
//        }
//        item {
//            SummaryItem(
//                subtotal = orderLines.map { it.snack.price * it.count }.sum(),
//                shippingCosts = 369
//            )
//        }
//        item {
//            SnackCollection(
//                snackCollection = inspiredByCart,
//                onSnackClick = onSnackClick,
//                highlight = false
//            )
//            Spacer(Modifier.height(56.dp))
//        }
//    }
//}
//
//@Composable
//fun CartItem(
//    orderLine: OrderLine,
//    removeSnack: (Long) -> Unit,
//    increaseItemCount: (Long) -> Unit,
//    decreaseItemCount: (Long) -> Unit,
//    onSnackClick: (Long) -> Unit,
//    modifier: Modifier = Modifier
//) {
//    val snack = orderLine.snack
//    ConstraintLayout(
//        modifier = modifier
//            .fillMaxWidth()
//            .clickable { onSnackClick(snack.id) }
//            .background(JetsnackTheme.colors.uiBackground)
//            .padding(horizontal = 24.dp)
//
//    ) {
//        val (divider, image, name, tag, priceSpacer, price, remove, quantity) = createRefs()
//        createVerticalChain(name, tag, priceSpacer, price, chainStyle = ChainStyle.Packed)
//        SnackImage(
//            imageUrl = snack.imageUrl,
//            contentDescription = null,
//            modifier = Modifier
//                .size(100.dp)
//                .constrainAs(image) {
//                    top.linkTo(parent.top, margin = 16.dp)
//                    bottom.linkTo(parent.bottom, margin = 16.dp)
//                    start.linkTo(parent.start)
//                }
//        )
//        Text(
//            text = snack.name,
//            style = MaterialTheme.typography.subtitle1,
//            color = JetsnackTheme.colors.textSecondary,
//            modifier = Modifier.constrainAs(name) {
//                linkTo(
//                    start = image.end,
//                    startMargin = 16.dp,
//                    end = remove.start,
//                    endMargin = 16.dp,
//                    bias = 0f
//                )
//            }
//        )
//        IconButton(
//            onClick = { removeSnack(snack.id) },
//            modifier = Modifier
//                .constrainAs(remove) {
//                    top.linkTo(parent.top)
//                    end.linkTo(parent.end)
//                }
//                .padding(top = 12.dp)
//        ) {
//            Icon(
//                imageVector = Icons.Filled.Close,
//                tint = JetsnackTheme.colors.iconSecondary,
//                contentDescription = stringResource(R.string.label_remove)
//            )
//        }
//        Text(
//            text = snack.tagline,
//            style = MaterialTheme.typography.body1,
//            color = JetsnackTheme.colors.textHelp,
//            modifier = Modifier.constrainAs(tag) {
//                linkTo(
//                    start = image.end,
//                    startMargin = 16.dp,
//                    end = parent.end,
//                    endMargin = 16.dp,
//                    bias = 0f
//                )
//            }
//        )
//        Spacer(
//            Modifier
//                .height(8.dp)
//                .constrainAs(priceSpacer) {
//                    linkTo(top = tag.bottom, bottom = price.top)
//                }
//        )
//        Text(
//            text = formatPrice(snack.price),
//            style = MaterialTheme.typography.subtitle1,
//            color = JetsnackTheme.colors.textPrimary,
//            modifier = Modifier.constrainAs(price) {
//                linkTo(
//                    start = image.end,
//                    end = quantity.start,
//                    startMargin = 16.dp,
//                    endMargin = 16.dp,
//                    bias = 0f
//                )
//            }
//        )
//        QuantitySelector(
//            count = orderLine.count,
//            decreaseItemCount = { decreaseItemCount(snack.id) },
//            increaseItemCount = { increaseItemCount(snack.id) },
//            modifier = Modifier.constrainAs(quantity) {
//                baseline.linkTo(price.baseline)
//                end.linkTo(parent.end)
//            }
//        )
//        JetsnackDivider(
//            Modifier.constrainAs(divider) {
//                linkTo(start = parent.start, end = parent.end)
//                top.linkTo(parent.bottom)
//            }
//        )
//    }
//}

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

//@Composable
//private fun CheckoutBar(modifier: Modifier = Modifier) {
//    Column(
//        modifier.background(
//            JetsnackTheme.colors.uiBackground.copy(alpha = AlphaNearOpaque)
//        )
//    ) {
//
//        JetsnackDivider()
//        Row {
//            Spacer(Modifier.weight(1f))
//            JetsnackButton(
//                onClick = { /* todo */ },
//                shape = RectangleShape,
//                modifier = Modifier
//                    .padding(horizontal = 12.dp, vertical = 8.dp)
//                    .weight(1f)
//            ) {
//                Text(
//                    text = stringResource(id = R.string.cart_checkout),
//                    modifier = Modifier.fillMaxWidth(),
//                    textAlign = TextAlign.Left,
//                    maxLines = 1
//                )
//            }
//        }
//    }
//}
//
//@Preview("default")
//@Preview("dark theme", uiMode = UI_MODE_NIGHT_YES)
//@Preview("large font", fontScale = 2f)
//@Composable
//private fun CartPreview() {
//    JetsnackTheme {
//        Cart(
//            orderLines = SnackRepo.getCart(),
//            removeSnack = {},
//            increaseItemCount = {},
//            decreaseItemCount = {},
//            inspiredByCart = SnackRepo.getInspiredByCart(),
//            onSnackClick = {}
//        )
//    }
//}