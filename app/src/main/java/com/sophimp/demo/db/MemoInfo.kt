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
    @PrimaryKey val id: Long,
    var richText: String? = ""
)