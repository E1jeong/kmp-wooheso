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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.ui.Alignment
import androidx.compose.runtime.rememberCoroutineScope
import com.sumas.wooheso.core.supabase.SupabaseClientProvider
import com.sumas.wooheso.features.auth.rememberGoogleIdTokenRequester
import com.sumas.wooheso.features.auth.signInWithGoogle
import com.sumas.wooheso.data.repository.ShowroomRepositories
import com.sumas.wooheso.data.repository.SavedProductRepository
import androidx.compose.runtime.LaunchedEffect
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.launch
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
    navController: WoohesoNavController = rememberWoohesoNavController(),
    googleWebClientId: String = ""
) {
    val auth = SupabaseClientProvider.client.auth
    val sessionStatus by auth.sessionStatus.collectAsState()
    val requestGoogleToken = rememberGoogleIdTokenRequester()
    val scope = rememberCoroutineScope()
    var pendingRoute by remember { mutableStateOf<WoohesoRoute?>(null) }
    var pendingSaveId by remember { mutableStateOf<String?>(null) }
    var signingIn by remember { mutableStateOf(false) }
    var authError by remember { mutableStateOf<String?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    fun navigateAfterAuth(route: WoohesoRoute) {
        val userId = auth.currentUserOrNull()?.id
        if (userId == null) {
            pendingRoute = route
        } else if (route == WoohesoRoute.CompanyRegistration) {
            scope.launch {
                try {
                    val company = ShowroomRepositories.companies.getCompanyOwnedBy(userId)
                    navController.navigate(company?.let { WoohesoRoute.ProductRegistration(it.companyId) } ?: route)
                } catch (e: Exception) {
                    snackbarHostState.showSnackbar(e.message ?: "회사 정보를 불러오지 못했습니다.")
                }
            }
        } else navController.navigate(route)
    }
    fun saveProduct(productId: String) {
        if (auth.currentUserOrNull() == null) pendingSaveId = productId
        else scope.launch {
            try {
                SavedProductRepository.toggleSavePersisted(productId)
            } catch (e: Exception) {
                snackbarHostState.showSnackbar(e.message ?: "저장에 실패했습니다.")
            }
        }
    }
    LaunchedEffect(sessionStatus) {
        try { SavedProductRepository.loadSaved() } catch (_: Exception) { }
    }
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
                            navigateAfterAuth(WoohesoRoute.CompanyRegistration)
                        },
                        onNavigateToSaved = {
                            navigateAfterAuth(WoohesoRoute.SavedList)
                        },
                        onSaveRequest = ::saveProduct
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
                        },
                        onSaveRequest = ::saveProduct
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
        if (pendingRoute != null || pendingSaveId != null) {
            AlertDialog(
                onDismissRequest = {
                    if (!signingIn) {
                        pendingRoute = null
                        pendingSaveId = null
                    }
                },
                title = { Text("Google 로그인") },
                text = {
                    if (signingIn) CircularProgressIndicator()
                    else Text(authError ?: "저장하거나 상품을 등록하려면 로그인해 주세요.")
                },
                confirmButton = {
                    TextButton(onClick = {
                        if (!signingIn) {
                            signingIn = true
                            authError = null
                            scope.launch {
                                try {
                                    signInWithGoogle(requestGoogleToken(googleWebClientId))
                                    val route = pendingRoute
                                    val saveId = pendingSaveId
                                    pendingRoute = null
                                    pendingSaveId = null
                                    route?.let(::navigateAfterAuth)
                                    if (saveId != null) {
                                        try {
                                            SavedProductRepository.loadSaved()
                                            SavedProductRepository.toggleSavePersisted(saveId)
                                        } catch (e: Exception) {
                                            snackbarHostState.showSnackbar(e.message ?: "저장에 실패했습니다.")
                                        }
                                    }
                                } catch (e: Exception) {
                                    authError = e.message ?: "로그인에 실패했습니다."
                                } finally {
                                    signingIn = false
                                }
                            }
                        }
                    }) { Text("계속") }
                },
                dismissButton = {
                    TextButton(onClick = { pendingRoute = null; pendingSaveId = null }) { Text("취소") }
                }
            )
        }
        SnackbarHost(snackbarHostState, modifier = Modifier.align(Alignment.BottomCenter))
    }
}
