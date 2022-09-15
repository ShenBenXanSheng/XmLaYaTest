package com.example.ximalaya.base

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

abstract class BaseViewModelFragment<vm:ViewModel> :BaseFragment(){
     var mViewModel:vm ?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        initViewModel()
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        initViewModel()
    }
     private fun initViewModel(){
         mViewModel = ViewModelProvider(requireActivity()).get(getViewModelClass())
     }

    abstract fun getViewModelClass():Class<vm>


}