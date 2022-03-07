package com.sophimp.are.models

import com.sophimp.are.RichEditText

/**
 */
interface StyleChangedListener {
    /**
     * 样式发生了变化， 用于通知上层应用及时保存
     */
    fun onStyleChanged(drEditText: RichEditText?)
}