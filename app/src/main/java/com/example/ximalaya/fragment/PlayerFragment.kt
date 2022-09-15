package com.example.ximalaya.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.example.ximalaya.R
import com.example.ximalaya.adapter.PlayPageAdapter
import com.example.ximalaya.base.BaseFragment

import com.example.ximalaya.databinding.FragmentPlayerBinding
import com.example.ximalaya.room.HistoryData
import com.example.ximalaya.utils.Constant
import com.example.ximalaya.utils.ToastUtil
import com.example.ximalaya.view.PlayerListPopupWin
import com.example.ximalaya.viewmodel.HistoryViewModel
import com.example.ximalaya.viewmodel.PlayerViewModel
import com.ximalaya.ting.android.opensdk.model.track.Track
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl
import java.text.SimpleDateFormat

class PlayerFragment : BaseFragment() {
    companion object {
        @JvmStatic
        @BindingAdapter("playMode_color")
        fun ImageView.setPlayModeColor(@ColorInt color: Int) {
            Log.d("color", "颜色${color}")
            setColorFilter(color)
        }

        @JvmStatic
        @BindingAdapter("playPre_color")
        fun ImageView.setPlayPreColor(@ColorInt color: Int) {
            setColorFilter(color)
        }

        @JvmStatic
        @BindingAdapter("playOrPause_color")
        fun ImageView.setPlayOrPauseColor(@ColorInt color: Int) {
            setColorFilter(color)
        }

        @JvmStatic
        @BindingAdapter("playNext_color")
        fun ImageView.setPlayNextColor(@ColorInt color: Int) {
            setColorFilter(color)
        }

        @JvmStatic
        @BindingAdapter("playList_color")
        fun ImageView.setPlayListColor(@ColorInt color: Int) {
            setColorFilter(color)
        }
    }

    private lateinit var dataBinding: FragmentPlayerBinding

    private var isPlaying = false

    private val playPageAdapter by lazy {
        PlayPageAdapter()
    }

    private var currentPlayPosition = 0

    private lateinit var currentTrack: Track

    @SuppressLint("SimpleDateFormat")
    private val minuteDateFormat = SimpleDateFormat("mm:ss")

    @SuppressLint("SimpleDateFormat")
    private val hourDateFormat = SimpleDateFormat("HH:mm:ss")

    private var isPlayerOk = false

    private var isFromUser = false

    private var seekBarProgress = 0

    private var playModeStatusToastText = ""

    private val playModeMap = mutableMapOf<XmPlayListControl.PlayMode, XmPlayListControl.PlayMode>()

    private var currentPlayMode = XmPlayListControl.PlayMode.PLAY_MODEL_LIST

    private lateinit var playModelSharedPreferences: SharedPreferences

    private val historyList = mutableListOf<HistoryData>()

    private val historyTitleList = mutableListOf<String>()

    private val playViewModel by lazy {
        ViewModelProvider(requireActivity())[PlayerViewModel::class.java]
    }

    private val playerPopupWindow by lazy {
        PlayerListPopupWin(this, playViewModel)
    }

    private val historyViewModel by lazy {
        ViewModelProvider(requireActivity())[HistoryViewModel::class.java]
    }

    override fun getSuccessFragmentView(rootFrameLayout: FrameLayout): View {
        dataBinding = DataBindingUtil.inflate(
            LayoutInflater.from(requireContext()),
            R.layout.fragment_player,
            rootFrameLayout,
            false
        )

        return dataBinding.root
    }


    override fun initView() {
        dispatchViewState(FragmentStatus.SUCCESS)

        playModelSharedPreferences =
            requireActivity().getSharedPreferences(Constant.PLAY_MODE_SP_KEY, Context.MODE_PRIVATE)

        val argument = arguments

        val detailListData = argument!!.getSerializable(Constant.DETAIL_LIST_DATA) as List<*>
        val detailPositionData = argument.getInt(Constant.DETAIL_POSITION_DATA, -1)




        playViewModel.apply {

            setPlayList(detailListData as List<Track>, detailPositionData)

            isPlaying()

            playStart()



            dataBinding.apply {
                playModeColor = ContextCompat.getColor(requireContext(), R.color.sienna)
                playPreColor = ContextCompat.getColor(requireContext(), R.color.sienna)
                playOrPauseColor = ContextCompat.getColor(requireContext(), R.color.sienna)
                playNextColor = ContextCompat.getColor(requireContext(), R.color.sienna)
                playListColor = ContextCompat.getColor(requireContext(), R.color.sienna)

                playerViewpage.adapter = playPageAdapter

            }

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

            val playStateSP = playModelSharedPreferences.getString(Constant.PLAY_MODE_STATE_KEY, "")

            if (playStateSP!!.isNotBlank()) {

                var spPlayMode: XmPlayListControl.PlayMode =
                    XmPlayListControl.PlayMode.PLAY_MODEL_LIST
                when (playStateSP) {
                    "列表播放" -> {

                        spPlayMode = XmPlayListControl.PlayMode.PLAY_MODEL_LIST

                    }
                    "列表循环" -> {

                        spPlayMode = XmPlayListControl.PlayMode.PLAY_MODEL_LIST_LOOP

                    }

                    "随机播放" -> {

                        spPlayMode = XmPlayListControl.PlayMode.PLAY_MODEL_RANDOM

                    }

                    "单曲循环" -> {

                        spPlayMode = XmPlayListControl.PlayMode.PLAY_MODEL_SINGLE_LOOP

                    }
                }
                getPlayModeControl(spPlayMode)
                currentPlayMode = spPlayMode

            }
        }
    }


