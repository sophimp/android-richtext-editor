package com.sophimp.demo

import android.content.Intent
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.sophimp.are.Constants
import com.sophimp.are.demo.databinding.ActivityMainBinding
import com.sophimp.are.inner.Html
import com.sophimp.are.spans.*
import com.sophimp.are.style.*
import com.sophimp.are.utils.Util
import com.sophimp.demo.db.MemoDao
import com.sophimp.demo.db.MemoDatabase
import com.sophimp.demo.db.MemoInfo
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

/**
 *
 * @author: sfx
 * @since: 2021/8/4
 */
class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    var memoDao: MemoDao = MemoDatabase.instance.getMemoDao()
    lateinit var imgStyle: ImageStyle
    lateinit var alignmentCenterStyle: AlignmentCenterStyle
    lateinit var alignmentRightStyle: AlignmentRightStyle
    lateinit var boldStyle: BoldStyle
    lateinit var fontBackgroundStyle: FontBackgroundStyle
    lateinit var fontColorStyle: FontColorStyle
    lateinit var fontSizeStyle: FontSizeStyle
    lateinit var hrStyle: HrStyle
    lateinit var italicStyle: ItalicStyle
    lateinit var lineSpaceEnlargeStyle: LineSpaceEnlargeStyle
    lateinit var indentRightStyle: IndentRightStyle
    lateinit var linkStyle: LinkStyle
    lateinit var listBulletStyle: ListBulletStyle
    lateinit var listNumberStyle: ListNumberStyle
    lateinit var todoStyle: TodoStyle
    lateinit var strikethroughStyle: StrikethroughStyle
    lateinit var underlineStyle: UnderlineStyle
    lateinit var memoAdapter: MemoAdapter
    val spannableStringBuilder = SpannableStringBuilder()
    var counter = 0
    val mainScope = MainScope()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnAdd.setOnClickListener {
            startActivity(Intent(this, EditActivity::class.java))
        }

        binding.rvMemoList.layoutManager = LinearLayoutManager(this)
        memoAdapter = MemoAdapter(memoDao.queryMemoAll())
        binding.rvMemoList.adapter = memoAdapter

        initStyle()

        binding.btnGenerateRandom.setOnClickListener {
            mainScope.launch {
                val job = async {
                    launch {
                        addCharacterStyle()
                    }
                }
                job.await()
                var memoInfo: MemoInfo = MemoInfo("", "")
                val saveJob = launch {
                    memoInfo = MemoInfo(Html.toHtml(spannableStringBuilder.subSequence(0, Math.min(1000, spannableStringBuilder.length)) as Spanned?,
                        Html.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE),
                        Html.toHtml(spannableStringBuilder, Html.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE))
                    val dbId = MemoDatabase.instance.getMemoDao().addMemo(memoInfo)
                    memoInfo.id = dbId
                }
                saveJob.join()
                memoAdapter.data.add(memoInfo)
                memoAdapter.notifyItemRangeInserted(memoAdapter.itemCount, 1)
            }
        }
    }

    fun addParagraphStyle() {
        // indent, align, todostyle, list bullet/number, line space
        val listNumberStr = "list number"
        repeat(1000) {
            val start = spannableStringBuilder.length
            spannableStringBuilder.append(listNumberStr)
            val end = spannableStringBuilder.length
            spannableStringBuilder.setSpan(ListNumberSpan(counter), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            spannableStringBuilder.setSpan(IndentSpan(counter % 5), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
//            addCharacterStyle()
            spannableStringBuilder.append("\n")
            counter++
            Util.log("add paragraph alive $counter")
        }

    }

    fun addCharacterStyle() {
        // img, attachment, bold, strike, underline, italic, font size/color/background, link
        val c = "character style test content"
        repeat(100) {
            val start = spannableStringBuilder.length
            spannableStringBuilder.append(c)
            val end = spannableStringBuilder.length
            when {
                counter % 3 == 0 -> {
                    spannableStringBuilder.setSpan(UnderlineSpan2(), start, end, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
                    spannableStringBuilder.setSpan(StrikethroughSpan2(), start, end, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
                }
                counter % 2 == 0 -> {
                    spannableStringBuilder.setSpan(BoldSpan(), start, end, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
                    spannableStringBuilder.setSpan(ItalicSpan(), start, end, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
                }
                else -> {
                    spannableStringBuilder.setSpan(FontSizeSpan(Constants.DEFAULT_FONT_SIZE + counter % 3),
                        start,
                        end,
                        Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
                    spannableStringBuilder.setSpan(FontForegroundColorSpan("#782230"), start, end, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
                }
            }
            counter++
        }
    }

    private fun initStyle() {
//        imgStyle = ImageStyle(binding.richEditText)
//        alignmentCenterStyle = AlignmentCenterStyle(binding.richEditText)
//        alignmentRightStyle = AlignmentRightStyle(binding.richEditText)
//        boldStyle = BoldStyle(binding.richEditText)
//        fontBackgroundStyle = FontBackgroundStyle(binding.richEditText)
//        fontColorStyle = FontColorStyle(binding.richEditText)
//        fontSizeStyle = FontSizeStyle(binding.richEditText)
//        hrStyle = HrStyle(binding.richEditText)
//        italicStyle = ItalicStyle(binding.richEditText)
//        lineSpaceEnlargeStyle = LineSpaceEnlargeStyle(binding.richEditText)
//        linkStyle = LinkStyle(binding.richEditText)
//        listBulletStyle = ListBulletStyle(binding.richEditText)
//        listNumberStyle = ListNumberStyle(binding.richEditText)
//        todoStyle = TodoStyle(binding.richEditText)
//        strikethroughStyle = StrikethroughStyle(binding.richEditText)
//        underlineStyle = UnderlineStyle(binding.richEditText)
//        indentRightStyle = IndentRightStyle(binding.richEditText)
    }

    override fun onResume() {
        Util.log("main onResume")
        (binding.rvMemoList.adapter as MemoAdapter).setNewData(memoDao.queryMemoAll())
        super.onResume()
    }
}