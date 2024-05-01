package com.example.opscpoe

import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.view.menu.MenuView.ItemView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.storage.FirebaseStorage

class PostViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView) {

    fun bind(models: Models){
        Log.d("FirebaseURI", "URI ${models.uri}")
        val storageReference= FirebaseStorage.getInstance().getReferenceFromUrl(models.uri ?: "")

        storageReference.downloadUrl.addOnSuccessListener { uri ->
            Log.d("FirebaseURI", "URI $storageReference")
           // Glide.with(itemView.context)
                // .load(post.imageUrl + "jpeg")
             //  .into(imageView)

        }
    }
}