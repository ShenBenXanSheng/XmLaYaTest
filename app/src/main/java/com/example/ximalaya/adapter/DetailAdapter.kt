package com.example.ximalaya.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.ximalaya.R
import com.example.ximalaya.databinding.ItemRecommendDetailBinding
import com.example.ximalaya.domian.DetailTrack
import com.ximalaya.ting.android.opensdk.model.track.Track
import java.text.SimpleDateFormat

class DetailAdapter : RecyclerView.Adapter<DetailAdapter.InnerHolder>() {
    private lateinit var onDetailClickListener: OnDetailClickListener
    private val trackList = mutableListOf<Track>()

    @SuppressLint("SimpleDateFormat")
    private val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")

    class InnerHolder(itemView: View, val dataBinding: ItemRecommendDetailBinding) :
        RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InnerHolder {
        val dataBinding = DataBindingUtil.inflate<ItemRecommendDetailBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_recommend_detail,
            parent,
            false
        )
        return InnerHolder(dataBinding.root, dataBinding)
    }

    override fun onBindViewHolder(holder: InnerHolder, position: Int) {
        val realPosition = position + 1
        trackList[position].apply {
            val format = simpleDateFormat.format(updatedAt)
            holder.dataBinding.detailTrackData =
                DetailTrack(realPosition.toString(), trackTitle, playCount.toString(), format)

        }

        holder.dataBinding.recommendDetailContainer.setOnClickListener {
            if (this@DetailAdapter::onDetailClickListener.isInitialized) {
                onDetailClickListener.onDetailClick(trackList,position)
            }
        }

    }

    override fun getItemCount() = trackList.size

    @SuppressLint("NotifyDataSetChanged")
    fun setData(tracks: List<Track>) {
        trackList.clear()
        if (tracks.isNotEmpty()) {
            trackList.addAll(tracks)
        }
        notifyDataSetChanged()
    }

    fun setLoadMoreData(tracks: List<Track>) {
        val trackListLastPosition = trackList.size
        if (tracks.isNotEmpty()) {
            trackList.addAll(tracks)
            notifyItemChanged(trackListLastPosition, trackList.size)
        }
    }

    fun setOnDetailClickListener(onDetailClickListener: OnDetailClickListener) {
        this.onDetailClickListener = onDetailClickListener

    }

    interface OnDetailClickListener {
        fun onDetailClick(track: List<Track>, position: Int)
    }
}