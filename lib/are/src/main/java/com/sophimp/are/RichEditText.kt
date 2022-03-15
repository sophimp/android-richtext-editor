package com.sophimp.are

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.text.*
import android.text.style.ImageSpan
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.EditText
import androidx.appcompat.widget.AppCompatEditText
import com.sophimp.are.inner.Html
import com.sophimp.are.listener.ImageLoadedListener
import com.sophimp.are.listener.OnSelectionChangeListener
import com.sophimp.are.models.StyleChangedListener
import com.sophimp.are.spans.*
import com.sophimp.are.style.IStyle
import com.sophimp.are.toolbar.DefaultToolbar
import com.sophimp.are.toolbar.items.IToolbarItem
import com.sophimp.are.utils.Util
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlin.math.min

/**
 * rich text editor
 * @author: sfx
 * @since: 2021/7/20
 */
class RichEditText(context: Context, attr: AttributeSet) : AppCompatEditText(context, attr) {

    @JvmField
    var spannedFromHtml: Spanned? = null

    var styleChangedListener: StyleChangedListener? = null

    var canMonitor: Boolean = true
    private val uiHandler = Handler(Looper.getMainLooper())

    private var styleList: MutableList<IStyle> = arrayListOf()

    var beforeSelectionStart = 0
    var beforeSelectionEnd = 0

    /**
     * 富文本点击事件处理
     */
    var clickStrategy: IEditorClickStrategy? = DefaultClickStrategyImpl()

    private var selectionChangesListeners = mutableListOf<OnSelectionChangeListener>()

    /**
     * 所有需上传的附件总大小
     */
    val allUploadFileSize: Long
        get() {
            val uploadSpans = editableText.getSpans(0, length(), IUploadSpan::class.java)
            var fileSize = 0L
            uploadSpans.forEach {
                fileSize += it.uploadFileSize()
            }
            return fileSize
        }

    /**
     * 所有上传路径
     */
    val uploadAnnexList: MutableList<String>
        get() {
            val list = mutableListOf<String>()
            if (text == null) return list

            val spans = editableText.getSpans(0, length(), IUploadSpan::class.java)
            if (spans == null || spans.isEmpty()) {
                return list
            }
            for (span in spans) {
                if (!TextUtils.isEmpty(span.uploadPath())) {
                    list.add(span.uploadPath()!!)
                }
            }
            return list
        }

    init {
        setupTextWatcher()
        Util.initEnv(context, IOssServerImpl(), object : ImageLoadedListener {
            override fun onImageLoaded(spanned: Spanned, start: Int, end: Int) {
                uiHandler.removeCallbacks(refreshRunnable)
                spannedFromHtml = spanned
                uiHandler.postDelayed(refreshRunnable, 500)
//                refresh(start)
            }
        })
    }

