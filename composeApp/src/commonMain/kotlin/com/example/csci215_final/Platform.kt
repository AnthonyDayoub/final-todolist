package com.example.csci215_final

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform