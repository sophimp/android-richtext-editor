package com.sophimp.are.decoration

import android.content.Context
import android.util.TypedValue

class Dp2Px {
    private fun Dp2Px() {
        /* cannot be instantiated */
        throw UnsupportedOperationException("cannot be instantiated")
    }

    companion object {
        /**
         * dp转px
         *
         * @param context
         * @param dpVal
         * @return
         */
        fun convert(context: Context, dpVal: Float): Int {
            return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, context.resources.displayMetrics).toInt()
        }
    }
}