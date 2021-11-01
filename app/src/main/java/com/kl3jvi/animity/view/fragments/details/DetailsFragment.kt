package com.kl3jvi.animity.view.fragments.details



import android.content.Intent
import android.graphics.Color

import android.os.Bundle
import android.view.*
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import coil.request.CachePolicy
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.kl3jvi.animity.R
import com.kl3jvi.animity.databinding.FragmentDetailsBinding
import com.kl3jvi.animity.utils.Constants
import com.kl3jvi.animity.utils.Constants.Companion.getBackgroundColor
import com.kl3jvi.animity.utils.Constants.Companion.getColor
import com.kl3jvi.animity.utils.Resource
import com.kl3jvi.animity.view.activities.MainActivity
import com.kl3jvi.animity.view.activities.player.PlayerActivity
import com.kl3jvi.animity.view.adapters.CustomEpisodeAdapter
import com.kl3jvi.animity.viewmodels.DetailsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailsFragment : Fragment() {

    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!
    private val args: DetailsFragmentArgs by navArgs()
    private val viewModel: DetailsViewModel by viewModels()
    private lateinit var episodeAdapter: CustomEpisodeAdapter
    private lateinit var menu: Menu
    private var title: String = ""
    private var check = false
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        firebaseAnalytics = Firebase.analytics
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        fetchAnimeInfo()
        fetchEpisodeList()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        args.animeDetails.let { animeInfo ->
            binding.apply {
                detailsPoster.load(animeInfo.imageUrl) {
                    crossfade(true)
                    diskCachePolicy(CachePolicy.ENABLED)
                }
                episodeListRecycler.layoutManager = LinearLayoutManager(requireContext())
                episodeListRecycler.isNestedScrollingEnabled = false
                resultTitle.text = animeInfo.title
                episodeAdapter =
                    CustomEpisodeAdapter(requireParentFragment(), animeInfo.title)
                title = animeInfo.title
                binding.episodeListRecycler.adapter = episodeAdapter


            }
            animeInfo.categoryUrl?.let { url ->
                viewModel.passUrl(url)
            }
            viewModel.passId(animeInfo.id)
        }
    }



    private fun fetchAnimeInfo() {
        viewModel.animeInfo.observe(viewLifecycleOwner, { res ->
            when (res) {
                is Resource.Success -> {
                    res.data?.let { info ->
                        binding.expandTextView.text = info.plotSummary
                        binding.releaseDate.text = info.releasedTime
                        binding.status.text = info.status
                        binding.type.text = info.type

                        binding.expandTextView.visibility = View.VISIBLE
                        binding.releaseDate.visibility = View.VISIBLE
                        binding.status.visibility = View.VISIBLE
                        binding.type.visibility = View.VISIBLE


                        // Check if the type is movie and this makes invisible the listview of the episodes
                        if (info.type == " Movie") {
                            binding.apply {
                                resultEpisodesText.visibility = View.GONE
                                binding.episodeListRecycler.visibility = View.GONE
                                resultPlayMovie.visibility = View.VISIBLE
                                imageButton.visibility = View.GONE
                            }
                        } else {
                            binding.apply {
                                resultEpisodesText.visibility = View.VISIBLE
                                resultPlayMovie.visibility = View.GONE
                                episodeListRecycler.visibility = View.VISIBLE
                            }
                        }
                        info.genre.forEach { data ->
                            val chip = Chip(requireContext())
                            chip.apply {
                                text = data.genreName
                                setTextColor(Color.WHITE)
                                chipStrokeColor = getColor()
                                chipStrokeWidth = 3f
                                chipBackgroundColor = getBackgroundColor()
                            }
                            binding.genreGroup.addView(chip)
                        }
                    }
                }
                is Resource.Loading -> {
                    binding.expandTextView.visibility = View.GONE
                    binding.releaseDate.visibility = View.GONE
                    binding.status.visibility = View.GONE
                    binding.type.visibility = View.GONE
                }
                is Resource.Error -> {
                    showSnack(res.message)
                }
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.favorite_menu, menu)
        this.menu = menu
        observeDatabase()
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun observeDatabase() {
        viewModel.isOnDatabase.observe(viewLifecycleOwner, {
            check = it
            if (!check) {
                menu[0].setIcon(R.drawable.ic_favorite_uncomplete)
            } else {
                menu[0].setIcon(R.drawable.ic_favorite_complete)
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.add_to_favorites -> {
                check = if (!check) {
                    menu[0].setIcon(R.drawable.ic_favorite_complete)
                    viewModel.insert(anime = args.animeDetails)
                    showSnack("Anime added to Favorites")
                    true
                } else {
                    menu[0].setIcon(R.drawable.ic_favorite_uncomplete)
                    viewModel.delete(args.animeDetails)
                    showSnack("Anime removed from Favorites")
                    false
                }
            }
            else -> findNavController().navigateUp()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun fetchEpisodeList() {
        viewModel.episodeList.observe(viewLifecycleOwner, { episodeListResponse ->
            episodeListResponse.data?.let { episodeList ->
                episodeAdapter.getEpisodeInfo(episodeList)
                binding.resultEpisodesText.text =
                    requireActivity().getString(
                        R.string.total_episodes,
                        episodeList.size.toString()
                    )
                var check = false
                binding.imageButton.setOnClickListener {
                    check = if (!check) {
                        binding.imageButton.load(R.drawable.ic_up_arrow) {
                            crossfade(true)
                        }
                        episodeAdapter.getEpisodeInfo(episodeList.reversed())
                        true
                    } else {
                        binding.imageButton.load(R.drawable.ic_down_arrow) {
                            crossfade(true)
                        }
                        episodeAdapter.getEpisodeInfo(episodeList)
                        false
                    }

                }
                if (episodeList.isNotEmpty()) {
                    binding.resultPlayMovie.setOnClickListener {
                        val intent =
                            Intent(requireActivity(), PlayerActivity::class.java)
                        intent.putExtra(Constants.EPISODE_DETAILS, episodeList.first())
                        intent.putExtra(Constants.ANIME_TITLE, title)
                        requireContext().startActivity(intent)
                        binding.resultPlayMovie.visibility = View.VISIBLE
                    }
                } else {
                    binding.resultPlayMovie.visibility = View.GONE
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        if (requireActivity() is MainActivity) {
            (activity as MainActivity?)?.hideBottomNavBar()
        }
    }

    private fun showSnack(message: String?) {
        val snack =
            Snackbar.make(binding.root, message ?: "Error Occurred", Snackbar.LENGTH_LONG)
        if (!snack.isShown) {
            snack.show()
        }
    }
}