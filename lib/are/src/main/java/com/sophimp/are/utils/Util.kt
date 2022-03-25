package com.sophimp.are.utils

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.text.*
import android.util.Log
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.sophimp.are.BuildConfig
import com.sophimp.are.Constants
import com.sophimp.are.inner.Html
import com.sophimp.are.listener.IOssServer
import com.sophimp.are.listener.ImageLoadedListener
import com.sophimp.are.spans.IndentSpan
import com.sophimp.are.spans.ListNumberSpan
import com.sophimp.are.table.TableCellInfo
import com.sophimp.are.utils.Util.initEnv
import org.htmlcleaner.HtmlCleaner
import java.io.File
import java.lang.reflect.Method
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt


/**
 * 使用富文本前首需需要调用 [initEnv] 初始化
 */
object Util {

    /**
     * 使用富文本前首需需要调用此方法, 确保已初始化
     */
    @JvmStatic
    fun initEnv(context: Context, server: IOssServer, imageLoadedListener: ImageLoadedListener?) {
        Html.sContext = context.applicationContext
        Html.ossServer = server
        Html.imageLoadedListener = imageLoadedListener
    }

    val textAlignPattern = Pattern.compile("(?:\\s+|\\A)text-align\\s*:\\s*(\\S*)\\b")
    var values = intArrayOf(1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1)
    var symbols = arrayOf("m", "cm", "d", "cd", "c", "xc", "l", "xl", "x", "ix", "v", "iv", "i")

    /**
     * 反射SpannableStringBuilder 的无排序getSpans
     */
    var getSpanMethodWithNoSort: Method? = null

