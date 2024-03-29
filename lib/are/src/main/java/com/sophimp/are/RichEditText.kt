package com.sophimp.are

import android.content.Context
import android.graphics.Canvas
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextUtils
import android.text.TextWatcher
import android.text.style.ImageSpan
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.graphics.withTranslation
import com.sophimp.are.inner.Html
import com.sophimp.are.listener.ImageLoadedListener
import com.sophimp.are.listener.OnSearchOperateListener
import com.sophimp.are.listener.OnSelectionChangeListener
import com.sophimp.are.models.StyleChangedListener
import com.sophimp.are.spans.AttachmentSpan
import com.sophimp.are.spans.AudioSpan
import com.sophimp.are.spans.IClickableSpan
import com.sophimp.are.spans.IUploadSpan
import com.sophimp.are.spans.ImageSpan2
import com.sophimp.are.spans.SearchHighlightSpan
import com.sophimp.are.spans.TableSpan
import com.sophimp.are.spans.TodoSpan
import com.sophimp.are.spans.UrlSpan
import com.sophimp.are.spans.VideoSpan
import com.sophimp.are.style.BaseCharacterStyle
import com.sophimp.are.style.IStyle
import com.sophimp.are.style.ImageStyle
import com.sophimp.are.toolbar.DefaultToolbar
import com.sophimp.are.toolbar.items.IToolbarItem
import com.sophimp.are.utils.TextRoundedBgHelper
import com.sophimp.are.utils.Util
import com.sophimp.are.utils.closeSearch
import com.sophimp.are.utils.searchNext
import com.sophimp.are.utils.searchPrev
import com.sophimp.are.utils.updateSearchKeys
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import kotlin.math.min

/**
 * rich text editor
 * @author: sfx
 * @since: 2021/7/20
 */
