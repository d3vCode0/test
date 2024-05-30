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
        "$mainUrl/movies/page/" to "Movies",
        "$mainUrl/animes/page/" to "Animes",
    )
    override suspend fun getMainPage(page: Int, request : MainPageRequest): HomePageResponse {
        val document = app.get(request.data + page).document
        val list = document.select("div.container div.row div").mapNotNull {element ->
            element.toSearchMovies()
        }
        return newHomePageResponse(request.name, list)
    }
    private fun Element.toSearchMovies(): SearchResponse? {
        val url = select("div.anime-card a")?.attr("href") ?: return null
        val poster = select("div.anime-card a")?.attr("data-src")
        val title = select("div.anime-card .info h3")?.text()?.trim() ?: return null
        if(title.isNullOrEmpty()) title else "Error"

        return MovieSearchResponse(
            title,
            url,
            this@ExampleAPi.name,
            TvType.AnimeMovie,
            poster
        )
    }
    // override suspend fun search(query: String): List<SearchResponse> {}
    // override suspend fun load(url: String): LoadResponse? {}
    // override suspend fun loadLinks(data: String, isCasting: Boolean, subtitleCallback: (SubtitleFile) -> Unit, callback: (ExtractorLink) -> Unit): Boolean {}
}
