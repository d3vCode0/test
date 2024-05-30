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
    override val hasMainPage = true
    override val supportedTypes = setOf(TvType.Anime)
    private  val cfKiller = CloudflareKiller()

    override val mainPage = mainPageOf(
        "$mainUrl/movies/page/" to "Movies",
        "$mainUrl/animes/page/" to "Animes",
    )
    override suspend fun getMainPage(page: Int, request : MainPageRequest): HomePageResponse {
        val document = app.get(request.data + page).document
        val list = document.select("div.container div.row div").mapNotNull {
            it.toSearchMovies()
        }
        return newHomePageResponse(request.name, list)
    }
    private fun Element.toSearchMovies(): SearchResponse? {
        val title = this.selectFirst("div.info h3")?.text()?.trim() ?: return null
        val href = this.selectFirst("a")?.attr("href") ?: return null
        val posterUrl = this.selectFirst("a")?.attr("data-src") ?: return null

        return if (href.contains("movies")) {
            newMovieSearchResponse(title, href, TvType.AnimeMovie) {
                this.posterUrl = posterUrl
            }
        } else if (href.contains("episodes")) {
            val e = this.selectFirst("a.episode")?.text()?.trim()?.replace("الحلقة ", "") ?: return null
            val s = this.selectFirst("a.extra")?.text()?.trim()?.replace("الموسم ", "") ?: return null
            newAnimeSearchResponse("${title} S${s}-E${e}", href, TvType.Anime) {
                this.posterUrl = posterUrl
            }
        } else if (href.contains("seasons")) {
            val s = this.selectFirst("div.info a.extra h4")?.text()?.trim()?.replace("الموسم ", "") ?: return null
            val t = if(s.isNullOrEmpty()) title else "${title} S${s}"
            newAnimeSearchResponse(t, href, TvType.Anime) {
                this.posterUrl = posterUrl
            }
        } else {
            newAnimeSearchResponse(title, href, TvType.Anime) {
                this.posterUrl = posterUrl
            }
        }
    }
    // override suspend fun search(query: String): List<SearchResponse> {}
    // override suspend fun load(url: String): LoadResponse? {}
    // override suspend fun loadLinks(data: String, isCasting: Boolean, subtitleCallback: (SubtitleFile) -> Unit, callback: (ExtractorLink) -> Unit): Boolean {}
}