    private fun setWindowAlpha(currentAlpha: Float) {
        val window = requireActivity().window
        val attributes = window.attributes
        attributes.alpha = currentAlpha
        window.attributes = attributes
    }

    @SuppressLint("ClickableViewAccessibility", "CommitPrefEdits")
    override fun initListener() {
        super.initListener()
        dataBinding.apply {
            playViewModel?.apply {

                playerPlayModeIv.setOnClickListener {
                    val playMode = playModeMap[currentPlayMode]

                    if (playMode != null) {
                        currentPlayMode = playMode
                    }

                    getPlayModeControl(currentPlayMode)


                }

                playerPlayPreIv.setOnClickListener {
                    playPre()
                    playerViewpage.setCurrentItem(currentPlayPosition, true)
                }

                playerPlayStartOrPauseIv.setOnClickListener {
                    if (isPlaying) {
                        playPause()
                        playerPlayStartOrPauseIv.setImageResource(R.mipmap.player_button)
                    } else {
                        playStart()
                        playerPlayStartOrPauseIv.setImageResource(R.mipmap.player_paus_click)
                    }


                }

                playerPlayNextIv.setOnClickListener {
                    playNext()

                    playerViewpage.setCurrentItem(currentPlayPosition, true)

                }

                playerPlayListIv.setOnClickListener {
                    playerPopupWindow.showAtLocation(it, Gravity.BOTTOM, 0, 0)
                    setWindowAlpha(0.8f)
                    playerPopupWindow.getCurrentPlayPosition(currentPlayPosition)
                }

                playerPopupWindow.setOnDismissListener {
                    setWindowAlpha(1.0f)
                }
                playerViewpage.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                    override fun onPageScrolled(
                        position: Int,
                        positionOffset: Float,
                        positionOffsetPixels: Int,
                    ) {

                    }

                    override fun onPageSelected(position: Int) {
                        playToIndex(position)

                    }

                    override fun onPageScrollStateChanged(state: Int) {

                    }

                })
            }

            playerPlayModeIv.setOnTouchListener(PlayerButtonTouchEvent(0))
            playerPlayPreIv.setOnTouchListener(PlayerButtonTouchEvent(1))
            playerPlayStartOrPauseIv.setOnTouchListener(PlayerButtonTouchEvent(2))
            playerPlayNextIv.setOnTouchListener(PlayerButtonTouchEvent(3))
            playerPlayListIv.setOnTouchListener(PlayerButtonTouchEvent(4))

            playerPlaySeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean,
                ) {
                    if (isFromUser) {
                        seekBarProgress = progress
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                    isFromUser = true
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    isFromUser = false
                    playViewModel?.seekTo(seekBarProgress)
                    if (seekBarProgress == seekBar?.max) {
                        playViewModel?.playNext()
                    }
                }
            })
        }

    }


    @SuppressLint("CommitPrefEdits")
    override fun initDataListener() {
        super.initDataListener()
        playViewModel.apply {
            dataBinding.apply {

                playListLiveData.observe(this@PlayerFragment) {


                    if (it != null) {
                        playPageAdapter.getData(it)
                        playerPopupWindow.getTrackList(it)

                    } else {
                        // Log.d("viewModel传递数据测试", "it不存在")
                    }
                }

                playState.observe(this@PlayerFragment) { isPlaying ->
                    this@PlayerFragment.isPlaying = isPlaying
                    if (!isPlaying) {
                        playerPlayStartOrPauseIv.setImageResource(R.mipmap.player_button)
                    } else {
                        playerPlayStartOrPauseIv.setImageResource(R.mipmap.player_paus_click)
                    }
                }



                adsStatusLiveData.observe(this@PlayerFragment) {
                    if (it == true) {
                        Toast.makeText(requireContext(), "正在播放广告，请稍等", Toast.LENGTH_SHORT).show()
                    }
                }



                playStatusIsOkLiveData.observe(this@PlayerFragment) {
                    this@PlayerFragment.isPlayerOk = it
                    // Log.d("isPlayerOk",isPlayerOk.toString())
                }



                playProcessLiveData.observe(this@PlayerFragment) {
                    val trackDuration = if (it.duration >= 3600000) {
                        hourDateFormat.format(it.duration)
                    } else {
                        minuteDateFormat.format(it.duration)
                    }
                    playerPlayStartTimeTv.text = minuteDateFormat.format(it.startTime)
                    playerPlayEndTimeTv.text = trackDuration

                    playerPlaySeekBar.progress = it.startTime
                    playerPlaySeekBar.max = it.duration

                }

                playModeStatusLiveData.observe(this@PlayerFragment) {
                    var statusImage = 0
                    when (it.status) {
                        PlayerViewModel.PlayModeStatus.PLAY_MODEL_LIST -> {
                            playModeStatusToastText = it.str
                            statusImage = R.mipmap.player_mode_listplay_click
                        }
                        PlayerViewModel.PlayModeStatus.PLAY_MODEL_LIST_LOOP -> {
                            playModeStatusToastText = it.str
                            statusImage = R.mipmap.player_mode_listcycle_click
                        }
                        PlayerViewModel.PlayModeStatus.PLAY_MODEL_RANDOM -> {
                            playModeStatusToastText = it.str
                            statusImage = R.mipmap.player_mode_random_click
                        }
                        PlayerViewModel.PlayModeStatus.PLAY_MODEL_SINGLE_LOOP -> {
                            playModeStatusToastText = it.str
                            statusImage = R.mipmap.player_mode_cycle_click
                        }
                        else -> {}
                    }
                    ToastUtil.setToast(playModeStatusToastText)
                    val edit = playModelSharedPreferences.edit()
                    edit.putString(Constant.PLAY_MODE_STATE_KEY, playModeStatusToastText)
                    edit.apply()
                    playerPlayModeIv.setImageResource(statusImage)
                }
            }
        }



        playViewModel.playPositionAndTrackLiveData.observe(this@PlayerFragment) {
            currentPlayPosition = it.position
            currentTrack = it.track
            dataBinding.playerViewpage.currentItem = it.position
            dataBinding.playerTitleTv.text = currentTrack.trackTitle

            playerPopupWindow.getCurrentPlayPosition(currentPlayPosition)
            historyViewModel.queryHistoryList().observe(viewLifecycleOwner) {
                historyList.clear()
                historyTitleList.clear()

                it.forEach { his ->
                    historyTitleList.add(his.title)
                }

                historyList.addAll(it)

                currentTrack.apply {

                    val nickname = if (announcer.nickname == null){
                        "6"
                    }else{
                        announcer.nickname
                    }
                    val historyData = HistoryData(trackTitle,
                        coverUrlMiddle,
                        kind,
                        playCount,
                        dataId,
                        updatedAt,
                        nickname)


                    historyTitleList.forEach {
                        Log.d("Player", it)
                    }

                    if (!historyTitleList.contains(historyData.title)) {
                        Log.d("Player", "没有标题")
                        historyViewModel.insertHistory(historyData)
                    }

                    historyList.reverse()

                    if (historyList.size >= 100) {
                        historyList.removeAt(historyList.size - 1)
                        historyTitleList.removeAt(historyTitleList.size - 1)
                        historyViewModel.deleteLastHistory(historyList.size - 1)
                    }
                }
            }
        }
    }

    inner class PlayerButtonTouchEvent(private var playColor: Int) : View.OnTouchListener {
        override fun onTouch(v: View?, event: MotionEvent?): Boolean {
            dataBinding.apply {
                if (event != null) {
                    val action = event.action
                    when (action) {
                        MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                            when (playColor) {
                                0 -> playModeColor =
                                    ContextCompat.getColor(requireContext(), R.color.secondColor)

                                1 -> playPreColor =
                                    ContextCompat.getColor(requireContext(), R.color.secondColor)

                                2 -> playOrPauseColor =
                                    ContextCompat.getColor(requireContext(), R.color.secondColor)

                                3 -> playNextColor =
                                    ContextCompat.getColor(requireContext(), R.color.secondColor)

                                4 -> playListColor =
                                    ContextCompat.getColor(requireContext(), R.color.secondColor)
                            }

                        }


                        MotionEvent.ACTION_UP -> {
                            when (playColor) {
                                0 -> playModeColor =
                                    ContextCompat.getColor(requireContext(), R.color.sienna)

                                1 -> playPreColor =
                                    ContextCompat.getColor(requireContext(), R.color.sienna)

                                2 -> playOrPauseColor =
                                    ContextCompat.getColor(requireContext(), R.color.sienna)

                                3 -> playNextColor =
                                    ContextCompat.getColor(requireContext(), R.color.sienna)

                                4 -> playListColor =
                                    ContextCompat.getColor(requireContext(), R.color.sienna)
                            }

                        }

                        else -> {
                            when (playColor) {
                                0 -> playModeColor =
                                    ContextCompat.getColor(requireContext(), R.color.sienna)

                                1 -> playPreColor =
                                    ContextCompat.getColor(requireContext(), R.color.sienna)

                                2 -> playOrPauseColor =
                                    ContextCompat.getColor(requireContext(), R.color.sienna)

                                3 -> playNextColor =
                                    ContextCompat.getColor(requireContext(), R.color.sienna)

                                4 -> playListColor =
                                    ContextCompat.getColor(requireContext(), R.color.sienna)
                            }

                        }
                    }
                }
            }
            return false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        playerPopupWindow.dismiss()
    }

    override fun onDestroy() {
        super.onDestroy()
        playViewModel?.unRegisterPlayStates()
        playViewModel?.unRegisterAdsStates()
    }


}