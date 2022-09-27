package com.example.ximalaya.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ximalaya.domian.MyAlbumData
import com.example.ximalaya.repository.DetailRepository
import com.example.ximalaya.utils.Constant
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack
import com.ximalaya.ting.android.opensdk.model.track.TrackList
import okhttp3.internal.notify

class DetailViewModel : ViewModel() {
    private var currentAlbumId: Int = 0
    private val detailRepository by lazy {
        DetailRepository()
    }
    var detailPage = Constant.DETAIL_PAGE
    val trackLiveList = MutableLiveData<TrackList>()
    val trackLoadMoreLiveList = MutableLiveData<TrackList>()
    val trackLiveState = MutableLiveData<DetailLoadStatus>()

    val albumLiveData = MutableLiveData<MyAlbumData>()

    enum class DetailLoadStatus {
        LOADING, SUCCESS, ERROR, EMPTY, LOAD_MORE_SUCCESS, LOAD_MORE_ERROR, LOAD_MORE_EMPTY
    }


    fun getDetailData(albumId: Int) {

            this.currentAlbumId = albumId
            trackLiveState.postValue(DetailLoadStatus.LOADING)
            detailRepository.getDetailData(object : IDataCallBack<TrackList> {
                override fun onSuccess(p0: TrackList?) {
                    if (p0 != null) {

                        trackLiveList.postValue(p0)
                        trackLiveState.postValue(DetailLoadStatus.SUCCESS)
                    } else {
                        trackLiveState.postValue(DetailLoadStatus.EMPTY)
                    }
                }

                override fun onError(p0: Int, p1: String?) {
                    trackLiveState.postValue(DetailLoadStatus.ERROR)
                }


            }, albumId.toString(), detailPage)


    }

    fun getCurrentAlbum(album: MyAlbumData) {
        albumLiveData.value = album
    }

    fun getDetailDataLoadMoreData() {
        detailPage++
        detailRepository.getDetailData(object : IDataCallBack<TrackList> {
            override fun onSuccess(p0: TrackList?) {
                if (p0 != null && p0.tracks.size != 0) {
                    trackLoadMoreLiveList.postValue(p0)
                    trackLiveState.postValue(DetailLoadStatus.LOAD_MORE_SUCCESS)
                } else {
                    trackLiveState.postValue(DetailLoadStatus.LOAD_MORE_EMPTY)
                }
            }

            override fun onError(p0: Int, p1: String?) {
                trackLiveState.postValue(DetailLoadStatus.LOAD_MORE_ERROR)
                Log.d("TAG", "专辑详情加载更多错误信息:${p1} 错误代码:${p0}")
            }

        }, currentAlbumId.toString(), detailPage)


    }


}