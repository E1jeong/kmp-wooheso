package com.sumas.wooheso.data.mock

import com.sumas.wooheso.data.model.PriceType
import com.sumas.wooheso.data.model.ProductCardModel

object MockFeedData {
    val mockProducts = listOf(
        ProductCardModel(
            id = "prod-001",
            companyId = "comp-001",
            companyName = "우회소 스튜디오",
            title = "모듈형 전시 부스 Alpha Pro",
            description = "공구 없이 30분 만에 조립 가능한 프리미엄 알루미늄 모듈형 전시 부스 시스템",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1511578314322-379afb476865?w=800&q=80",
                "https://images.unsplash.com/photo-1497366216548-37526070297c?w=800&q=80"
            ),
            videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
            category = "모듈형 부스",
            keyFeatures = listOf(
                "공구 불필요 30분 원터치 조립",
                "재활용 항공 알루미늄 초경량 프레임",
                "커스텀 4K 조명 패널 일체형"
            ),
            priceType = PriceType.FIXED,
            price = 3500000L,
            inquiryUrl = "https://pf.kakao.com",
            saveCount = 142
        ),
        ProductCardModel(
            id = "prod-002",
            companyId = "comp-002",
            companyName = "네오디스플레이",
            title = "스마트 인터랙티브 터치 키오스크 55인치",
            description = "전시회 방문객의 시선을 사로잡는 초고화질 슬림 베젤 4K 인터랙티브 키오스크",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1517245386807-bb43f82c33c4?w=800&q=80",
                "https://images.unsplash.com/photo-1460925895917-afdab827c52f?w=800&q=80"
            ),
            videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4",
            category = "디스플레이",
            keyFeatures = listOf(
                "4K UHD 10포인트 정전용량 멀티터치",
                "안드로이드/윈도우 듀얼 OS 지원",
                "현장 방문객 데이터 실시간 분석 SDK"
            ),
            priceType = PriceType.INQUIRY,
            price = null,
            inquiryUrl = "https://pf.kakao.com",
            saveCount = 89
        ),
        ProductCardModel(
            id = "prod-003",
            companyId = "comp-003",
            companyName = "에코스페이스",
            title = "친환경 페이퍼 모듈 팝업스토어 키트",
            description = "100% 재생 종이로 제작되어 전시 후 폐기물 걱정 없는 감성 친환경 팝업 키트",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1542744173-8e7e53415bb0?w=800&q=80"
            ),
            videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4",
            category = "팝업스토어",
            keyFeatures = listOf(
                "FSC 인증 친환경 고강도 허니컴 보드",
                "수성 잉크 고해상도 그래픽 프린팅",
                "운반 및 보관이 간편한 접이식 플랫팩"
            ),
            priceType = PriceType.FIXED,
            price = 1800000L,
            inquiryUrl = "https://pf.kakao.com",
            saveCount = 230
        )
    )
}
