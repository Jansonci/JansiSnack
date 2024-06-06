package com.example.jetsnack.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

/**
 * Destinations used in the [JetsnackApp].
 */
object MainDestinations {
    const val HOME_ROUTE = "home"
    const val SNACK_DETAIL_ROUTE = "snack"
    const val SNACK_ID_KEY = "snackId"
    const val CATEGORY_DETAIL_ROUTE = "category"
    const val CATEGORY_ID_KEY = "categoryId"
    const val POST_DETAIL_ROUTE = "post"
    const val POST_ID_KEY = "postId"
    const val LOGIN_ROUTE = "login"
    const val EDIT_ROUTE = "edit"
    const val EDIT_SPEC = "spec"
    const val EDIT_CONTENT = "speContent"
}

/**
 * Remembers and creates an instance of [JetsnackNavController]
 */
@Composable
fun rememberJetsnackNavController(
    navController: NavHostController = rememberNavController()
): JetsnackNavController = remember(navController) {
    JetsnackNavController(navController)
}

/**
 * Responsible for holding UI Navigation logic.
 */
@Stable
class JetsnackNavController(
    val navController: NavHostController,
) {// 将一个navController作为属性进行包装，本质是一个定制化的navController，和jetnews不同，这里传递的是navController本身，而不是导航的路径，所以他可以在任何地方被重复使用

    // ----------------------------------------------------------
    // Navigation state source of truth
    // ----------------------------------------------------------

    private val currentRoute: String?
        get() = navController.currentDestination?.route

    fun upPress() {
        navController.navigateUp()
    }

    fun navigateToBottomBarRoute(route: String) {
        if (route.substringBeforeLast("/") != currentRoute.toString().substringBeforeLast("/")) {
            navController.popBackStack()
            navController.navigate(route) {
                launchSingleTop = true
                restoreState = false//此处修改为true会使得页面状态在多次导航之间得到保留，但会影响动画
                // Pop up backstack to the first destination and save state. This makes going back
                // to the start destination when pressing back in any other bottom tab.
                popUpTo(findStartDestination(navController.graph).id) {
                    saveState = false
                    inclusive = true
                }
            }
        }
    }

    fun navigateToSnackDetail(snackId: Long, from: NavBackStackEntry) {
        // In order to discard duplicated navigation events, we check the Lifecycle
        if (from.lifecycleIsResumed()) {
            navController.navigate("${MainDestinations.SNACK_DETAIL_ROUTE}/$snackId")
        }
    }

    fun navigateToCategoryDetail(categoryName: String, from: NavBackStackEntry) {
        if (from.lifecycleIsResumed()) {
            navController.navigate("${MainDestinations.CATEGORY_DETAIL_ROUTE}/$categoryName")
        }
    }

    fun navigateToCategorySearch(from: NavBackStackEntry){
        if (from.lifecycleIsResumed()) {
            navController.navigate("CategorySearchScreen")
        }
    }

    fun navigateToPostDetail(postId: String, from: NavBackStackEntry) {
        if (from.lifecycleIsResumed()) {
            navController.navigate("${MainDestinations.POST_DETAIL_ROUTE}/$postId")
        }
    }

    fun navigateToPostDetailForLocal(postId: String) {
            navController.navigate("${MainDestinations.POST_DETAIL_ROUTE}/$postId")
    }

    fun navigateToEdit(){
        navController.popBackStack()
        navController.navigate("EditInfo")
    }

    fun navigateToEditSpec(spec:String, specContent:String){
        val specContent1 = "$specContent?"
        navController.popBackStack()
        navController.navigate("${MainDestinations.EDIT_ROUTE}/$spec/$specContent1")
    }

    fun navigateToOrder(orderTd: Long, needToPay: Boolean){
        navController.navigate("Order/$orderTd/$needToPay")
    }
}

/**
 * If the lifecycle is not resumed it means this NavBackStackEntry already processed a nav event.
 *
 * This is used to de-duplicate navigation events.
 */
private fun NavBackStackEntry.lifecycleIsResumed() =
    this.lifecycle.currentState == Lifecycle.State.RESUMED

private val NavGraph.startDestination: NavDestination?
    get() = findNode(startDestinationId)

/**
 * Copied from similar function in NavigationUI.kt
 *
 * https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:navigation/navigation-ui/src/main/java/androidx/navigation/ui/NavigationUI.kt
 */
private tailrec fun findStartDestination(graph: NavDestination): NavDestination {
    return if (graph is NavGraph) findStartDestination(graph.startDestination!!) else graph
}
