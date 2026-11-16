package com.seda.browser.browser

import android.net.Uri

object UrlUtils {

    private val shortcuts = mapOf(
        "google" to "https://www.google.com",
        "youtube" to "https://www.youtube.com",
        "wikipedia" to "https://www.wikipedia.org",
        "github" to "https://www.github.com"
    )

    fun processInput(rawInput: String): String {
        val input = rawInput.trim()
        if (input.isEmpty()) return "https://www.google.com"

        shortcuts[input.lowercase()]?.let { return it }

        return if (isLikelyUrl(input)) {
            ensureScheme(input)
        } else {
            "https://www.google.com/search?q=" + Uri.encode(input)
        }
    }

    private fun isLikelyUrl(text: String): Boolean {
        if (text.contains(" ")) return false
        if (text.startsWith("http://") || text.startsWith("https://")) return true
        val domainPattern = Regex("^([a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}(:\\d+)?(/.*)?$")
        return domainPattern.matches(text)
    }

    private fun ensureScheme(text: String): String {
        return if (text.startsWith("http://") || text.startsWith("https://")) text else "https://$text"
    }
}
