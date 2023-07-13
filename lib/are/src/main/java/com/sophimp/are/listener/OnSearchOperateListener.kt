package com.sophimp.are.listener

/**
 * 搜索交互监听
 */
interface OnSearchOperateListener {
    /**
     * 上一个
     */
    fun onPreClick(): Int

    /**
     * 下一个
     */
    fun onNextClick(): Int

    /**
     * 搜索
     */
    fun onSearch(searchKeys: List<String>): Int

    /**
     * 关闭
     */
    fun onCloseClick()
}