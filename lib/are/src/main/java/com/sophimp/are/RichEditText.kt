package com.sophimp.are

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatEditText
import com.sophimp.are.spans.IClickableSpan
import com.sophimp.are.spans.ImageSpan2
import com.sophimp.are.spans.UrlSpan
import com.sophimp.are.spans.VideoSpan
import com.sophimp.are.style.IStyle
import kotlin.math.min

/**
 * rich text editor
 * @author: sfx
 * @since: 2021/7/20
 */
class RichEditText(context: Context, attr: AttributeSet) : AppCompatEditText(context, attr) {
    private var canMonitor: Boolean = true
    private val uiHandler = Handler(Looper.getMainLooper())

    private var styleList: MutableList<IStyle> = arrayListOf()

    private var isEditMode = true

    var clickStrategy: IEditorClickStrategy? = DefaultClickStrategyImpl()

    init {
        setupTextWatcher()
    }

    private val gestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
        override fun onSingleTapUp(e: MotionEvent?): Boolean {
            if (!isEditMode) return false
            val offset = Util.getTextOffset(this@RichEditText, e!!)
            val clickSpans = editableText.getSpans(offset, offset, IClickableSpan::class.java)
            if (clickSpans.isEmpty()) return false
            when {
                clickSpans[0] is UrlSpan -> {
                    clickStrategy?.onClickUrl(context, clickSpans[0] as UrlSpan)
                }
                clickSpans[0] is ImageSpan2 -> {
                    clickStrategy?.onClickImage(context, clickSpans[0] as ImageSpan2)
                }
                clickSpans[0] is VideoSpan -> {
                    clickStrategy?.onClickVideo(context, clickSpans[0] as VideoSpan)
                }
            }
            return false
        }
    })

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        gestureDetector.onTouchEvent(event)
        return super.onTouchEvent(event)
    }

    /**
     * Monitoring text changes.
     */
    private fun setupTextWatcher() {
        // 用来判断是点周事件还是删除事件
        var beforeSelectionStart = 0
        var beforeSelectionEnd = 0
        var afterSelectionStart = 0
        var changedText: String = ""
        val mTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence,
                start: Int,
                count: Int,
                after: Int
            ) {
                if (!canMonitor) {
                    return
                }
                beforeSelectionStart = selectionStart
                beforeSelectionEnd = selectionEnd
                changedText = ""
//                if (BuildConfig.DEBUG) {
//                    Util.log(("beforeTextChanged:: s = $s, start = $start, count = $count, after = $after"))
//                }
            }

            override fun onTextChanged(
                s: CharSequence,
                start: Int,
                before: Int,
                count: Int
            ) {
                if (!canMonitor) {
                    return
                }
                isChange = true
//                if (BuildConfig.DEBUG) {
//                    Util.log(("onTextChanged:: s = $s, start = $start, count = $count, before = $before"))
//                }
            }

            override fun afterTextChanged(s: Editable) {
                if (!canMonitor) {
                    return
                }

                // 一旦内容发生变化后，结束后肯定是光标状态, start == end
                afterSelectionStart = selectionStart
                var textEvent: IStyle.TextEvent = IStyle.TextEvent.IDLE
                if (beforeSelectionStart == beforeSelectionEnd) {
                    // 处于光标状态
                    if (beforeSelectionStart < afterSelectionStart) {
                        // 输入或复制操作
                        changedText = getEditableText().subSequence(
                            beforeSelectionStart,
                            Math.min(afterSelectionStart, length())
                        ).toString()
                        val findNewLine: Int = changedText.indexOf("\n")
                        textEvent = if (findNewLine > 0 && findNewLine < changedText.length - 1) {
                            IStyle.TextEvent.INPUT_MULTI_PARAGRAPH
                        } else if (findNewLine == 0 && changedText.length > 1) {
                            IStyle.TextEvent.INPUT_MULTI_PARAGRAPH
                        } else if (findNewLine == changedText.length - 1) {
                            IStyle.TextEvent.INPUT_NEW_LINE
                        } else {
                            // 没找到
                            IStyle.TextEvent.INPUT_SINGLE_PARAGRAPH
                        }
                    } else if (beforeSelectionStart > afterSelectionStart) {
                        // 删除操作
                        textEvent = IStyle.TextEvent.DELETE
                    } //else {/*copy操作, 不会触发，由Menu处理*/}
                } else {
                    // 处于选择状态
                    if (beforeSelectionStart == afterSelectionStart) {
                        // 删除或剪切操作
                        textEvent = IStyle.TextEvent.DELETE
                    } else if (afterSelectionStart > beforeSelectionStart) {
                        // 输入或复制操作
                        changedText = getEditableText().subSequence(
                            beforeSelectionStart,
                            min(afterSelectionStart + 1, length())
                        ).toString()
                        val findNewLine: Int = changedText.indexOf("\n")
                        textEvent = if (findNewLine > 0 && findNewLine <= changedText.length - 1) {
                            IStyle.TextEvent.INPUT_MULTI_PARAGRAPH
                        } else if (findNewLine == 0 && changedText.length > 1) {
                            IStyle.TextEvent.INPUT_MULTI_PARAGRAPH
                        } else {
                            // find nothing
                            IStyle.TextEvent.INPUT_SINGLE_PARAGRAPH
                        }
                    }
                }
//                if (BuildConfig.DEBUG)
//                    Util.log(("sgx cake before change selection: $beforeSelectionStart - $beforeSelectionEnd after change selection: $afterSelectionStart   \n textEvent: $textEvent start: $startPos end: $endPos changeText: $changedText"))
                stopMonitor()

                for (style: IStyle in styleList) {
                    val epStart = Util.getParagraphStart(this@RichEditText, min(beforeSelectionStart, afterSelectionStart))
                    val epEnd = Util.getParagraphEnd(editableText, afterSelectionStart)
                    style.applyStyle(s, textEvent, changedText, beforeSelectionStart, afterSelectionStart, epStart, epEnd)
                }
                refresh(0)
                startMonitor()
            }
        }
        addTextChangedListener(mTextWatcher)
    }

    /**
     * 编辑时需要作用的style
     */
    fun addStyle(style: IStyle) {
        styleList.add(style)
    }

    /**
     * 编辑时需要作用的style list
     */
    fun addStyleList(styles: List<IStyle>) {
        styleList.clear()
        styleList.addAll(styles)
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