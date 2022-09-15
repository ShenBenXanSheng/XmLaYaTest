package com.example.ximalaya.utils

import com.example.ximalaya.base.BaseFragment
import com.example.ximalaya.fragment.HistoryFragment
import com.example.ximalaya.fragment.RecommendFragment
import com.example.ximalaya.fragment.SubscribeFragment
import com.example.ximalaya.utils.Constant.HISTORY_FRAGMENT_CONTROL
import com.example.ximalaya.utils.Constant.RECOMMEND_FRAGMENT_CONTROL
import com.example.ximalaya.utils.Constant.SUBSCRIBE_FRAGMENT_CONTROL

object FragmentControl {
    private val fragmentMap = mutableMapOf<Int, BaseFragment?>()
    private var baseFragment: BaseFragment? = null

    fun getFragmentToPosition(position: Int): BaseFragment? {
        baseFragment = fragmentMap[position]
        if (baseFragment != null) {
            return baseFragment
        }

        when (position) {
            RECOMMEND_FRAGMENT_CONTROL -> {
                baseFragment = RecommendFragment()
            }

            SUBSCRIBE_FRAGMENT_CONTROL -> {
                baseFragment = SubscribeFragment()
            }

            HISTORY_FRAGMENT_CONTROL -> {
                baseFragment = HistoryFragment()
            }
        }
        fragmentMap.put(position, baseFragment)

        return baseFragment
    }
}