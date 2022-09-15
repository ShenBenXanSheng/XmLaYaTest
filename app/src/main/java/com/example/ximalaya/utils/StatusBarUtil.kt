package com.example.ximalaya.utils

import android.content.Context

object StatusBarUtil {
    fun getStatusBarHeightCompat(context: Context): Int {
        var height = 0
        val resourceId: Int = context.resources
            .getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            height = context.resources.getDimensionPixelSize(resourceId)
        }

        return height
    }


}