package com.example.ximalaya.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.ximalaya.ting.android.opensdk.model.track.Track

@Dao
interface HistoryDao {
    @Insert
    suspend fun insertHistory(track: HistoryData)

    @Query("DELETE FROM history ")
    suspend fun clearHistory()


    @Query("DELETE FROM history WHERE title = :keyword")
    suspend fun deleteHistory(keyword: String)

    @Query("DELETE FROM HISTORY WHERE id = :listPosition")
    suspend fun deleteLastHistory(listPosition: Int)

    @Query("SELECT * FROM history")
    fun queryHistory(): LiveData<List<HistoryData>>


}