    private val gestureDetector =
        GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onSingleTapUp(e: MotionEvent?): Boolean {
                val offset = Util.getTextOffset(this@RichEditText, e!!)
                if (offset >= 0 && offset != selectionEnd) {
                    selectionChangesListeners.forEach {
                        it.onSelectionChanged(offset, offset)
                    }
                }
                if (offset < 0) {
                    if (!editMode) {
                        editMode = true
                        setSelection(Math.min(Math.max(offset, 0), length()))
                    }
                    return false
                }
                val clickSpans = editableText.getSpans(offset, offset, IClickableSpan::class.java)
                if (clickSpans.isEmpty()) {
                    if (!editMode) {
                        editMode = true
                        setSelection(Math.min(Math.max(offset, 0), length()))
                    }
                    return false
                }
                when {
                    clickSpans[0] is UrlSpan -> {
                        clickStrategy?.onClickUrl(this@RichEditText, clickSpans[0] as UrlSpan)
                    }
                    clickSpans[0] is ImageSpan2 -> {
                        clickStrategy?.onClickImage(this@RichEditText, clickSpans[0] as ImageSpan2)
                    }
                    clickSpans[0] is TableSpan -> {
                        clickStrategy?.onClickTable(this@RichEditText, clickSpans[0] as TableSpan)
                    }
                    clickSpans[0] is AudioSpan -> {
                        clickStrategy?.onClickAudio(this@RichEditText, clickSpans[0] as AudioSpan)
                    }
                    clickSpans[0] is VideoSpan -> {
                        clickStrategy?.onClickVideo(this@RichEditText, clickSpans[0] as VideoSpan)
                    }
                    clickSpans[0] is TodoSpan -> {
                        if (e.x <= (clickSpans[0] as TodoSpan).drawableRectf.right) {
                            (clickSpans[0] as TodoSpan).onClick(
                                this@RichEditText,
                                e.x,
                                beforeSelectionEnd
                            )
                            editMode = false
                        } else {
                            if (!editMode) {
                                editMode = true
                                setSelection(Math.min(Math.max(offset, 0), length()))
                            }
                        }
                    }
                    clickSpans[0] is AttachmentSpan -> {
                        clickStrategy?.onClickAttachment(
                            this@RichEditText,
                            clickSpans[0] as AttachmentSpan
                        )
                    }
                }
                return false
            }
        })

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        beforeSelectionStart = selectionStart
        beforeSelectionEnd = selectionEnd
        gestureDetector.onTouchEvent(event)
        return super.onTouchEvent(event)
    }

    /**
     * 标记内容(文字, style)发生了改变
     */
    fun markChanged() {
        isChange = true
        if (styleChangedListener != null) {
            styleChangedListener!!.onStyleChanged(this)
        }
    }

    /**
     * Monitoring text changes.
     */
    private fun setupTextWatcher() {
        // 用来判断是点周事件还是删除事件
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
//                    Util.log(("before change selection: $beforeSelectionStart - $beforeSelectionEnd after change selection: $afterSelectionStart   \n textEvent: $textEvent start: $startPos end: $endPos changeText: $changedText"))
                MainScope().launch {
                    stopMonitor()
                    val job = async {
                        val epStart = Util.getParagraphStart(
                            this@RichEditText,
                            min(beforeSelectionStart, afterSelectionStart)
                        )
                        val epEnd = Util.getParagraphEnd(editableText, afterSelectionStart)
                        for (style: IStyle in styleList) {
                            launch {
                                style.applyStyle(
                                    s,
                                    textEvent,
                                    changedText,
                                    beforeSelectionStart,
                                    afterSelectionStart,
                                    epStart,
                                    epEnd
                                )
                            }
                        }
                    }
                    job.await()
                    refresh(0)
                    startMonitor()
                }
//                isFromHtmlRefresh = false
//                uiHandler.postDelayed(refreshRunnable, 500)
            }
        }
        addTextChangedListener(mTextWatcher)
    }

    /**
     * 编辑时需要作用的style
     */
    fun addStyle(style: IStyle) {
        if (!styleList.contains(style)) {
            styleList.add(style)
        }
    }

    /**
     * 编辑时需要作用的style list
     */
    fun addStyleList(styles: List<IStyle>) {
        styles.forEach {
            addStyle(it)
        }
    }

    fun stopMonitor() {
        canMonitor = false
    }

    fun startMonitor() {
        canMonitor = true
    }

    val refreshRunnable = {
        stopMonitor()
        text = spannedFromHtml as Editable?
        startMonitor()
    }

    fun refresh(start: Int) {
//        stopMonitor()
//        editableText.insert(0, " ")
//        editableText.delete(0, 1)
//
//        editableText.insert(start, " ")
//        editableText.delete(start, start + 1)
//        startMonitor()
    }

    fun postDelayUIRun(runnable: Runnable, delay: Long) {
        uiHandler.postDelayed(runnable, delay);
    }

    fun fromHtml(html: String?): SpannableStringBuilder {
        if (html == null) return SpannableStringBuilder()
        spannedFromHtml = Html.fromHtml(
            html,
            Html.FROM_HTML_SEPARATOR_LINE_BREAK_PARAGRAPH
        ) as SpannableStringBuilder
        stopMonitor()
        setText(spannedFromHtml)
        startMonitor()
        return spannedFromHtml as SpannableStringBuilder
    }

    fun toHtml(): String? {
        stopMonitor()
        Html.sContext = context
        val html = StringBuffer()
//        val editTextHtml = Html.toHtml(editableText, Html.TO_HTML_PARAGRAPH_LINES_INDIVIDUAL)
        val editTextHtml = Html.toHtml(editableText, Html.TO_HTML_PARAGRAPH_LINES_INDIVIDUAL)
        html.append(editTextHtml)
        val htmlContent =
            html.toString().replace(Constants.ZERO_WIDTH_SPACE_STR_ESCAPE.toRegex(), "")
        startMonitor()
        return htmlContent
    }

    /**
     * call once after init toolbar items
     */
    fun bindStyles(mToolbar: DefaultToolbar) {
        for (item: IToolbarItem in mToolbar.mToolItems) {
            addStyle(item.mStyle)
        }
    }

    fun fromHtmlWithoutSet(richTextContent: String): SpannableStringBuilder {
        Html.sContext = context.applicationContext
        spannedFromHtml = Html.fromHtml(
            richTextContent,
            Html.FROM_HTML_SEPARATOR_LINE_BREAK_PARAGRAPH,
            null,
            null,
            false
        ) as SpannableStringBuilder
        return spannedFromHtml as SpannableStringBuilder
    }

    var isChange: Boolean = false

    /**
     *  编辑模式
     */
    var editMode: Boolean = false
        set(flag) {
            field = flag
            isLongClickable = flag
            isCursorVisible = flag
            isFocusableInTouchMode = flag
            isFocusable = flag
            val cls = EditText::class.java
            try {
                val method =
                    cls.getMethod("setShowSoftInputOnFocus", Boolean::class.javaPrimitiveType)
                method.isAccessible = true
                method.invoke(this, flag)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            if (flag) {
                requestFocus()
            }
        }

    /**
     * 上传附件
     */
    val extendData: Map<String, Any?>
        get() {
            val map = mutableMapOf<String, Any?>()
            if (text == null) return map
            val videoSpans = text!!.getSpans(
                0, text!!.length,
                VideoSpan::class.java
            )
            if (videoSpans != null && videoSpans.size > 0) {
                val videoUrl = videoSpans[0].serverUrl
                val videoPath = videoSpans[0].localPath
                map["url"] = videoUrl
                map["path"] = videoPath
                map["type"] = AttachFileType.VIDEO.attachmentValue
            }
            val spans = text!!.getSpans(
                0, text!!.length,
                ImageSpan::class.java
            )
            if (spans == null || spans.isEmpty()) {
                return map
            }
            val span = spans[0]
            if (span is AudioSpan) {
                map["url"] = span.serverUrl
                map["path"] = span.localPath
                map["type"] = AttachFileType.AUDIO.attachmentValue
            } else if (span is ImageSpan2) {
                map["path"] = span.localPath
                map["url"] = span.serverUrl
                map["type"] = AttachFileType.IMG.attachmentValue
            } else if (span is AttachmentSpan) {
                map["url"] = span.serverUrl
                map["path"] = span.localPath
                map["type"] = span.attachValue
            }
            if (text != null && !TextUtils.isEmpty(text.toString())) {
                val textContent = if (text.toString().length >= 80) text.toString()
                    .substring(0, 80) else text.toString()
                map["textContent"] = textContent
            }
            return map
        }

    fun removeVideoByPosition(index: Int) {
        val videoSpans = editableText.getSpans(0, length(), VideoSpan::class.java)
        if (index < 0 || index > videoSpans.size) return
        deleteVideoSpan(videoSpans[index])
    }


    fun removeImageByPosition(imagePosition: Int) {
        val allImageSpan = editableText.getSpans(0, length(), ImageSpan2::class.java)
        if (imagePosition < 0 || imagePosition > allImageSpan.size) return
        deleteImageSpan(allImageSpan[imagePosition])
    }

    private fun deleteVideoSpan(videoSpan: VideoSpan?) {
        if (videoSpan == null) return
        if (editableText == null) return
        val start = editableText!!.getSpanStart(videoSpan)
        val end = editableText!!.getSpanEnd(videoSpan)
        editableText!!.replace(start, end, "")
        isChange = true
    }

    private fun deleteImageSpan(span: ImageSpan?) {
        if (span == null) return
        if (editableText == null) return
        val start = editableText!!.getSpanStart(span)
        val end = editableText!!.getSpanEnd(span)
        editableText!!.replace(start, end, "")
        isChange = true
    }

    fun deleteAttachment(attachmentValue: String?) {
        if (TextUtils.isEmpty(attachmentValue)) return
        if (editableText == null) return
        val spans = editableText.getSpans(0, length(), AttachmentSpan::class.java)
        for (span in spans) {
            if (span.attachValue == attachmentValue) {
                val start = editableText!!.getSpanStart(span)
                val end = editableText!!.getSpanEnd(span)
                editableText!!.replace(start, end, "")
            }
        }
        isChange = true
    }

    fun registerOnSelectionChangedListener(listener: OnSelectionChangeListener) {
        if (!selectionChangesListeners.contains(listener)) {
            selectionChangesListeners.add(listener)
        }
    }

    fun removeOnSelectionChangedListener(listener: OnSelectionChangeListener) {
        if (!selectionChangesListeners.contains(listener)) {
            selectionChangesListeners.remove(listener)
        }
    }
}