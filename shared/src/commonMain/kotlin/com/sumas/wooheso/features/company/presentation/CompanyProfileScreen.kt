package com.sumas.wooheso.features.company.presentation

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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.sumas.wooheso.core.designsystem.AppColors
import com.sumas.wooheso.core.util.rememberUrlLauncher
import com.sumas.wooheso.data.mock.MockFeedData

@Composable
fun CompanyProfileScreen(
    companyId: String,
    onNavigateBack: () -> Unit,
    onNavigateToProductDetail: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val company = remember(companyId) {
        MockFeedData.mockCompanies.find { it.companyId == companyId }
            ?: MockFeedData.mockCompanies.first()
    }

    val companyProducts = remember(companyId) {
        val filtered = MockFeedData.mockProducts.filter { it.companyId == companyId }
        if (filtered.isEmpty()) MockFeedData.mockProducts else filtered
    }

    val urlLauncher = rememberUrlLauncher()

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(AppColors.DarkBackground),
        containerColor = AppColors.DarkBackground,
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AppColors.PrimaryDark)
                    .statusBarsPadding()
                    .padding(horizontal = 4.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "뒤로가기",
                            tint = AppColors.TextWhite
                        )
                    }

                    Text(
                        text = "기업 / 브랜드 프로필",
                        color = AppColors.TextWhite,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.size(48.dp))
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding()
        ) {
            // 1. Company Header Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AppColors.PrimaryDark)
                    .padding(horizontal = 20.dp, vertical = 28.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Logo / Avatar
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(76.dp)
                            .clip(CircleShape)
                            .background(AppColors.PrimaryLight)
                            .border(2.dp, AppColors.Accent, CircleShape)
                    ) {
                        if (company.logoUrl != null) {
                            AsyncImage(
                                model = company.logoUrl,
                                contentDescription = company.name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Business,
                                contentDescription = null,
                                tint = AppColors.Accent,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Company Name & Verified Badge
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = company.name,
                            color = AppColors.TextWhite,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = "인증 기업",
                            tint = AppColors.Accent,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Tagline
                    Text(
                        text = company.tagline,
                        color = AppColors.TextWhiteDim,
                        fontSize = 13.sp
                    )

                    if (company.description != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = company.description,
                            color = AppColors.TextWhiteDim.copy(alpha = 0.85f),
                            fontSize = 12.sp,
                            lineHeight = 18.sp,
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Action Buttons (Website & Kakao)
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedButton(
                            onClick = {
                                urlLauncher(company.websiteUrl ?: "https://wooheso.com")
                            },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.height(42.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Language,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = AppColors.TextWhite
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "공식 웹사이트",
                                color = AppColors.TextWhite,
                                fontSize = 13.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Button(
                            onClick = {
                                val kakaoUrl = company.snsLinks["kakao"] ?: "https://pf.kakao.com"
                                urlLauncher(kakaoUrl)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AppColors.Accent,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.height(42.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Chat,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "카카오 채널",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Registered Exhibition Cards Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "등록된 전시 카드",
                        color = AppColors.TextWhite,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(AppColors.Accent.copy(alpha = 0.2f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "${companyProducts.size}개",
                            color = AppColors.Accent,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Product Cards List
                companyProducts.forEach { product ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = AppColors.PrimaryDark),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                            .border(1.dp, AppColors.BorderGlass, RoundedCornerShape(12.dp))
                            .clickable { onNavigateToProductDetail(product.id) }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                        ) {
                            // Thumbnail
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(AppColors.PrimaryLight)
                            ) {
                                AsyncImage(
                                    model = product.imageUrls.firstOrNull(),
                                    contentDescription = product.title,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            // Details
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = product.title,
                                    color = AppColors.TextWhite,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = product.formattedPrice,
                                    color = AppColors.Accent,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = "상세보기",
                                tint = AppColors.TextWhiteDim,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
