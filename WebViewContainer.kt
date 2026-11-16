package com.seda.browser.ui.browser

import android.graphics.Bitmap
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.seda.browser.browser.NetworkUtils
import com.seda.browser.viewmodel.LoadRequest

class WebViewController(private val webView: WebView) {
    fun goBack() { if (webView.canGoBack()) webView.goBack() }
    fun goForward() { if (webView.canGoForward()) webView.goForward() }
    fun reload() { webView.reload() }
    fun canGoBack(): Boolean = webView.canGoBack()
}

@Composable
fun WebViewContainer(
    loadRequest: LoadRequest,
    onProgressChanged: (Int) -> Unit,
    onPageStarted: (String) -> Unit,
    onPageFinished: (String, String) -> Unit,
    onReceivedError: (String) -> Unit,
    onNavStateChanged: (Boolean, Boolean) -> Unit,
    onControllerReady: (WebViewController) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val webView = remember {
        WebView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )

            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.loadWithOverviewMode = true
            settings.useWideViewPort = true
            settings.setSupportZoom(true)
            settings.builtInZoomControls = true
            settings.displayZoomControls = false
            settings.mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
            settings.cacheMode = WebSettings.LOAD_DEFAULT

            CookieManager.getInstance().setAcceptCookie(true)
            CookieManager.getInstance().setAcceptThirdPartyCookies(this, true)

            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(
                    view: WebView,
                    request: WebResourceRequest
                ): Boolean = false

                override fun onPageStarted(view: WebView, url: String?, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                    onPageStarted(url ?: "")
                    onNavStateChanged(view.canGoBack(), view.canGoForward())
                }

                override fun onPageFinished(view: WebView, url: String?) {
                    super.onPageFinished(view, url)
                    onPageFinished(url ?: "", view.title ?: "")
                    onNavStateChanged(view.canGoBack(), view.canGoForward())
                }

                override fun onReceivedError(
                    view: WebView,
                    request: WebResourceRequest,
                    error: WebResourceError
                ) {
                    super.onReceivedError(view, request, error)
                    if (request.isForMainFrame) {
                        onReceivedError("Sayfa yüklenemedi.")
                    }
                }
            }

            webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView, newProgress: Int) {
                    super.onProgressChanged(view, newProgress)
                    onProgressChanged(newProgress)
                }
            }
        }
    }

    DisposableEffect(Unit) {
        onControllerReady(WebViewController(webView))
        onDispose {
            webView.stopLoading()
            webView.destroy()
        }
    }

    // loadRequest her değiştiğinde (yeni adres, tekrar dene) sayfa yüklenir.
    LaunchedEffect(loadRequest) {
        if (NetworkUtils.isNetworkAvailable(context)) {
            webView.loadUrl(loadRequest.url)
        } else {
            onReceivedError("İnternet bağlantısı yok.")
        }
    }

    AndroidView(factory = { webView }, modifier = modifier.fillMaxSize())
}
