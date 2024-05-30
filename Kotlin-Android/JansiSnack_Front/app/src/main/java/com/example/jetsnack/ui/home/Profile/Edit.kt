package com.example.jetsnack.ui.home.Profile

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.jetsnack.R
import com.example.jetsnack.ui.components.JetsnackDivider
import com.example.jetsnack.ui.components.JetsnackSurface
import com.example.jetsnack.ui.theme.AlphaNearOpaque
import com.example.jetsnack.ui.theme.JetnewsTheme
import com.example.jetsnack.ui.theme.JetsnackTheme
import com.example.jetsnack.ui.utils.mirroringBackIcon
import com.google.gson.JsonNull

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Edit1(upPress:()->Unit,
          onNavigateToEditSpec: (String, String) -> Unit,
) {
    Scaffold(topBar = { EditTopBar(upPress) }) {paddingValues ->
        JetsnackSurface(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)) {
            EditContent(onNavigateToEditSpec)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EditContent(onNavigateToEditSpec: (String, String) -> Unit,
                profileViewModel: ProfileViewModel = hiltViewModel()
) {
    val userInfo = profileViewModel.userInfo.value?.asJsonObject
    val infoItems = listOf(
        "昵称" to userInfo?.get("username")?.takeIf { it !is JsonNull }?.asString.orEmpty(),
        "性别" to userInfo?.get("gender")?.takeIf { it !is JsonNull }?.asString.orEmpty(),
        ("个性签名" to userInfo?.get("motto")?.takeIf { it !is JsonNull }?.asString),
        "年龄" to userInfo?.get("age")?.takeIf { it !is JsonNull }?.asString.orEmpty(),
        "所在地" to userInfo?.get("locale")?.takeIf { it !is JsonNull }?.asString.orEmpty(),
        "职业" to userInfo?.get("job")?.takeIf { it !is JsonNull }?.asString.orEmpty()
    )
    LazyColumn {

        item { EditPhoto(profileViewModel) }
        infoItems.forEach { (title, value) ->
            item {
                if (value != null) {
                    Info(title = title, titleValue = value, onNavigateToEditSpec = onNavigateToEditSpec)
                }
            }
        }

    }
}

@Composable
fun EditTopBar(upPress:()->Unit) {
    TopAppBar(
        backgroundColor = JetsnackTheme.colors.uiBackground.copy(alpha = AlphaNearOpaque),
        contentColor = JetsnackTheme.colors.textSecondary,
        contentPadding = WindowInsets.systemBars.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top).asPaddingValues(),
        elevation = 0.dp,
        modifier = Modifier.height(90.dp)
    )
    {
     Row(verticalAlignment = Alignment.CenterVertically,
         modifier = Modifier
             .fillMaxSize()
             .wrapContentHeight()) {
         Up1(modifier = Modifier
             .weight(1f)
             .offset(y = -12.dp),
             upPress =upPress)
         Text(text = "编辑资料",
             fontFamily = FontFamily(Font(R.font.karla_regular)),
             color =  Color.Black,
             style = MaterialTheme.typography.titleMedium, fontSize = 26.sp ,
             modifier = Modifier
             .weight(2f)
             .wrapContentWidth(Alignment.CenterHorizontally)
             .offset(x = 0.dp)
         )
         TextButton(onClick = { /*TODO*/ },modifier = Modifier
             .weight(1f)) {
             Text(text = "Reset")
         }


     }
     JetsnackDivider(modifier = Modifier.offset(y=60.dp))
 }
}

@Composable
fun Up1(upPress: () -> Unit, modifier: Modifier) {
    IconButton(
        onClick = upPress,
        modifier = modifier
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .size(36.dp)
            .background(
                color = Color.White,
                shape = CircleShape
            )
    ) {
        Icon(
            imageVector = mirroringBackIcon(),
            tint = Color.Black,
            contentDescription = stringResource(R.string.label_back),
            modifier = Modifier.size(36.dp)

        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EditPhoto(profileViewModel: ProfileViewModel){
    Box(modifier = Modifier
        .height(150.dp)
        .fillMaxWidth()){
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(profileViewModel.userInfo.value?.asJsonObject?.get("avatar")?.takeIf { it !is JsonNull }?.asString ?:"",)
                .crossfade(true)
                .build(),
            contentDescription = null, // decorative
            placeholder = painterResource(R.drawable.kygo),
            modifier = Modifier
                .size(120.dp, 120.dp)
                .clip(CircleShape)
                .align(Alignment.Center)
        )
    }
    JetsnackDivider(modifier = Modifier.width(380.dp), startIndent = 10.dp)
}
@Composable
fun Info(title: String, titleValue: String, onNavigateToEditSpec: (String, String) -> Unit) {
    Row (
        modifier = Modifier
            .height(70.dp)
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ){

        Text(text = "$title:",style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .offset(x = 5.dp)
                .width(80.dp)
        )
        Text(
            text = titleValue,style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.End,
            modifier = Modifier.width(250.dp),  // 固定宽度为200dp
            maxLines = 1,           // 限制为一行显示
            overflow = TextOverflow.Ellipsis  )
        Icon(
            imageVector = Icons.Outlined.ChevronRight,
            tint = Color.Black,
            contentDescription = stringResource(R.string.label_back),
            modifier = Modifier
                .size(36.dp)
                .clickable { onNavigateToEditSpec(title, titleValue) }
        )

    }
    JetsnackDivider(modifier = Modifier.width(380.dp), startIndent = 10.dp)
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
fun EditPreview(){
    JetnewsTheme {
        Edit1({}, {_,_->})
    }
}

