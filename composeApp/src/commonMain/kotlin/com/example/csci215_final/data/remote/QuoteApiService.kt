package com.example.csci215_final.data.remote

import com.example.csci215_final.data.remote.dto.QuoteDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

// ZenQuotes.io — free, no API key required
// GET https://zenquotes.io/api/today  →  [{"q":"...","a":"...","h":"..."}]
class QuoteApiService {

    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
    }

    suspend fun fetchTodayQuote(): QuoteDto {
        val quotes: List<QuoteDto> = client.get("https://zenquotes.io/api/today").body()
        return quotes.first()
    }

    suspend fun fetchRandomQuote(): QuoteDto {
        val quotes: List<QuoteDto> = client.get("https://zenquotes.io/api/random").body()
        return quotes.first()
    }
}
