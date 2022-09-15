package com.example.ximalaya.utils

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.View

object ButtonSelectColorUtil {
    @SuppressLint("ClickableViewAccessibility")
    fun setImageTint( view: View, pressFalseColor: Int, pressTureColor: Int): Int {
        var resultColor = pressFalseColor

            view.setOnTouchListener { v, event ->
                val action = event.action

                resultColor = when (action) {
                    MotionEvent.ACTION_DOWN -> {
                        pressTureColor
                    }
                    MotionEvent.ACTION_MOVE -> {
                        pressTureColor
                    }
                    MotionEvent.ACTION_UP -> {
                        pressFalseColor
                    }
                    else -> {
                        pressFalseColor
                    }
                }
                return@setOnTouchListener true
            }

        return resultColor
    }
}