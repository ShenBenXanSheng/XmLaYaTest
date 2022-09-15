package com.example.ximalaya.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.ximalaya.utils.Constant
import com.example.ximalaya.utils.FragmentControl
import java.lang.Exception

class MainViewPageAdapter(fm: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fm, lifecycle) {
    override fun getItemCount() = Constant.TAB_TITLE_SIZE

    override fun createFragment(position: Int): Fragment {
        val fragmentToPosition = FragmentControl.getFragmentToPosition(position)
        if (fragmentToPosition != null) {
            return fragmentToPosition
        } else {
            throw Exception("没找到Fragment")
        }

    }

}