package com.sumas.wooheso.features.feed.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.VideocamOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sumas.wooheso.core.designsystem.AppColors
import com.sumas.wooheso.core.designsystem.frostedGlass
import com.sumas.wooheso.features.feed.presentation.widgets.CategorySelector
import com.sumas.wooheso.features.feed.presentation.widgets.ProductFeedCard

import com.sumas.wooheso.core.util.rememberShareLauncher
import com.sumas.wooheso.core.util.rememberUrlLauncher
import com.sumas.wooheso.data.repository.ShowroomRepositories
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch

import androidx.compose.material.icons.filled.BookmarkBorder

@Composable
fun FeedScreen(
    isFeedActive: Boolean = true,
    onNavigateToDetail: (String) -> Unit = {},
    onNavigateToCompany: (String) -> Unit = {},
    onNavigateToRegistration: () -> Unit = {},
    onNavigateToSaved: () -> Unit = {},
    onSaveRequest: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf("전체") }
    val urlLauncher = rememberUrlLauncher()
    val shareLauncher = rememberShareLauncher()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val products by ShowroomRepositories.products.products.collectAsState()
    var loadError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        try {
            ShowroomRepositories.products.fetchProducts()
            loadError = null
        } catch (e: Exception) {
            loadError = "전시 상품을 불러오지 못했습니다. 다시 시도해 주세요."
        }
    }

    val filteredProducts = remember(selectedCategory, products) {
        if (selectedCategory == "전체") {
            products
        } else {
            products.filter { it.category == selectedCategory }
        }
    }

    val pagerState = rememberPagerState(pageCount = { filteredProducts.size })

    LaunchedEffect(selectedCategory) {
        if (filteredProducts.isNotEmpty()) {
            pagerState.scrollToPage(0)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // 1. Shorts / TikTok Style Fast Vertical Snap Feed
        if (filteredProducts.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.VideocamOff,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.4f),
                    modifier = Modifier.size(56.dp)
                )
                Spacer(modifier = Modifier.height(14.dp))
                if (loadError != null) Text(loadError!!, color = Color.White)
                if (loadError != null) TextButton(onClick = {
                    scope.launch {
                        try {
                            ShowroomRepositories.products.fetchProducts()
                            loadError = null
                        } catch (_: Exception) { }
                    }
                }) { Text("다시 시도") }
                Text(
                    text = "해당 카테고리의 전시 쇼츠 카드가 없습니다.",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 15.sp
                )
            }
        } else {
            VerticalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                beyondViewportPageCount = 1
            ) { pageIndex ->
                val product = filteredProducts[pageIndex]
                ProductFeedCard(
                    product = product,
                    isCurrentPage = isFeedActive && (pageIndex == pagerState.currentPage),
                    onDetailClick = onNavigateToDetail,
                    onCompanyClick = onNavigateToCompany,
                    onInquiryClick = { prod ->
                        val targetUrl = prod.inquiryUrl?.takeIf { it.startsWith("https://") }
                        if (targetUrl == null || !urlLauncher(targetUrl)) {
                            scope.launch { snackbarHostState.showSnackbar("문의 링크를 열 수 없습니다.") }
                        } else {
                            scope.launch {
                                try {
                                    ShowroomRepositories.products.incrementInquiryClick(prod.id)
                                } catch (e: Exception) {
                                    snackbarHostState.showSnackbar("문의 기록을 저장하지 못했습니다.")
                                }
                            }
                        }
                    },
                    onShareClick = { prod ->
                        shareLauncher(
                            "[우회소] ${prod.title} 전시 카드를 확인해보세요!\nhttps://wooheso.com/product/${prod.id}",
                            "[우회소] ${prod.title}"
                        )
                    },
                    onSaveClick = { prod -> onSaveRequest(prod.id) }
                )
            }
        }
        SnackbarHost(snackbarHostState, modifier = Modifier.align(Alignment.BottomCenter))

        // 2. Floating Top Frosted Header Layer (Wordmark + Actions + Category Selector)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(top = 8.dp)
                .align(Alignment.TopCenter)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Frosted Glass Brand Capsule
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .frostedGlass(shape = RoundedCornerShape(24.dp))
                        .padding(horizontal = 14.dp, vertical = 7.dp)
                ) {
                    Text(
                        text = "우회소",
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp,
                        letterSpacing = (-0.3).sp
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(AppColors.Accent)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // Saved Bookmarks Button
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .frostedGlass(shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(onClick = onNavigateToSaved) {
                        Icon(
                            imageVector = Icons.Filled.BookmarkBorder,
                            contentDescription = "Saved Showrooms",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Search Action Button
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .frostedGlass(shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(onClick = { /* Search query */ }) {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Search",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Add Product Button
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .frostedGlass(shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(onClick = onNavigateToRegistration) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Add Product",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Category Filter Chips
            CategorySelector(
                selectedCategory = selectedCategory,
                onCategorySelected = { category ->
                    selectedCategory = category
                }
            )
        }
    }
}
