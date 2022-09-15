package com.example.ximalaya.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewModelScope


import com.example.ximalaya.repository.SubscribeRepository
import com.example.ximalaya.room.AlbumData
import com.ximalaya.ting.android.opensdk.model.album.Album
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SubscribeViewModel(application: Application) : AndroidViewModel(application) {

    private val subscribeRepository: SubscribeRepository by lazy {
        SubscribeRepository(application.applicationContext)
    }

    fun insertAlbum(album: AlbumData) {
        viewModelScope.launch {
             subscribeRepository.insertAlbum(album)
        }

    }

    fun deleteAlbum(keyword: String) {
        viewModelScope.launch {
             subscribeRepository.deleteAlbum(keyword)
        }

    }

    fun clearAlbum() {
        viewModelScope.launch {
             subscribeRepository.clearAlbum()
        }

    }

    fun queryAlbums() = subscribeRepository.queryAlbums()

    fun queryAlbum(keyword: String) = subscribeRepository.queryAlbum(keyword)
}