package com.example.ximalaya.repository

import android.content.Context
import com.example.ximalaya.room.AlbumData
import com.example.ximalaya.room.XmDataBase

class SubscribeRepository(val context: Context) {
    private val xmDataBase: XmDataBase by lazy {
        XmDataBase.getAlbumDataBase(context)
    }

    private val albumDao = xmDataBase.getAlbumDao()

    suspend fun insertAlbum(album: AlbumData) {

        albumDao.insertAlbum(album)

    }

    suspend fun deleteAlbum(keyword: String) {

        albumDao.deleteAlbum(keyword)

    }

    suspend fun clearAlbum() {

        albumDao.clearAlbum()

    }

    fun queryAlbums() = albumDao.queryAlbums()

    fun queryAlbum(keyword: String) = albumDao.queryAlbum(keyword)
}