package com.project.avara.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.avara.data.Quote
import com.project.avara.data.QuoteRepository
import kotlinx.coroutines.launch

data class QuoteState(
    val quote: Quote? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

class QuoteViewModel : ViewModel() {
    private val _quoteState = mutableStateOf(QuoteState())
    val quoteState: State<QuoteState> = _quoteState

    private val repository = QuoteRepository()

    init {
        getRandomQuote()
    }

    private fun getRandomQuote() {
        viewModelScope.launch {
            _quoteState.value = QuoteState(isLoading = true)
            try {
                val quote = repository.getRandomQuote()
                _quoteState.value = QuoteState(quote = quote)
            } catch (e: Exception) {
                _quoteState.value = QuoteState(error = "Failed to fetch quote: ${e.message}")
            }
        }
    }
}