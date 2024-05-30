version = 1



cloudstream {
    language = "ar"
    // All of these properties are optional, you can safely remove them

    description = "Description Here"
    authors = listOf(
        //YourNameHere
        "d3vCode0",
        )

    /**
     * Status int as the following:
     * 0: Down
     * 1: Ok
     * 2: Slow
     * 3: Beta only
     * */
    status = 1 // will be 3 if unspecified
    /**
    * All Type is:
    * 0: Movie
    * 1: TvSeries
    * 2: Cartoon
    * 3: Anime
    * 4: OVA
    * 5: Torrent
    * 6: Documentary
    * 7: AsianDrama
    * 8: Live
    * 9: NSFW
    * 10: Others
    * 11: Music
    * 12: AudioBook
    * 13: CustomMedia
    * */
    tvTypes = listOf(
        "AnimeMovie",
        "Anime",
        "OVA",
    )

    //Icon
    iconUrl = "https://ww3.animerco.org/wp-content/uploads/2024/03/apple-touch-icon.png"
}