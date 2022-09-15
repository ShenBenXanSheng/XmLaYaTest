package com.example.ximalaya.room

import android.util.Log
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history")
data class HistoryData(
    @ColumnInfo(name = "title", typeAffinity = ColumnInfo.TEXT)
    var title: String,
    @ColumnInfo(name = "cover", typeAffinity = ColumnInfo.TEXT)
    var cover: String,
    @ColumnInfo(name = "kind", typeAffinity = ColumnInfo.TEXT)
    var kind: String,
    @ColumnInfo(name = "playCount", typeAffinity = ColumnInfo.INTEGER)
    var playCount: Int,
    @ColumnInfo(name = "trackId", typeAffinity = ColumnInfo.INTEGER)
    var trackId: Long,
    @ColumnInfo(name = "upDateTime", typeAffinity = ColumnInfo.INTEGER)
    var upDateTime: Long,
    @ColumnInfo(name = "nickName", typeAffinity = ColumnInfo.TEXT)
    var nickName: String,
) {
    @ColumnInfo(name = "id", typeAffinity = ColumnInfo.INTEGER)
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}

