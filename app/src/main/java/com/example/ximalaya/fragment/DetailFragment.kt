package com.example.ximalaya.fragment

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.databinding.BindingAdapter
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.ximalaya.R
import com.example.ximalaya.adapter.DetailAdapter
import com.example.ximalaya.base.BaseApp
import com.example.ximalaya.base.BaseViewModelFragment
import com.example.ximalaya.databinding.FragmentDetailBinding

import com.example.ximalaya.domian.MyAlbumData
import com.example.ximalaya.domian.RecommendAlbumDetail
import com.example.ximalaya.room.AlbumData
import com.example.ximalaya.room.HistoryData
import com.example.ximalaya.utils.Constant
import com.example.ximalaya.utils.StatusBarUtil
import com.example.ximalaya.view.ImageBlur
import com.example.ximalaya.viewmodel.DetailViewModel
import com.example.ximalaya.viewmodel.HistoryViewModel
import com.example.ximalaya.viewmodel.PlayerViewModel
import com.example.ximalaya.viewmodel.SubscribeViewModel
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.ximalaya.ting.android.opensdk.model.track.Track
import java.io.Serializable

class DetailFragment : BaseViewModelFragment<DetailViewModel>() {
    private lateinit var currentAlbum: MyAlbumData

    private lateinit var dataBinding: FragmentDetailBinding


