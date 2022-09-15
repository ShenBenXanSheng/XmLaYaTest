package com.example.ximalaya.repository

import android.content.Context
import com.example.ximalaya.room.HistoryData
import com.example.ximalaya.room.XmDataBase

class HistoryRepository(context: Context) {
    private val xmDataBase by lazy {
        XmDataBase.getAlbumDataBase(context)
    }

    private val historyDao by lazy {
        xmDataBase.getHistoryDao()
    }

    suspend fun insertHistory(historyData: HistoryData) {
        historyDao.insertHistory(historyData)
    }


    suspend fun deleteHistory(keyword:String){
        historyDao.deleteHistory(keyword)
    }

    suspend fun clearHistory(){
        historyDao.clearHistory()
    }

    suspend fun deleteLastHistory(position:Int){
        historyDao.deleteLastHistory(position)
    }

    fun queryHistoryList() = historyDao.queryHistory()
}