package com.sophimp.are.toolbar

import com.sophimp.are.toolbar.items.IToolbarItem

/**
 * custom the action for the item click
 * @author: sfx
 * @since: 2021/7/30
 */
interface IToolbarItemClickAction {
    fun onItemClick(item: IToolbarItem)
}