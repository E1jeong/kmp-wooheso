package com.sumas.wooheso.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.sumas.wooheso.features.company.presentation.CompanyProfileScreen
import com.sumas.wooheso.features.company.registration.CompanyRegistrationScreen
import com.sumas.wooheso.features.feed.presentation.FeedScreen
import com.sumas.wooheso.features.product.presentation.ProductDetailScreen
import com.sumas.wooheso.features.product.registration.ProductRegistrationScreen
import com.sumas.wooheso.features.saved.SavedListScreen

class WoohesoNavController(initialRoute: WoohesoRoute = WoohesoRoute.Feed) {
    private val backStack = mutableStateListOf<WoohesoRoute>(initialRoute)

    val currentRoute: WoohesoRoute
        get() = backStack.lastOrNull() ?: WoohesoRoute.Feed

    val isFeedActive: Boolean
        get() = currentRoute is WoohesoRoute.Feed

    fun navigate(route: WoohesoRoute) {
        backStack.add(route)
    }

    fun popBackStack(): Boolean {
        return if (backStack.size > 1) {
            backStack.removeAt(backStack.lastIndex)
            true
        } else {
            false
        }
    }

    fun popToRoot() {
        while (backStack.size > 1) {
            backStack.removeAt(backStack.lastIndex)
        }
    }
}

@Composable
fun rememberWoohesoNavController(initialRoute: WoohesoRoute = WoohesoRoute.Feed): WoohesoNavController {
    return remember { WoohesoNavController(initialRoute) }
}

@Composable
fun WoohesoNavHost(
    modifier: Modifier = Modifier,
    navController: WoohesoNavController = rememberWoohesoNavController()
) {
    Box(modifier = modifier.fillMaxSize()) {
        AnimatedContent(
            targetState = navController.currentRoute,
            transitionSpec = {
                (slideInHorizontally { width -> width / 2 } + fadeIn())
                    .togetherWith(slideOutHorizontally { width -> -width / 2 } + fadeOut())
            },
            label = "WoohesoNavTransition"
        ) { targetRoute ->
            when (targetRoute) {
                is WoohesoRoute.Feed -> {
                    FeedScreen(
                        isFeedActive = navController.isFeedActive,
                        onNavigateToDetail = { productId ->
                            navController.navigate(WoohesoRoute.ProductDetail(productId))
                        },
                        onNavigateToCompany = { companyId ->
                            navController.navigate(WoohesoRoute.CompanyProfile(companyId))
                        },
                        onNavigateToRegistration = {
                            navController.navigate(WoohesoRoute.CompanyRegistration)
                        },
                        onNavigateToSaved = {
                            navController.navigate(WoohesoRoute.SavedList)
                        }
                    )
                }
                is WoohesoRoute.ProductDetail -> {
                    ProductDetailScreen(
                        productId = targetRoute.productId,
                        onNavigateBack = {
                            navController.popBackStack()
                        },
                        onNavigateToCompany = { companyId ->
                            navController.navigate(WoohesoRoute.CompanyProfile(companyId))
                        }
                    )
                }
                is WoohesoRoute.CompanyProfile -> {
                    CompanyProfileScreen(
                        companyId = targetRoute.companyId,
                        onNavigateBack = {
                            navController.popBackStack()
                        },
                        onNavigateToProductDetail = { productId ->
                            navController.navigate(WoohesoRoute.ProductDetail(productId))
                        }
                    )
                }
                is WoohesoRoute.SavedList -> {
                    SavedListScreen(
                        onNavigateBack = {
                            navController.popBackStack()
                        },
                        onNavigateToProductDetail = { productId ->
                            navController.navigate(WoohesoRoute.ProductDetail(productId))
                        }
                    )
                }
                is WoohesoRoute.CompanyRegistration -> {
                    CompanyRegistrationScreen(
                        onNavigateBack = {
                            navController.popBackStack()
                        },
                        onRegistrationSuccess = { companyId ->
                            navController.navigate(WoohesoRoute.ProductRegistration(companyId))
                        }
                    )
                }
                is WoohesoRoute.ProductRegistration -> {
                    ProductRegistrationScreen(
                        companyId = targetRoute.companyId,
                        onNavigateBack = {
                            navController.popBackStack()
                        },
                        onRegistrationSuccess = { productId ->
                            navController.popToRoot()
                            navController.navigate(WoohesoRoute.ProductDetail(productId))
                        }
                    )
                }
            }
        }
    }
}
