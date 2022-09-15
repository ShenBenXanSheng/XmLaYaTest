package com.example.ximalaya.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.ximalaya.ting.android.opensdk.model.album.Album

@Dao
interface AlbumDao {

    @Insert
    suspend fun insertAlbum(album: AlbumData)

    @Query("delete from AlbumData where albumTitle = :title")
    suspend fun deleteAlbum(title: String)

    @Query("delete from AlbumData")
    suspend fun clearAlbum()

    @Query("select * from AlbumData")
     fun queryAlbums(): LiveData<List<AlbumData>>

    @Query("select * from AlbumData where albumTitle like '%' || :keyword || '%'")
     fun queryAlbum(keyword: String): LiveData<List<AlbumData>>
}