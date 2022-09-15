package com.example.ximalaya.utils

import android.content.Context
import android.inputmethodservice.InputMethodService
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.example.ximalaya.base.BaseApp

object KeyboardUtil {
    fun showKeyboard(view: View){
        BaseApp.mBaseContext?.apply {
            val inputMethodService =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

            inputMethodService.showSoftInput(view,0)
        }
    }

    fun hideKeyboard(context: Context,view: View){

            val inputMethodService =
                context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

            inputMethodService.hideSoftInputFromWindow(view.windowToken,0)

    }
}