package com.example.ximalaya.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ximalaya.repository.HistoryRepository
import com.example.ximalaya.room.HistoryData
import kotlinx.coroutines.launch

class HistoryViewModel(application: Application) : AndroidViewModel(application) {
    private val historyRepository by lazy {
        HistoryRepository(application.applicationContext)
    }

    fun insertHistory(historyData: HistoryData) {
        viewModelScope.launch {
            historyRepository.insertHistory(historyData)
        }
    }

    fun deleteHistory(keyword: String) {
        viewModelScope.launch {
            historyRepository.deleteHistory(keyword)
        }
    }


    fun clearHistory() {
        viewModelScope.launch {
            historyRepository.clearHistory()
        }
    }

    fun deleteLastHistory(position: Int) {
        viewModelScope.launch {
            historyRepository.deleteLastHistory(position)
        }
    }

    fun queryHistoryList() = historyRepository.queryHistoryList()
}