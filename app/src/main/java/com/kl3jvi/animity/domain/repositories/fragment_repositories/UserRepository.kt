package com.kl3jvi.animity.domain.repositories.fragment_repositories

import com.apollographql.apollo3.api.ApolloResponse
import com.kl3jvi.animity.*
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    val bearerToken: String?
    val refreshToken: String?
    val guestToken: String?
    val isAuthenticated: Boolean
    val isGuest: Boolean
    val userId: String?

    fun setBearerToken(authToken: String?)
    fun setRefreshToken(refreshToken: String?)
    fun setAniListUserId(sync: String?)
    fun clearStorage()

    fun getSessionForUser(): Flow<ApolloResponse<SessionQuery.Data>>
    fun getUserData(id: Int?): Flow<ApolloResponse<UserQuery.Data>>
    fun getAnimeListData(userId: Int?): Flow<ApolloResponse<AnimeListCollectionQuery.Data>>
    fun getFavoriteAnimes(userId: Int?, page: Int?): Flow<ApolloResponse<FavoritesAnimeQuery.Data>>
    fun getTopTenTrending(): Flow<ApolloResponse<TrendingMediaQuery.Data>>

    //    suspend fun getMediaId(query: String?): Flow<ApolloResponse<MediaIdFromNameQuery.Data>>
    fun markAnimeAsFavorite(animeId: Int?): Flow<ApolloResponse<ToggleFavouriteMutation.Data>>
}