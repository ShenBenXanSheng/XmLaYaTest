package com.example.ximalaya.repository

import com.example.ximalaya.utils.Constant.RECOMMEND_LIKE_COUNT
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack
import com.ximalaya.ting.android.opensdk.model.album.GussLikeAlbumList


class RecommendRepository {
    fun getGuessLikeAlbum(callback: IDataCallBack<GussLikeAlbumList>){
        val map: MutableMap<String, String> = HashMap()
        map[DTransferConstants.LIKE_COUNT] = RECOMMEND_LIKE_COUNT
        CommonRequest.getGuessLikeAlbum(map,callback)
    }
}