package com.example


import android.util.Log
import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.LoadResponse.Companion.addTrailer
import com.lagradost.cloudstream3.network.CloudflareKiller
import org.jsoup.nodes.Element

class ExampleAPi : MainAPI() {
    override var mainUrl = "https://ww3.animerco.org"
    override var name = "Animerco"
    override val hasMainPage = true
    override var lang = "ar"
    override val supportedTypes = setOf(TvType.AnimeMovie, TvType.Anime)
    private val cfKiller = CloudflareKiller()

    override val mainPage = mainPageOf(
        "${mainUrl}/animes/page/" to "Animes"
    )

    override suspend fun getMainPage(page: Int, request: MainPageRequest): HomePageResponse {
        val doc = app.get(request.data + page).document
        if(doc.select("title").text() == "Just a moment...") {
            val doc = app.get(request.data + page, interceptor = cfKiller, timeout = 120).document
        }
        val home = doc.select("div.page-content .row div.box-5x1").mapNotNull {
            it.toSearchResponse()
        }
        return newHomePageResponse(request.name, home)
    }

    private fun Element.toSearchResponse(): SearchResponse? {
        val title = this.selectFirst("div.info h3")?.text()?.trim() ?: return null
        val href = this.selectFirst("a")?.attr("href") ?: return null
        val posterUrl = this.selectFirst("a")?.attr("data-src") ?: return null

        Log.d("D3V title",title)
        Log.d("D3V title",href)

        return newAnimeSearchResponse(title, href, TvType.Anime) {
            this.posterUrl = posterUrl
        }
    }

    override suspend fun load(url: String): LoadResponse? {
        val document = app.get(request.data + page).document
        if(document.select("title").text() == "Just a moment...") {
            val document = app.get(request.data + page, interceptor = cfKiller, timeout = 120).document
        }

        val title     = document.selectFirst("div.head-box div.media-title h3")?.text()?.trim() ?: document.selectFirst("div.head-box div.media-title h1")?.text()?.trim() ?: return null
        val bgImage   = fixUrlNull(document.selectFirst("div.banner")?.attr("data-src")) ?: return null
        val posterUrl = fixUrlNull(document.selectFirst("div.anime-card div.image")?.attr("data-src")) ?: fixUrlNull(document.selectFirst("div.head-box div.banner")?.attr("data-src")) ?: return null
        
        Log.d("D3V > title", title)
        Log.d("D3V > bg", bgImage)
        Log.d("D3V > poster", posterUrl)
        return newAnimeLoadResponse(title, url, TvType.Anime, true) {
            this.posterUrl = posterUrl
        }
    }

}
