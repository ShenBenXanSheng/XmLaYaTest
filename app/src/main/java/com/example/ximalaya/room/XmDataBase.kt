package com.example.ximalaya.room

import android.content.Context
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(version = 2, entities = [AlbumData::class, HistoryData::class], exportSchema = true)
abstract class XmDataBase : RoomDatabase() {
    abstract fun getAlbumDao(): AlbumDao

    abstract fun getHistoryDao(): HistoryDao


    companion object {
        private var instance: XmDataBase? = null

        private val MIGRATION1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE 'history' ('id' INTEGER  PRIMARY KEY autoincrement NOT NULL," +
                        "'title' TEXT NOT NULL , " +
                        "'cover' TEXT NOT NULL , " +
                        "'kind' TEXT NOT NULL , " +
                        "'playCount' INTEGER NOT NULL DEFAULT 0," +
                        "'trackId' INTEGER NOT NULL DEFAULT 0," +
                        "'upDateTime' INTEGER NOT NULL DEFAULT 0," +
                        "'nickName' TEXT NOT NULL )")
            }

        }

        @Synchronized
        fun getAlbumDataBase(context: Context): XmDataBase {
            instance?.let {
                return it
            }

            return Room.databaseBuilder(context, XmDataBase::class.java, "ximalayaDataBase.db")

                .addMigrations(MIGRATION1_2)
                .build().apply {
                    instance = this
                }
        }
    }
}