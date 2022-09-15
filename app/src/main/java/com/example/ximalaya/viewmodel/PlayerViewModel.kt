package com.example.ximalaya.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.ximalaya.base.BaseApp
import com.example.ximalaya.domian.PlayerCurrentTrack
import com.example.ximalaya.domian.PlayerPlayModeStatus
import com.example.ximalaya.domian.PlayerTrackDuration
import com.ximalaya.ting.android.opensdk.model.PlayableModel
import com.ximalaya.ting.android.opensdk.model.advertis.Advertis
import com.ximalaya.ting.android.opensdk.model.advertis.AdvertisList
import com.ximalaya.ting.android.opensdk.model.track.Track
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager
import com.ximalaya.ting.android.opensdk.player.advertis.IXmAdsStatusListener
import com.ximalaya.ting.android.opensdk.player.advertis.MiniPlayer
import com.ximalaya.ting.android.opensdk.player.constants.PlayerConstants.STATE_PREPARED
import com.ximalaya.ting.android.opensdk.player.service.IXmPlayerStatusListener
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl
import com.ximalaya.ting.android.opensdk.player.service.XmPlayerException
import java.lang.Exception

class PlayerViewModel : ViewModel(), IXmPlayerStatusListener, IXmAdsStatusListener {
    private val xmPlayerManager by lazy {
        XmPlayerManager.getInstance(BaseApp.mBaseContext)
    }



    val playListLiveData = MutableLiveData<List<Track>>()
    val playPositionAndTrackLiveData = MutableLiveData<PlayerCurrentTrack>()
    val playState = MutableLiveData<Boolean>()
    val playProcessLiveData = MutableLiveData<PlayerTrackDuration>()
    val playStatusIsOkLiveData = MutableLiveData<Boolean>()

    enum class PlayModeStatus {
        PLAY_MODEL_LIST, PLAY_MODEL_LIST_LOOP, PLAY_MODEL_RANDOM, PLAY_MODEL_SINGLE_LOOP
    }

    private var hasList = false
    val playModeStatusLiveData = MutableLiveData<PlayerPlayModeStatus>()
    fun setPlayList(trackList: List<Track>, startIndex: Int) {
        //   xmPlayerManager.clearPlayCache()
        //xmPlayerManager.resetPlayList()

        hasList = true

        xmPlayerManager.setPlayList(trackList, startIndex)


        Log.d("PlayModeStatus",trackList[0].toString())
        playListLiveData.value = xmPlayerManager.playList
        xmPlayerManager.addPlayerStatusListener(this)
        xmPlayerManager.addAdsStatusListener(this)



    }

    fun hasPlayList() = hasList


    fun playStart() {
        if (hasList) {
            xmPlayerManager.play()
            playState.value = true

        }

    }

    fun playPause() {
        xmPlayerManager.pause()
        playState.value = false
    }

    fun isPlaying() {
        playState.value = xmPlayerManager.isPlaying
    }


    fun playPre() {
        if (hasList) {
            xmPlayerManager.playPre()

        }

    }

    fun playToIndex(index: Int) {
        if (hasList) {
            xmPlayerManager.play(index)

        }

    }

    fun getPlaying() = xmPlayerManager.isPlaying

    fun playNext() {
        if (hasList) {
            xmPlayerManager.playNext()


        }

    }

    fun addPositionAndTrack() {
        playPositionAndTrackLiveData.postValue(PlayerCurrentTrack(
            xmPlayerManager.currentIndex,
            xmPlayerManager.getTrack(xmPlayerManager.currentIndex)
        ))

    }

    fun seekTo(seekBarProgress: Int) {
        if (hasList) {
            xmPlayerManager.seekTo(seekBarProgress)
        }

    }

