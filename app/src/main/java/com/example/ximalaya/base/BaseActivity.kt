package com.example.ximalaya.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.ximalaya.R

abstract class BaseActivity : AppCompatActivity() {
    private lateinit var frameLayout: FrameLayout
    private lateinit var loadingView: View
    private lateinit var successView: View
    private lateinit var errorView: View
    private lateinit var emptyView: View

    enum class ActivityState {
        LOADING, SUCCESS, ERROR, EMPTY
    }

    open fun switchUpdateActivityState(activityState: ActivityState) {
        hideAllView()
        when (activityState) {
            ActivityState.LOADING -> {
                loadingView.visibility = View.VISIBLE
            }
            ActivityState.SUCCESS -> {
                successView.visibility = View.VISIBLE
            }

            ActivityState.ERROR -> {
                errorView.visibility = View.VISIBLE
            }
            ActivityState.EMPTY -> {
                emptyView.visibility = View.VISIBLE
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(setRootViewId())
        frameLayout = findViewById(R.id.base_activity_container)

        switchAddActivity()
        hideAllView()
        initView()
        initDataListener()
        initListener()
        reLoad()
    }


    open fun setRootViewId():View{
        return LayoutInflater.from(this).inflate(R.layout.activity_base,null)
    }

    open fun reLoad() {

    }


    open fun initListener() {

    }

    open fun initDataListener() {

    }


    abstract fun initView()


    private fun switchAddActivity() {
        loadingView = addFrameLayoutView(R.layout.state_loading)
        successView = addSuccessView(frameLayout)
        errorView = addFrameLayoutView(R.layout.state_error)
        errorView.setOnClickListener {
            reLoad()
        }
        emptyView = addFrameLayoutView(R.layout.state_empty)
        frameLayout.addView(loadingView)
        frameLayout.addView(successView)
        frameLayout.addView(errorView)
        frameLayout.addView(emptyView)


    }

    abstract fun addSuccessView(frameLayout: FrameLayout): View

    private fun addFrameLayoutView(resId: Int): View {
        return LayoutInflater.from(this).inflate(resId, frameLayout, false)
    }

    private fun hideAllView() {
        loadingView.visibility = View.GONE
        successView.visibility = View.GONE
        errorView.visibility = View.GONE
        emptyView.visibility = View.GONE
    }
}