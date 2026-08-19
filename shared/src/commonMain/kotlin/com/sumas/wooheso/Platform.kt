package com.sumas.wooheso

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform