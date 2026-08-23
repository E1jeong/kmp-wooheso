package com.sumas.wooheso.features.company.registration

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Check
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sumas.wooheso.core.designsystem.AppColors
import com.sumas.wooheso.data.model.CompanyModel
import com.sumas.wooheso.data.repository.SupabaseCompanyRepository
import kotlinx.coroutines.launch

private val CATEGORIES = listOf(
    "Tech/SaaS", "제조/하드웨어", "F&B/외식", "패션/라이프스타일", "디자인/크리에이티브", "B2B/전문서비스"
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CompanyRegistrationScreen(
    onNavigateBack: () -> Unit,
    onRegistrationSuccess: (companyId: String) -> Unit,
    modifier: Modifier = Modifier,
    companyRepository: SupabaseCompanyRepository = remember { SupabaseCompanyRepository() }
) {
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()

    var name by remember { mutableStateOf("") }
    var tagline by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(CATEGORIES.first()) }
    var description by remember { mutableStateOf("") }
    var websiteUrl by remember { mutableStateOf("") }
    var kakaoUrl by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val isFormValid = name.isNotBlank() && tagline.isNotBlank()

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
                    text = "공급자 회사/브랜드 등록",
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
                            val newCompanyId = "comp_${name.hashCode().toString().takeLast(6)}"
                            val newCompany = CompanyModel(
                                companyId = newCompanyId,
                                ownerUid = "current_user",
                                name = name.trim(),
                                tagline = tagline.trim(),
                                category = selectedCategory,
                                description = description.trim(),
                                websiteUrl = websiteUrl.trim().ifBlank { null },
                                snsLinks = if (kakaoUrl.isNotBlank()) mapOf("kakao" to kakaoUrl.trim()) else emptyMap()
                            )
                            val result = companyRepository.createCompany(newCompany)
                            isLoading = false
                            result.onSuccess {
                                onRegistrationSuccess(it.companyId)
                            }.onFailure {
                                errorMessage = it.message ?: "회사 등록에 실패했습니다."
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
                            text = "회사 등록하고 제품 등록하기",
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
                text = "우회소에 제품을 전시할\n기업 및 브랜드 정보를 입력해주세요.",
                color = AppColors.TextSecondary,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Brand Logo Placeholder
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(AppColors.CardBackground)
                    .border(1.dp, AppColors.GlassBorder, CircleShape)
                    .align(Alignment.CenterHorizontally),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AddPhotoAlternate,
                    contentDescription = "Logo Upload",
                    tint = AppColors.TextSecondary,
                    modifier = Modifier.size(32.dp)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "브랜드 로고 (선택)",
                color = AppColors.TextSecondary,
                fontSize = 12.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Company Name
            Text(
                text = "회사 / 브랜드명 *",
                color = AppColors.TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                placeholder = { Text("예: 넥스트이노베이션, 우회소 스튜디오", color = AppColors.TextSecondary.copy(alpha = 0.6f), fontSize = 14.sp) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = outlinedFieldColors()
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Tagline
            Text(
                text = "한 줄 소개 *",
                color = AppColors.TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = tagline,
                onValueChange = { tagline = it },
                placeholder = { Text("예: 미래형 B2B 모바일 솔루션을 만듭니다", color = AppColors.TextSecondary.copy(alpha = 0.6f), fontSize = 14.sp) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = outlinedFieldColors()
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Category Selection
            Text(
                text = "업종 카테고리 *",
                color = AppColors.TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CATEGORIES.forEach { category ->
                    val isSelected = category == selectedCategory
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isSelected) AppColors.ElectricCyan.copy(alpha = 0.15f) else AppColors.CardBackground)
                            .border(
                                width = 1.dp,
                                color = if (isSelected) AppColors.ElectricCyan else AppColors.GlassBorder,
                                shape = RoundedCornerShape(20.dp)
                            )
                            .clickable { selectedCategory = category }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = category,
                            color = if (isSelected) AppColors.ElectricCyan else AppColors.TextSecondary,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Description
            Text(
                text = "상세 소개 (선택)",
                color = AppColors.TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                placeholder = { Text("기업의 비전, 주력 기술, 서비스 가치를 소개해 주세요.", color = AppColors.TextSecondary.copy(alpha = 0.6f), fontSize = 14.sp) },
                minLines = 3,
                maxLines = 5,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = outlinedFieldColors()
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Website URL
            Text(
                text = "공식 웹사이트 URL (선택)",
                color = AppColors.TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = websiteUrl,
                onValueChange = { websiteUrl = it },
                placeholder = { Text("https://example.com", color = AppColors.TextSecondary.copy(alpha = 0.6f), fontSize = 14.sp) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = outlinedFieldColors()
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Kakao Channel URL
            Text(
                text = "카카오 채널 / 문의 URL (선택)",
                color = AppColors.TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = kakaoUrl,
                onValueChange = { kakaoUrl = it },
                placeholder = { Text("https://pf.kakao.com/_xxxx", color = AppColors.TextSecondary.copy(alpha = 0.6f), fontSize = 14.sp) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = outlinedFieldColors()
            )

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
