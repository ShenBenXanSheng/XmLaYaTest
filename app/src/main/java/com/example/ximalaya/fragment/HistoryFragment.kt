package com.example.ximalaya.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ximalaya.R
import com.example.ximalaya.adapter.HistoryAdapter
import com.example.ximalaya.base.BaseViewModelFragment
import com.example.ximalaya.databinding.FragmentHistoryBinding
import com.example.ximalaya.room.HistoryData
import com.example.ximalaya.utils.Constant
import com.example.ximalaya.view.DialogControl
import com.example.ximalaya.viewmodel.HistoryViewModel
import com.ximalaya.ting.android.opensdk.model.track.Track
import java.io.Serializable

class HistoryFragment : BaseViewModelFragment<HistoryViewModel>() {
    private lateinit var dataBinding: FragmentHistoryBinding
    private val xmDialog by lazy {
        DialogControl(requireContext())
    }
    private val historyAdapter by lazy {
        HistoryAdapter()
    }

    private val trackList = mutableListOf<Track>()

    override fun getSuccessFragmentView(rootFrameLayout: FrameLayout): View {
        dataBinding = DataBindingUtil.inflate(
            LayoutInflater.from(requireContext()),
            R.layout.fragment_history,
            rootFrameLayout,
            false
        )

        return dataBinding.root
    }


    override fun getViewModelClass(): Class<HistoryViewModel> {
        return HistoryViewModel::class.java
    }

    override fun initView() {
        dispatchViewState(FragmentStatus.SUCCESS)
        dataBinding.apply {
            historyRv.layoutManager = LinearLayoutManager(requireContext())
            historyRv.adapter = historyAdapter
        }
    }


    override fun initListener() {
        super.initListener()
        dataBinding.apply {
            historyClearData.setOnClickListener {
                xmDialog.apply {
                    upDateDialogState(DialogControl.DialogStatus.CLEAR)
                    setOnDialogClearClickListener(object :
                        DialogControl.OnDialogClearClickListener {
                        override fun onClearClick() {
                            mViewModel?.clearHistory()
                        }
                    })

                    show()
                }
            }

            historyAdapter.setOnHistoryClickListener(object :
                HistoryAdapter.OnHistoryClickListener {
                override fun onHistoryClick(track: List<HistoryData>, position: Int) {
                    trackList.clear()

                    track.forEach {
                        val currentTrack = Track()
                        currentTrack.trackTitle = it.title
                        currentTrack.coverUrlMiddle = it.cover
                        currentTrack.kind = it.kind
                        currentTrack.playCount = it.playCount
                        currentTrack.dataId = it.trackId
                        currentTrack.updatedAt = it.upDateTime
                        currentTrack.announcer.nickname = it.nickName

                        trackList.add(currentTrack)
                    }


                   val bundle = Bundle()
                   bundle.putInt(Constant.DETAIL_POSITION_DATA, position)
                   bundle.putSerializable(Constant.DETAIL_LIST_DATA, trackList as Serializable)
                   findNavController().navigate(R.id.player_fragment, bundle)
                }

            })
        }
    }


    override fun initDataListener() {
        super.initDataListener()

        mViewModel?.queryHistoryList()?.observe(viewLifecycleOwner) {
            historyAdapter.setData(it)
        }
    }

}