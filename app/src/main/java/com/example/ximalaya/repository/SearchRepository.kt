package com.example.ximalaya.repository

import com.ximalaya.ting.android.opensdk.constants.DTransferConstants
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack
import com.ximalaya.ting.android.opensdk.model.album.SearchAlbumList
import com.ximalaya.ting.android.opensdk.model.word.SuggestWords

class SearchRepository {
    fun getSuggestWords(word: String, iDataCallBack: IDataCallBack<SuggestWords>) {
        val map = mutableMapOf<String, String>()
        map[DTransferConstants.SEARCH_KEY] = word
        CommonRequest.getSuggestWord(map, iDataCallBack)
    }


    fun getSearchAlbums(iDataCallBack: IDataCallBack<SearchAlbumList>, keyword: String, page: Int) {
        val map = mutableMapOf<String, String>()
        map.put(DTransferConstants.SEARCH_KEY, keyword)
        map.put(DTransferConstants.CATEGORY_ID, 0.toString())
        map.put(DTransferConstants.PAGE, page.toString())
        CommonRequest.getSearchedAlbums(map, iDataCallBack)
    }
}