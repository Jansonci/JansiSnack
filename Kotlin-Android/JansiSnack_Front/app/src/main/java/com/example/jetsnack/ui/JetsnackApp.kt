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

package com.example.jetsnack.ui

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import androidx.room.Room
import com.example.jetnews.ui.article.ArticleScreen
import com.example.jetsnack.data.AppDatabase
import com.example.jetsnack.data.PostDao
import com.example.jetsnack.ui.home.Cart.Order
import com.example.jetsnack.ui.home.Feed.FeedViewModel
import com.example.jetsnack.ui.home.Profile.Edit1
import com.example.jetsnack.ui.home.Profile.EditSpec
import com.example.jetsnack.ui.home.Search.CategoryDetail
import com.example.jetsnack.ui.home.Search.CategorySearch
import com.example.jetsnack.ui.home.addHomeGraph
import com.example.jetsnack.ui.navigation.JetsnackNavController
import com.example.jetsnack.ui.navigation.MainDestinations
import com.example.jetsnack.ui.navigation.rememberJetsnackNavController
import com.example.jetsnack.ui.snackdetail.SnackDetail
import com.example.jetsnack.ui.theme.JetnewsTheme

val LocalNavController = staticCompositionLocalOf<JetsnackNavController> { // 定义全局LocalNavController，实现自定义导航，作用范围涵盖整个应用，不适合于在不同Compose树中应用不同导航栈的情况
    error("No NavController provided")
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun JetsnackApp() {
    JetnewsTheme {
        val feedViewModel: FeedViewModel = hiltViewModel()// 将viewmodel声明在这个级别会一定程度上浪费内存但可以提高页面切换时的流畅度，因为不用再在每次切换页面是都新建一个viewmodel
        val jetsnackNavController = rememberJetsnackNavController()
        val context = LocalContext.current
        val appContext = context.applicationContext
        val db = Room.databaseBuilder(
            appContext,
            AppDatabase::class.java, "database-name"
        ).build()
        val postDao = db.postDao()
        CompositionLocalProvider(LocalNavController provides jetsnackNavController) {
            NavHost(
                navController = jetsnackNavController.navController,
                startDestination = MainDestinations.HOME_ROUTE
            ) {
                jetsnackNavGraph( //传递导航方法本身，可被重复利用（授予渔），但需要嵌套传参
                    onSnackSelected = jetsnackNavController::navigateToSnackDetail,
                    onPostSelected = jetsnackNavController::navigateToPostDetail,
                    onCategorySelected = jetsnackNavController::navigateToCategoryDetail,
                    upPress = jetsnackNavController::upPress,
                    onNavigateToRoute = jetsnackNavController::navigateToBottomBarRoute,
                    onNavigateToCategorySearch = jetsnackNavController::navigateToCategorySearch,
                    onNavigateToEdit = jetsnackNavController::navigateToEdit,
                    onNavigateToEditSpec = jetsnackNavController::navigateToEditSpec,
                    onNavigateToOrder =  jetsnackNavController::navigateToOrder,
                    feedViewModel,
                    postDao,
                )  // NavHost里面包NavGraph，NavHost本质上就是个工具人，关键在于NavHost内的composable块
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun NavGraphBuilder.jetsnackNavGraph(
    onSnackSelected: (Long, NavBackStackEntry) -> Unit,
    onPostSelected: (String, NavBackStackEntry) -> Unit,
    onCategorySelected: (String, NavBackStackEntry) -> Unit,
    upPress: () -> Unit,
    onNavigateToRoute: (String) -> Unit,
    onNavigateToCategorySearch: (NavBackStackEntry) -> Unit,
    onNavigateToEdit:() -> Unit,
    onNavigateToEditSpec: (String, String) -> Unit,
    onNavigateToOrder: (Long, Boolean) -> Unit,
    feedViewModel: FeedViewModel,
    postDao: PostDao,
) {
    navigation(
        route = MainDestinations.HOME_ROUTE,
        startDestination =
                MainDestinations.LOGIN_ROUTE
//        HomeSections.FEED.route+"/"
    ) {
        addHomeGraph(onSnackSelected, onPostSelected ,onCategorySelected,
            onNavigateToRoute, onNavigateToEdit, onNavigateToOrder, feedViewModel,
            postDao,
        )
    }

    composable(
        "${MainDestinations.SNACK_DETAIL_ROUTE}/{${MainDestinations.SNACK_ID_KEY}}",
        arguments = listOf(navArgument(MainDestinations.SNACK_ID_KEY) { type = NavType.LongType })
    ) { backStackEntry ->
        val arguments = requireNotNull(backStackEntry.arguments)
        val snackId = arguments.getLong(MainDestinations.SNACK_ID_KEY)
        SnackDetail(snackId, upPress)
    }
    composable(
        "${MainDestinations.CATEGORY_DETAIL_ROUTE}/{${MainDestinations.CATEGORY_ID_KEY}}",
        arguments = listOf(navArgument(MainDestinations.CATEGORY_ID_KEY) { type = NavType.StringType })
    ) { backStackEntry ->
        val arguments = requireNotNull(backStackEntry.arguments)
        val categoryName = arguments.getString(MainDestinations.CATEGORY_ID_KEY)
        CategoryDetail(categoryName?:"Nuts", upPress, onSnackClick = { id -> onSnackSelected(id, backStackEntry) }, { onNavigateToCategorySearch(backStackEntry)})
    }

    composable("CategorySearchScreen"){backStackEntry ->
        CategorySearch(onSnackClick = { id -> onSnackSelected(id, backStackEntry)},upPress  )
    }

    composable("EditInfo"){backStackEntry ->
        Edit1({ onNavigateToRoute("home/profile/fromprofile") }, onNavigateToEditSpec)
    }

    composable("${MainDestinations.POST_DETAIL_ROUTE}/{${MainDestinations.POST_ID_KEY}}",
        arguments =listOf(navArgument(MainDestinations.POST_ID_KEY) { type = NavType.StringType })
    ){backStackEntry ->
        val arguments = requireNotNull(backStackEntry.arguments)
        val postId = arguments.getString(MainDestinations.POST_ID_KEY)
        if (postId != null) {
            ArticleScreen(
                postId = postId,
                isExpandedScreen = false,
                onBack = upPress,
                postDao
                )
        }
    }

    composable("${MainDestinations.EDIT_ROUTE}/{${MainDestinations.EDIT_SPEC}}/{${MainDestinations.EDIT_CONTENT}}",
        arguments = listOf(navArgument(MainDestinations.EDIT_SPEC) { type = NavType.StringType },
                           navArgument(MainDestinations.EDIT_CONTENT) {
                               type = NavType.StringType
                           }))
    {
        backStackEntry ->
        val arguments = requireNotNull(backStackEntry.arguments)
        val spec = arguments.getString(MainDestinations.EDIT_SPEC)
        val specContent = arguments.getString(MainDestinations.EDIT_CONTENT)
        Log.i("specContent", specContent?.dropLast(1).toString())
        EditSpec(onNavigateToEdit, spec?:"", specContent?:"")
    }

    composable("Order/{orderTd}/{needToPay}", arguments = listOf(
        navArgument("orderTd") { type = NavType.LongType },
        navArgument("needToPay") { type = NavType.BoolType }

    )){ backStackEntry ->
        val arguments = requireNotNull(backStackEntry.arguments)
        val orderTd = arguments.getLong("orderTd")
        val needToPay = arguments.getBoolean("needToPay")
        Order({ id -> onSnackSelected(id, backStackEntry)},  upPress, orderTd, needToPay, onNavigateToRoute)
    }
}
