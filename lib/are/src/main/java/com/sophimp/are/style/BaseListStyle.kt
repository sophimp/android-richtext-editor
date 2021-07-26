package com.sophimp.are.style

import android.text.Editable
import android.text.Spanned
import android.text.TextUtils
import com.sophimp.are.BuildConfig
import com.sophimp.are.Constants
import com.sophimp.are.RichEditText
import com.sophimp.are.Util
import com.sophimp.are.spans.IListSpan
import com.sophimp.are.spans.ISpan
import com.sophimp.are.spans.IndentSpan
import java.util.*
import kotlin.math.max
import kotlin.math.min

/**
 * Abstract for ListBullet, ListTodo, ListNumber
 * @author: sfx
 * @since: 2021/7/22
 */
abstract class BaseListStyle<B : IListSpan, T : IListSpan, TT : IListSpan>(
    editText: RichEditText,
    protected var basicClass: Class<B>,
    protected var targetClass1: Class<T>,
    protected var targetClass2: Class<TT>
) : BaseStyle<B>(editText) {

    /**
     * 每次插入一个span, 可能会多插入一个字符，使用off记录
     */
    private var off = 0
    private var isEmptyLine = false

    override fun itemClickOnEmptyParagraph(curPStart: Int, curPEnd: Int): Int {
        isEmptyLine = true
        handleClickCase2(curPStart, curPEnd + 1)
        return off
    }

    override fun itemClickOnNonEmptyParagraph(curPStart: Int, curPEnd: Int): Int {
        // 每一种case 的变化只针对ListBulletSpan, 保留 DRLeadingSpan, Alignment
        /*
            case 1: ListBulletSpan 有->无
            . aa            . aa
            . bb   ->       bb
            . cc            . cc
         */
        /*
            case 2: ListBulletSpan 无->有
            aa            . aa
            bb   ->       . bb
            cc            . cc
            特例： 空行 -> 有
         */
        /*
            case 3: ListNumberSpan -> ListBulletSpan
            1. aa            1. aa
            2. bb   ->       .bb
            3. cc            1. cc
            替换逻辑同case 2, 选中的下一段如果是 ListNumberSpan 需要重新排序
         */
        /*
            case 4: DRTodoSpan -> ListBulletSpan
            O aa            O aa
            O bb   ->       .bb
            O cc            O cc
            替换逻辑同case 2
         */
        val editable = mEditText.editableText
        val basicSpans =
            editable.getSpans(curPStart, curPEnd, basicClass)
        val targetSpans1 =
            editable.getSpans(curPStart, curPEnd, targetClass1)
        val targetSpans2 =
            editable.getSpans(curPStart, curPEnd, targetClass2)
        if (basicSpans != null && basicSpans.isNotEmpty()) {
            // case 1: 有 -> 无
            handleClickCase1(basicSpans)
        } else {
            if (targetSpans1 != null && targetSpans1.isNotEmpty()) {
                // case 3: TargetSpan1 -> BasicSpan, 每一行的转换
                handleClickCase3(targetSpans1)
            } else if (targetSpans2 != null && targetSpans2.isNotEmpty()) {
                // case 4: TargetSpan2 -> BasicSpan, 每一行的转换
                handleClickCase4(targetSpans2)
            } else {
                // case 2: 无 -> 有
                handleClickCase2(curPStart, curPEnd)
            }
        }
        return off
    }

    override fun toolItemIconClick() {
        super.toolItemIconClick()

        // 重排上一段落及后面所有的 ListNumberSpan, 因为数据量并不会大， 所在重排的性能损失可以忽略，但是实现方法简单得多
        mEditText.postDelayUIRun(Runnable {
            Util.renumberAllListItemSpans(mEditText.editableText)
            mEditText.refresh(0)
            logAllSpans(mEditText.editableText, "style 处理后", 0, mEditText.editableText.length)
            mEditText.beginBatchEdit()
            mEditText.endBatchEdit()
        }, 30)
    }

    private fun handleClickCase2(start: Int, end: Int) {
//        LogUtils.d("sgx cake setSpan start-end: " + currentStart + " - " + currentEnd);
        var currentStart = start
        val editable = mEditText.editableText
        if (editable != null) {
            if (editable.isEmpty()) {
                // 全为空
                currentStart = 0
                editable.append(Constants.ZERO_WIDTH_SPACE_STR)
                off = 1
            } else if (currentStart >= editable.length) {
                // 最后一行行尾
                editable.append(Constants.ZERO_WIDTH_SPACE_STR)
                off = 1
            } else if (currentStart == end && editable[currentStart] == '\n') {
                editable.insert(currentStart, Constants.ZERO_WIDTH_SPACE_STR)
                off = 1
            } else {
                if (editable[currentStart].toInt() != Constants.ZERO_WIDTH_SPACE_INT) {
                    // 每个ListBullet 插入一个零字符， 用于删除时处理最一个字符的临界情况
                    editable.insert(currentStart, Constants.ZERO_WIDTH_SPACE_STR)
                    off = 1
                }
            }
            val finalCurrentStart = currentStart
            if (isEmptyLine) {
                // 针对空行情况, 第一次添加，处理不了那么快
                mEditText.postDelayUIRun(Runnable {
                    try {
                        isEmptyLine = false
                        setSpan(newSpan()!!, currentStart, currentStart + 1)
                        mEditText.refresh(0)
                    } catch (e: IllegalAccessException) {
                        e.printStackTrace()
                    } catch (e: InstantiationException) {
                        e.printStackTrace()
                    }
                }, 0)
            } else {
                try {
                    setSpan(newSpan()!!, currentStart, end)
                } catch (e: IllegalAccessException) {
                    e.printStackTrace()
                } catch (e: InstantiationException) {
                    e.printStackTrace()
                }
                mEditText.refresh(0)
            }
        }
        //        logAllSpans(editable, "case2 添加span后");
    }

    /**
     * case 4: TargetSpan2 -> BasicSpan, 每一行的转换
     */
    private fun handleClickCase4(spans: Array<TT>) {
        val editable = mEditText.editableText
        val start = editable.getSpanStart(spans[0])
        val end = editable.getSpanEnd(spans[0])
        editable.removeSpan(spans[0])
        handleClickCase2(start, end)
    }

    /**
     * case 3: TargetSpan1 -> BasicSpan, 每一行的转换
     */
    private fun handleClickCase3(spans: Array<T>) {
        val editable = mEditText.editableText
        val start = editable.getSpanStart(spans[0])
        val end = editable.getSpanEnd(spans[0])
        editable.removeSpan(spans[0])
        handleClickCase2(start, end)
    }

    /**
     * case 1 : 有 -> 无
     */
    private fun handleClickCase1(spans: Array<B>) {
        val editable = mEditText.editableText
        removeSpans(editable, spans)
    }

    override fun handleSingleParagraphInput(
        editable: Editable,
        changedText: String?,
        beforeSelectionStart: Int,
        afterSelectionEnd: Int
    ) {
        // 由于是 SPAN_EXCLUSIVE_EXCLUSIVE， 格式需要设置到整个段落，否则光标会错位
        // 所以每次输入完后，需要重新设置一下span为整个段落, 这里由于只是对一段的处理，性能影响O(n)，n为段落长度，可以忽略
        val pStart: Int = Util.getParagraphStart(mEditText, beforeSelectionStart)
        val pEnd: Int = Util.getParagraphEnd(editable, afterSelectionEnd)
        if (pStart < pEnd) {
            val base = editable.getSpans(pStart, pEnd, basicClass)
            if (base.isNotEmpty()) {
                removeSpans(editable, base)
                setSpan(base[0], pStart, pEnd)
                mEditText.refresh(beforeSelectionStart)
            }
        }
    }

    /**
     * 处理换行操作
     */
    override fun handleInputNewLine(editable: Editable, beforeSelectionStart: Int) {
        /*
            case 1: 有 内容 换行
                移除换行前的span, 然后在当前行与前一行分别添加baseClassSpan即可
            case 2: 没有内容换行
                取消当前的baseClassSpan, 同时移除缩进
         */
        // start 换到当前行的上一行的末尾
        val lastPStart: Int = Util.getParagraphStart(mEditText, beforeSelectionStart)
        var lastPEnd: Int = Util.getParagraphEnd(editable, beforeSelectionStart)
        if (lastPEnd < lastPStart) lastPEnd = lastPStart
        Util.log("sgx cake: 上一行: " + lastPStart + " - " + lastPEnd + " 当前行: " + mEditText.getSelectionStart() + " - " + mEditText.getSelectionEnd())
        val preListSpan: Array<B> = editable.getSpans(lastPStart, lastPEnd, basicClass)
        val preLeading: Array<IndentSpan> =
            editable.getSpans(lastPStart, lastPEnd, IndentSpan::class.java)
        // 目前只处理带列表或缩进的
        if (preListSpan.isEmpty()) return
        // 先移除上一行的span
        removeSpans(editable, preListSpan)
        removeSpans(editable, preLeading)
        // 移除当前行的Spans
        removeSpans(
            editable,
            editable.getSpans(
                mEditText.selectionStart,
                mEditText.selectionEnd,
                basicClass
            )
        )
        removeSpans(
            editable,
            editable.getSpans(
                mEditText.selectionStart,
                mEditText.selectionEnd,
                IndentSpan::class.java
            )
        )

        // 再将上一行与当前行统一处理
        val lastContent = editable.subSequence(lastPStart, lastPEnd).toString()
        if (TextUtils.isEmpty(lastContent) || lastContent.length == 1 && lastContent[0].toInt() == Constants.ZERO_WIDTH_SPACE_INT) {
            // case 2: 没有内容换行
            editable.delete(
                max(0, mEditText.selectionStart - 1),
                mEditText.selectionStart
            )
            //            getEditText().setSelection(Math.max(0, getEditText().getSelectionStart() - 1));
        } else {
            // case 1: 有内容换行,
            // 前一行添加span
            if (preListSpan.isNotEmpty()) {
                setSpan(preListSpan[0], lastPStart, lastPEnd)
            }
            if (preLeading.isNotEmpty()) {
                setSpan(preLeading[0], lastPStart, lastPEnd)
            }
            // 当前行添加span
            if (preListSpan.isNotEmpty()) {
                try {
                    val curStart: Int = mEditText.selectionStart
                    editable.insert(curStart, Constants.ZERO_WIDTH_SPACE_STR)
                    setSpan(newSpan()!!, curStart, min(curStart + 1, editable.length))
                } catch (e: IllegalAccessException) {
                    e.printStackTrace()
                } catch (e: InstantiationException) {
                    e.printStackTrace()
                }
            }
            if (preLeading.isNotEmpty()) {
                setSpan(
                    IndentSpan(preLeading[0].mLevel),
                    lastPEnd + 1,
                    min(lastPEnd + 2, editable.length)
                )
            }
        }

        // 重排所有的 ListNumberSpan, 因为数据量并不会大， 所在重排的性能损失可以忽略，但是实现方法简单得多
        Util.renumberAllListItemSpans(editable)
        mEditText.refresh(0)
        logAllSpans(editable, javaClass.simpleName + " apply style 后", 0, editable.length)
    }

    /**
     * 处理多行输入
     */
    override fun handleMultiParagraphInput(
        editable: Editable,
        changedText: String?,
        beforeSelectionStart: Int,
        afterSelectionEnd: Int
    ) {
        val changedEnd: Int = afterSelectionEnd
        val effectPStart: Int = Util.getParagraphStart(mEditText, beforeSelectionStart)
        val effectPEnd: Int = Util.getParagraphEnd(editable, changedEnd)
        val effectFirstPEnd: Int = Util.getParagraphEnd(editable, beforeSelectionStart)
        val firstPListSpans: Array<B> = editable.getSpans(effectPStart, effectFirstPEnd, basicClass)
        val firstPLeadingSpans: Array<IndentSpan> =
            editable.getSpans(effectPStart, effectFirstPEnd, IndentSpan::class.java)
        if (firstPListSpans.isEmpty() && firstPLeadingSpans.isEmpty()) return
        val allPListSpans: Array<B> = editable.getSpans(effectPStart, effectPEnd, basicClass)
        val allPLeadSpans: Array<IndentSpan> =
            editable.getSpans(effectPStart, effectPEnd, IndentSpan::class.java)
        // 先移复制内容部分所有的缩进与
        removeSpans(editable, allPListSpans)
        removeSpans(editable, allPLeadSpans)
        logAllSpans(editable, "多行输入前处理", 0, editable.length)
        // 暂时都使用同级类目处理
        handleCommonInput(editable, effectPStart, effectPEnd, firstPListSpans, firstPLeadingSpans)
        Util.renumberAllListItemSpans(editable)
        mEditText.refresh(0)
        logAllSpans(editable, javaClass.simpleName + " apply style 后", 0, editable.length)
    }

    /**
     * 普通的多段输入
     */
    private fun handleCommonInput(
        editable: Editable,
        effectPStart: Int,
        effectPEnd: Int,
        firstPListSpans: Array<B>,
        firstPLeadingSpans: Array<IndentSpan>
    ) {
        var index = effectPStart
        var off = 0
        while (index < effectPEnd) {
            off = 0
            var pEnd: Int = Util.getParagraphEnd(editable, index)
            if (pEnd < 0) {
                // 最后一段
                pEnd = effectPEnd
            }
            if (index < pEnd) {
                if (firstPListSpans.isNotEmpty()) {
                    try {
                        setSpan(newSpan()!!, index, pEnd)
                    } catch (e: IllegalAccessException) {
                        e.printStackTrace()
                    } catch (e: InstantiationException) {
                        e.printStackTrace()
                    }
                }
                if (firstPLeadingSpans.isNotEmpty()) {
                    setSpan(IndentSpan(firstPLeadingSpans[0].mLevel), index, pEnd)
                }
            } // else { // 相等， 空行, 不处理
            index = pEnd + 1 + off
        }
    }

    /**
     * 处理删除事件
     */
    override fun handleDeleteEvent(editable: Editable) {
        /*
               删除操作
               case 1: 空行换上一行, 需要删除当前行的span
               case 2: 带内容删除换上一行, 需要删除当前行span
               case 3: 带内容，直接从行首删除换上一行, 需要合并内容
               case 4: 不换行, 正常删除
             */

        // 不管哪一种删除，只要将删除后的当前光标段落, 再重新设置一下Span即可
        val curPStart: Int = Util.getParagraphStart(mEditText, mEditText.getSelectionStart())
        val curPEnd: Int = Util.getParagraphEnd(editable, mEditText.getSelectionStart())
        val preDelListSpans: Array<B> = editable.getSpans(
            curPStart,
            max(mEditText.selectionStart - 1, curPStart),
            basicClass
        )
        val preLeadSpans: Array<IndentSpan> = editable.getSpans(
            curPStart,
            max(mEditText.selectionStart - 1, curPStart),
            IndentSpan::class.java
        )
        val curListSpans: Array<B> = editable.getSpans(curPStart, curPEnd, basicClass)
        val curLeadingSpans: Array<IndentSpan> =
            editable.getSpans(curPStart, curPEnd, IndentSpan::class.java)
        if (curListSpans.isEmpty() && curLeadingSpans.isEmpty()) return
        logAllSpans(editable, "即将删除的spans ", curPStart, curPEnd)
        removeSpans(editable, curListSpans)
        removeSpans(editable, curLeadingSpans)
        if (preDelListSpans.isNotEmpty()) {
            try {
                setSpan(newSpan()!!, curPStart, curPEnd)
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            } catch (e: InstantiationException) {
                e.printStackTrace()
            }
        }
        if (preLeadSpans.isNotEmpty()) {
            setSpan(IndentSpan(preLeadSpans[0].mLevel), curPStart, curPEnd)
        }

        // 重排所有的 ListNumberSpan, 因为数据量并不会大， 所在重排的性能损失可以忽略，但是实现方法简单得多
        Util.renumberAllListItemSpans(editable)
        mEditText.refresh(0)
        logAllSpans(editable, javaClass.simpleName + " apply style 后", 0, editable.length)
    }

    private fun logAllSpans(
        editable: Editable,
        tag: String,
        start: Int,
        end: Int
    ) {
        if (!BuildConfig.DEBUG) return
        val listItemSpans = editable.getSpans(start, end, basicClass)
        // 坑点， 这里取出来的span 并不是按先后顺序， 需要先排序
        Arrays.sort(
            listItemSpans
        ) { o1: B, o2: B ->
            editable.getSpanStart(o1) - editable.getSpanStart(o2)
        }
        val leadingMarginSpans: Array<IndentSpan> =
            editable.getSpans(start, end, IndentSpan::class.java)
        Util.log("-----------$tag--------------")
        for (span in listItemSpans) {
            val ss = editable.getSpanStart(span)
            val se = editable.getSpanEnd(span)
            Util.log("List All " + basicClass.simpleName + ": " + " :: start == " + ss + ", end == " + se)
        }
        for (span in leadingMarginSpans) {
            val ss = editable.getSpanStart(span)
            val se = editable.getSpanEnd(span)
            Util.log("List All leading span:  :: start == $ss, end == $se")
        }
        Util.log(tag + " : " + "总长度: " + editable.length)
    }

    override fun setSpan(span: ISpan, start: Int, end: Int) {
        mEditText.editableText.setSpan(span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }
}