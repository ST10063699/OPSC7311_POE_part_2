package com.example.opscpoe

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class Adapter(private var models: List<Models>) : RecyclerView.Adapter<PostViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent,
            false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val model = models[position]
        holder.bind(model)
    }

    override fun getItemCount(): Int {
        return models.size
    }

    fun updateData(newList: List<Models>) {
        models = newList
    }
}
