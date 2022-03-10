package com.sophimp.demo.db

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 *
 * @author: sfx
 * @since: 2021/8/4
 */
@Entity
data class MemoInfo(
    var title: String?,
    var richText: String?
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}