package com.seda.browser.browser

data class PinnedSite(
    val name: String,
    val url: String
)

object PinnedSites {
    val defaults = listOf(
        PinnedSite("Google", "https://www.google.com"),
        PinnedSite("YouTube", "https://www.youtube.com"),
        PinnedSite("Wikipedia", "https://www.wikipedia.org"),
        PinnedSite("GitHub", "https://www.github.com")
    )
}