    companion object {
        @JvmStatic
        @BindingAdapter("backgroundCover")
        fun setDetailBackground(imageView: ImageView, background: Any?) {
            Glide.with(imageView).asBitmap().load(background)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?,
                    ) {
                        imageView.setImageBitmap(resource)
                        imageView.drawable?.also {
                            ImageBlur.makeBlur(
                                imageView,
                                BaseApp.mBaseContext
                            )
                        }
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {

                    }
                })
        }

        @JvmStatic
        @BindingAdapter("detailCover")
        fun setDetailCover(imageView: ImageView, cover: Any?) {
            Glide.with(imageView).load(cover)
                .apply(RequestOptions.bitmapTransform(RoundedCorners(20))).into(imageView)
        }

        @JvmStatic
        @BindingAdapter("mid_cover_color")
        fun setMidCoverColor(imageView: ImageView, color: Int) {
            imageView.setColorFilter(color)
        }
    }

    private val detailAdapter by lazy {
        DetailAdapter()
    }

    private val subscribeViewModel by lazy {
        ViewModelProvider(this)[SubscribeViewModel::class.java]
    }


    private val subAlbumTitleList = mutableListOf<String>()


    private val playViewModel by lazy {
        ViewModelProvider(requireActivity())[PlayerViewModel::class.java]
    }

    private val detailViewModel by lazy {
        ViewModelProvider(requireActivity())[DetailViewModel::class.java]
    }

    private val historyViewModel by lazy {
        ViewModelProvider(requireActivity())[HistoryViewModel::class.java]
    }
    private val historyList = mutableListOf<HistoryData>()

    private val historyTitleList = mutableListOf<String>()

    override fun getSuccessFragmentView(rootFrameLayout: FrameLayout): View {
        dataBinding = FragmentDetailBinding.inflate(layoutInflater)

        return dataBinding.root
    }


    override fun getViewModelClass() = DetailViewModel::class.java


    private var albumId = 0

    private val currentTrackList = mutableListOf<Track>()

    private var currentTrackTitle = ""

    private lateinit var currentTrack: Track

    override fun initView() {
        requireActivity().window.apply {
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            statusBarColor = context.getColor(R.color.transparent)
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE;

        }

        val argument = arguments
        argument?.let { currentArg ->
            albumId = currentArg.getInt(Constant.PUT_ALBUM_ID, 0)

            detailViewModel.getDetailData(albumId)
            currentArg.getParcelable<MyAlbumData>(Constant.PUT_ALBUM)?.let {
                currentAlbum = it
                detailViewModel.getCurrentAlbum(currentAlbum)
            }

        }

        dataBinding.apply {
            detailRv.layoutManager = LinearLayoutManager(requireContext())
            detailRv.adapter = detailAdapter

            detailSmartRefresh.setEnableRefresh(false)
            detailSmartRefresh.setEnableLoadMore(true)
            detailSmartRefresh.setRefreshFooter(ClassicsFooter(requireContext()))
            detailSmartRefresh.isNestedScrollingEnabled = false

            midCoverColor = requireContext().getColor(R.color.sienna)
        }

        playViewModel.isPlaying()


    }


    override fun reload() {
        super.reload()
        if (albumId != 0) {
            detailViewModel.getDetailData(albumId)
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun initListener() {
        dataBinding.apply {
            detailContainer.viewTreeObserver.addOnGlobalLayoutListener(object :
                ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    val contentLayoutParams = detailContentView.layoutParams
                    contentLayoutParams.height = detailContainer.measuredHeight
                    detailContentView.layoutParams = contentLayoutParams


                    detailNv.getHeadViewHeight(
                        detailHeadView.measuredHeight
                                - StatusBarUtil.getStatusBarHeightCompat(
                            requireContext()
                        )
                    )

                    detailNv.getActivity(requireActivity())

                    if (contentLayoutParams.height != 0) {
                        detailContainer.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    }
                }

            })

            detailSmartRefresh.setOnLoadMoreListener {
                detailViewModel.getDetailDataLoadMoreData()
                it.finishLoadMore()
            }

            detailSubscribeBt.setOnClickListener {
                currentAlbum.apply {
                    val albumData = AlbumData(albumTitle,
                        albumId,
                        coverUrlMiddle,
                        albumIntro,
                        playCount.toString(),
                        includeTrackCount.toString())

                    if (subAlbumTitleList.size == 0) {
                        subscribeViewModel.insertAlbum(albumData)
                    } else {
                        if (subAlbumTitleList.contains(albumData.albumTitle)) {
                            detailSubscribeBt.text = "订阅+"
                            subAlbumTitleList.clear()
                            subscribeViewModel.deleteAlbum(albumData.albumTitle)
                        } else {
                            subscribeViewModel.insertAlbum(albumData)
                            detailSubscribeBt.text = "取消订阅-"

                        }
                    }
                }
            }


            detailMidPlayContainer.setOnTouchListener { v, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {

                        midCoverColor = requireContext().getColor(R.color.secondColor)
                    }

                    MotionEvent.ACTION_MOVE -> {
                        midCoverColor = requireContext().getColor(R.color.secondColor)
                    }

                    MotionEvent.ACTION_UP -> {
                        midCoverColor = requireContext().getColor(R.color.sienna)
                    }
                }
                false
            }

            detailMidPlayContainer.setOnClickListener {
                playViewModel.apply {

                    if (!hasPlayList()) {
                        setPlayList(currentTrackList, 0)

                        playStart()
                        detailMidPlayTv.text = currentTrackTitle

                        if (this@DetailFragment::currentTrack.isInitialized) {
                            currentTrack.apply {
                                if (historyList.isEmpty()) {
                                    val historyData = HistoryData(trackTitle,
                                        coverUrlMiddle,
                                        kind,
                                        playCount,
                                        dataId,
                                        updatedAt,
                                        announcer.nickname)
                                    historyViewModel.insertHistory(historyData)
                                } else {
                                    val historyData = HistoryData(trackTitle,
                                        coverUrlMiddle,
                                        kind,
                                        playCount,
                                        dataId,
                                        updatedAt,
                                        announcer.nickname)

                                    if (!historyTitleList.contains(historyData.title)) {
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
                    } else {
                        Log.d("Detail", "播放状态${getPlaying()}")
                        if (getPlaying()) {
                            detailMidPlayTv.text = "点击播放"
                            detailMidPlayIv.setImageResource(R.mipmap.player_button)
                            playPause()
                        } else {
                            playStart()
                            detailMidPlayTv.text = currentTrackTitle
                            detailMidPlayIv.setImageResource(R.mipmap.player_paus_click)
                        }
                    }


                }

            }
        }

        detailAdapter.setOnDetailClickListener(object : DetailAdapter.OnDetailClickListener {
            override fun onDetailClick(track: List<Track>, position: Int) {
                val bundle = Bundle()

                bundle.putInt(Constant.DETAIL_POSITION_DATA, position)
                bundle.putSerializable(Constant.DETAIL_LIST_DATA, track as Serializable)
                findNavController().navigate(R.id.player_fragment, bundle)
            }
        })


    }

    var loadMoreListSize = 0


    override fun initDataListener() {

        detailViewModel.trackLiveList.observe(this) {

            val detailBackground = it.coverUrlLarge.ifBlank {
                R.mipmap.xiaogong
            }

            val detailCover = it.coverUrlLarge.ifBlank {
                R.mipmap.paimeng
            }

            dataBinding.detailData =
                RecommendAlbumDetail(
                    detailBackground,
                    detailCover,
                    it.albumTitle,
                    it.tracks[0].announcer.nickname
                )
            currentTrackList.clear()

            currentTrackList.addAll(it.tracks)

            detailAdapter.setData(it.tracks)


        }

        detailViewModel.trackLoadMoreLiveList.observe(this) {
            loadMoreListSize = it.tracks.size
            detailAdapter.setLoadMoreData(it.tracks)
        }

        detailViewModel.trackLiveState.observe(this) {
            when (it) {
                DetailViewModel.DetailLoadStatus.LOADING -> {
                    dispatchViewState(FragmentStatus.LOADING)
                }
                DetailViewModel.DetailLoadStatus.SUCCESS -> {
                    dispatchViewState(FragmentStatus.SUCCESS)
                }
                DetailViewModel.DetailLoadStatus.ERROR -> {
                    dispatchViewState(FragmentStatus.ERROR)
                }
                DetailViewModel.DetailLoadStatus.EMPTY -> {
                    dispatchViewState(FragmentStatus.EMPTY)
                }
                DetailViewModel.DetailLoadStatus.LOAD_MORE_SUCCESS -> {
                    Toast.makeText(
                        requireContext(),
                        "成功加载${loadMoreListSize}条数据",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                DetailViewModel.DetailLoadStatus.LOAD_MORE_EMPTY -> {
                    Toast.makeText(requireContext(), "没有更多数据了", Toast.LENGTH_SHORT).show()
                }
                DetailViewModel.DetailLoadStatus.LOAD_MORE_ERROR -> {
                    Toast.makeText(requireContext(), "加载失败", Toast.LENGTH_SHORT).show()
                }
                else -> {}
            }
        }

        subscribeViewModel.queryAlbums().observe(this) {
            subAlbumTitleList.clear()
            it.forEach { alubm ->
                subAlbumTitleList.add(alubm.albumTitle)
            }

            if (subAlbumTitleList.contains(currentAlbum.albumTitle)) {
                dataBinding.detailSubscribeBt.text = "取消订阅-"
            } else {
                dataBinding.detailSubscribeBt.text = "订阅+"
            }
        }



        playViewModel.playPositionAndTrackLiveData.observe(this) {
            currentTrackTitle = it.track.trackTitle
            currentTrack = it.track
            dataBinding.apply {
                playViewModel.playState.observe(this@DetailFragment) { playState ->
                    Log.d("Detail", "回调状态${playState}")
                    if (playState) {
                        detailMidPlayTv.text = currentTrackTitle
                        detailMidPlayIv.setImageResource(R.mipmap.player_paus_click)

                    } else {
                        detailMidPlayTv.text = "点击播放"
                        detailMidPlayIv.setImageResource(R.mipmap.player_button)
                    }
                }
            }

        }

        historyViewModel.queryHistoryList().observe(viewLifecycleOwner) {
            historyList.clear()
            historyTitleList.clear()
            historyList.addAll(it)
            it.forEach { his ->
                historyTitleList.add(his.title)
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        if (detailViewModel.isFirstLoad == false) {
            detailViewModel.isFirstLoad = true
        }
    }


}


