package com.sophimp.are.spans

/**
 * record  font size, foreground color, background color
 * @author: sfx
 * @since: 2021/7/20
 */
interface IDynamicSpan : ISpan {
    val dynamicFeature: String
}