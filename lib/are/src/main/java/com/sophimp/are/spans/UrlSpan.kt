package com.sophimp.are.spans

import android.text.style.URLSpan

/**
 * wrap in future, just to know there is URLSpan now time
 * @author: sfx
 * @since: 2021/7/20
 */
class UrlSpan(url: String?) : URLSpan(url), IClickableSpan