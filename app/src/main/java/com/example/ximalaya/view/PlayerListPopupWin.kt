package com.example.ximalaya.view

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout


import android.widget.PopupWindow
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ximalaya.R
import com.example.ximalaya.fragment.PlayerFragment
import com.example.ximalaya.adapter.PlayerPopupWinAdapter

import com.example.ximalaya.databinding.PopPlayerListBinding
import com.example.ximalaya.utils.SizeUtil
import com.example.ximalaya.viewmodel.PlayerViewModel
import com.ximalaya.ting.android.opensdk.model.track.Track
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl

class PlayerListPopupWin(val playerFragment: PlayerFragment, val mViewModel: PlayerViewModel?) :
    PopupWindow(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    ) {
    companion object {
        @JvmStatic
        @BindingAdapter("play_mode_color")
        fun setPlayModeColor(linearLayout: LinearLayout, color: Int) {
            linearLayout.findViewById<ImageView>(R.id.pop_play_mode_iv).setColorFilter(color)
            linearLayout.findViewById<TextView>(R.id.pop_play_mode_tv).setTextColor(color)
        }

        @JvmStatic
        @BindingAdapter("play_asc_color")
        fun setPlayAscColor(linearLayout: LinearLayout, color: Int) {
            linearLayout.findViewById<ImageView>(R.id.pop_play_asc_iv).setColorFilter(color)
            linearLayout.findViewById<TextView>(R.id.pop_play_asc_tv).setTextColor(color)
        }
    }

    private var currentPlayPosition: Int = 0
    private var isAsc = false

    private val playModeMap = mutableMapOf<XmPlayListControl.PlayMode, XmPlayListControl.PlayMode>()

    private var currentPlayMode = XmPlayListControl.PlayMode.PLAY_MODEL_LIST

    private var dataBinding: PopPlayerListBinding = DataBindingUtil.inflate(
        LayoutInflater.from(playerFragment.requireContext()),
        R.layout.pop_player_list, null, false
    )

    private val playerPopupWinAdapter by lazy {
        PlayerPopupWinAdapter()
    }


    init {
        isOutsideTouchable = true
        animationStyle = R.style.pop_anima
        initView()
        initListener()
        initDataListener()
        contentView = dataBinding.root

        playModeMap.apply {
            put(
                XmPlayListControl.PlayMode.PLAY_MODEL_LIST,
                XmPlayListControl.PlayMode.PLAY_MODEL_LIST_LOOP
            )
            put(
                XmPlayListControl.PlayMode.PLAY_MODEL_LIST_LOOP,
                XmPlayListControl.PlayMode.PLAY_MODEL_RANDOM
            )
            put(
                XmPlayListControl.PlayMode.PLAY_MODEL_RANDOM,
                XmPlayListControl.PlayMode.PLAY_MODEL_SINGLE_LOOP
            )
            put(
                XmPlayListControl.PlayMode.PLAY_MODEL_SINGLE_LOOP,
                XmPlayListControl.PlayMode.PLAY_MODEL_LIST
            )
        }
    }


    private fun initView() {
        dataBinding.apply {
            popPlayModeColor =
                ContextCompat.getColor(playerFragment.requireContext(), R.color.sienna)
            popPlayAscColor =
                ContextCompat.getColor(playerFragment.requireContext(), R.color.sienna)

            popRv.layoutManager = if (isAsc) {
                LinearLayoutManager(playerFragment.requireContext(), RecyclerView.VERTICAL, true)
            } else {
                LinearLayoutManager(playerFragment.requireContext(), RecyclerView.VERTICAL, false)
            }

            popRv.adapter = playerPopupWinAdapter

            popRv.scrollToPosition(currentPlayPosition)


        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initListener() {
        dataBinding.apply {
            popPlayModeContainer.setOnClickListener {
                val playMode = playModeMap[currentPlayMode]
                if (playMode != null) {
                    mViewModel?.getPlayModeControl(playMode)
                    currentPlayMode = playMode
                }

            }

            popPlayAscContainer.setOnClickListener {
                isAsc = !isAsc
                if (isAsc) {
                    popPlayAscTv.text = "倒序播放"
                    popPlayAscIv.setImageResource(R.mipmap.player_mode_listplay_click)
                    popRv.layoutManager =
                        LinearLayoutManager(playerFragment.requireContext(),
                            RecyclerView.VERTICAL,
                            true)
                } else {
                    popPlayAscTv.text = "正序播放"
                    popPlayAscIv.setImageResource(R.mipmap.player_playasc_click)
                    popRv.layoutManager =
                        LinearLayoutManager(playerFragment.requireContext(),
                            RecyclerView.VERTICAL,
                            false)
                }
                popRv.scrollToPosition(currentPlayPosition)
            }

            popDismiss.setOnClickListener {
                dismiss()
            }

            playerPopupWinAdapter.setOnPlayerPopWinItemClickListener(object :
                PlayerPopupWinAdapter.OnPlayerPopWinItemClickListener {
                override fun onPopItemClick(track: List<Track>, position: Int) {
                    mViewModel?.apply {
                        setPlayList(track, position)
                        playStart()
                    }

                }

            })

            popPlayModeContainer.setOnTouchListener { v, event ->
                val action = event.action
                when (action) {
                    MotionEvent.ACTION_DOWN -> {
                        dataBinding.popPlayModeColor =
                            ContextCompat.getColor(playerFragment.requireContext(),
                                R.color.secondColor)

                    }

                    MotionEvent.ACTION_MOVE -> {
                        dataBinding.popPlayModeColor =
                            ContextCompat.getColor(playerFragment.requireContext(),
                                R.color.secondColor)

                    }

                    MotionEvent.ACTION_UP -> {
                        dataBinding.popPlayModeColor =
                            ContextCompat.getColor(playerFragment.requireContext(), R.color.sienna)

                    }
                }
                false
            }

            popPlayAscContainer.setOnTouchListener { v, event ->
                val action = event.action
                when (action) {
                    MotionEvent.ACTION_DOWN -> {

                        dataBinding.popPlayAscColor =
                            ContextCompat.getColor(playerFragment.requireContext(),
                                R.color.secondColor)
                    }

                    MotionEvent.ACTION_MOVE -> {

                        dataBinding.popPlayAscColor =
                            ContextCompat.getColor(playerFragment.requireContext(),
                                R.color.secondColor)
                    }

                    MotionEvent.ACTION_UP -> {

                        dataBinding.popPlayAscColor =
                            ContextCompat.getColor(playerFragment.requireContext(), R.color.sienna)
                    }
                }
                false
            }
        }
    }

    private fun initDataListener() {
        mViewModel?.apply {
            dataBinding.apply {
                playModeStatusLiveData.observe(playerFragment) {
                    var stateImage = 0
                    when (it.status) {
                        PlayerViewModel.PlayModeStatus.PLAY_MODEL_LIST -> {
                            stateImage = R.mipmap.player_mode_listplay_click
                        }
                        PlayerViewModel.PlayModeStatus.PLAY_MODEL_LIST_LOOP -> {
                            stateImage = R.mipmap.player_mode_listcycle_click
                        }
                        PlayerViewModel.PlayModeStatus.PLAY_MODEL_RANDOM -> {
                            stateImage = R.mipmap.player_mode_random_click
                        }
                        PlayerViewModel.PlayModeStatus.PLAY_MODEL_SINGLE_LOOP -> {
                            stateImage = R.mipmap.player_mode_cycle_click
                        }
                        else -> {}
                    }
                    popPlayModeTv.text = it.str
                    popPlayModeIv.setImageResource(stateImage)
                }
            }
        }
    }

    fun getTrackList(it: List<Track>) {
        dataBinding.apply {
            popContainer.viewTreeObserver.addOnGlobalLayoutListener {
                if (it.size < 8) {
                    popContainer.layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
                } else {
                    popContainer.layoutParams.height = SizeUtil.dip2px(500f)

                }
            }
        }



        playerPopupWinAdapter.getTrackList(it)
    }

    fun getCurrentPlayPosition(currentPlayPosition: Int) {
        this.currentPlayPosition = currentPlayPosition

        playerPopupWinAdapter.getCurrentPosition(currentPlayPosition)
        dataBinding.popRv.scrollToPosition(currentPlayPosition)
    }
}


