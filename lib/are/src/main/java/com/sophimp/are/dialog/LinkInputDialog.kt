package com.sophimp.are.dialog

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.KeyEvent
import com.sophimp.are.databinding.DialogLinkInputBinding

/**
 * @author: sfx
 * @since: 2021/6/9
 */
class LinkInputDialog(context: Context) : Dialog(context) {

    var binding: DialogLinkInputBinding? = null
    private var linkLister: OnInertLinkListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogLinkInputBinding.inflate(layoutInflater)
        setContentView(binding?.root!!)
        setUpView()
        setOnKeyListener(object : DialogInterface.OnKeyListener {
            override fun onKey(dialog: DialogInterface?, keyCode: Int, event: KeyEvent?): Boolean {
                if (keyCode == KeyEvent.KEYCODE_BACK && event?.action == KeyEvent.ACTION_UP) {
                    dismiss()
                    return true
                }
                return false
            }
        })
    }

    private fun setUpView() {
//        window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
        binding!!.iclTitle.tvBtnConfirm.text = "save"
        updateConfirmBtnState()
    }

    protected fun setListenner() {
        binding!!.etLinkAddr.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence,
                start: Int,
                before: Int,
                count: Int
            ) {
                updateConfirmBtnState()
            }

            override fun afterTextChanged(s: Editable) {}
        })
        binding!!.iclTitle.tvBtnClose.setOnClickListener({ v -> dismiss() })
        binding!!.iclTitle.tvBtnConfirm.setOnClickListener {
            if (linkLister != null) {
                linkLister!!.onLinkInert(
                    binding!!.etLinkAddr.text.toString(),
                    binding!!.etLinkAddrName.text.toString()
                )
            }
            dismiss()
        }
    }

    fun setLinkLister(linkLister: OnInertLinkListener?): LinkInputDialog {
        this.linkLister = linkLister
        return this
    }

    private fun updateConfirmBtnState() {
        if (TextUtils.isEmpty(binding!!.etLinkAddr.text)) {
            binding!!.iclTitle.tvBtnConfirm.setTextColor(Color.parseColor("#4d3c3c43"))
            binding!!.iclTitle.tvBtnConfirm.setEnabled(false)
        } else {
            binding!!.iclTitle.tvBtnConfirm.setTextColor(Color.parseColor("#ff2899fb"))
            binding!!.iclTitle.tvBtnConfirm.setEnabled(true)
        }
    }

    override fun dismiss() {
        super.dismiss()
    }

    interface OnInertLinkListener {
        fun onLinkInert(linkAddr: String, linkName: String)
    }
}