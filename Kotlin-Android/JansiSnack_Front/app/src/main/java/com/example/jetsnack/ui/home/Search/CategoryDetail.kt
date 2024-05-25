package com.example.jetsnack.ui.home.Search

import android.os.Build
import android.util.Log
import androidx.activity.compose.ReportDrawnWhen
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.jetsnack.R
import com.example.jetsnack.ui.components.HighlightSnackItem1
import com.example.jetsnack.ui.components.JetsnackScaffold
import com.example.jetsnack.ui.components.JetsnackSurface
import com.example.jetsnack.ui.home.Feed.FeedViewModel
import com.example.jetsnack.ui.theme.AlphaNearOpaque
import com.example.jetsnack.ui.theme.JetsnackTheme
import com.example.jetsnack.ui.theme.Typography1
import com.example.jetsnack.ui.utils.mirroringBackIcon

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CategoryDetail(
    categoryName: String,
    upPress: () -> Unit,
    onSnackClick: (Long) -> Unit,
    onNavigateToCategorySearch: () -> Unit,
    feedViewModel: FeedViewModel = hiltViewModel(),
) {
    LaunchedEffect(categoryName) {
        feedViewModel.findDessertsByCategory(categoryName)
    }
    JetsnackScaffold(
        topBar = {
            CategoryTopBar(upPress = upPress, onNavigateToCategorySearch = onNavigateToCategorySearch)
        },
    ) { paddingValues ->
        JetsnackSurface(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)) {

            val gridState = rememberLazyGridState()
            ReportDrawnWhen { gridState.layoutInfo.totalItemsCount > 0 }
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.imePadding(),
                state = gridState,
                contentPadding = PaddingValues(
                    horizontal = 10.dp,
                    vertical = 10.dp
                )
            ) {
                items(
                    items = feedViewModel.sortedDesserts.value?: listOf()
                ) {dessert ->
                    Log.i("Findor", "Response: ${dessert.name}")
                    HighlightSnackItem1(
                        dessert = dessert,
                        onSnackClick = onSnackClick,
                        index = 1,
                        gradient = JetsnackTheme.colors.gradient6_1,
                        scrollProvider = { 1f },
                        modifier = Modifier.padding(start = 8.dp, end = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryTopBar(upPress: () -> Unit, onNavigateToCategorySearch: () -> Unit, modifier: Modifier = Modifier) {
    TopAppBar(
        backgroundColor = JetsnackTheme.colors.uiBackground.copy(alpha = AlphaNearOpaque),
        contentColor = JetsnackTheme.colors.textSecondary,
        contentPadding = WindowInsets.systemBars.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top).asPaddingValues(),
        elevation = 0.dp,
        modifier = Modifier.height(90.dp)
    ) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(top = 0.dp), verticalAlignment = Alignment.CenterVertically,) {
            // 返回图标
            Up(modifier = Modifier
                .weight(1f)
                .offset(y = -12.dp), upPress = upPress)
            // 标题
            Text(text = "Cake",style = Typography1.h4, modifier = Modifier
                .weight(2f)
                .wrapContentWidth(Alignment.CenterHorizontally)
            )
            // 搜索图标
            DoSearch(onNavigateToCategorySearch = onNavigateToCategorySearch, modifier = Modifier
                .weight(1f)
                .offset(y = -10.dp)
            )
        }
    }
}

@Composable
fun Up(upPress:() -> Unit, modifier: Modifier) {
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

@Composable
fun DoSearch( onNavigateToCategorySearch: () -> Unit, modifier: Modifier) {
    IconButton(
        onClick =  onNavigateToCategorySearch,
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
            imageVector = Icons.Outlined.Search,
            tint = JetsnackTheme.colors.textHelp,
            contentDescription = stringResource(R.string.label_search),
            modifier = Modifier.size(36.dp)
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
fun Preview() {
    JetsnackTheme {
        CategoryDetail(categoryName = "", upPress = { /*TODO*/ }, onSnackClick = {}, onNavigateToCategorySearch = {})
    }
}