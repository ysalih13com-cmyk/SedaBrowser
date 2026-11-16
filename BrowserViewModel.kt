package com.seda.browser.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class LoadRequest(val url: String, val requestId: Long)

class BrowserViewModel(initialUrl: String = "https://www.google.com") : ViewModel() {

    private val _loadRequest = MutableStateFlow(LoadRequest(initialUrl, 0L))
    val loadRequest: StateFlow<LoadRequest> = _loadRequest.asStateFlow()

    private val _currentDisplayUrl = MutableStateFlow(initialUrl)
    val currentDisplayUrl: StateFlow<String> = _currentDisplayUrl.asStateFlow()

    private val _pageTitle = MutableStateFlow("")
    val pageTitle: StateFlow<String> = _pageTitle.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _progress = MutableStateFlow(0)
    val progress: StateFlow<Int> = _progress.asStateFlow()

    private val _canGoBack = MutableStateFlow(false)
    val canGoBack: StateFlow<Boolean> = _canGoBack.asStateFlow()

    private val _canGoForward = MutableStateFlow(false)
    val canGoForward: StateFlow<Boolean> = _canGoForward.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun loadNewUrl(url: String) {
        _errorMessage.value = null
        _loadRequest.value = LoadRequest(url, _loadRequest.value.requestId + 1)
    }

    fun onPageStarted(url: String) {
        _isLoading.value = true
        _errorMessage.value = null
        if (url.isNotBlank()) _currentDisplayUrl.value = url
    }

    fun onPageFinished(url: String, title: String) {
        _isLoading.value = false
        if (url.isNotBlank()) _currentDisplayUrl.value = url
        if (title.isNotBlank()) _pageTitle.value = title
    }

    fun onProgressChanged(newProgress: Int) {
        _progress.value = newProgress
        _isLoading.value = newProgress in 1..99
    }

    fun onNavStateChanged(canBack: Boolean, canForward: Boolean) {
        _canGoBack.value = canBack
        _canGoForward.value = canForward
    }

    fun onReceivedError(message: String) {
        _isLoading.value = false
        _errorMessage.value = message
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
