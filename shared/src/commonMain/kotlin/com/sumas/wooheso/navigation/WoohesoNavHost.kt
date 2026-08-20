package com.sumas.wooheso.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.sumas.wooheso.features.company.presentation.CompanyProfileScreen
import com.sumas.wooheso.features.feed.presentation.FeedScreen
import com.sumas.wooheso.features.product.presentation.ProductDetailScreen

@Composable
fun WoohesoNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Feed is active only when it is the top destination
    val isFeedActive = currentRoute?.contains("Feed") == true || currentRoute == null

    NavHost(
        navController = navController,
        startDestination = WoohesoRoute.Feed,
        enterTransition = {
            slideInHorizontally(
                initialOffsetX = { fullWidth -> fullWidth },
                animationSpec = tween(300)
            ) + fadeIn(animationSpec = tween(300))
        },
        exitTransition = {
            slideOutHorizontally(
                targetOffsetX = { fullWidth -> -fullWidth / 3 },
                animationSpec = tween(300)
            ) + fadeOut(animationSpec = tween(300))
        },
        popEnterTransition = {
            slideInHorizontally(
                initialOffsetX = { fullWidth -> -fullWidth / 3 },
                animationSpec = tween(300)
            ) + fadeIn(animationSpec = tween(300))
        },
        popExitTransition = {
            slideOutHorizontally(
                targetOffsetX = { fullWidth -> fullWidth },
                animationSpec = tween(300)
            ) + fadeOut(animationSpec = tween(300))
        },
        modifier = modifier
    ) {
        composable<WoohesoRoute.Feed> {
            FeedScreen(
                isFeedActive = isFeedActive,
                onNavigateToDetail = { productId ->
                    navController.navigate(WoohesoRoute.ProductDetail(productId))
                },
                onNavigateToCompany = { companyId ->
                    navController.navigate(WoohesoRoute.CompanyProfile(companyId))
                }
            )
        }

        composable<WoohesoRoute.ProductDetail> { backStackEntry ->
            val route = backStackEntry.toRoute<WoohesoRoute.ProductDetail>()
            ProductDetailScreen(
                productId = route.productId,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToCompany = { companyId ->
                    navController.navigate(WoohesoRoute.CompanyProfile(companyId))
                }
            )
        }

        composable<WoohesoRoute.CompanyProfile> { backStackEntry ->
            val route = backStackEntry.toRoute<WoohesoRoute.CompanyProfile>()
            CompanyProfileScreen(
                companyId = route.companyId,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToProductDetail = { productId ->
                    navController.navigate(WoohesoRoute.ProductDetail(productId))
                }
            )
        }
    }
}