class RichEditText(context: Context, attr: AttributeSet) : AppCompatEditText(context, attr),
    OnSearchOperateListener {
    var sendWatchersMethod: Method? = null

    val MSG_HANDLE_STYLE = 0x101

    @JvmField
    var spannedFromHtml: Spanned? = null

    var styleChangedListener: StyleChangedListener? = null

    var canMonitor: Boolean = true
    private val uiHandler = Handler(
        Looper.getMainLooper()
    ) {
        when (it.what) {
            MSG_HANDLE_STYLE -> {
                textChangedAndHandleStyle(it.obj as IStyle.TextEvent)
            }
        }

        return@Handler true
    }

    var styleList: MutableList<IStyle> = arrayListOf()

    val imageStyle: ImageStyle? = null
        get() {
            return field ?: ImageStyle(this)
        }

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

    private val textRoundedBgHelper: TextRoundedBgHelper

    init {
        Constants.DEFAULT_FONT_SIZE =
            (textSize / getContext().resources.displayMetrics.scaledDensity).toInt()
        textRoundedBgHelper = TextRoundedBgHelper(
            horizontalPadding = 1,
            verticalPadding = 1,
        )
        try {
            sendWatchersMethod = SpannableStringBuilder::class.java.getDeclaredMethod(
                "sendToSpanWatchers",
                Int::class.java,
                Int::class.java,
                Int::class.java
            )
            sendWatchersMethod?.isAccessible = true
        } catch (e: Exception) {
            e.printStackTrace()
        }
        setupTextWatcher()
        Util.initEnv(context, IOssServerImpl())
    }

    val imageLoadedListener = object : ImageLoadedListener {
        override fun onImageRefresh(start: Int, end: Int) {
            uiHandler.removeCallbacks(refreshRunnable)
            uiHandler.postDelayed(refreshRunnable, 500)
//            refresh(0)
        }
    }

    fun notifySelectionChanged(start: Int, end: Int) {
        selectionChangesListeners.forEach {
            it.onSelectionChanged(start, end)
        }
    }

    private val gestureDetector =
        GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onSingleTapUp(e: MotionEvent): Boolean {
                val offset = Util.getTextOffset(this@RichEditText, e!!)
                if (offset >= 0 && offset != selectionEnd) {
                    notifySelectionChanged(offset, offset)
                }
                if (offset < 0) {
                    if (!editMode) {
                        editMode = true
                        post {
                            setSelection(Math.min(Math.max(offset, 0), length()))
                        }
                    }
                    return false
                }
                val clickSpans = editableText.getSpans(offset, offset, IClickableSpan::class.java)
                if (clickSpans.isEmpty()) {
                    if (!editMode) {
                        editMode = true
                        post {
                            setSelection(Math.min(Math.max(offset, 0), length()))
                        }
                    }
                    return false
                }
                when {
                    clickSpans[0] is UrlSpan -> {
                        clickStrategy?.onClickUrl(context, editableText, clickSpans[0] as UrlSpan)
                    }

                    clickSpans[0] is ImageSpan2 -> {
                        clickStrategy?.onClickImage(
                            context,
                            editableText,
                            clickSpans[0] as ImageSpan2
                        )
                    }

                    clickSpans[0] is TableSpan -> {
                        clickStrategy?.onClickTable(
                            context,
                            editableText,
                            clickSpans[0] as TableSpan
                        )
                    }

                    clickSpans[0] is AudioSpan -> {
                        clickStrategy?.onClickAudio(
                            context,
                            editableText,
                            clickSpans[0] as AudioSpan
                        )
                    }

                    clickSpans[0] is VideoSpan -> {
                        clickStrategy?.onClickVideo(
                            context,
                            editableText,
                            clickSpans[0] as VideoSpan
                        )
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
                                post {
                                    setSelection(Math.min(Math.max(offset, 0), length()))
                                }
                            }
                        }
                    }

                    clickSpans[0] is AttachmentSpan -> {
                        clickStrategy?.onClickAttachment(
                            context, editableText,
                            clickSpans[0] as AttachmentSpan
                        )
                    }
                }
                return false
            }
        })

    override fun onTouchEvent(event: MotionEvent): Boolean {
        gestureDetector.onTouchEvent(event)
        return try {
            super.onTouchEvent(event)
        } catch (e: Exception) {
            false
        }

    }

    /**
     * 标记内容(文字, style)发生了改变
     */
    fun markChanged() {
        isChange = true
        styleChangedListener?.onStyleChanged(this)
    }

    /**
     * 上一次处理样式的段尾
     */
    var lastHandleEnd = 0
    var changedText: String = ""

    /**
     * 用来判断是否已经执行完style
     */
    var hasAppliedStyle = true

    /**
     * 异步处理样式时，是否有阻塞
     */
    var hasBlock = false

    /**
     * handle style changed
     */
    private fun textChangedAndHandleStyle(textEvent: IStyle.TextEvent) { // 一旦内容发生变化后，结束后肯定是光标状态, start == end

        var epStart = Util.getParagraphStart(
            this@RichEditText,
            min(beforeSelectionStart, selectionStart)
        )
        var epEnd = Util.getParagraphEnd(editableText, selectionEnd)
//                if (BuildConfig.DEBUG)
//                    Util.log(("before change selection: $beforeSelectionStart - $beforeSelectionEnd after change selection: $afterSelectionStart   \n textEvent: $textEvent start: $startPos end: $endPos changeText: $changedText"))

        lastHandleEnd = epEnd
        val beforeSelectStart = beforeSelectionStart
        val afterSelectEnd = selectionEnd
        CoroutineScope(Dispatchers.Main).launch {
            hasAppliedStyle = false
//            LogUtils.d("sgx start handle style")
            if (textEvent == IStyle.TextEvent.INPUT_NEW_LINE) {
                epStart = selectionStart
                epEnd = selectionEnd
            }
            for (style: IStyle in styleList) {
                style.applyStyle(
                    editableText,
                    textEvent,
                    changedText,
                    beforeSelectStart,
                    afterSelectEnd,
                    epStart,
                    epEnd
                )
            }
            refresh(0)
            hasAppliedStyle = true

            styleChangedListener?.onStyleChanged(this@RichEditText)
        }
    }

    /**
     * Monitoring text changes.
     */
    private fun setupTextWatcher() {
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
//                LogUtils.d("sgx textChanged hasAppliedStyle: $hasAppliedStyle")
                if (hasAppliedStyle) {
//                    LogUtils.d("sgx apply style cur selection - beforeChangedEnd: $selectionStart - $lastHandleEnd")
                    if (!hasBlock) {
                        beforeSelectionStart = selectionStart
                        beforeSelectionEnd = selectionEnd
                    }
                    hasBlock = false
//                    LogUtils.d("sgx apply style cur beforeStart: $beforeSelectionStart - $beforeSelectionEnd")
                    changedText = ""
                } else {
//                    LogUtils.d("sgx hasBlock selectStart-lastSelectEnd: $selectionStart - $lastHandleEnd")
                    // 英文输入过快，异步处理未完成，会导致样式断裂
                    beforeSelectionStart = lastHandleEnd
                    beforeSelectionEnd = lastHandleEnd
                    hasBlock = true
                }
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
                if (!canMonitor || !hasAppliedStyle) {
                    return
                }
                var textEvent: IStyle.TextEvent = IStyle.TextEvent.IDLE
                if (beforeSelectionStart == beforeSelectionEnd) {
                    // 处于光标状态
                    if (beforeSelectionStart < selectionStart) {
                        // 输入或复制操作
                        changedText = editableText.subSequence(
                            beforeSelectionStart,
                            Math.min(selectionStart, length())
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
                    } else if (beforeSelectionStart > selectionStart) {
                        // 删除操作
                        textEvent = IStyle.TextEvent.DELETE
                    } //else {/*copy操作, 不会触发，由Menu处理*/}
                } else {
                    // 处于选择状态
                    if (beforeSelectionStart == selectionStart) {
                        // 删除或剪切操作
                        textEvent = IStyle.TextEvent.DELETE
                    } else if (selectionStart > beforeSelectionStart) {
                        // 输入或复制操作
                        changedText = editableText.subSequence(
                            beforeSelectionStart,
                            min(selectionStart + 1, length())
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
                var msg = Message.obtain()
                msg.what = MSG_HANDLE_STYLE
                msg.obj = textEvent
                if (textEvent == IStyle.TextEvent.DELETE || length() > 5000) {
                    uiHandler.removeMessages(MSG_HANDLE_STYLE)
                    uiHandler.sendMessageDelayed(msg, 80)
                } else {
                    uiHandler.sendMessage(msg)
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
        text = editableText
        startMonitor()
    }

    fun refreshRange(start: Int, end: Int, needRefreshByInsert: Boolean = false) {
        if (needRefreshByInsert) {
            stopMonitor()
            editableText.insert(end, "\u3000")
            editableText.delete(end, min(length(), end + 1))
            startMonitor()
        }
        post {
            if (start < end) {
                try {
                    sendWatchersMethod?.invoke(editableText, start, end, end - start)
                } catch (e: IllegalAccessException) {
                    e.printStackTrace()
                } catch (e: IllegalArgumentException) {
                    e.printStackTrace()
                } catch (e: InvocationTargetException) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun refresh(start: Int) {
        refreshRange(start, length())
//        requestLayout()
    }

    fun refreshByInsert(start: Int) {
//        postDelayed({
        stopMonitor()
//            editableText.insert(0, "-")
//            editableText.delete(0, 1)
        editableText.insert(start, " ")
        editableText.delete(start, start + 1)
        startMonitor()
//        }, 50)
    }

    fun postDelayUIRun(runnable: Runnable, delay: Long) {
        uiHandler.postDelayed(runnable, delay)
    }

    fun fromHtml(html: String?, callback: (() -> Unit)?) {
        html?.let {
            CoroutineScope(Dispatchers.IO).launch {
                spannedFromHtml = Html.fromHtml(
                    html,
                    Html.FROM_HTML_SEPARATOR_LINE_BREAK_PARAGRAPH
                ) as SpannableStringBuilder

                withContext(Dispatchers.Main) {
                    stopMonitor()
                    setText(spannedFromHtml)
                    loadImageSpanWithGlide()
                    callback?.invoke()
                    startMonitor()
                }
            }
        }
    }

    fun fromHtml(html: String?) {
        fromHtml(html, null)
    }

    /**
     * 使用Glide加载图片
     */
    private fun loadImageSpanWithGlide() {
        val imageSpans = editableText.getSpans(0, length(), ImageSpan2::class.java)
        imageSpans.forEach {
            imageStyle?.loadImageSpanWithGlide(context, it, imageLoadedListener)
        }

        val videoSpans = editableText.getSpans(0, length(), VideoSpan::class.java)
        videoSpans.forEach { vSpan ->
            vSpan.previewUrl?.apply {
                imageStyle?.loadVideoSpanPreviewFrame(context, vSpan, imageLoadedListener)
            }
        }

    }

    fun toHtml(): String {
        stopMonitor()
        Html.sContext = context.applicationContext
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
        loadImageSpanWithGlide()
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
//            val cls = EditText::class.java
//            try {
//                val method =
//                    cls.getDeclaredMethod("setShowSoftInputOnFocus", Boolean::class.javaPrimitiveType)
//                method.isAccessible = true
//                method.invoke(this, flag)
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }

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
            if (span.spanId == attachmentValue) {
                val start = editableText!!.getSpanStart(span)
                val end = editableText!!.getSpanEnd(span)
                editableText!!.replace(start, end, "")
            }
        }
        isChange = true
    }

    fun registerOnSelectionChangedListener(listener: OnSelectionChangeListener?) {
        listener?.let {
            if (!selectionChangesListeners.contains(listener)) {
                selectionChangesListeners.add(listener)
            }
        }
    }

    fun removeOnSelectionChangedListener(listener: OnSelectionChangeListener) {
        if (!selectionChangesListeners.contains(listener)) {
            selectionChangesListeners.remove(listener)
        }
    }

    fun styleSelectionChanged(characterStyle: BaseCharacterStyle<*>) {
        // 性能待优化
        if (selectionChangesListeners.isNotEmpty()) {
            selectionChangesListeners.forEach {
                it.onSelectionChanged(selectionStart, selectionEnd)
            }
        }
    }

    //    private var mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    override fun onDraw(canvas: Canvas?) {
        // need to draw bg first so that text can be on top during super.onDraw()
        if (text is Spanned && layout != null) {
            canvas?.withTranslation(totalPaddingLeft.toFloat(), totalPaddingTop.toFloat()) {
                textRoundedBgHelper.drawFontBg(canvas, text as Spanned, layout)
                if (curHighlightSpans.isNotEmpty()) {
                    textRoundedBgHelper.drawSearchHighlight(
                        canvas,
                        curHighlightSpans,
                        text as Spanned,
                        layout
                    )
                }
            }
        }
        try {
            super.onDraw(canvas)
        } catch (e: IndexOutOfBoundsException) {
            e.printStackTrace()
        }
    }

    /**
     * 当前高亮的
     */
    var curHighlightSpanIndex = -1

    /**
     * 搜索关关键字
     */
    var searchKeys = mutableListOf<String>()

    /**
     * 搜索高这缓存
     */
    val cacheSearchHighlightSpans = mutableListOf<SearchHighlightSpan>()

    val curHighlightSpans = mutableListOf<SearchHighlightSpan>()

    override fun onPreClick(): Int {
        searchPrev()
        return curHighlightSpanIndex
    }

    override fun onNextClick(): Int {
        searchNext()
        return curHighlightSpanIndex
    }

    override fun onSearch(keys: List<String>): Int {
        return updateSearchKeys(keys)
    }

    override fun onCloseClick() {
        closeSearch()
    }
}