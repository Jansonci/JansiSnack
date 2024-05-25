package com.example.jetsnack.ui.home.Cart

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.SnackbarHost
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.jetsnack.api.DessertService
import com.example.jetsnack.model.CartDetail
import com.example.jetsnack.ui.components.JetsnackButton
import com.example.jetsnack.ui.components.JetsnackDivider
import com.example.jetsnack.ui.components.JetsnackScaffold
import com.example.jetsnack.ui.components.JetsnackSnackbar
import com.example.jetsnack.ui.components.QuantityIndicator
import com.example.jetsnack.ui.components.SnackImage
import com.example.jetsnack.ui.components.rememberJetsnackScaffoldState
import com.example.jetsnack.ui.home.HomeSections
import com.example.jetsnack.ui.home.Search.Up
import com.example.jetsnack.ui.theme.AlphaNearOpaque
import com.example.jetsnack.ui.theme.JetsnackTheme
import com.example.jetsnack.ui.utils.formatPrice
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

var addresss: String = ""

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Order(
    onSnackClick: (Long) -> Unit,
    onBack: () -> Unit,
    orderTd: Long,
    needToPay: Boolean = true,
    onNavigateToRoute: (String) -> Unit,
    modifier: Modifier = Modifier,
    ) {
    val jetsnackScaffoldState = rememberJetsnackScaffoldState()
    val cartViewModel1: CartViewModel1 = viewModel(factory = CartViewModel1.provideFactory(orderTd))
    val orderLines: State<List<CartDetail>?> = cartViewModel1.orderLines

    JetsnackScaffold(
        topBar = {
            if(needToPay) {
                OrderTopBar(onBack)
            }else OrderTopBar(upPress = onBack, address = cartViewModel1.address.value?:"")
        },
        bottomBar = {
                if(needToPay) {
                ConfirmBar(orderTd, onNavigateToRoute)
            } else TimeBar(stillNeeds = cartViewModel1.stillNeeds.value?:0)
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
    ){
        LazyColumn(modifier) {
            item { JetsnackDivider() }
            items(orderLines.value?: listOf()){orderLine ->
                OrderItem(
                    orderLine = orderLine,
                    onSnackClick = onSnackClick
                )
            }
            item { cartViewModel1.shippingCost.value.toLong().let { it1 -> SummaryItem(subtotal = cartViewModel1.subtotal.value.toLong(), shippingCosts = it1) } }
        }
    }
}

@Composable
fun OrderTopBar(upPress: () -> Unit, address: String = "", modifier: Modifier = Modifier) {
    TopAppBar(
        backgroundColor = JetsnackTheme.colors.uiBackground.copy(alpha = AlphaNearOpaque),
        contentColor = JetsnackTheme.colors.textSecondary,
        contentPadding = WindowInsets.systemBars.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top).asPaddingValues(),
        elevation = 0.dp,
        modifier = Modifier.height(90.dp)
    ) {
        var realAddress:String
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(top = 0.dp, end = 30.dp),
            horizontalArrangement = Arrangement.SpaceBetween
            ) {
            // 返回图标
            Up(modifier = Modifier
                .offset(x =10.dp,y = -12.dp),
                    upPress = upPress)            // 标题
            realAddress = address.ifBlank { addresss }
            Text(text = "送货地址：$realAddress",
                style = MaterialTheme.typography.subtitle1,
                color = JetsnackTheme.colors.brand,
                modifier = Modifier
                .offset(x = 0.dp,y = 22.dp)
            )
        }
    }

}


@Composable
fun OrderItem(
    orderLine: CartDetail,
    onSnackClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val snack = orderLine.dessert
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
        QuantityIndicator(
            count = orderLine.amount,
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

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ConfirmBar(orderTd: Long, onNavigateToRoute: (String) -> Unit, modifier: Modifier = Modifier){
    Column(
        modifier.background(
            JetsnackTheme.colors.uiBackground.copy(alpha = AlphaNearOpaque)
        )
    ) {
        JetsnackDivider()
        Row {
            Spacer(Modifier.width(10.dp))
            JetsnackButton(
                onClick = { /* todo */ },
                shape = RectangleShape,
                modifier = Modifier
                    .padding(horizontal = 12.dp, vertical = 8.dp)
                    .weight(1f)
            ) {
                Text(
                    text = "Cancel",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )
            }
            Spacer(Modifier.width(10.dp))
            JetsnackButton(
                onClick = {
                    CoroutineScope(Dispatchers.IO).launch {
                        DessertService.create().submitPaymentWithKafka(orderTd, "WECHAT")
                        withContext(Dispatchers.Main) {
                            onNavigateToRoute(
                                HomeSections.PROFILE.route + "/from" + HomeSections.CART.route.substringAfter(
                                    "/"
                                )
                            )
                        }
                    }
                },
                shape = RectangleShape,
                modifier = Modifier
                    .padding(horizontal = 12.dp, vertical = 8.dp)
                    .weight(1f)
            ) {
                Text(
                    text = "Pay",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )
            }
        }
    }

}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TimeBar(stillNeeds: Long, modifier: Modifier = Modifier) {
    Column(
        modifier
            .background(
                JetsnackTheme.colors.uiBackground.copy(alpha = AlphaNearOpaque)
            )
            .fillMaxWidth()
            .padding(bottom = 40.dp)
    ) {
        var stillNeedsAsDay = convertSecondsToTimeFormat(stillNeeds)
        if (stillNeeds == 0L){
            stillNeedsAsDay = "已完成"
        }
        Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
            Text(text = stillNeedsAsDay,
                style = MaterialTheme.typography.subtitle1,
                color = JetsnackTheme.colors.textSecondary
            )
        }
    }

}

fun convertSecondsToTimeFormat(seconds: Long): String {
    val days = seconds / (24 * 3600)
    val hours = (seconds % (24 * 3600)) / 3600
    val minutes = (seconds % 3600) / 60

    val result = StringBuilder()

    if (days > 0) {
        result.append("$days 天 ")
    }
    if (hours > 0) {
        result.append("$hours 小时 ")
    }
    if (minutes > 0 || result.isEmpty()) { // 如果分钟为0，但没有其他时间单位，也应显示0分钟
        result.append("$minutes 分钟")
    }

    return result.toString().trim()
}