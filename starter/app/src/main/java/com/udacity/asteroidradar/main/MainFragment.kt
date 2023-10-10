package com.udacity.asteroidradar.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.databinding.FragmentMainBinding
import kotlinx.coroutines.launch

class MainFragment : Fragment(), MenuProvider {

    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this)[MainViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        requireActivity().addMenuProvider(this)
        val binding = FragmentMainBinding.inflate(inflater)
        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        val adapter = AsteroidAdapter(AsteroidClickListener {
            val action = MainFragmentDirections.actionShowDetail(it)
            view?.findNavController()?.navigate(action)
        })
        binding.asteroidRecycler.adapter = adapter

        with(viewModel) {
            pictureOfDay.observe(viewLifecycleOwner) { pictureOfDay ->
                Picasso.with(requireContext()).load(pictureOfDay.url)
                    .placeholder(R.drawable.placeholder_picture_of_day)
                    .error(R.drawable.placeholder_picture_of_day)
                    .into(binding.activityMainImageOfTheDay)
                pictureOfDay?.title?.let {
                    binding.textView.text = it
                }
            }
            asteroidData.observe(viewLifecycleOwner) {
                if (it.isNotEmpty()) {
                    adapter.submitList(it)
                }
            }
        }
        return binding.root
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.main_overflow_menu, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.view_week_asteroids -> {
                viewModel.getWeekAsteroidFromDb()
            }

            R.id.view_today_asteroids -> {
                viewModel.getTodayAsteroidFromDb()
            }

            R.id.view_saved_asteroids -> {
                lifecycleScope.launch {
                    viewModel.getAsteroidFromDb()
                }
            }
        }
        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        requireActivity().removeMenuProvider(this)
    }
}
