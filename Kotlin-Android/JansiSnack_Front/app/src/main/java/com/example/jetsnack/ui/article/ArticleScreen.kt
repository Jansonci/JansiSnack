package com.example.jetsnack.ui.article

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.jetsnack.R
import com.example.jetsnack.api.WebSocketViewModel
import com.example.jetsnack.data.PostDao
import com.example.jetsnack.data.PostRepository
import com.example.jetsnack.model.Post
import com.example.jetsnack.ui.components.AlertDialog
import com.example.jetsnack.ui.home.News.HomeViewModel
import com.example.jetsnack.ui.home.Profile.loggedInUser
import com.example.jetsnack.ui.home.overallMessageMap
import com.example.jetsnack.ui.home.updateMap
import com.example.jetsnack.ui.utils.BookmarkButton
import com.example.jetsnack.ui.utils.FavoriteButton
import com.example.jetsnack.ui.utils.FunctionalityNotAvailablePopup
import com.example.jetsnack.ui.utils.ShareButton
import com.example.jetsnack.ui.utils.TextSettingsButton
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticleScreen(
    postId: String,
    isExpandedScreen: Boolean,
    onBack: () -> Unit,
    postDao: PostDao,
    modifier: Modifier = Modifier,
    lazyListState: LazyListState = rememberLazyListState(),
    ) {
    val homeViewModel: HomeViewModel = viewModel(
        factory = HomeViewModel.provideFactory(
            postRepository = PostRepository.getInstance(postDao))
    )
    LaunchedEffect(postId) {
        homeViewModel.getPostDetail(postId)
    }
    val webSocketViewModel1: WebSocketViewModel = viewModel()
    var showUnimplementedActionDialog by rememberSaveable { mutableStateOf(false) }
    if (showUnimplementedActionDialog) {
        FunctionalityNotAvailablePopup { showUnimplementedActionDialog = false }
    }

    val postDetailState = homeViewModel.postDetail
    val post1 = postDetailState.value
    if (post1 != null) {
        post1.isCollected = overallMessageMap[post1.id]!!
    }
    if (post1 != null) {
        Log.i("post1", post1.isCollected.toString())
    }

    val messages = webSocketViewModel1.messages.collectAsState(initial = listOf<String>()).value
    if (messages is String) {
        overallMessageMap.updateMap(messages)
    }

    if (post1 != null) {
        Log.i("number",post1.title)
        // 如果dessertDetail不是null，显示甜品详情
        SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshing = false),
            onRefresh = { homeViewModel.getPostDetail(postId, isRefresh = true) },
        ) {
            Row(modifier.fillMaxSize()) {
                val context = LocalContext.current
                ArticleScreenContent(
                    post = post1,
                    // Allow opening the Drawer if the screen is not expanded
                    navigationIconContent = {
                        if (!isExpandedScreen) {
                            IconButton(onClick = onBack) {
                                Icon(
                                    imageVector = Icons.Filled.ArrowBack,
                                    contentDescription = stringResource(R.string.cd_navigate_up),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    },
                    // Show the bottom bar if the screen is not expanded
                    bottomBarContent = {
                        if (!isExpandedScreen) {
                            BottomAppBar(
                                actions = {
                                    FavoriteButton(onClick = {
                                        showUnimplementedActionDialog = true
                                    })
                                    BookmarkButton(
                                        isBookmarked = overallMessageMap[post1.id]?: post1.isCollected,
                                        onClick = {
                                            webSocketViewModel1.sendMessage(loggedInUser.toString()+","+post1.id+","+!overallMessageMap[post1.id]!!)
                                        })
                                    ShareButton(onClick = { sharePost(post1, context) })
                                    TextSettingsButton(onClick = {
                                        showUnimplementedActionDialog = true
                                    })
                                }
                            )
                        }
                    },
                    lazyListState = lazyListState
                )
            }
        }
    }
    else {
        // 如果dessertDetail是null，显示加载指示器或错误消息
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator() // 加载指示器
        }
    }
}

@ExperimentalMaterial3Api
@Composable
private fun ArticleScreenContent(
    post: Post,
    navigationIconContent: @Composable () -> Unit = { },
    bottomBarContent: @Composable () -> Unit = { },
    lazyListState: LazyListState = rememberLazyListState()
) {
    val topAppBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(topAppBarState)
    Scaffold(
        topBar = {
            TopAppBar(
                title = post.publication?.name.orEmpty(),
                navigationIconContent = navigationIconContent,
                scrollBehavior = scrollBehavior
            )
        },
        bottomBar = bottomBarContent
    ) { innerPadding ->
        PostContent(
            post = post,
            contentPadding = innerPadding,
            modifier = Modifier
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            state = lazyListState,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopAppBar(
    title: String,
    navigationIconContent: @Composable () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior?,
    modifier: Modifier = Modifier
) {
    CenterAlignedTopAppBar(
        title = {
            Row {
                Image(
                    painter = painterResource(id = R.drawable.icon_article_background),
                    contentDescription = null,
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(36.dp)
                )
                Text(
                    text = stringResource(R.string.published_in, title),
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        },
        navigationIcon = navigationIconContent,
        scrollBehavior = scrollBehavior,
        modifier = modifier
    )
}

@Composable
private fun FunctionalityNotAvailablePopup(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        text = {
            Text(
                text = stringResource(id = R.string.article_functionality_not_available),
                style = MaterialTheme.typography.bodyLarge
            )
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(id = R.string.close))
            }
        }
    )
}

fun sharePost(post: Post, context: Context) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TITLE, post.title)
        putExtra(Intent.EXTRA_TEXT, post.url)
    }
    context.startActivity(
        Intent.createChooser(
            intent,
            context.getString(R.string.article_share_post)
        )
    )
}


