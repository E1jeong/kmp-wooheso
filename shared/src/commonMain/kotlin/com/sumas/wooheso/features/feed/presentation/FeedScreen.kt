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
import com.sumas.wooheso.data.mock.MockFeedData
import com.sumas.wooheso.features.feed.presentation.widgets.CategorySelector
import com.sumas.wooheso.features.feed.presentation.widgets.ProductFeedCard

@Composable
fun FeedScreen(
    onNavigateToDetail: (String) -> Unit = {},
    onNavigateToCompany: (String) -> Unit = {},
    onNavigateToRegistration: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf("전체") }

    val filteredProducts = remember(selectedCategory) {
        if (selectedCategory == "전체") {
            MockFeedData.mockProducts
        } else {
            MockFeedData.mockProducts.filter { it.category == selectedCategory }
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
                    isCurrentPage = (pageIndex == pagerState.currentPage),
                    onDetailClick = onNavigateToDetail,
                    onCompanyClick = onNavigateToCompany,
                    onInquiryClick = { /* Will connect in Phase 2 URL launcher */ },
                    onShareClick = { /* Will connect in Phase 2 Share launcher */ }
                )
            }
        }

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
