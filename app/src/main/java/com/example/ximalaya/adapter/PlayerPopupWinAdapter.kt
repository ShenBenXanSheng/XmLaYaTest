package com.example.ximalaya.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.ximalaya.R
import com.example.ximalaya.base.BaseApp
import com.example.ximalaya.databinding.ItemPopupwindowRvBinding
import com.example.ximalaya.domian.PlayerPopupWinItemData
import com.ximalaya.ting.android.opensdk.model.track.Track

class PlayerPopupWinAdapter() : RecyclerView.Adapter<PlayerPopupWinAdapter.InnerHolder>() {
    private var currentPlayPosition: Int = 0
    private lateinit var onPlayerPopWinItemClickListener: OnPlayerPopWinItemClickListener
    private val trackList = mutableListOf<Track>()

    class InnerHolder(itemView: View, val dataBinding: ItemPopupwindowRvBinding) :
        RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InnerHolder {
        val dataBinding = DataBindingUtil.inflate<ItemPopupwindowRvBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_popupwindow_rv,
            parent,
            false
        )
        return InnerHolder(dataBinding.root, dataBinding)
    }

    override fun onBindViewHolder(holder: InnerHolder, position: Int) {
        val track = trackList[position]

        holder.dataBinding.apply {
            val realPosition = position + 1



            playerPopData = PlayerPopupWinItemData(realPosition.toString(), track.trackTitle)

            setColorByPosition(position)


            popItemContainer.setOnClickListener {
                if (this@PlayerPopupWinAdapter::onPlayerPopWinItemClickListener.isInitialized) {
                    onPlayerPopWinItemClickListener.onPopItemClick(trackList, position)
                }
                setColorByPosition(position)
            }
        }
    }

    private fun ItemPopupwindowRvBinding.setColorByPosition(position: Int) {
        if (currentPlayPosition == position) {
            popItemNumber.setTextColor(ContextCompat.getColor(BaseApp.mBaseContext!!,
                R.color.sienna))
            popItemTitle.setTextColor(ContextCompat.getColor(BaseApp.mBaseContext!!,
                R.color.sienna))
        } else {
            popItemNumber.setTextColor(ContextCompat.getColor(BaseApp.mBaseContext!!,
                R.color.black))
            popItemTitle.setTextColor(ContextCompat.getColor(BaseApp.mBaseContext!!, R.color.black))
        }
    }

    override fun getItemCount() = trackList.size

    @SuppressLint("NotifyDataSetChanged")
    fun getTrackList(it: List<Track>) {
        trackList.clear()
        if (it.isNotEmpty()) {
            trackList.addAll(it)
        }
        notifyDataSetChanged()
    }

    fun setOnPlayerPopWinItemClickListener(onPlayerPopWinItemClickListener: OnPlayerPopWinItemClickListener) {
        this.onPlayerPopWinItemClickListener = onPlayerPopWinItemClickListener
    }

    fun getCurrentPosition(currentPlayPosition: Int) {

        this.currentPlayPosition = currentPlayPosition

        notifyDataSetChanged()
    }

    interface OnPlayerPopWinItemClickListener {
        fun onPopItemClick(track: List<Track>, position: Int)
    }
}