    fun toast(context: Context?, msg: String?) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
    }

    @JvmStatic
    fun log(s: String) {
        if (BuildConfig.DEBUG) {
            Log.d("CAKE", s)
        }
    }

    /**
     * 获取屏幕宽度(px)
     */
    @JvmStatic
    fun getScreenWidth(context: Context?): Int {
        return if (context == null || context.resources == null || context.resources.displayMetrics == null) {
            480
        } else context.resources.displayMetrics.widthPixels
    }

    /**
     * dip 转 px
     */
    @JvmStatic
    fun dip2px(context: Context, dpValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    /**
     * Returns the line number of current cursor.
     *
     * @param editText
     * @return
     */
    @JvmStatic
    fun getCurrentCursorLine(editText: EditText): Int {
        val selectionStart = Selection.getSelectionStart(editText.text)
        val layout = editText.layout ?: return -1
        return if (selectionStart != -1) {
            layout.getLineForOffset(selectionStart)
        } else -1
    }

    /**
     * Returns the selected area line numbers.
     *
     * @param editText
     * @return
     */
    @JvmStatic
    fun getCurrentSelectionLines(editText: EditText): IntArray {
        val editable = editText.text
        val selectionStart = Selection.getSelectionStart(editable)
        val selectionEnd = Selection.getSelectionEnd(editable)
        val layout = editText.layout
        val lines = IntArray(2)
        if (selectionStart != -1) {
            val startLine = layout.getLineForOffset(selectionStart)
            lines[0] = startLine
        }
        if (selectionEnd != -1) {
            val endLine = layout.getLineForOffset(selectionEnd)
            lines[1] = endLine
        }
        return lines
    }

    @JvmStatic
    fun getAllSelectionLines(editText: EditText): List<AbstractMap.SimpleEntry<Int, Int>> {
        var selectionStart = editText.selectionStart
        var selectionEnd = editText.selectionEnd
        val list: MutableList<AbstractMap.SimpleEntry<Int, Int>> = ArrayList()
        val text = editText.text.toString()
        selectionStart =
            getThisLineStart(editText, editText.layout.getLineForOffset(selectionStart))
        selectionEnd = getParagraphEnd(editText.editableText, selectionEnd)
        if (selectionEnd >= text.length) selectionEnd -= 1
        for (i in selectionStart..selectionEnd) {
            val lastChar = text[i]
            if (lastChar == '\n') {
                val entry = AbstractMap.SimpleEntry(selectionStart, i)
                list.add(entry)
                if (i + 1 <= selectionEnd) {
                    selectionStart = i + 1
                }
            } else if (i == editText.text.length - 1) {
                val entry = AbstractMap.SimpleEntry(selectionStart, i)
                list.add(entry)
            }
        }
        return list
    }

    /**
     * Returns the line start position of the current line (which cursor is focusing now).
     *
     * @param editText
     * @return
     */
    @JvmStatic
    fun getThisLineStart(editText: EditText, currentLine: Int): Int {
        val layout = editText.layout
        var start = 0
        if (currentLine > 0) {
            start = layout.getLineStart(currentLine)
            start = getParagraphStart(editText, start)
        }
        return start
    }

    /**
     * Returns the line end position of the current line (which cursor is focusing now).
     *
     * @param editText
     * @return
     */
    @JvmStatic
    fun getThisLineEnd(editText: EditText, currentLine: Int): Int {
        val layout = editText.layout
        return if (-1 != currentLine) {
            layout.getLineEnd(currentLine)
        } else -1
    }

    /**
     * 空行: 第一行， 中间行， 最后一行
     * 非空行: selectionStart 在末尾, selectionStart 不在末尾
     * case 1: 光标在空行, 且是第一行， selection start = end = length = 0;
     * case 2: 光标在行尾, 且不是最后一行， selection (start = end  = '\n') < length
     * case 3: 光标在最后一行， 空行 selection (start = end = length)
     * case 3.1: 光标在最后一行， 非空行 selection (start = end = length)
     * case 4: 光标在非空行行首, selection start 即为该段落start = 非空字符
     * case 5: 光标在非空行中间, selection start 即为常规位置，无须考虑
     *
     * 坑点，case 3, 3.1 光标在最后一行空行 与 在最后一行非空行， selection 是一样的
     */
    fun getParagraphStart(text: EditText, selectionIndex: Int): Int {
        var selection = max(0, min(selectionIndex - 1, text.length() - 1))
        val editable = text.editableText
        if (editable.isEmpty() || selection == 0) {
            // case 1: 首行空行
            return 0
        }
        val pStartIndex = editable.lastIndexOf('\n', selection)
        if (pStartIndex > 0) {
            return min(pStartIndex + 1, text.length())
        }
        return 0
    }

    /**
     * 空行: 第一行， 中间行， 最后一行
     * 非空行: selectionStart 在末尾, selectionStart 不在末尾
     * case 1: 光标在空行, 且是第一行， selection start = end = length = 0;
     * case 2: 光标在行尾, 且不是最后一行， selection (start = end  = '\n') < length
     * case 3: 光标在空行, 且是最后一行， selection (start = end = length)
     * case 3.1: 光标在最后一行， 非空行 selection (start = end = length)
     * case 4: 光标在非空行行首, selection start 即为该段落start = 非空字符
     * case 5: 光标在非空行中间, selection start 即为常规位置，无须考虑
     */
    fun getParagraphEnd(editable: Editable, selectionIndex: Int): Int {
        var selection = selectionIndex
        if (editable.isEmpty()) {
            // case 1: 首行空行
            return 0
        }
        val pEndIndex = editable.indexOf('\n', selection)
        if (pEndIndex >= selection) {
            return max(0, pEndIndex)
        }
        return editable.length
    }

    fun getParagraphEndWithLine(editText: EditText, line: Int): Int {
        val lineEnd = getThisLineEnd(editText, line)
        return getParagraphEnd(editText.editableText, lineEnd - 1)
    }

    @JvmStatic
    fun renumberAllListItemSpans(editable: Editable) {
        // 所有的段落重新排序, 同级的段落
        val levelCache = IntArray(6)

        Arrays.fill(levelCache, 1)
        if (getSpanMethodWithNoSort == null) {
            try {
                getSpanMethodWithNoSort = SpannableStringBuilder::class.java.getDeclaredMethod(
                    "getSpans",
                    Int::class.java,
                    Int::class.java,
                    Class::class.java,
                    Boolean::class.java
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        val listNumberSpans: Array<ListNumberSpan>? =
            if (getSpanMethodWithNoSort != null) {
                val spans = getSpanMethodWithNoSort!!.invoke(
                    editable,
                    0,
                    editable.length,
                    ListNumberSpan::class.java,
                    false
                )
                if (spans is Array<*> && spans.size > 0) {
                    spans as Array<ListNumberSpan>
                } else {
                    null
                }
            } else {
                editable.getSpans(0, editable.length, ListNumberSpan::class.java)
            }
        listNumberSpans?.let {
            if (getSpanMethodWithNoSort == null) {
                // 坑点， 这里取出来的span 并不是按先后顺序，源码里默认是按照插入顺序排过序了
                Arrays.sort(listNumberSpans) { o1: ListNumberSpan?, o2: ListNumberSpan? ->
                    editable.getSpanEnd(o1) - editable.getSpanEnd(o2)
                }
            }
            for (i in listNumberSpans.indices) {
                val span: ListNumberSpan = listNumberSpans[i]
                // 获取当前段落的缩进等级
                val curSpanStart = editable.getSpanStart(span)
                val curSpanEnd = editable.getSpanEnd(span)
                // 判断是否有隔断
                if (i > 0) {
                    val preListNumberSpan: ListNumberSpan = listNumberSpans[i - 1]
                    val preEnd = editable.getSpanEnd(preListNumberSpan)
                    val preParaEnd: Int = getParagraphEnd(editable, preEnd)
                    if (curSpanStart - preParaEnd > 1) {
                        // 后面的重新排
                        Arrays.fill(levelCache, 1)
                    }
                }
                // log("当前 span: start-end: " + curSpanStart + "-" + curSpanEnd);
                val curPLeading: Array<IndentSpan> =
                    editable.getSpans(curSpanStart, curSpanEnd, IndentSpan::class.java)
                span?.let {
                    if (curPLeading.isNotEmpty()) {
                        span.number = levelCache[curPLeading[0].mLevel]
                        levelCache[curPLeading[0].mLevel] += 1
                    } else {
                        span.number = levelCache[0]
                        levelCache[0] += 1
                    }
                }
            }
        }
    }

    /**
     * 重新排序
     */
    fun reNumberBehindListItemSpans(start: Int, text: EditText) {
        val editable = text.editableText
        // 获取当前段落后的所有 ListNumberSpan,
        val behindListItemSpans =
            editable.getSpans(start, editable.length, ListNumberSpan::class.java)
        // 坑点， 这里取出来的span 并不是按先后顺序， 需要先排序
        Arrays.sort(behindListItemSpans) { o1: ListNumberSpan?, o2: ListNumberSpan? ->
            editable.getSpanEnd(o1) - editable.getSpanEnd(o2)
        }
        log("重排 " + start + " 后的 ListNumberSpan: " + behindListItemSpans.size)
        for (i in behindListItemSpans.indices) {
            val span: ListNumberSpan = behindListItemSpans[i]
            // 获取当前段落的缩进等级
            val curSpanStart = editable.getSpanStart(span)
            val curSpanEnd = editable.getSpanEnd(span)
            log("当前 span: start-end: $curSpanStart-$curSpanEnd")
            var curLevel = 0
            var preLevel: Int
            val curPLeading: Array<IndentSpan> =
                editable.getSpans(curSpanStart, curSpanEnd, IndentSpan::class.java)
            if (curPLeading.size > 0) {
                curLevel = curPLeading[0].mLevel
            }
            // 获取上一段落的起始点的 排序和缩进等级
            var lastStart = curSpanStart - 1
            var lastEnd = curSpanStart - 1
            if (lastEnd > 0) {
                if (editable[lastEnd] != '\n') {
                    lastEnd -= 1
                    break
                }
                // 有上一段
                lastStart = getParagraphStart(text, lastEnd - 1)
                log("前一段 span: start-end: $lastStart-$lastEnd")
                if (lastStart <= lastEnd) {
                    val preNumberSpan: Array<ListNumberSpan> =
                        editable.getSpans(lastStart, lastEnd, ListNumberSpan::class.java)
                    if (preNumberSpan.size > 0) {
                        // 有排序，判断是否相同等级
                        val preLeadingSpan: Array<IndentSpan> =
                            editable.getSpans(lastStart, lastEnd, IndentSpan::class.java)
                        preLevel = if (preLeadingSpan.size > 0) {
                            preLeadingSpan[preLeadingSpan.size - 1].mLevel
                        } else {
                            0
                        }
                        if (curLevel == preLevel) {
                            // 接着排
                            span.number = preNumberSpan[preNumberSpan.size - 1].number + 1
                            //                            editable.removeSpan(span);
//                            editable.setSpan(span, curSpanStart, curSpanEnd, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                        } else {
                            // 重新排
                            span.number = 1
                        }
                    } else {
                        // 重新排
                        span.number = 1
                    }
                } // else  无效case
            } // else 默认1, 无须处理
        }
    }

    /**
     * Gets the pixels by the given number of dp.
     *
     * @param context
     * @param dp
     * @return
     */
    fun getPixelByDp(context: Context, dp: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            context.resources.displayMetrics
        ).toInt()
    }

    /**
     * Returns the screen width and height.
     *
     * @param context
     * @return
     */
    fun getScreenWidthAndHeight(context: Context): IntArray {
        val displayMetrics = context.resources.displayMetrics
        val widthAndHeight = IntArray(2)
        widthAndHeight[0] = displayMetrics.widthPixels
        widthAndHeight[1] = displayMetrics.heightPixels
        return widthAndHeight
    }

    /**
     * Returns the color in string format.
     *
     * @param intColor
     * @param containsAlphaChannel
     * @param removeAlphaFromResult
     * @return
     */
    fun colorToString(
        intColor: Int,
        containsAlphaChannel: Boolean,
        removeAlphaFromResult: Boolean
    ): String {
        var strColor = String.format("#%06X", 0xFFFFFF and intColor)
        if (containsAlphaChannel) {
            strColor = String.format("#%06X", -0x1 and intColor)
            if (removeAlphaFromResult) {
                val buffer = StringBuffer(strColor)
                buffer.delete(1, 3)
                strColor = buffer.toString()
            }
        }
        return strColor
    }

    fun scaleBitmapToFitWidth(bitmap: Bitmap, maxWidth: Int): Bitmap {
        val w = bitmap.width
        val h = bitmap.height
        val newHeight = maxWidth * h / w
        val matrix = Matrix()
        val scaleWidth = maxWidth.toFloat() / w
        val scaleHeight = newHeight.toFloat() / h
        if (w < maxWidth * 0.2) {
            return bitmap
        }
        matrix.postScale(scaleWidth, scaleHeight)
        return Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true)
    }

    fun scaleBitmapToFitWidthHeight(bitmap: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {
        var maxWidth = maxWidth
        var maxHeight = maxHeight
        var w = bitmap.width
        var h = bitmap.height
        if (maxWidth == 0) {
            maxWidth = w
        }
        if (maxHeight == 0) {
            maxHeight = h
        }
        var inSampleSize = 1
        while (h > maxHeight || w > maxWidth) {
            h = h shr 1
            w = w shr 1
            inSampleSize = inSampleSize shl 1
        }
        val newWidth = bitmap.width / inSampleSize
        val newHeight = bitmap.height / inSampleSize
        val matrix = Matrix()
        val scaleWidth = newWidth.toFloat() / bitmap.width
        val scaleHeight = newHeight.toFloat() / bitmap.height
        matrix.postScale(scaleWidth, scaleHeight)
        return Bitmap.createBitmap(
            bitmap,
            0,
            0,
            bitmap.width,
            bitmap.height,
            matrix,
            true
        )
    }

    fun mergeBitmaps(background: Bitmap?, foreground: Bitmap): Bitmap? {
        if (background == null) {
            return null
        }
        val bgWidth = background.width
        val bgHeight = background.height

        //create the new blank bitmap
        val newBitmap = Bitmap.createBitmap(bgWidth, bgHeight, Bitmap.Config.ARGB_8888)
        val cv = Canvas(newBitmap)
        //draw bg into
        cv.drawBitmap(background, 0f, 0f, null)
        val fgLeft = (bgWidth - foreground.width) / 2
        val fgTop = (bgHeight - foreground.height) / 2

        //draw fg into
        val src = Rect(
            fgLeft,
            fgTop,
            fgLeft + foreground.width,
            fgTop + foreground.height
        )
        cv.drawBitmap(foreground, null, RectF(src), null)
        //save all clip
        cv.save()
        //store
        cv.restore()
        return newBitmap
    }

    /**
     * 在限定匹域内合成
     */
    fun mergeBitMapWithLimit(
        background: Bitmap,
        foreground: Bitmap,
        maxWidth: Int,
        maxHeight: Int
    ): Bitmap {
        val videoCompose = Bitmap.createBitmap(maxWidth, maxHeight, Bitmap.Config.ARGB_8888)
        val cv = Canvas(videoCompose)
        var bgLeft = 0
        var bgTop = 0
        if (background.width > maxWidth) {
            bgLeft = (background.width - maxWidth) / 2
        }
        if (background.height > maxHeight) {
            bgTop = (background.height - maxHeight) / 2
        }
        cv.drawBitmap(
            background,
            Rect(bgLeft, bgTop, bgLeft + maxWidth, bgTop + maxHeight),
            RectF(0F, 0F, maxWidth.toFloat(), maxHeight.toFloat()),
            null
        )
        val fgLeft = (maxWidth - foreground.width) / 2
        val fgTop = (maxHeight - foreground.height) / 2

        cv.drawBitmap(foreground, fgLeft.toFloat(), fgTop.toFloat(), null)
        cv.save()
        cv.restore()
        return videoCompose
    }

    fun toAbcOrder(num: Int): String {
        var num = num
        if (num < 1) num = 1
        val abc = StringBuilder()
        val times = (num - 1) / 26
        val delta = (num - 1) % 26
        for (i in 0..times) {
            abc.append(('a'.toInt() + delta).toChar())
        }
        return abc.toString()
    }

    fun toRomanOrder(num: Int): String {
        var num = num
        val roman = StringBuilder()
        for (i in values.indices) {
            val value = values[i]
            val symbol = symbols[i]
            while (num >= value) {
                num -= value
                roman.append(symbol)
            }
            if (num == 0) {
                break
            }
        }
        return roman.toString()
    }

    fun compressByScale(
        src: Bitmap,
        newWidth: Int,
        newHeight: Int,
        recycle: Boolean
    ): Bitmap {
        return scale(src, newWidth, newHeight, recycle)
    }

    fun scale(
        src: Bitmap,
        newWidth: Int,
        newHeight: Int,
        recycle: Boolean
    ): Bitmap {
        val ret = Bitmap.createScaledBitmap(src, newWidth, newHeight, true)
        if (recycle && !src.isRecycled && ret != src) src.recycle()
        return ret
    }

    private fun isEmptyBitmap(src: Bitmap?): Boolean {
        return src == null || src.width == 0 || src.height == 0
    }

    /**
     * 解析表格数据，只适用单个表格，不适合嵌套表格
     */
//    fun parseTableCell(html: String): List<List<TableCellInfo>> {
//        val tableData: MutableList<List<TableCellInfo>> =
//            LinkedList<List<TableCellInfo>>()
//        if (TextUtils.isEmpty(html)) return tableData
//        var rowIndex = 0
//        while (rowIndex < html.length) {
//            val startTrStart = html.indexOf("<tr", rowIndex)
//            rowIndex = if (startTrStart >= 0) {
//                // 解析行
//                val startTrEnd = html.indexOf(">", startTrStart + 1)
//                if (startTrEnd > startTrStart) {
//                    val endTrStart = html.indexOf("</tr>", startTrEnd + 1)
//                    if (endTrStart > startTrEnd) {
//                        // 解析row
//                        tableData.add(
//                            parseRowCell(
//                                html.substring(
//                                    startTrEnd + 1,
//                                    endTrStart
//                                )
//                            )
//                        )
//                        endTrStart + "</tr>".length
//                    } else {
//                        // 标签有误，跳过，继续后续的表格过滤
//                        startTrEnd + 1
//                    }
//                } else {
//                    // 标签有误， 跳过, 继续后续的表格过滤
//                    startTrStart + 1
//                }
//            } else {
//                // 没有了 tr
//                break
//            }
//        }
//        return tableData
//    }

//    private fun parseRowCell(rowHtml: String): List<TableCellInfo> {
//        val cellInfos: MutableList<TableCellInfo> =
//            LinkedList<TableCellInfo>()
//        var colIndex = 0
//        while (colIndex < rowHtml.length) {
//            val startTdStart = rowHtml.indexOf("<td", colIndex)
//            colIndex = if (startTdStart >= 0) {
//                // 解析行
//                val startTdEnd = rowHtml.indexOf(">", startTdStart + 1)
//                if (startTdEnd > startTdStart) {
//                    val endTdStart = rowHtml.indexOf("</td>", startTdEnd + 1)
//                    if (endTdStart > startTdEnd) {
//                        // 解析cell
//                        cellInfos.add(TableCellInfo(rowHtml.substring(startTdEnd + 1, endTdStart)))
//                        endTdStart + "</td>".length
//                    } else {
//                        // 标签有误，跳过，继续后续的表格过滤
//                        startTdEnd + 1
//                    }
//                } else {
//                    // 标签有误， 跳过, 继续后续的表格过滤
//                    startTdStart + 1
//                }
//            } else {
//                // 没有了 tr
//                break
//            }
//        }
//        return cellInfos
//    }

    fun hideKeyboard(view: View?, context: Context?) {
        if (view != null && context != null) {
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm?.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    fun drawableToBitmap(drawable: Drawable): Bitmap {
        val w = drawable.bounds.width()
        val h = drawable.bounds.height()
        val bitmap = Bitmap.createBitmap(
            w, h,
            if (drawable.opacity != PixelFormat.OPAQUE) Bitmap.Config.ARGB_8888 else Bitmap.Config.RGB_565
        )
        val canvas = Canvas(bitmap)
        //canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, w, h)
        drawable.draw(canvas)
        return bitmap
    }

    @JvmStatic
    fun getFileSize(path: String): Long {

        val file = File(path)
        if (file.exists() && file.isFile) {
            return file.length()
        }
        return 0L
    }

    @JvmStatic
    fun getFileSize(paths: List<String?>?): Long {
        var totalSize: Long = 0
        if (paths == null || paths.isEmpty()) {
            return totalSize
        }
        for (path in paths) {
            if (!TextUtils.isEmpty(path)) {
                totalSize += getFileSize(path!!)
            }
        }
        return totalSize
    }

    @JvmStatic
    fun getFileName(path: String, defaultName: String): String {
        if (TextUtils.isEmpty(path)) return defaultName
        val index = path.lastIndexOf("/")
        return if (index < 0 || index == path.length - 1) {
            defaultName
        } else path.substring(index + 1)
    }


    @JvmStatic
    fun view2Bitmap(view: View?): Bitmap? {
        if (view == null) return null
        val dm = view.context.resources.displayMetrics
        val screenWidth = dm.widthPixels - (dm.density * 42)
        val drawingCacheEnabled = view.isDrawingCacheEnabled
        val willNotCacheDrawing = view.willNotCacheDrawing()
        view.isDrawingCacheEnabled = true
        view.setWillNotCacheDrawing(false)
        var drawingCache = view.drawingCache
        val bitmap: Bitmap
        if (null == drawingCache) {
            view.measure(
                View.MeasureSpec.makeMeasureSpec(
                    screenWidth.roundToInt(),
                    View.MeasureSpec.EXACTLY
                ),  //  view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(
                    0,
                    View.MeasureSpec.UNSPECIFIED
                )
            )
            view.layout(0, 0, view.measuredWidth, view.measuredHeight)
            view.buildDrawingCache()
            drawingCache = view.drawingCache
            if (drawingCache != null) {
                bitmap = Bitmap.createBitmap(drawingCache)
            } else {
                bitmap = Bitmap.createBitmap(
                    view.measuredWidth,
                    view.measuredHeight,
                    Bitmap.Config.ARGB_8888
                )
                val canvas = Canvas(bitmap)
                view.draw(canvas)
            }
        } else {
            bitmap = Bitmap.createBitmap(drawingCache)
        }
        view.destroyDrawingCache()
        view.setWillNotCacheDrawing(willNotCacheDrawing)
        view.isDrawingCacheEnabled = drawingCacheEnabled
        return bitmap
    }


    fun drawable2Bitmap(drawable: Drawable): Bitmap {
        if (drawable is BitmapDrawable) {
            val bitmapDrawable = drawable
            if (bitmapDrawable.bitmap != null) {
                return bitmapDrawable.bitmap
            }
        }
        val bitmap: Bitmap
        bitmap = if (drawable.intrinsicWidth <= 0 || drawable.intrinsicHeight <= 0) {
            Bitmap.createBitmap(
                1, 1,
                if (drawable.opacity != PixelFormat.OPAQUE) Bitmap.Config.ARGB_8888 else Bitmap.Config.RGB_565
            )
        } else {
            Bitmap.createBitmap(
                drawable.intrinsicWidth,
                drawable.intrinsicHeight,
                if (drawable.opacity != PixelFormat.OPAQUE) Bitmap.Config.ARGB_8888 else Bitmap.Config.RGB_565
            )
        }
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    object GetPathFromUri4kitkat {
        /**
         * For Android 4.4
         */
        @SuppressLint("NewApi")
        fun getPath(context: Context, uri: Uri): String? {
            val isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT

            // DocumentProvider
            if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
                // ExternalStorageProvider
                if (isExternalStorageDocument(uri)) {
                    val docId = DocumentsContract.getDocumentId(uri)
                    val split =
                        docId.split(":".toRegex()).toTypedArray()
                    val type = split[0]
                    if ("primary".equals(type, ignoreCase = true)) {
                        return Environment.getExternalStorageDirectory()
                            .toString() + "/" + split[1]
                    }

                    // TODO handle non-primary volumes
                } else if (isDownloadsDocument(uri)) {
                    val id = DocumentsContract.getDocumentId(uri)
                    if (id.startsWith("raw:")) {
                        return id.substring(4)
                    }
                    val contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        id.toLong()
                    )
                    return getDataColumn(context, contentUri, null, null)
                } else if (isMediaDocument(uri)) {
                    val docId = DocumentsContract.getDocumentId(uri)
                    val split =
                        docId.split(":".toRegex()).toTypedArray()
                    val type = split[0]
                    var contentUri: Uri? = null
                    if ("image" == type) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    } else if ("video" == type) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    } else if ("audio" == type) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                    }
                    val selection = "_id=?"
                    val selectionArgs =
                        arrayOf(split[1])
                    return getDataColumn(
                        context,
                        contentUri,
                        selection,
                        selectionArgs
                    )
                }
            } else if ("content".equals(uri.scheme, ignoreCase = true)) {
                return getDataColumn(context, uri, null, null)
            } else if ("file".equals(uri.scheme, ignoreCase = true)) {
                return uri.path
            }
            return uri.path
        }

        /**
         * Get the value of the data column for this Uri. This is useful for
         * MediaStore Uris, and other file-based ContentProviders.
         *
         * @param context       The context.
         * @param uri           The Uri to query.
         * @param selection     (Optional) Filter used in the query.
         * @param selectionArgs (Optional) Selection arguments used in the query.
         * @return The value of the _data column, which is typically a file path.
         */
        fun getDataColumn(
            context: Context, uri: Uri?, selection: String?,
            selectionArgs: Array<String>?
        ): String? {
            var cursor: Cursor? = null
            val column = "_data"
            val projection = arrayOf(column)
            try {
                cursor = context.contentResolver.query(
                    uri!!, projection, selection, selectionArgs,
                    null
                )
                if (cursor != null && cursor.moveToFirst()) {
                    val column_index = cursor.getColumnIndexOrThrow(column)
                    return cursor.getString(column_index)
                }
            } finally {
                cursor?.close()
            }
            return null
        }

        /**
         * @param uri The Uri to check.
         * @return Whether the Uri authority is ExternalStorageProvider.
         */
        fun isExternalStorageDocument(uri: Uri): Boolean {
            return "com.android.externalstorage.documents" == uri.authority
        }

        /**
         * @param uri The Uri to check.
         * @return Whether the Uri authority is DownloadsProvider.
         */
        fun isDownloadsDocument(uri: Uri): Boolean {
            return "com.android.providers.downloads.documents" == uri.authority
        }

        /**
         * @param uri The Uri to check.
         * @return Whether the Uri authority is MediaProvider.
         */
        fun isMediaDocument(uri: Uri): Boolean {
            return "com.android.providers.media.documents" == uri.authority
        }
    }

    /**
     * find touch point of offset in line
     */
    fun getTextOffset(widget: TextView, event: MotionEvent): Int {
        var x: Int = event.x.toInt()
        var y: Int = event.y.toInt()
        x -= widget.totalPaddingLeft
        y -= widget.totalPaddingTop
        x += widget.scrollX
        y += widget.scrollY
        val layout: Layout = widget.layout
        val line: Int = layout.getLineForVertical(y)
        val lineBottom = layout.getLineBottom(line)
        if (y >= lineBottom) return -1
        return layout.getOffsetForHorizontal(line, x.toFloat())
    }

    /**
     * convert the span end offset to touch coordinate x
     */
    fun getSpanXEnd(widget: TextView, spanEnd: Int): Int {
        val layout: Layout = widget.layout
        val xEnd = layout.getPrimaryHorizontal(spanEnd)
        return xEnd.toInt()
    }

    @JvmStatic
    fun parseTableByHtmlCleaner(html: String): MutableList<MutableList<TableCellInfo>> {
        val tableData: MutableList<MutableList<TableCellInfo>> = mutableListOf()
        if (TextUtils.isEmpty(html)) return tableData

        val htmlCleaner = HtmlCleaner()
        val tagNode = htmlCleaner.clean(html)
        val rows = tagNode.getElementListByName("tr", true)
        // 添加所有行
        rows.forEach { _ ->
            tableData.add(mutableListOf())
        }

        rows.forEachIndexed { rowInd, eachRow ->
            val cols = eachRow.getElementListByName("td", false)
            cols.forEach { eachCol ->
                val contentText = htmlCleaner.getInnerHtml(eachCol)
                var alignAttr = eachCol.getAttributeByName("text-align")
                val textAlign = when (alignAttr) {
                    "center" -> {
                        Layout.Alignment.ALIGN_CENTER
                    }
                    "right" -> {
                        Layout.Alignment.ALIGN_OPPOSITE
                    }
                    else -> {
                        Layout.Alignment.ALIGN_NORMAL
                    }
                }
                // 先将当前行添加上
                tableData[rowInd].add(TableCellInfo(contentText, textAlign))

                // 补全rowspan, colspan
                val colspanAttr: String? = eachCol.getAttributeByName("colspan")
                val rowspanAttr: String? = eachCol.getAttributeByName("rowspan")
                rowspanAttr?.let { rowspan ->
                    // 行与列同时合并了
                    for (index in 1 until rowspan.toInt()) {
                        tableData[rowInd + index].add(TableCellInfo(alignAttr))
                    }
                    colspanAttr?.let { colspan ->
                        for (index in 1 until colspan.toInt()) {
                            tableData[rowInd].add(TableCellInfo(alignAttr))
                        }
                    }
                } ?: run {
                    colspanAttr?.let { colspan ->
                        // 列合并了
                        for (index in 1 until colspan.toInt()) {
                            tableData[rowInd].add(TableCellInfo(alignAttr))
                        }
                    }
                }
            }
        }
        return tableData
    }

    /**
     * 解析表格数据，只适用单个表格，不适合嵌套表格
     */
    @JvmStatic
    fun parseTableCell(html: String): List<List<TableCellInfo>> {
        val tableData: MutableList<List<TableCellInfo>> = LinkedList<List<TableCellInfo>>()
        if (TextUtils.isEmpty(html)) return tableData
        var rowIndex = 0
        while (rowIndex < html.length) {
            val startTrStart = html.indexOf("<tr", rowIndex)
            rowIndex = if (startTrStart >= 0) {
                // 解析行
                val startTrEnd = html.indexOf(">", startTrStart + 1)
                if (startTrEnd > startTrStart) {
                    val endTrStart = html.indexOf("</tr>", startTrEnd + 1)
                    if (endTrStart > startTrEnd) {
                        // 解析row
                        tableData.add(parseRowCell(html.substring(startTrEnd + 1, endTrStart)))
                        endTrStart + "</tr>".length
                    } else {
                        // 标签有误，跳过，继续后续的表格过滤
                        startTrEnd + 1
                    }
                } else {
                    // 标签有误， 跳过, 继续后续的表格过滤
                    startTrStart + 1
                }
            } else {
                // 没有了 tr
                break
            }
        }
        return tableData
    }

    private fun parseRowCell(rowHtml: String): List<TableCellInfo> {
        val cellInfos: MutableList<TableCellInfo> = LinkedList()
        var colIndex = 0
        while (colIndex < rowHtml.length) {
            val startTdStart = rowHtml.indexOf("<td", colIndex)
            if (startTdStart >= 0) {
                // 解析行
                val startTdEnd = rowHtml.indexOf(">", startTdStart + 1)
                if (startTdEnd > startTdStart) {
                    val endTdStart = rowHtml.indexOf("</td>", startTdEnd + 1)
                    if (endTdStart > startTdEnd) {
                        val cellInfo = TableCellInfo(rowHtml.substring(startTdEnd + 1, endTdStart))
                        // 解析cell
                        cellInfos.add(cellInfo)
                        // 解析 cell属性
                        if (startTdEnd - startTdStart > "<td style=".length) {
                            val style = rowHtml.substring(startTdStart + "<td ".length, startTdEnd)
                            if (!TextUtils.isEmpty(style) && style.contains("style")) {
                                val m: Matcher = textAlignPattern.matcher(style)
                                if (m.find()) {
                                    val alignment = m.group(1)
                                    if (alignment.equals(
                                            "start",
                                            ignoreCase = true
                                        ) || alignment.equals("left", ignoreCase = true)
                                    ) {
                                        cellInfo.alignment = Layout.Alignment.ALIGN_NORMAL
                                    } else if (alignment.equals("center", ignoreCase = true)) {
                                        cellInfo.alignment = Layout.Alignment.ALIGN_CENTER
                                    } else if (alignment.equals(
                                            "end",
                                            ignoreCase = true
                                        ) || alignment.equals("right", ignoreCase = true)
                                    ) {
                                        cellInfo.alignment = Layout.Alignment.ALIGN_OPPOSITE
                                    }
                                }
                            }
                        }
                        colIndex = endTdStart + "</td>".length
                    } else {
                        // 标签有误，跳过，继续后续的表格过滤
                        colIndex = startTdEnd + 1
                    }
                } else {
                    // 标签有误， 跳过, 继续后续的表格过滤
                    colIndex = startTdStart + 1
                }
            } else {
                // 没有了 tr
                break
            }
        }
        return cellInfos
    }

    @JvmStatic
    fun getTimeDurationDesc(timeDuration: Long): String {
        var timeDesc = "00:00"
        if (timeDuration >= 1) {
            timeDesc = if (timeDuration < 60) {   //小于60
                "00:" + if (timeDuration < 10) "0" + timeDuration.toInt() else timeDuration.toInt()
            } else {
                val minute = (timeDuration / 60).toInt()
                val seconds = (timeDuration % 60).toInt()
                if (minute > 10) {
                    minute.toString() + ":" + if (seconds < 10) "0" + seconds else seconds
                } else {
                    "0" + minute + ":" + if (seconds < 10) "0" + seconds else seconds
                }
            }
        }
        return timeDesc
    }

    fun getFileSizeDesc(fileSize: Long): String? {
        return byte2FitMemorySize(fileSize, 2)
    }

    fun byte2FitMemorySize(byteSize: Long, precision: Int): String? {
        require(precision >= 0) { "precision shouldn't be less than zero!" }
        return if (byteSize <= 0) {
            "0B"
        } else if (byteSize < Constants.KB) {
            String.format("%." + precision + "fB", byteSize.toDouble())
        } else if (byteSize < Constants.MB) {
            String.format("%." + precision + "fKB", byteSize.toDouble() / Constants.KB)
        } else if (byteSize < Constants.GB) {
            String.format("%." + precision + "fMB", byteSize.toDouble() / Constants.MB)
        } else {
            String.format("%." + precision + "fGB", byteSize.toDouble() / Constants.GB)
        }
    }

}