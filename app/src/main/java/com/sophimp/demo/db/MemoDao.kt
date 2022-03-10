package com.sophimp.demo.db

import androidx.room.*

/**
 *
 * @author: sfx
 * @since: 2021/8/5
 */
@Dao
interface MemoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addMemo(vararg memoInfo: MemoInfo)

    @Delete
    fun deleteMemo(memoInfo: MemoInfo)

    @Query("select * from MemoInfo")
    fun queryMemoAll(): MutableList<MemoInfo>

    @Query("select * from MemoInfo where id = :id")
    fun queryMemoById(id: Long): MemoInfo
}