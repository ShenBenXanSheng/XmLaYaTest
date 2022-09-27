package com.example.ximalaya.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.ximalaya.R
import com.example.ximalaya.databinding.ItemRecommendBinding
import com.example.ximalaya.domian.MyAlbumData
import com.example.ximalaya.domian.RecommendAlbum
import com.ximalaya.ting.android.opensdk.model.album.Album

class AlbumAdapter : RecyclerView.Adapter<AlbumAdapter.InnerHolder>() {
    private lateinit var onRecommendItemClickListener: OnRecommendItemClickListener
    private val albumList = mutableListOf<MyAlbumData>()

    companion object {
        @JvmStatic
        @BindingAdapter("playCover")
        fun setRecommendAlbumCover(imageView: ImageView, cover: Any) {

            Glide.with(imageView).load(cover)
                .apply(RequestOptions.bitmapTransform(RoundedCorners(20))).into(imageView)
        }
    }

    class InnerHolder(itemView: View, val dataBinding: ItemRecommendBinding) :
        RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InnerHolder {
        val dataBinding = DataBindingUtil.inflate<ItemRecommendBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_recommend,
            parent,
            false
        )

        return InnerHolder(dataBinding.root, dataBinding)
    }

    override fun onBindViewHolder(holder: InnerHolder, position: Int) {
        val album = albumList[position]
        album.apply {
            val title = albumTitle
            val cover = coverUrlMiddle.ifBlank {
                R.mipmap.paimeng
            }
            val msg = albumIntro

            val albumPlayCount = if (playCount > 99999999) {
                String.format("%.1f", (playCount / 100000000).toDouble()) + "亿"
            } else if (playCount > 9999) {
                String.format("%.1f", (playCount / 10000).toDouble()) + "万"
            } else {
                playCount.toString()
            }

            val albumPlayEpisodes = includeTrackCount.toString() + "集"

            val handleAlbum = RecommendAlbum(title, cover, msg, albumPlayCount, albumPlayEpisodes)
            holder.dataBinding.album = handleAlbum

            handleAlbum.albumid = album.id.toInt()

            if (this@AlbumAdapter::onRecommendItemClickListener.isInitialized) {
                holder.dataBinding.recommendContainer.apply {
                    setOnClickListener {
                        onRecommendItemClickListener.onRecommendItemClick(album.id.toInt(), album)


                    }
                    setOnLongClickListener {
                        onRecommendItemClickListener.onRecommendLongClick(album)

                        true
                    }
                }


            }
        }
    }

    override fun getItemCount() = albumList.size

    @SuppressLint("NotifyDataSetChanged")
    fun setData(it: List<MyAlbumData>?) {
        albumList.clear()
        if (it != null) {
            albumList.addAll(it)
        }
        notifyDataSetChanged()

    }

    fun setLoadMoreData(it: List<MyAlbumData>?) {
        if (it != null) {
            albumList.addAll(it)
            notifyItemChanged(albumList.size, it.size - 1)
        }
    }

    fun setOnRecommendItemClickListener(onRecommendItemClickListener: OnRecommendItemClickListener) {
        this.onRecommendItemClickListener = onRecommendItemClickListener

    }


    interface OnRecommendItemClickListener {
        fun onRecommendItemClick(albumId: Int, album: MyAlbumData)
        fun onRecommendLongClick(album: MyAlbumData)
    }
}