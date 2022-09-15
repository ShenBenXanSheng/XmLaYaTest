package com.example.ximalaya.domian

import android.os.Parcel
import android.os.Parcelable
import androidx.versionedparcelable.VersionedParcelize
import com.ximalaya.ting.android.opensdk.model.album.Album
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MyAlbumData(
    val albumTitle: String,
    val coverUrlMiddle: String,
    val albumIntro: String,
    val playCount: Long,
    val includeTrackCount: Long,
):Parcelable {

    var id = ""

}