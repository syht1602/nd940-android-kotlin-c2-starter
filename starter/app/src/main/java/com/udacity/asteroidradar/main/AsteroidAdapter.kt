package com.udacity.asteroidradar.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.udacity.asteroidradar.databinding.AsteroidItemBinding
import com.udacity.asteroidradar.domain.models.Asteroid

class AsteroidAdapter(private val listener: AsteroidClickListener) :
    ListAdapter<Asteroid, AsteroidAdapter.AsteroidViewHolder>(AsteroidDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AsteroidViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val viewHolder = AsteroidItemBinding.inflate(inflater)
        return AsteroidViewHolder(viewHolder, listener)
    }

    override fun onBindViewHolder(holder: AsteroidViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class AsteroidViewHolder(
        private val binding: AsteroidItemBinding,
        private val listener: AsteroidClickListener
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(asteroid: Asteroid) {
            val layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            binding.root.layoutParams = layoutParams

            binding.asteroid = asteroid
            binding.listener = listener
            binding.executePendingBindings()
        }
    }

    class AsteroidDiffCallback : DiffUtil.ItemCallback<Asteroid>() {
        override fun areItemsTheSame(oldItem: Asteroid, newItem: Asteroid) =
            oldItem === newItem

        override fun areContentsTheSame(oldItem: Asteroid, newItem: Asteroid) =
            oldItem == newItem

    }
}

class AsteroidClickListener(val listener: (asteroid: Asteroid) -> Unit) {
    fun onItemClick(item: Asteroid) = listener(item)
}