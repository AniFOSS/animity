package com.kl3jvi.animity.view.fragments.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kl3jvi.animity.databinding.FragmentHomeBinding
import com.kl3jvi.animity.model.network.ApiHelper
import com.kl3jvi.animity.model.network.RetrofitBuilder
import com.kl3jvi.animity.utils.Status
import com.kl3jvi.animity.view.adapters.CustomSubAdapter
import com.kl3jvi.animity.view.adapters.CustomTodaysSelectionAdapter

class HomeFragment : Fragment() {


    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels {
        HomeViewModelFactory(ApiHelper(RetrofitBuilder.apiService))
    }
    private lateinit var subAdapter: CustomSubAdapter
    private lateinit var newSeasonAdapter: CustomSubAdapter
    private lateinit var todayAdapter: CustomTodaysSelectionAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root


        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recentSub.layoutManager = LinearLayoutManager(
            requireContext(),
            RecyclerView.HORIZONTAL, false
        )
        subAdapter = CustomSubAdapter(this)
        binding.recentSub.adapter = subAdapter

        binding.todaySelection.layoutManager = LinearLayoutManager(requireContext())
        todayAdapter = CustomTodaysSelectionAdapter(this)
        binding.todaySelection.adapter = todayAdapter

        binding.newSeasonRv.layoutManager = LinearLayoutManager(
            requireContext(),
            RecyclerView.HORIZONTAL, false
        )
        newSeasonAdapter = CustomSubAdapter(this)
        binding.newSeasonRv.adapter = newSeasonAdapter


        getNewSeason()
        fetchRecentDub()
        getPopularAnime()

    }


    private fun fetchRecentDub() {
        viewModel.fetchRecentSubOrDub().observe(viewLifecycleOwner, { res ->
            res?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        binding.progressBar.visibility = View.GONE
                        resource.data?.let { entry ->
                            subAdapter.getAnimes(entry)
                        }
                        binding.recentSub.visibility = View.VISIBLE
                        binding.textView.visibility = View.VISIBLE
                        binding.textView2.visibility = View.VISIBLE

                    }
                    Status.ERROR -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(requireActivity(), res.message, Toast.LENGTH_LONG).show()
                    }
                    Status.LOADING -> {
                        binding.progressBar.visibility = View.VISIBLE

                        binding.textView.visibility = View.GONE
                        binding.textView2.visibility = View.GONE
                        binding.recentSub.visibility = View.GONE

                    }
                }
            }

        })
    }


    private fun getPopularAnime() {
        viewModel.fetchPopularAnime().observe(viewLifecycleOwner, { res ->
            res?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        binding.progressBar.visibility = View.GONE
                        resource.data?.let { entry ->
                            todayAdapter.getSelectedAnime(entry)
                        }
                        binding.recentSub.visibility = View.VISIBLE
                    }
                    Status.ERROR -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(requireActivity(), res.message, Toast.LENGTH_LONG).show()
                    }
                    Status.LOADING -> {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.recentSub.visibility = View.GONE
                    }
                }
            }

        })
    }


    private fun getNewSeason() {
        viewModel.fetchNewSeason().observe(viewLifecycleOwner, { res ->
            res?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        binding.progressBar.visibility = View.GONE
                        resource.data?.let { entry ->
                            newSeasonAdapter.getAnimes(entry)
                        }
                        binding.recentSub.visibility = View.VISIBLE
                    }
                    Status.ERROR -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(requireActivity(), res.message, Toast.LENGTH_LONG).show()
                    }
                    Status.LOADING -> {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.recentSub.visibility = View.GONE
                    }
                }
            }

        })
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}