package com.example.jetnews.ui.collects

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.jetnews.ui.home.PostCardSimpleForCollection
import com.example.jetsnack.api.WebSocketViewModel
import com.example.jetsnack.model.OrderForCheck
import com.example.jetsnack.model.Post
import com.example.jetsnack.model.orderForCheck
import com.example.jetsnack.ui.components.JetsnackSurface
import com.example.jetsnack.ui.components.PostListDivider
import com.example.jetsnack.ui.home.Profile.loggedInUser
import com.example.jetsnack.ui.home.overallMessageMap
import com.example.jetsnack.ui.home.updateMap
import com.example.jetsnack.ui.theme.JetsnackTheme
import java.time.format.DateTimeFormatter

@Composable
fun TabWithCollections(
    posts: List<Post>,
    onPostSelected: (String) -> Unit,
    webSocketViewModel: WebSocketViewModel
) {
    val messages = webSocketViewModel.messages.collectAsState(initial = listOf<String>()).value
    if (messages is String) {
        overallMessageMap.updateMap(messages)
    }
    Column{
        posts.forEach{ post ->
            PostCardSimpleForCollection(
                post = post,
                navigateToArticle ={ onPostSelected(post.id)},
                isBookmarked = overallMessageMap[post.id] ?: post.isCollected,
                onToggleFavorite = { webSocketViewModel.sendMessage(loggedInUser.toString()+","+post.id+","+!overallMessageMap[post.id]!!)
                }
            )
        }
            PostListDivider()
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TabWithOrders(onNavigateToOrder: (Long, Boolean) -> Unit, orderForChecks: List<OrderForCheck>){
    Column {
        orderForChecks.forEach{orderForCheck ->
            OrderCard(orderForCheck, onNavigateToOrder)
            Divider()
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun OrderCard(orderForCheck: OrderForCheck, onNavigateToOrder: (Long, Boolean) -> Unit, ){
    JetsnackSurface {
        val status = when (orderForCheck.stillNeeds) {
            in Int.MIN_VALUE until 0 -> "未支付"
            0L -> "已完成"
            in 1..Int.MAX_VALUE -> "运输中"
            else -> "未知状态"
        }
        val needToPay = status=="未支付"
        Row(
            modifier = Modifier
                .clickable { onNavigateToOrder(orderForCheck.orderId, needToPay) }
                .fillMaxWidth()  // 确保Row使用最大宽度
                .padding( end = 10.dp)
                .padding(horizontal = 8.dp, vertical = 8.dp),  // 添加一些padding以避免内容挤压到边缘

            verticalAlignment = Alignment.CenterVertically  // 垂直居中子组件
        ) {
            Spacer(modifier = Modifier.width(10.dp))

            // 重叠图片组件，假设已正确实现
            OverlappingImages(orderForCheck.orderItems.map { it.dessert.imageUrl })

            Spacer(modifier = Modifier.width(10.dp)) // 在图片和文本之间添加间隔

            Column(
                modifier = Modifier.weight(1f)  // 让Column占据剩余的空间
            ) {
                val text = orderForCheck.orderItems
                    .take(if (orderForCheck.orderItems.size > 3) 3 else orderForCheck.orderItems.size) // 判断数量并取前三个或所有
                    .joinToString(separator = "、") { it.dessert.name }
                Text(
                    text = text,
                    style = androidx.compose.material.MaterialTheme.typography.subtitle1,
                    color = JetsnackTheme.colors.textSecondary,
                    modifier = Modifier.padding(end = 16.dp),  // 确保文本不会触及到行末
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = orderForCheck.payTime.format(formatter),
                    style = androidx.compose.material.MaterialTheme.typography.subtitle1,
                    color = JetsnackTheme.colors.textSecondary                )
            }

            Text(
                text = status,
                style = androidx.compose.material.MaterialTheme.typography.subtitle1,
                color = JetsnackTheme.colors.textSecondary,
                modifier = Modifier.padding(start = 16.dp)  // 在状态和前一个组件之间添加一些间隔
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
val formatter = DateTimeFormatter.ofPattern("M月d日")

@Composable
fun OverlappingImages(pics: List<String>) {
    Log.i("pics",pics.toString())
    val imageOffset = 15.dp // 每张图片的偏移量
    val totalWidth = 100.dp  // 计算整个Row的宽度

 Box(modifier = Modifier
     .height(75.dp)
     .width(totalWidth)
     .padding(6.dp)
 )
 {
     val picsToShow = if (pics.size>3) pics.subList(0,2) else pics
     picsToShow.forEachIndexed { index, imageRes ->
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageRes)
                    .crossfade(true)
                    .build(),
                contentDescription = "Image $index",
                contentScale = ContentScale.Crop,
                modifier = Modifier

                    .size(62.dp, 72.dp) // 设置图片大小
                    .offset(x = imageOffset*index)
                    .clip(MaterialTheme.shapes.small)
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
fun OrderCardPreview(){
    JetsnackTheme {
        OrderCard(orderForCheck = orderForCheck , onNavigateToOrder = {_,_->} )
    }
}



