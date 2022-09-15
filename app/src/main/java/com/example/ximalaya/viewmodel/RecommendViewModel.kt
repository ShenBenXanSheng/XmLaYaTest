package com.example.ximalaya.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ximalaya.livedata.MutableLiveDataStatus
import com.example.ximalaya.repository.RecommendRepository
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack
import com.ximalaya.ting.android.opensdk.model.album.Album
import com.ximalaya.ting.android.opensdk.model.album.GussLikeAlbumList

class RecommendViewModel : ViewModel() {
    private val recommendRepository by lazy {
        RecommendRepository()
    }

    enum class RecommendStatus {
        LOADING, SUCCESS, EMPTY, ERROR,REFRESH_SUCCESS,REFRESH_ERROR
    }


    val recommendStatusLive = MutableLiveData<RecommendStatus>()

    val albumLiveList = MutableLiveDataStatus<List<Album>>()

    fun getRecommendData() {

        recommendStatusLive.postValue(RecommendStatus.LOADING)
        recommendRepository.getGuessLikeAlbum(object : IDataCallBack<GussLikeAlbumList> {
            override fun onSuccess(p0: GussLikeAlbumList?) {
                if (p0 != null) {
                    if (p0.albumList.isNotEmpty()) {
                        recommendStatusLive.postValue(RecommendStatus.SUCCESS)


                        albumLiveList.value = p0.albumList
                    } else {
                        recommendStatusLive.postValue(RecommendStatus.EMPTY)
                        Log.d("TAG","数据为空")
                    }
                }
            }

            override fun onError(p0: Int, p1: String?) {
                recommendStatusLive.postValue(RecommendStatus.ERROR)
            }

        })
    }



}