package com.sumas.wooheso.features.product.registration

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sumas.wooheso.core.designsystem.AppColors
import com.sumas.wooheso.data.model.PriceType
import com.sumas.wooheso.data.model.ProductCardModel
import com.sumas.wooheso.data.repository.SupabaseProductRepository
import com.sumas.wooheso.data.repository.SupabaseStorageRepository
import com.sumas.wooheso.data.repository.ShowroomRepositories
import com.sumas.wooheso.core.media.SelectedImage
import com.sumas.wooheso.core.media.SelectedVideo
import com.sumas.wooheso.core.media.rememberImagePicker
import com.sumas.wooheso.core.media.rememberVideoPicker
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import kotlinx.coroutines.launch

private val PRODUCT_CATEGORIES = listOf(
    "AI/Tech", "Hardware", "SaaS", "Creative", "Lifestyle", "Industrial"
)

@OptIn(ExperimentalLayoutApi::class, ExperimentalUuidApi::class)
@Composable
fun ProductRegistrationScreen(
    companyId: String,
    onNavigateBack: () -> Unit,
    onRegistrationSuccess: (productId: String) -> Unit,
    modifier: Modifier = Modifier,
    productRepository: SupabaseProductRepository = remember { SupabaseProductRepository() }
) {
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    val pickImages = rememberImagePicker()
    val pickVideo = rememberVideoPicker()
    val storageRepository = remember { SupabaseStorageRepository() }

    var title by remember { mutableStateOf("") }
    var shortDescription by remember { mutableStateOf("") }
    var fullDescription by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(PRODUCT_CATEGORIES.first()) }
    var priceType by remember { mutableStateOf(PriceType.INQUIRY) }
    var priceText by remember { mutableStateOf("") }
    var feature1 by remember { mutableStateOf("") }
    var feature2 by remember { mutableStateOf("") }
    var feature3 by remember { mutableStateOf("") }
    var inquiryUrl by remember { mutableStateOf("") }
    var videoUrl by remember { mutableStateOf("") }
    var selectedImages by remember { mutableStateOf<List<SelectedImage>>(emptyList()) }
    var selectedVideo by remember { mutableStateOf<SelectedVideo?>(null) }

    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val isFormValid = title.isNotBlank() &&
            shortDescription.isNotBlank() &&
            fullDescription.isNotBlank() &&
            selectedImages.isNotEmpty() &&
            inquiryUrl.trim().startsWith("https://") &&
            (selectedVideo != null || videoUrl.isBlank() || videoUrl.trim().startsWith("https://")) &&
            (priceType == PriceType.INQUIRY || priceText.toLongOrNull() != null)

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
                    text = "새 제품 / 쇼룸 전시 등록",
                    color = AppColors.TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(16.dp)
            ) {
                Button(
                    onClick = {
                        if (!isFormValid || isLoading) return@Button
                        isLoading = true
                        errorMessage = null
                        coroutineScope.launch {
                            val keyFeatures = listOf(feature1, feature2, feature3).filter { it.isNotBlank() }
                            val companyName = ShowroomRepositories.companies.getCompanyById(companyId)?.name.orEmpty()
                            val productId = Uuid.random().toString()
                            val imageUrls = mutableListOf<String>()
                            val uploadedNames = mutableListOf<String>()
                            for (image in selectedImages) {
                                val uploaded = storageRepository.uploadProductMedia(
                                    companyId, productId, image.fileName, image.bytes
                                )
                                if (uploaded.isFailure) {
                                    try { storageRepository.deleteProductMedia(companyId, productId, uploadedNames) } catch (_: Exception) { }
                                    isLoading = false
                                    errorMessage = uploaded.exceptionOrNull()?.message ?: "이미지 업로드에 실패했습니다."
                                    return@launch
                                }
                                imageUrls += uploaded.getOrThrow()
                                uploadedNames += image.fileName
                            }
                            val uploadedVideoUrl = selectedVideo?.let { video ->
                                val uploaded = storageRepository.uploadProductMedia(
                                    companyId, productId, video.fileName, video.bytes
                                )
                                if (uploaded.isFailure) {
                                    try { storageRepository.deleteProductMedia(companyId, productId, uploadedNames) } catch (_: Exception) { }
                                    isLoading = false
                                    errorMessage = uploaded.exceptionOrNull()?.message ?: "동영상 업로드에 실패했습니다."
                                    return@launch
                                }
                                uploadedNames += video.fileName
                                uploaded.getOrThrow()
                            }
                            val newProduct = ProductCardModel(
                                id = productId,
                                companyId = companyId,
                                companyName = companyName,
                                title = title.trim(),
                                description = shortDescription.trim(),
                                fullDescription = fullDescription.trim(),
                                imageUrls = imageUrls,
                                videoUrl = uploadedVideoUrl ?: videoUrl.trim().ifBlank { null },
                                category = selectedCategory,
                                keyFeatures = if (keyFeatures.isEmpty()) listOf("핵심 기능 소개 1", "핵심 기능 소개 2", "핵심 기능 소개 3") else keyFeatures,
                                priceType = priceType,
                                price = if (priceType == PriceType.FIXED) priceText.toLongOrNull() else null,
                                inquiryUrl = inquiryUrl.trim()
                            )
                            val result = productRepository.createProduct(newProduct)
                            isLoading = false
                            result.onSuccess {
                                onRegistrationSuccess(it.id)
                            }.onFailure {
                                try { storageRepository.deleteProductMedia(companyId, productId, uploadedNames) } catch (_: Exception) { }
                                errorMessage = it.message ?: "제품 등록에 실패했습니다."
                            }
                        }
                    },
                    enabled = isFormValid && !isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AppColors.ElectricCyan,
                        contentColor = AppColors.DarkBackground,
                        disabledContainerColor = AppColors.CardBackground.copy(alpha = 0.5f),
                        disabledContentColor = AppColors.TextSecondary
                    )
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = AppColors.DarkBackground,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "쇼룸 피드에 전시 등록하기",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Text(
                text = "숏폼 비디오와 핵심 3대 가치로\n고객의 시선을 사로잡는 쇼룸 카드를 만듭니다.",
                color = AppColors.TextSecondary,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Title
            Text(
                text = "제품 / 전시명 *",
                color = AppColors.TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                placeholder = { Text("예: AI 기반 업무 자동화 스마트 비서", color = AppColors.TextSecondary.copy(alpha = 0.6f), fontSize = 14.sp) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = outlinedFieldColors()
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Short Description
            Text(
                text = "피드 노출 한 줄 소개 *",
                color = AppColors.TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = shortDescription,
                onValueChange = { shortDescription = it },
                placeholder = { Text("예: 반복 업무를 90% 줄여주는 솔루션", color = AppColors.TextSecondary.copy(alpha = 0.6f), fontSize = 14.sp) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = outlinedFieldColors()
            )

            Spacer(modifier = Modifier.height(18.dp))

            Text("상세 설명 *", color = AppColors.TextPrimary, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = fullDescription,
                onValueChange = { fullDescription = it },
                minLines = 3,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = outlinedFieldColors()
            )
            Spacer(modifier = Modifier.height(18.dp))

            // Category Selection
            Text(
                text = "카테고리 *",
                color = AppColors.TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PRODUCT_CATEGORIES.forEach { cat ->
                    val isSelected = cat == selectedCategory
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isSelected) AppColors.ElectricCyan.copy(alpha = 0.15f) else AppColors.CardBackground)
                            .border(
                                width = 1.dp,
                                color = if (isSelected) AppColors.ElectricCyan else AppColors.GlassBorder,
                                shape = RoundedCornerShape(20.dp)
                            )
                            .clickable { selectedCategory = cat }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = cat,
                            color = if (isSelected) AppColors.ElectricCyan else AppColors.TextSecondary,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Price Type Selection
            Text(
                text = "가격 정책 (상세 화면 노출) *",
                color = AppColors.TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (priceType == PriceType.INQUIRY) AppColors.ElectricCyan.copy(alpha = 0.15f) else AppColors.CardBackground)
                        .border(
                            1.dp,
                            if (priceType == PriceType.INQUIRY) AppColors.ElectricCyan else AppColors.GlassBorder,
                            RoundedCornerShape(10.dp)
                        )
                        .clickable { priceType = PriceType.INQUIRY }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "가격 문의 / 상담",
                        color = if (priceType == PriceType.INQUIRY) AppColors.ElectricCyan else AppColors.TextSecondary,
                        fontSize = 14.sp,
                        fontWeight = if (priceType == PriceType.INQUIRY) FontWeight.Bold else FontWeight.Normal
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (priceType == PriceType.FIXED) AppColors.ElectricCyan.copy(alpha = 0.15f) else AppColors.CardBackground)
                        .border(
                            1.dp,
                            if (priceType == PriceType.FIXED) AppColors.ElectricCyan else AppColors.GlassBorder,
                            RoundedCornerShape(10.dp)
                        )
                        .clickable { priceType = PriceType.FIXED }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "정가 표시",
                        color = if (priceType == PriceType.FIXED) AppColors.ElectricCyan else AppColors.TextSecondary,
                        fontSize = 14.sp,
                        fontWeight = if (priceType == PriceType.FIXED) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }

            if (priceType == PriceType.FIXED) {
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = priceText,
                    onValueChange = { priceText = it.filter { ch -> ch.isDigit() } },
                    placeholder = { Text("금액 입력 (예: 50000)", color = AppColors.TextSecondary.copy(alpha = 0.6f), fontSize = 14.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    prefix = { Text("₩ ", color = AppColors.ElectricCyan, fontWeight = FontWeight.Bold) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = outlinedFieldColors()
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 3 Key Highlights
            Text(
                text = "3대 핵심 가치 / 하이라이트 (선택)",
                color = AppColors.TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = feature1,
                onValueChange = { feature1 = it },
                placeholder = { Text("1. 핵심 특징 (예: 실시간 음성인식 엔진 탑재)", color = AppColors.TextSecondary.copy(alpha = 0.6f), fontSize = 13.sp) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = outlinedFieldColors()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = feature2,
                onValueChange = { feature2 = it },
                placeholder = { Text("2. 핵심 특징 (예: 온프레미스 / 클라우드 하이브리드)", color = AppColors.TextSecondary.copy(alpha = 0.6f), fontSize = 13.sp) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = outlinedFieldColors()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = feature3,
                onValueChange = { feature3 = it },
                placeholder = { Text("3. 핵심 특징 (예: 초기 도입비 0원 & 월간 구독)", color = AppColors.TextSecondary.copy(alpha = 0.6f), fontSize = 13.sp) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = outlinedFieldColors()
            )

            Spacer(modifier = Modifier.height(18.dp))

            // 1-Sec Inquiry CTA Link
            Text(
                text = "1초 문의 / 상담 연결 URL *",
                color = AppColors.TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = inquiryUrl,
                onValueChange = { inquiryUrl = it },
                placeholder = { Text("https://pf.kakao.com/_xxxx 또는 상담 폼 링크", color = AppColors.TextSecondary.copy(alpha = 0.6f), fontSize = 14.sp) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = outlinedFieldColors()
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Video URL
            Text(
                text = "숏폼 비디오 URL (선택)",
                color = AppColors.TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = videoUrl,
                onValueChange = { videoUrl = it },
                placeholder = { Text("https://example.com/video.mp4", color = AppColors.TextSecondary.copy(alpha = 0.6f), fontSize = 14.sp) },
                singleLine = true,
                leadingIcon = {
                    Icon(imageVector = Icons.Default.VideoLibrary, contentDescription = "Video", tint = AppColors.TextSecondary)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = outlinedFieldColors()
            )

            Button(onClick = {
                coroutineScope.launch {
                    try {
                        selectedImages = pickImages()
                        if (selectedImages.isEmpty()) errorMessage = "JPG, PNG 또는 WebP 이미지 1~5개를 선택해 주세요 (각 10MB 이하)."
                    } catch (e: Exception) {
                        errorMessage = e.message ?: "이미지를 선택하지 못했습니다."
                    }
                }
            }) { Text("이미지 선택 (${selectedImages.size}/5)") }

            Button(onClick = {
                coroutineScope.launch {
                    try {
                        selectedVideo = pickVideo()
                        if (selectedVideo != null) videoUrl = ""
                    } catch (e: Exception) {
                        errorMessage = e.message ?: "동영상을 선택하지 못했습니다."
                    }
                }
            }) { Text(if (selectedVideo == null) "동영상 선택 (선택, MP4 30초/30MB 이하)" else "동영상 선택됨") }

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = errorMessage ?: "",
                    color = Color(0xFFFF5252),
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun outlinedFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = AppColors.TextPrimary,
    unfocusedTextColor = AppColors.TextPrimary,
    focusedContainerColor = AppColors.CardBackground,
    unfocusedContainerColor = AppColors.CardBackground,
    focusedBorderColor = AppColors.ElectricCyan,
    unfocusedBorderColor = AppColors.GlassBorder,
    cursorColor = AppColors.ElectricCyan
)
