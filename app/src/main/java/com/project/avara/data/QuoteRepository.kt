package com.project.avara.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class QuoteRepository {
    private val retrofit = Retrofit.Builder().baseUrl("https://zenquotes.io/api/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val quoteApiService = retrofit.create(QuoteApiService::class.java)

    suspend fun getRandomQuote(): Quote {
        // The API returns a list with one quote, so we get the first element.
        return quoteApiService.getRandomQuote().first()
    }
}
