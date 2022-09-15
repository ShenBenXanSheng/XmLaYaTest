package com.example.ximalaya.base

import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

abstract class BaseViewModelActivity<vm : ViewModel> : BaseActivity() {
    var mViewModel:vm ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        mViewModel = ViewModelProvider(this)[setViewModel()]
        super.onCreate(savedInstanceState)

    }


    abstract fun setViewModel(): Class<vm>

}