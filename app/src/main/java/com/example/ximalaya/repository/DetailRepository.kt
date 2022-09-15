package com.example.ximalaya.repository

import com.example.ximalaya.utils.Constant
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack
import com.ximalaya.ting.android.opensdk.model.track.TrackList


class DetailRepository {
    fun getDetailData(detailCallBack: IDataCallBack<TrackList>, albumId: String, detailPage:Int) {
        val map: MutableMap<String, String> = HashMap()
        map[DTransferConstants.ALBUM_ID] = albumId
        map[DTransferConstants.SORT] = "asc"
        map[DTransferConstants.PAGE] = detailPage.toString()
        map[DTransferConstants.PAGE_SIZE] = Constant.DETAIL_COUNT

        CommonRequest.getTracks(map, detailCallBack)
    }
}