package com.example.ximalaya.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ximalaya.R
import com.example.ximalaya.adapter.AlbumAdapter
import com.example.ximalaya.base.BaseViewModelFragment

import com.example.ximalaya.databinding.FragmentSubscribeBinding
import com.example.ximalaya.domian.MyAlbumData
import com.example.ximalaya.utils.Constant
import com.example.ximalaya.view.DialogControl
import com.example.ximalaya.viewmodel.SubscribeViewModel

class SubscribeFragment : BaseViewModelFragment<SubscribeViewModel>() {
    private lateinit var dataBinding: FragmentSubscribeBinding
    private val albumAdapter by lazy {
        AlbumAdapter()
    }

    private val dialogControl by lazy {
        DialogControl(requireContext())
    }
    override fun getSuccessFragmentView(rootFrameLayout: FrameLayout): View {
        dataBinding = DataBindingUtil.inflate(
            LayoutInflater.from(requireContext()),
            R.layout.fragment_subscribe,
            rootFrameLayout,
            false
        )

        return dataBinding.root
    }

    override fun getViewModelClass() = SubscribeViewModel::class.java

    override fun initView() {
        dispatchViewState(FragmentStatus.SUCCESS)
        dataBinding.subscribeRv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = albumAdapter
        }
    }

    private val myAlbumDataList = mutableListOf<MyAlbumData>()


    override fun initListener() {
        super.initListener()
        albumAdapter.setOnRecommendItemClickListener(object :AlbumAdapter.OnRecommendItemClickListener{
            override fun onRecommendItemClick(albumId: Int, album: MyAlbumData) {
                val intent = Intent(requireActivity(), DetailFragment::class.java)
                intent.putExtra(Constant.PUT_ALBUM_ID, albumId)
                intent.putExtra(Constant.PUT_ALBUM,album)
                startActivity(intent)
            }

            override fun onRecommendLongClick(album: MyAlbumData) {
                dialogControl.apply {
                    upDateDialogState(DialogControl.DialogStatus.DELETE)
                    setOnDialogDeleteClickListener(object :DialogControl.OnDialogDeleteClickListener{
                        @SuppressLint("NotifyDataSetChanged")
                        override fun onDeleteClick() {
                            mViewModel?.deleteAlbum(album.albumTitle)
                            Toast.makeText(requireContext(),"删除成功",Toast.LENGTH_SHORT).show()
                            myAlbumDataList.clear()
                        }
                    })
                    show()
                }
            }
        })
    }

    override fun initDataListener() {
        super.initDataListener()
        mViewModel?.apply {
            queryAlbums().observe(this@SubscribeFragment) {
                myAlbumDataList.clear()

                it.forEach { albumData ->
                    val myAlbumData = MyAlbumData(albumData.albumTitle,
                        albumData.albumCover,
                        albumData.albumMsg,
                        albumData.albumPlayCount.toLong(),
                        albumData.albumPlayEpisodes.toLong())
                    myAlbumData.id = albumData.albumId.toString()
                    myAlbumDataList.add(myAlbumData)
                }
                albumAdapter.setData(myAlbumDataList)
            }
        }
    }

}