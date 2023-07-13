package com.sophimp.are.utils

import android.text.Spanned
import com.sophimp.are.RichEditText
import com.sophimp.are.spans.SearchHighlightSpan
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

/**
 * RichEditText 搜索方法扩展
 */

/**
 * 搜索上一下
 */
fun RichEditText.searchPrev() {
    if (cacheSearchHighlightSpans.isEmpty()) return
    curHighlightSpanIndex -= 1
    if (curHighlightSpanIndex < 0) {
        curHighlightSpanIndex = cacheSearchHighlightSpans.size - 1
    }
    curHighlightSpans.clear()
    curHighlightSpans.add(cacheSearchHighlightSpans[curHighlightSpanIndex])
    postInvalidate()
//    scrollTo(0, getSearchSpanYOffset(cacheSearchHighlightSpans[curHighlightSpanIndex]))
}

/**
 * 搜索下一个
 */
fun RichEditText.searchNext() {
    if (cacheSearchHighlightSpans.isEmpty()) return
    curHighlightSpanIndex += 1
    if (curHighlightSpanIndex >= cacheSearchHighlightSpans.size) {
        curHighlightSpanIndex = 0;
    }
    curHighlightSpans.clear()
    curHighlightSpans.add(cacheSearchHighlightSpans[curHighlightSpanIndex])
    postInvalidate()
//    scrollTo(0, getSearchSpanYOffset(cacheSearchHighlightSpans[curHighlightSpanIndex]))
}

/**
 * 获取span的 Y偏移坐标
 */
fun RichEditText.getSearchSpanYOffset(): Int {
    if (curHighlightSpanIndex < 0 || curHighlightSpanIndex >= cacheSearchHighlightSpans.size) return 0
    val spanStart = editableText.getSpanStart(cacheSearchHighlightSpans[curHighlightSpanIndex])
    val startLine = layout.getLineForOffset(spanStart)
    val lineTop = layout.getLineTopWithoutPadding(startLine) - 1
    return lineTop
}

/**
 * 关闭搜索
 */
fun RichEditText.closeSearch() {
    cacheSearchHighlightSpans.forEach { span ->
        editableText.removeSpan(span)
    }
    curHighlightSpanIndex = -1
    cacheSearchHighlightSpans.clear()
    curHighlightSpans.clear()
    searchKeys.clear()
    invalidate()
}

/**
 * 更新搜索内容
 */
fun RichEditText.updateSearchKeys(keys: List<String>): Int {
    searchKeys.clear()
    cacheSearchHighlightSpans.clear()
    curHighlightSpanIndex = 0
    searchKeys.addAll(keys)
    var startIndex = 0
    val totalLength = length()
    keys.forEach { key ->
        startIndex = 0
        while (startIndex < totalLength) {
            val matchIndex = editableText.indexOf(key, startIndex)
            if (matchIndex >= 0) {
                val highlightSpan = SearchHighlightSpan()
                editableText.setSpan(
                    highlightSpan,
                    matchIndex,
                    matchIndex + key.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                cacheSearchHighlightSpans.add(highlightSpan)
                startIndex = matchIndex + key.length
            } else {
                startIndex = totalLength
            }
        }
    }
    MainScope().launch {
        launch(Dispatchers.IO) {
            cacheSearchHighlightSpans.sortBy { span -> editableText.getSpanStart(span) }
            curHighlightSpans.clear()
            curHighlightSpans.addAll(cacheSearchHighlightSpans)
            postInvalidate()
        }
    }
    return cacheSearchHighlightSpans.size
}
