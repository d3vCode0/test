package com.example


import android.util.Log
import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.utils.ExtractorLink
import com.lagradost.cloudstream3.LoadResponse.Companion.addTrailer
import com.lagradost.cloudstream3.network.CloudflareKiller
import org.jsoup.nodes.Element

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
        val title = this.selectFirst("div.info h3")?.text()?.trim() ?: return null
        val href = this.selectFirst("a")?.attr("href") ?: return null
        val posterUrl = this.selectFirst("a")?.attr("data-src") ?: return null
        return newMovieSearchResponse(title, href, TvType.Movie, true){
            this.posterUrl = posterUrl
        }
    }
    override suspend fun getMainPage(page: Int, request : MainPageRequest): HomePageResponse {
        val document = app.get(request.data).document
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
    // override suspend fun load(url: String): LoadResponse {
    //     return newMovieLoadResponse()
    // }
    // override suspend fun loadLinks(data: String, isCasting: Boolean, subtitleCallback: (SubtitleFile) -> Unit, callback: (ExtractorLink) -> Unit): Boolean {}
}
