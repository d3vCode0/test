package com.example

import com.lagradost.cloudstream3.*
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


    

    
}
