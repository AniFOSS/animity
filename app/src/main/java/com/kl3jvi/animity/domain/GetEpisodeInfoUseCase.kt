package com.kl3jvi.animity.domain

import com.kl3jvi.animity.model.database.ContentRepository
import com.kl3jvi.animity.utils.Constants
import com.kl3jvi.animity.utils.Resource
import com.kl3jvi.animity.utils.parser.HtmlParser
import com.kl3jvi.animity.view.activities.player.PlayerRepository
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetEpisodeInfoUseCase @Inject constructor(
    private val playerRepository: PlayerRepository,
    private val contentRepository: ContentRepository
) {
    fun fetchEpisodeMediaUrl(url: String) = flow {
        emit(Resource.Loading())
        try {
            val response = HtmlParser.parseMediaUrl(
                playerRepository.fetchEpisodeMediaUrl(
                    Constants.getHeader(),
                    url
                ).string()
            )
            emit(Resource.Success(data = response))
        } catch (e: Exception) {
            emit(Resource.Error("Oops an error occurred, try again!"))
        }
    }

    fun fetchM3U8(url: String?) = flow {
        emit(Resource.Loading())
        try {
            val response = HtmlParser.parseM3U8Url(
                playerRepository.fetchM3u8Url(
                    Constants.getHeader(),
                    url ?: ""
                ).string()
            )
            emit(Resource.Success(data = response))
        } catch (e: Exception) {
            emit(Resource.Error("Couldn't find a Stream for this Anime"))
        }
    }

    fun checkIfExists(episodeUrl: String) = flow {
        emit(contentRepository.episodeExists(episodeUrl))
    }
}