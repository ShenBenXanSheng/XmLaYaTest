package com.example.ximalaya.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.ximalaya.R
import com.example.ximalaya.databinding.ItemSuggestBinding
import com.ximalaya.ting.android.opensdk.model.word.QueryResult

class SuggestAdapter : RecyclerView.Adapter<SuggestAdapter.InnerHolder>() {
    private lateinit var onSuggestItemClickListener: OnSuggestItemClickListener
    private val keyWordList = mutableListOf<QueryResult>()

    class InnerHolder(itemView: View, val dataBinding: ItemSuggestBinding) :
        RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InnerHolder {
        val dataBinding =
            DataBindingUtil.inflate<ItemSuggestBinding>(LayoutInflater.from(parent.context),
                R.layout.item_suggest,
                parent,
                false)
        return InnerHolder(dataBinding.root, dataBinding)
    }

    override fun onBindViewHolder(holder: InnerHolder, position: Int) {
        val queryResult = keyWordList[position]

        holder.dataBinding.apply {
            suggestTv.text = queryResult.keyword
            suggestContainer.setOnClickListener {
                if (this@SuggestAdapter::onSuggestItemClickListener.isInitialized){
                    onSuggestItemClickListener.onSuggestClick(queryResult.keyword)
                }
            }
        }
    }

    override fun getItemCount() = keyWordList.size

    fun setData(dataList: List<QueryResult>) {
        keyWordList.clear()
        dataList?.let {
            keyWordList.addAll(dataList)
            notifyDataSetChanged()
        }
    }

    fun setOnSuggestItemClickListener(onSuggestItemClickListener: OnSuggestItemClickListener){
        this.onSuggestItemClickListener = onSuggestItemClickListener
    }

    interface OnSuggestItemClickListener{
        fun onSuggestClick(text:String)
    }
}