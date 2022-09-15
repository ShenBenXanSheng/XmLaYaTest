package com.example.ximalaya.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import com.example.ximalaya.R

abstract class BaseFragment : Fragment() {
    private lateinit var rootFrameLayout: FrameLayout
    private lateinit var loadingView: View
    private lateinit var successView: View
    private lateinit var emptyView: View
    private lateinit var errorView: View

    enum class FragmentStatus {
        LOADING, SUCCESS, ERROR, EMPTY
    }

    fun dispatchViewState(fragmentStatus: FragmentStatus) {
        hideAllView()
        when (fragmentStatus) {
            FragmentStatus.LOADING -> {
                loadingView.visibility = View.VISIBLE
            }
            FragmentStatus.SUCCESS -> {
                successView.visibility = View.VISIBLE
            }
            FragmentStatus.ERROR -> {
                errorView.visibility = View.VISIBLE
            }
            FragmentStatus.EMPTY -> {
                emptyView.visibility = View.VISIBLE
            }

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = getRootView(inflater, container)
        rootFrameLayout = rootView.findViewById(R.id.baseFragmentContainer)
        loadAllView()
        hideAllView()
        initView()
        initDataListener()
        initListener()
        return rootView
    }

    open fun initListener() {

    }

    open fun initDataListener() {

    }

    abstract fun initView()


    private fun loadAllView() {
        loadingView = loadView(R.layout.state_loading)
        successView = getSuccessFragmentView(rootFrameLayout)
        emptyView = loadView(R.layout.state_empty)

        errorView = loadView(R.layout.state_error)

        errorView.setOnClickListener {
            reload()
        }

        rootFrameLayout.addView(loadingView)
        rootFrameLayout.addView(successView)
        rootFrameLayout.addView(emptyView)
        rootFrameLayout.addView(errorView)

    }

    private fun loadView(stateId: Int): View {
        return LayoutInflater.from(requireContext()).inflate(stateId, rootFrameLayout, false)
    }


    private fun hideAllView() {
        loadingView.visibility = View.GONE
        successView.visibility = View.GONE
        emptyView.visibility = View.GONE
        errorView.visibility = View.GONE
    }

    abstract fun getSuccessFragmentView(rootFrameLayout: FrameLayout): View

    open fun getRootView(inflater: LayoutInflater, container: ViewGroup?): View {
        return inflater.inflate(R.layout.fragment_base, container, false)
    }

    open fun reload() {

    }
}