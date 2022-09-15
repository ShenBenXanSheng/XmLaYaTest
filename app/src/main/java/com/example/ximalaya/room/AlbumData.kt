package com.example.ximalaya.room

import android.util.Log
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ximalaya.ting.android.opensdk.model.album.Album


@Entity
data class AlbumData(
    var albumTitle: String,
    var albumId: Int,
    var albumCover:String,
    var albumMsg: String,
    var albumPlayCount: String,
    var albumPlayEpisodes: String
){

    @PrimaryKey(autoGenerate = true)
    var _id: Long = 0
}
