package com.example.ximalaya.utils

import android.view.Gravity
import android.widget.Toast
import com.example.ximalaya.base.BaseApp

object ToastUtil {
    private var toast: Toast? = null
    fun setToast(string: String) {
        if (toast==null){
            toast = Toast.makeText(BaseApp.mBaseContext,string,Toast.LENGTH_SHORT)
        }else{
            toast?.setText(string)
        }
        toast?.setGravity(Gravity.CENTER,0,0)
        toast?.show()
    }




}