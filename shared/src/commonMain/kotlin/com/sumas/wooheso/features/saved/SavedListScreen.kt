package com.sumas.wooheso.features.saved

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.sumas.wooheso.core.designsystem.AppColors
import com.sumas.wooheso.data.model.ProductCardModel
import com.sumas.wooheso.data.repository.SavedProductRepository
import com.sumas.wooheso.data.repository.SupabaseProductRepository

@Composable
fun SavedListScreen(
    onNavigateBack: () -> Unit,
    onNavigateToProductDetail: (productId: String) -> Unit,
    modifier: Modifier = Modifier,
    productRepository: SupabaseProductRepository = remember { SupabaseProductRepository() }
) {
    val savedIds by SavedProductRepository.savedProductIds.collectAsState()
    val allProducts by productRepository.products.collectAsState()

    val savedProducts = remember(savedIds, allProducts) {
        allProducts.filter { savedIds.contains(it.id) }
    }

    Scaffold(
        containerColor = AppColors.DarkBackground,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = AppColors.TextPrimary
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "저장한 쇼룸 (${savedProducts.size})",
                    color = AppColors.TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        modifier = modifier
    ) { innerPadding ->
        if (savedProducts.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(horizontal = 32.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(AppColors.CardBackground)
                            .border(1.dp, AppColors.GlassBorder, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.BookmarkBorder,
                            contentDescription = null,
                            tint = AppColors.TextSecondary,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "아직 저장한 쇼룸이 없습니다",
                        color = AppColors.TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "피드에서 관심 있는 제품이나 브랜드를\n북마크하여 언제든 모아보세요.",
                        color = AppColors.TextSecondary,
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                items(savedProducts, key = { it.id }) { product ->
                    SavedProductCard(
                        product = product,
                        onClick = { onNavigateToProductDetail(product.id) },
                        onToggleSave = { SavedProductRepository.toggleSave(product.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun SavedProductCard(
    product: ProductCardModel,
    onClick: () -> Unit,
    onToggleSave: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = AppColors.CardBackground),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, AppColors.GlassBorder, RoundedCornerShape(12.dp))
            .clickable { onClick() }
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
            ) {
                AsyncImage(
                    model = product.imageUrls.firstOrNull() ?: "",
                    contentDescription = product.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Category Tag
                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(AppColors.DarkBackground.copy(alpha = 0.8f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                        .align(Alignment.TopStart)
                ) {
                    Text(
                        text = product.category,
                        color = AppColors.ElectricCyan,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Bookmark Toggle
                Box(
                    modifier = Modifier
                        .padding(6.dp)
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(AppColors.DarkBackground.copy(alpha = 0.7f))
                        .clickable { onToggleSave() }
                        .align(Alignment.TopEnd),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Bookmark,
                        contentDescription = "Remove Bookmark",
                        tint = AppColors.ElectricCyan,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = product.companyName,
                    color = AppColors.TextSecondary,
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = product.title,
                    color = AppColors.TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
