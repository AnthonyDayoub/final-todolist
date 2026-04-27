package com.example.csci215_final.repository

import com.example.csci215_final.domain.model.Quote

interface QuoteRepository {
    suspend fun getTodayQuote(): Result<Quote>
    suspend fun getRandomQuote(): Result<Quote>
}
