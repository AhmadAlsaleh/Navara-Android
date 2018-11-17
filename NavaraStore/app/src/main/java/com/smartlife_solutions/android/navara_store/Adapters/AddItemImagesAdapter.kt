package com.smartlife_solutions.android.navara_store.Adapters

import android.graphics.Bitmap
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.smartlife_solutions.android.navara_store.AddItemActivity
import com.smartlife_solutions.android.navara_store.R
import com.smartlife_solutions.android.navara_store.StaticInformation

class AddItemImagesAdapter(private var activity: AddItemActivity, private var images: ArrayList<Bitmap>) : RecyclerView.Adapter<AddItemImagesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int) =
            ViewHolder(LayoutInflater.from(p0.context)
                .inflate(R.layout.add_item_images, p0, false))

    override fun getItemCount() = images.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.itemImageIV.setImageBitmap(images[position])
        holder.itemImageRemoveIV.setOnClickListener {
            it.startAnimation(StaticInformation().clickAnim(activity))
            activity.removeImage(position)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemImageIV: ImageView = itemView.findViewById(R.id.itemImagesIV)!!
        val itemImageRemoveIV: ImageView = itemView.findViewById(R.id.itemImageRemoveIV)!!
    }

}