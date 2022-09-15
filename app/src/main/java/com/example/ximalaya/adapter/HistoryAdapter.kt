package com.example.ximalaya.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.ximalaya.R
import com.example.ximalaya.databinding.ItemHistoryBinding

import com.example.ximalaya.domian.HistoryTrackData
import com.example.ximalaya.room.HistoryData
import com.ximalaya.ting.android.opensdk.model.track.Track
import java.text.SimpleDateFormat


class HistoryAdapter(): RecyclerView.Adapter<HistoryAdapter.InnerHolder>() {
    private val  historyList = mutableListOf<HistoryData>()
    private lateinit var onHistoryClickListener: OnHistoryClickListener

    private val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
    class InnerHolder(itemView: View, val dataBinding: ItemHistoryBinding) :
        RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryAdapter.InnerHolder {
        val dataBinding = DataBindingUtil.inflate<ItemHistoryBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_history,
            parent,
            false
        )
        return InnerHolder(dataBinding.root, dataBinding)
    }

    override fun onBindViewHolder(holder: InnerHolder, position: Int) {
        val realPosition = position + 1
        historyList[position].apply {
            val format = simpleDateFormat.format(upDateTime)
            holder.dataBinding.historyTrackData =
                HistoryTrackData(realPosition.toString(), title,nickName, playCount.toString(), format)
        }

        holder.dataBinding.historyContainer.setOnClickListener {
            if (this@HistoryAdapter::onHistoryClickListener.isInitialized) {
                onHistoryClickListener.onHistoryClick(historyList,position)
            }
        }
    }

    override fun getItemCount() = historyList.size

    fun setData(it: List<HistoryData>?) {
        historyList.clear()
        if (it != null) {
            historyList.addAll(it)
            notifyDataSetChanged()
        }
    }
    
    fun setOnHistoryClickListener(onHistoryClickListener: OnHistoryClickListener) {
        this.onHistoryClickListener = onHistoryClickListener

    }

    interface OnHistoryClickListener {
        fun onHistoryClick(track: List<HistoryData>, position: Int)
    }
    
}