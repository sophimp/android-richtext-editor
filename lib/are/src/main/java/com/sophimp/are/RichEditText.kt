package com.sophimp.are

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText

/**
 * rich text editor
 * @author: sfx
 * @since: 2021/7/20
 */
class RichEditText(context: Context, attr: AttributeSet) : AppCompatEditText(context, attr) {
    private var canMonitor: Boolean = true
    private val uiHandler = Handler(Looper.getMainLooper())

    init {

    }

    fun stopMonitor() {
        canMonitor = false
    }

    fun startMonitor() {
        canMonitor = true
    }

    fun refresh(start: Int) {
        stopMonitor()
        editableText.insert(0, " ")
        editableText.delete(0, 1)

        editableText.insert(start, " ")
        editableText.delete(start, start + 1)
        startMonitor()
    }

    fun postDelayUIRun(runnable: Runnable, delay: Long) {
        uiHandler.postDelayed(runnable, delay);
    }

    var isChange: Boolean = false

}