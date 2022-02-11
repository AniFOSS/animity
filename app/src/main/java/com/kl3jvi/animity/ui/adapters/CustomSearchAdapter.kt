package com.kl3jvi.animity.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kl3jvi.animity.data.model.ui_models.AnimeMetaModel
import com.kl3jvi.animity.databinding.SearchLayoutBinding
import com.kl3jvi.animity.ui.fragments.search.SearchFragmentDirections

class CustomSearchAdapter : PagingDataAdapter<AnimeMetaModel, CustomSearchAdapter.ViewHolder>(
    MainDiffUtil()
) {

    inner class ViewHolder(private val binding: SearchLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.setClickListener { view ->
                binding.animeInfo?.let {
                    navigateToDetails(it, view)
                }
            }
        }

        private fun navigateToDetails(animeDetails: AnimeMetaModel, view: View) {
            try {
                val direction =
                    SearchFragmentDirections.actionNavigationExploreToNavigationDetails(animeDetails)
                view.findNavController().navigate(direction)
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
            }
        }

        fun bindAnimeInfo(animeInfo: AnimeMetaModel) {
            binding.animeInfo = animeInfo
            binding.isVisible = !animeInfo.episodeNumber.isNullOrEmpty()
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: SearchLayoutBinding =
            SearchLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val animeInfo = getItem(position)
        if (animeInfo != null) {
            holder.bindAnimeInfo(animeInfo)
        }
    }
}

