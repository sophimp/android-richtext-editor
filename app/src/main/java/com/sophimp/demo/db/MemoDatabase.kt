package com.sophimp.demo.db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.sophimp.demo.AppApplication

/**
 *
 * @author: sfx
 * @since: 2021/8/5
 */
@Database(entities = [MemoInfo::class], version = 1)
abstract class MemoDatabase : RoomDatabase() {

    companion object {
        val instance = Room.databaseBuilder(AppApplication.context!!, MemoDatabase::class.java, "memo_list_database")
            .allowMainThreadQueries().build()
    }

    abstract fun getMemoDao(): MemoDao

}