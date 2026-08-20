package com.sumas.wooheso.features.feed.presentation.widgets

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Verified
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.sumas.wooheso.core.designsystem.AppColors
import com.sumas.wooheso.core.designsystem.bottomProtectionGradient
import com.sumas.wooheso.core.designsystem.frostedGlass
import com.sumas.wooheso.core.designsystem.topProtectionGradient
import com.sumas.wooheso.core.media.VideoPlayer
import com.sumas.wooheso.data.model.ProductCardModel
import kotlinx.coroutines.delay

import androidx.compose.runtime.collectAsState
import com.sumas.wooheso.data.repository.SavedProductRepository

@Composable
fun ProductFeedCard(
    product: ProductCardModel,
    isCurrentPage: Boolean,
    onDetailClick: (String) -> Unit = {},
    onCompanyClick: (String) -> Unit = {},
    onInquiryClick: (ProductCardModel) -> Unit = {},
    onShareClick: (ProductCardModel) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var isPlaying by remember(isCurrentPage) { mutableStateOf(isCurrentPage) }
    val savedProductIds by SavedProductRepository.savedProductIds.collectAsState()
    val isSaved = savedProductIds.contains(product.id)
    var saveCount by remember { mutableStateOf(product.saveCount) }
    var showRippleIndicator by remember { mutableStateOf(false) }

    LaunchedEffect(isCurrentPage) {
        isPlaying = isCurrentPage
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                if (product.isVideo) {
                    isPlaying = !isPlaying
                    showRippleIndicator = true
                } else {
                    onDetailClick(product.id)
                }
            }
    ) {
        // 1. Video Player or High-Res Image Background
        if (product.isVideo && product.videoUrl != null) {
            VideoPlayer(
                url = product.videoUrl,
                isPlaying = isPlaying,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            AsyncImage(
                model = product.imageUrls.firstOrNull(),
                contentDescription = product.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        // 2. Play / Pause Center Ripple Animation
        if (showRippleIndicator) {
            LaunchedEffect(showRippleIndicator) {
                delay(700)
                showRippleIndicator = false
            }
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.55f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Filled.PlayArrow else Icons.Filled.Pause,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }
        }

        // 3. Top & Bottom Cinematic Gradient Overlays
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .align(Alignment.TopCenter)
                .topProtectionGradient()
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .align(Alignment.BottomCenter)
                .bottomProtectionGradient()
        )

        // 4. Right Action Bar (TikTok / Shorts Idiom)
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .navigationBarsPadding()
                .padding(end = 14.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Company Avatar
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(AppColors.PrimaryDark)
                    .border(2.dp, AppColors.Accent, CircleShape)
                    .clickable { onCompanyClick(product.companyId) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = product.companyName.take(1),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
            }

            // Save / Bookmark
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable {
                    val saved = SavedProductRepository.toggleSave(product.id)
                    saveCount += if (saved) 1 else -1
                }
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.4f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isSaved) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                        contentDescription = "Save",
                        tint = if (isSaved) AppColors.Accent else Color.White,
                        modifier = Modifier.size(26.dp)
                    )
                }
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = "$saveCount",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Share
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable { onShareClick(product) }
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.4f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Share,
                        contentDescription = "Share",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = "공유",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // 1-Sec Inquiry Button (Glowing Accent Gradient)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable { onInquiryClick(product) }
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(AppColors.AccentGradientStart, AppColors.AccentGradientEnd)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Bolt,
                        contentDescription = "Inquiry",
                        tint = Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                }
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = "1초 문의",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // 5. Bottom Left Metadata & Details Pill
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth(0.76f)
                .navigationBarsPadding()
                .padding(start = 16.dp, bottom = 20.dp),
            horizontalAlignment = Alignment.Start
        ) {
            // Company Verified Chip
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .frostedGlass(shape = RoundedCornerShape(16.dp))
                    .clickable { onCompanyClick(product.companyId) }
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Text(
                    text = "@" + product.companyName,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Filled.Verified,
                    contentDescription = "Verified",
                    tint = AppColors.Accent,
                    modifier = Modifier.size(14.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Product Title
            Text(
                text = product.title,
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 25.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            // 1-Line Description Only (Subtractive Feed Rule)
            Text(
                text = product.description,
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 13.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Glassmorphic "상세보기 >" Pill Button
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .frostedGlass(
                        shape = RoundedCornerShape(16.dp),
                        backgroundColor = Color.White.copy(alpha = 0.18f),
                        borderColor = Color.White.copy(alpha = 0.35f)
                    )
                    .clickable { onDetailClick(product.id) }
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "상세보기",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Filled.ChevronRight,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}
