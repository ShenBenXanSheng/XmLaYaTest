package com.example.ximalaya.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.example.ximalaya.R
import com.ximalaya.ting.android.opensdk.model.track.Track

class PlayPageAdapter : PagerAdapter() {
    private val trackList = mutableListOf<Track>()
    override fun getCount(): Int {
        return trackList.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val layout = LayoutInflater.from(container.context)
            .inflate(R.layout.item_player_page, container, false)
        val track = trackList[position]
        val image = layout.findViewById<ImageView>(R.id.item_page_image)



        Glide.with(image).load(track.coverUrlMiddle).into(image)
        container.addView(layout)
        return layout
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {

        container.removeView(`object` as View?)
    }

    fun getData(it: List<Track>) {
        trackList.clear()
        if (it.isNotEmpty()) {
            trackList.addAll(it)
            notifyDataSetChanged()
        }
    }
}