    fun getPlayModeControl(mode: XmPlayListControl.PlayMode) {
        var str = ""
        when (mode) {
            XmPlayListControl.PlayMode.PLAY_MODEL_LIST -> {
                xmPlayerManager.playMode = XmPlayListControl.PlayMode.PLAY_MODEL_LIST
                str = "列表播放"

                playModeStatusLiveData.postValue(PlayerPlayModeStatus(PlayModeStatus.PLAY_MODEL_LIST,
                    str))
            }
            XmPlayListControl.PlayMode.PLAY_MODEL_LIST_LOOP -> {
                xmPlayerManager.playMode = XmPlayListControl.PlayMode.PLAY_MODEL_LIST_LOOP
                str = "列表循环"

                playModeStatusLiveData.postValue(PlayerPlayModeStatus(PlayModeStatus.PLAY_MODEL_LIST_LOOP,
                    str))
            }

            XmPlayListControl.PlayMode.PLAY_MODEL_RANDOM -> {
                xmPlayerManager.playMode = XmPlayListControl.PlayMode.PLAY_MODEL_RANDOM
                str = "随机播放"

                playModeStatusLiveData.postValue(PlayerPlayModeStatus(PlayModeStatus.PLAY_MODEL_RANDOM,
                    str))
            }

            XmPlayListControl.PlayMode.PLAY_MODEL_SINGLE_LOOP -> {
                xmPlayerManager.playMode = XmPlayListControl.PlayMode.PLAY_MODEL_SINGLE_LOOP
                str = "单曲循环"

                playModeStatusLiveData.postValue(PlayerPlayModeStatus(PlayModeStatus.PLAY_MODEL_SINGLE_LOOP,
                    str))
            }

            else -> {}
        }

        // Log.d("当前播放状态", str)
    }


    val adsStatusLiveData = MutableLiveData<Boolean>()


    override fun onPlayStart() {
       // addPositionAndTrack()
        Log.d("registerPlayStates", "onPlayStart")

    }

    override fun onPlayPause() {
        // Log.d("registerPlayStates", "onPlayPause")

    }

    override fun onPlayStop() {
        // Log.d("registerPlayStates", "onPlayStop")
    }

    override fun onSoundPlayComplete() {
        Log.d("registerPlayStates", "onSoundPlayComplete")

    }

    override fun onSoundPrepared() {
        Log.d("registerPlayStates", "onSoundPrepared")
        if (xmPlayerManager.playerStatus == STATE_PREPARED) {
            playStatusIsOkLiveData.postValue(true)
            xmPlayerManager.play()
            xmPlayerManager.seekTo(0)

            addPositionAndTrack()
        }

    }

    override fun onSoundSwitch(p0: PlayableModel?, p1: PlayableModel?) {
        Log.d("registerPlayStates", "onSoundSwitch")


    }

    override fun onBufferingStart() {
        Log.d("registerPlayStates", "onBufferingStart")
    }

    override fun onBufferingStop() {
        Log.d("registerPlayStates", "onBufferingStop")
    }

    override fun onBufferProgress(p0: Int) {
        Log.d("registerPlayStates", "onBufferProgress")
    }


    override fun onPlayProgress(p0: Int, p1: Int) {
        Log.d("registerPlayStates", "currentTime${p0} duration${p1}")
        playProcessLiveData.value = (PlayerTrackDuration(p0, p1))
    }

    override fun onError(p0: XmPlayerException?): Boolean {
        Log.d("registerPlayStates", "onError-->${p0.toString()}")
        return true
    }

    override fun onStartGetAdsInfo() {
        adsStatusLiveData.value = true
        Log.d("registerAdsStates", "onStartGetAdsInfo")
    }

    override fun onGetAdsInfo(p0: AdvertisList?) {
        Log.d("registerAdsStates", "onGetAdsInfo")
    }

    override fun onAdsStartBuffering() {
        Log.d("registerAdsStates", "onAdsStartBuffering")
    }

    override fun onAdsStopBuffering() {
        Log.d("registerAdsStates", "onAdsStopBuffering")
    }

    override fun onStartPlayAds(p0: Advertis?, p1: Int) {

        Log.d("registerAdsStates", "onStartPlayAds")
    }

    override fun onCompletePlayAds() {
        Log.d("registerAdsStates", "onCompletePlayAds")
    }

    override fun onError(p0: Int, p1: Int) {
        Log.d("registerAdsStates", "onError code:${p0} msg:${p1}")
    }


    fun unRegisterPlayStates() {
        xmPlayerManager.removePlayerStatusListener(this)
    }

    fun unRegisterAdsStates() {
        xmPlayerManager.removeAdsStatusListener(this)
    }


}