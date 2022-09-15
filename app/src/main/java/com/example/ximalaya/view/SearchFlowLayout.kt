package com.example.ximalaya.view

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.ximalaya.R

class SearchFlowLayout : ViewGroup {
    private lateinit var onSearchFlowItemClickListener: OnSearchFlowItemClickListener
    private val flowContentList = mutableListOf<String>()

    private val spacing = 20

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, 0)

    constructor(context: Context, attributeSet: AttributeSet?, defaultAttrs: Int) : super(context,
        attributeSet,
        defaultAttrs)

    private lateinit var lines: MutableList<View>
    private val linesList = mutableListOf<List<View>>()
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (childCount != 0) {
            linesList.clear()
            lines = mutableListOf()
            linesList.add(lines)
            for (child in 0 until childCount) {
                val childAt = getChildAt(child)
                if (childAt.visibility == View.GONE) {
                    continue
                }
                measureChild(childAt, widthMeasureSpec, heightMeasureSpec)
                if (!canBeAdd(childAt.measuredWidth,
                        lines,
                        MeasureSpec.getSize(widthMeasureSpec))
                ) {
                    lines = mutableListOf()
                    linesList.add(lines)
                }
                lines.add(childAt)
            }

            val contentHeight =
                linesList.size * getChildAt(0).measuredHeight + linesList.size * spacing + spacing

            setMeasuredDimension(widthMeasureSpec, contentHeight)
        }
    }

    private fun canBeAdd(
        currentChildWidth: Int,
        lines: MutableList<View>,
        parentWidth: Int,
    ): Boolean {
        var total = 0
        lines.forEach {
            total += it.measuredWidth
            total += spacing
        }
        total += currentChildWidth + spacing
        return parentWidth >= total
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        if (childCount != 0) {
            val child = getChildAt(0)
            var currentLeft = spacing
            var currentTop = spacing
            var currentRight = 0
            var currentBottom = child.measuredHeight + spacing
            linesList.forEach { lines ->
                lines.forEach {
                    currentRight += it.measuredWidth + spacing
                    it.layout(currentLeft, currentTop, currentRight, currentBottom)
                    currentLeft = currentRight + spacing
                }
                currentLeft = spacing
                currentRight = 0
                currentTop += child.measuredHeight + spacing
                currentBottom += child.measuredHeight + spacing
            }
        }
    }

    fun setData(flowList: List<String>) {
        flowContentList.clear()
        removeAllViews()
        flowContentList.addAll(flowList)



        flowContentList.forEach {
            val inflate = LayoutInflater.from(this.context)
                .inflate(R.layout.item_search_flow_text, this, false) as TextView
            inflate.text = it

            inflate.setOnClickListener {
                if (this::onSearchFlowItemClickListener.isInitialized) {
                    onSearchFlowItemClickListener.onFlowItemClick(inflate.text.toString())
                }
            }

            addView(inflate)


        }
    }

    fun clearDataList() {
        flowContentList.clear()
        removeAllViews()
    }

    fun setOnSearchFlowItemClickListener(onSearchFlowItemClickListener: OnSearchFlowItemClickListener) {
        this.onSearchFlowItemClickListener = onSearchFlowItemClickListener
    }

    interface OnSearchFlowItemClickListener {
        fun onFlowItemClick(text: String)
    }
}