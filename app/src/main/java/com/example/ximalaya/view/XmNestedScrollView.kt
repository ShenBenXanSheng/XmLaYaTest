package com.example.ximalaya.view

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.View
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.FragmentActivity
import com.example.ximalaya.R
import com.scwang.smart.refresh.layout.SmartRefreshLayout

class XmNestedScrollView : NestedScrollView {
    private lateinit var detailFragment: FragmentActivity
    private var verticalSize: Int = 0
    private var headViewHeight: Int = 0

    private lateinit var detailSmartRefreshLayout: SmartRefreshLayout

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attributeSet: AttributeSet?) : this(
        context,
        attributeSet,
        0
    )

    constructor(context: Context, attributeSet: AttributeSet?, defaultAttr: Int) : super(
        context,
        attributeSet,
        defaultAttr
    ) {

    }

    fun getHeadViewHeight(measuredHeight: Int) {
        this.headViewHeight = measuredHeight

    }

    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (verticalSize < headViewHeight) {

                scrollBy(dx, dy)
                consumed[0] = dx
                consumed[1] = dy

                setStatusBarColorByScroll(context.getColor(R.color.transparent))
                detailSmartRefreshLayout.isNestedScrollingEnabled = false
                detailSmartRefreshLayout.setEnableLoadMore(false)

            } else {
                detailSmartRefreshLayout.isNestedScrollingEnabled = true
                detailSmartRefreshLayout.setEnableLoadMore(true)
                setStatusBarColorByScroll(context.getColor(R.color.mainColor))
            }


        }
        super.onNestedPreScroll(target, dx, dy, consumed, type)
    }


    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {

        this.verticalSize = t
        super.onScrollChanged(l, t, oldl, oldt)
    }

    fun getActivity(detailFragment: FragmentActivity) {
        this.detailFragment = detailFragment

        detailSmartRefreshLayout = detailFragment.findViewById(R.id.detail_smart_refresh)
    }

    private fun setStatusBarColorByScroll(color: Int) {
        detailFragment.window.statusBarColor = color
    }
}