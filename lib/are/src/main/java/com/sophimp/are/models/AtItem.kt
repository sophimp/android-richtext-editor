package com.sophimp.are.models

import android.graphics.Color
import java.io.Serializable

class AtItem constructor(
    var mKey: String,
    var mName: String,
    color: Int = Color.BLUE
) : Serializable {
    var mIconId: Int
    var mColor: Int

    init {
        mIconId = mKey.toInt()
        mColor = color
    }
}