package com.example.ximalaya.utils

import android.content.Context

import com.example.ximalaya.base.BaseApp

object SizeUtil {
    fun dip2px(dpValue: Float): Int {
        val scale: Float = BaseApp.mBaseContext!!.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }
}