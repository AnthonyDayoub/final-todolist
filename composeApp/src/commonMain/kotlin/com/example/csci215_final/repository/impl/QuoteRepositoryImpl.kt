package com.example.csci215_final.repository.impl

import com.example.csci215_final.data.local.entity.toDomain
import com.example.csci215_final.data.remote.QuoteApiService
import com.example.csci215_final.domain.model.Quote
import com.example.csci215_final.repository.QuoteRepository

class QuoteRepositoryImpl(
    private val apiService: QuoteApiService,
) : QuoteRepository {
    override suspend fun getTodayQuote(): Result<Quote> = runCatching {
        apiService.fetchTodayQuote().toDomain()
    }
}
