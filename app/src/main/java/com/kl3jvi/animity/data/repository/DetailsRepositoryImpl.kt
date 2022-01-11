package com.kl3jvi.animity.data.repository

import com.kl3jvi.animity.data.model.AnimeInfoModel
import com.kl3jvi.animity.data.model.EpisodeModel
import com.kl3jvi.animity.data.model.EpisodeReleaseModel
import com.kl3jvi.animity.data.network.AnimeApiClient
import com.kl3jvi.animity.domain.repositories.DetailsRepository
import com.kl3jvi.animity.utils.parser.HtmlParser
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@Suppress("BlockingMethodInNonBlockingContext")
class DetailsRepositoryImpl @Inject constructor(
    private val apiClient: AnimeApiClient,
    private val ioDispatcher: CoroutineDispatcher
) : DetailsRepository {
    override val parser: HtmlParser
        get() = HtmlParser

    override suspend fun fetchAnimeInfo(
        header: Map<String, String>,
        episodeUrl: String
    ): AnimeInfoModel = withContext(ioDispatcher) {
        parser.parseAnimeInfo(
            apiClient.fetchAnimeInfo(header = header, episodeUrl = episodeUrl).string()
        )
    }

    override suspend fun fetchEpisodeList(
        header: Map<String, String>,
        id: String,
        endEpisode: String,
        alias: String
    ): List<EpisodeModel> = withContext(ioDispatcher) {
        parser.fetchEpisodeList(
            apiClient.fetchEpisodeList(
                header = header,
                id = id,
                endEpisode = endEpisode,
                alias = alias
            ).string()
        )
    }

    override suspend fun fetchEpisodeTimeRelease(episodeUrl: String): EpisodeReleaseModel =
        withContext(ioDispatcher) {
            parser.fetchEpisodeReleaseTime(
                apiClient.fetchEpisodeTimeRelease(episodeUrl = episodeUrl).string()
            )
        }
}
