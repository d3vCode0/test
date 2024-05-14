package com.example

import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.utils.loadExtractor
import com.lagradost.cloudstream3.utils.ExtractorLink
// import com.lagradost.cloudstream3.LoadResponse.Companion.addTrailer
import org.jsoup.nodes.Element


class ExampleAPi : MainAPI() {
    override var mainUrl = "https://ww3.animerco.org"
    override var name = "Example"
    override val hasMainPage = true
    override var lang = "en"
    override val supportedTypes = setOf(
        TvType.Anime,
        TvType.AnimeMovie
    )

    override val mainPage = mainPageOf(
        "${mainUrl}/episodes/page/" to "Episodes",
        //add more here
    )

    override suspend fun getMainPage(page: Int, request: MainPageRequest): HomePageResponse {
        val document = app.get(request.data).document
        val lists = document.select("div.page-content div.row div.col-12").mapNotNull {
            it.toSearchResponse()
        }
        return newHomePageResponse(
            val list = HomePageList(
                name = request.name,
                list = lists,
                isHorizontalImages = true
            ),
            hasNext = true
        )
    }

    private fun Element.toSearchResponse(): SearchResponse? {
        val title = this.selectFirst("div.title h1")?.text()?.trim() ?: return null
        val href = fixUrlNull(this.selectFirst("a")?.attr("href")) ?: return null
        val posterUrl = this.selectFirst("a")?.attr("data-src") ?: return null

        return newAnimeSearchResponse(title, href, TvType.Anime) {
            // this.name = name // String
            // this.url = url // String
            // this.apiName = apiName // String
            // this.type = TvType.Anime // TvType
            this.posterUrl = posterUrl // String
            // this.year = year //Int
            // this.dubStatus = dubStatus // Ex: EnumSet.of(DubStatus.None, DubStatus.Dubbed, DubStatus.Subbed)
            // this.otherName = otherName // String
            // this.episodes = episodes // Ex: MutableMap<String, Int> = MutableMapOf(DubStatus.None, 0)
            // this.id = id // Int
            // this.quality = quality // Ex: SearchQuality.HD
            // this.posterHeaders = posterHeaders // Ex: mapOf<String, String>() Or network.CloudflareKiller.Intercepter.getCookieHeaders(mainUrl).toMap()
        }
    }

    override suspend fun search(query: String): List<SearchResponse> {
        val document = app.get("${mainUrl}?s=${query}").document
        return document.select("div.search div.animes").mapNotNull {
            it.toSearchResponse()
        }
    }

    override suspend fun load(url: String): LoadResponse? {
        val document = app.get(url).document

        val title = document.selectFirst("div.title h1")?.text()?.trim() ?: return null
        val href = fixUrlNull(document.selectFirst("a")?.attr("href")) ?: return null
        val posterUrl = document.selectFirst("a")?.attr("data-src") ?: return null
        val comingSoon = true

        return newAnimeLoadResponse(title, href, TvType.Anime, comingSoon) {
            // this.engName = eng // String
            // this.japName = jap // String
            // this.name = title // String
            // this.url = href // String
            // this.apiName = this@ExampleAPi.name
            // this.type = TvType.Anime // TvType
            this.posterUrl = posterUrl // String
            // this.year = year // Int
            // this.episodes = episodes // Ex: MutableMap<DubStatus, List<Episode>> = MutableMapOf()
            // this.showStatus = showStatus // Ex ShowStatus.Companion Or ShowStatus.Ongoing
            // this.plot = plot // String
            // this.tags = tags // List Ex: document.select("div.genres a").map { it.text() }
            // this.synonyms = synonyms // List
            // this.rating = rating // Int
            // this.duration = duration // Int
            // this.trailers = trailers // String Ex: addTrailer(trailers)
            // this.recommendations = recommendations // List<SearchResponse> Ex: document.select("div.animes div.recommendations").map { it.toSearchResponse() }
            // this.actors = actors // List<ActorData> Ex: .mapNotNull { ActorData(actor =  Actor(name, image), role = ActorRole.Main or Supporting or Background, voiceActor =  Actor(name, image)) }
            // this.comingSoon = comingSoon // Boolean
            // this.syncData = syncData //
            // this.posterHeaders = posterHeaders // Ex: mapOf<String, String>() Or network.CloudflareKiller.Intercepter.getCookieHeaders(mainUrl).toMap()
            // this.nextAiring = nextAiring // NextAiring(episode: Int, unixTime: Long, season: Int)
            // this.seasonNames = seasonNames // List<SeasonData> Ex: SeasonData(val season: Int, val name: String? = null, val displaySeason: Int? = null)
            // this.backgroundPosterUrl = backgroundPosterUrl // String
            // this.contentRating = contentRating // String
        }
    }

    override suspend fun loadLinks(data: String, isCasting: Boolean, subtitleCallback: (SubtitleFile) -> Unit, callback: (ExtractorLink) -> Unit): Boolean {
        val document = app.get(data).document
        val video_link = document.select("div.link iframe").map {
            fixUrl( it.attr("src"))
        }

        for (video_map in video_link) {
            loadExtractor(video_map, "${mainUrl}/", subtitleCallback, callback)
        }
     
        return true
    }
}
