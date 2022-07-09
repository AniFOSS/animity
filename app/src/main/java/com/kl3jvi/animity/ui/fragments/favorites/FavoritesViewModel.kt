package com.kl3jvi.animity.ui.fragments.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kl3jvi.animity.data.model.ui_models.AniListMedia
import com.kl3jvi.animity.domain.repositories.FavoriteRepository
import com.kl3jvi.animity.domain.repositories.PersistenceRepository
import com.kl3jvi.animity.utils.Result
import com.kl3jvi.animity.utils.asResult
import com.kl3jvi.animity.utils.logError
import com.kl3jvi.animity.utils.logMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val favoriteRepository: FavoriteRepository,
    private val localStorage: PersistenceRepository,
    private val ioDispatcher: CoroutineDispatcher,
) : ViewModel() {

    private var _favoriteAniListAnimeList =
        MutableStateFlow<List<AniListMedia>?>(null)
    val favoriteAniListAnimeList = _favoriteAniListAnimeList.asStateFlow()
    val shouldRefresh = MutableStateFlow(true)

    init {
        getFavoriteAnimes()
    }

    /**
     * It gets the favorite animes from the AniList API.
     */
    private fun getFavoriteAnimes() {
        viewModelScope.launch(ioDispatcher) {
            shouldRefresh.collectLatest { _ ->
                favoriteRepository.getFavoriteAnimesFromAniList(localStorage.aniListUserId?.toInt(), 1)
                    .flowOn(ioDispatcher)
                    .catch { e -> logError(e) }
                    .asResult()
                    .collect {
                        when (it) {
                            is Result.Error -> logMessage(it.exception?.message)
                            Result.Loading -> {}
                            is Result.Success -> _favoriteAniListAnimeList.value = it.data
                        }
                    }
            }
        }
    }

}


