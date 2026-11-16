package com.seda.browser.ui.browser

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.seda.browser.browser.UrlUtils
import com.seda.browser.ui.components.AddressBar
import com.seda.browser.ui.components.BrowserProgressBar
import com.seda.browser.viewmodel.BrowserViewModel

@Composable
fun BrowserScreen(
    viewModel: BrowserViewModel,
    onNavigateHome: () -> Unit
) {
    val loadRequest by viewModel.loadRequest.collectAsState()
    val displayUrl by viewModel.currentDisplayUrl.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val progress by viewModel.progress.collectAsState()
    val canGoBack by viewModel.canGoBack.collectAsState()
    val canGoForward by viewModel.canGoForward.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    var controller by remember { mutableStateOf<WebViewController?>(null) }

    // Android geri tuşu: önce web geçmişinde geri, geçmiş bittiyse ana sayfaya.
    BackHandler(enabled = true) {
        val ctrl = controller
        if (errorMessage == null && ctrl != null && ctrl.canGoBack()) {
            ctrl.goBack()
        } else {
            onNavigateHome()
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {

        Surface(tonalElevation = 2.dp) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        AddressBar(
                            initialValue = displayUrl,
                            placeholder = "Ne arıyorsun canım?",
                            onSubmit = { input ->
                                viewModel.loadNewUrl(UrlUtils.processInput(input))
                            }
                        )
                    }
                }
                BrowserProgressBar(progress = progress, isLoading = isLoading)
            }
        }

        Box(modifier = Modifier.weight(1f)) {
            if (errorMessage != null) {
                ErrorScreen(
                    message = errorMessage ?: "Sayfa yüklenemedi.",
                    onRetry = {
                        viewModel.loadNewUrl(loadRequest.url)
                    }
                )
            } else {
                WebViewContainer(
                    loadRequest = loadRequest,
                    onProgressChanged = viewModel::onProgressChanged,
                    onPageStarted = viewModel::onPageStarted,
                    onPageFinished = viewModel::onPageFinished,
                    onReceivedError = viewModel::onReceivedError,
                    onNavStateChanged = viewModel::onNavStateChanged,
                    onControllerReady = { controller = it }
                )
            }
        }

        BottomAppBar {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { controller?.goBack() }, enabled = canGoBack) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Geri")
                }
                IconButton(onClick = { controller?.goForward() }, enabled = canGoForward) {
                    Icon(Icons.Filled.ArrowForward, contentDescription = "İleri")
                }
                IconButton(onClick = { controller?.reload() }) {
                    Icon(Icons.Filled.Refresh, contentDescription = "Yenile")
                }
                IconButton(onClick = onNavigateHome) {
                    Icon(Icons.Filled.Home, contentDescription = "Ana sayfa")
                }
            }
        }
    }
}

@Composable
private fun ErrorScreen(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Filled.WifiOff,
            contentDescription = null,
            modifier = Modifier.size(56.dp),
            tint = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(20.dp))
        Button(onClick = onRetry) {
            Text("Tekrar Dene")
        }
    }
}
