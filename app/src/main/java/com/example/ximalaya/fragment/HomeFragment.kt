package com.example.ximalaya.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.ximalaya.R
import com.example.ximalaya.activity.SearchActivity
import com.example.ximalaya.adapter.MainViewPageAdapter

import com.example.ximalaya.databinding.FragmentHomeBinding
import com.google.android.material.tabs.TabLayoutMediator


class HomeFragment : Fragment() {
    private val titleString = arrayOf("推荐", "订阅", "历史")
    private lateinit var dataBinding: FragmentHomeBinding
    private lateinit var tabLayoutMediator: TabLayoutMediator
    private lateinit var mainViewPageAdapter: MainViewPageAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dataBinding = FragmentHomeBinding.inflate(layoutInflater)
        initView()
        return dataBinding.root
    }
    private fun initView() {

        mainViewPageAdapter = MainViewPageAdapter(childFragmentManager, lifecycle)
        dataBinding.apply {
            mainViewPage.offscreenPageLimit = ViewPager2.OFFSCREEN_PAGE_LIMIT_DEFAULT
            mainViewPage.adapter = mainViewPageAdapter
            mainViewPage.registerOnPageChangeCallback(viewPagerCallback)
            tabLayoutMediator = TabLayoutMediator(
                mainTabLayout,
                mainViewPage
            ) { tab, position ->
                val title = titleString[position]

                val textView = TextView(requireContext())
                textView.text = title
                textView.gravity = Gravity.CENTER


                tab.customView = textView
            }

            tabLayoutMediator.attach()


            mainSearchIv.setOnClickListener {
                val intent = Intent(requireContext(), SearchActivity::class.java)
                startActivity(intent)
            }

        }
    }

    private val viewPagerCallback = object : ViewPager2.OnPageChangeCallback() {
        @SuppressLint("ResourceType")
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            for (i in titleString.indices) {
                val tabAt = dataBinding.mainTabLayout.getTabAt(i)
                if (tabAt != null) {
                    val textView = tabAt.customView as TextView
                    if (i == position) {
                        textView.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.sienna
                            )
                        )
                        textView.textSize = 18.0f

                    } else {
                        textView.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.black
                            )
                        )
                        textView.textSize = 16.0f
                    }
                }

            }

        }
    }


    override fun onDestroy() {
        super.onDestroy()
        tabLayoutMediator.detach()
    }
}
