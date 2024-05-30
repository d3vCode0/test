package com.example


import android.util.Log
import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.utils.ExtractorLink
import com.lagradost.cloudstream3.LoadResponse.Companion.addTrailer
import com.lagradost.cloudstream3.network.CloudflareKiller
import org.jsoup.nodes.Element
import org.jsoup.nodes.Document

class ExampleAPi : MainAPI() {
    override var lang = "ar"
    override var mainUrl = "https://ww3.animerco.org/"
    override var name = "Animerco"
    override val usesWebView = false
    override val hasMainPage = true
    override val supportedTypes = setOf(TvType.Anime)
    private  val cfKiller = CloudflareKiller()

    override val mainPage = mainPageOf(
        "$mainUrl/movies/page/" to "Movies"
    )
    private fun Element.toSearchResponse(): SearchResponse? {
        val title = this.selectFirst("div.anime-card a")?.attr("title") ?: return null
        // val postId = this?.attr("id")?.split("-") ?: return null
        // val href = "https://ww3.animerco.org/?page_id=${postId[1]}" ?: this.selectFirst("a.image")?.attr("href") ?: return null
        val href = this.selectFirst("div.anime-card a")?.attr("href") ?: return null
        val posterUrl = this.selectFirst("div.anime-card a")?.attr("data-src") ?: "https://placehold.jp/500x750.png"


        return newMovieSearchResponse(title, href, TvType.Movie, true){
            this.posterUrl = posterUrl
        }
    }
    override suspend fun getMainPage(page: Int, request : MainPageRequest): HomePageResponse {
        val document = app.get(request.data + page).document
        val list = document.select("div.page-content .row div.box-5x1").mapNotNull {
            it.toSearchResponse()
        }
        return newHomePageResponse(request.name, list)
    }
    override suspend fun search(query: String): List<SearchResponse> {
        val document = app.get("${mainUrl}/?s=${query}").document
        return document.select("div.page-content .row div.col-12").mapNotNull {
            it.toSearchResponse()
        }
    }
    override suspend fun load(url: String): LoadResponse? {
        var document = app.get(url).document
        if(document.select("title").text() == "Just a moment...") {
            document = app.get(url, interceptor = cfKiller, timeout = 120).document
        }
        val title = document.selectFirst("div.head-box div.media-title h3")?.text()?.trim() ?: "Not find"

        val poster = document.selectFirst("div.anime-card div.image")?.attr("data-src")
        val poster2 = document.selectFirst("div.head-box div.banner")?.attr("data-src")
        val posterUrl = if(poster.isNullOrEmpty()) {
            poster
        } else if (poster2.isNullOrEmpty()) {
            poster2
        } else {
            null
        }

        Log.d(":D3V: title", title)
        return newAnimeLoadResponse(title, url, TvType.Anime) {
            this.posterUrl = posterUrl
        }
    }
    // override suspend fun loadLinks(data: String, isCasting: Boolean, subtitleCallback: (SubtitleFile) -> Unit, callback: (ExtractorLink) -> Unit): Boolean {}
}
