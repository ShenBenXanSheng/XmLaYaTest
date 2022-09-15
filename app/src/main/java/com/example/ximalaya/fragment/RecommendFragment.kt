package com.example.ximalaya.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.ximalaya.R
import com.example.ximalaya.adapter.AlbumAdapter
import com.example.ximalaya.base.BaseViewModelFragment
import com.example.ximalaya.databinding.FragmentRecommendBinding
import com.example.ximalaya.domian.MyAlbumData
import com.example.ximalaya.livedata.LiveDataStatus
import com.example.ximalaya.room.AlbumData
import com.example.ximalaya.utils.Constant
import com.example.ximalaya.view.DialogControl
import com.example.ximalaya.viewmodel.DetailViewModel
import com.example.ximalaya.viewmodel.PlayerViewModel
import com.example.ximalaya.viewmodel.RecommendViewModel
import com.example.ximalaya.viewmodel.SubscribeViewModel
import com.scwang.smart.refresh.header.MaterialHeader
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener

class RecommendFragment : BaseViewModelFragment<RecommendViewModel>() {
    companion object {
        @JvmStatic
        @BindingAdapter("recommend_pbc")
        fun setRecommendPlayerButtonColor(imageView: ImageView, color: Int) {
            imageView.setColorFilter(color)
        }
    }

    private lateinit var dataBinding: FragmentRecommendBinding

    private val albumAdapter by lazy {
        AlbumAdapter()
    }

    private val subscribeViewModel by lazy {
        ViewModelProvider(this)[SubscribeViewModel::class.java]
    }

    private val xmDialog by lazy {
        DialogControl(requireContext())
    }

    override fun getSuccessFragmentView(rootFrameLayout: FrameLayout): View {
        dataBinding = FragmentRecommendBinding.inflate(layoutInflater)
        return dataBinding.root
    }


    override fun getViewModelClass(): Class<RecommendViewModel> {
        return RecommendViewModel::class.java
    }

    private val playViewModel by lazy {
        ViewModelProvider(requireActivity())[PlayerViewModel::class.java]
    }

    private val detailViewModel by lazy {
        ViewModelProvider(requireActivity())[DetailViewModel::class.java]
    }

    private lateinit var fromDetailAlbum: MyAlbumData
    override fun initView() {
        dataBinding.apply {
            recommendRv.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = albumAdapter
            }

            recommendSmartRefresh.apply {
                setRefreshHeader(MaterialHeader(requireContext()))
                setEnableLoadMore(false)
                setEnableHeaderTranslationContent(true)
            }

            recommendPlayerButtonColor =
                ContextCompat.getColor(requireContext(), R.color.sienna)

            recommendPlayerTitle.isSelected = true
        }
    }

    override fun reload() {
        mViewModel?.getRecommendData()
    }

    private val dbTitleList = mutableListOf<String>()
    val myAlbumDataList = mutableListOf<MyAlbumData>()


    override fun initDataListener() {
        super.initDataListener()
        dataBinding.apply {


            mViewModel?.apply {

                albumLiveList.observe(viewLifecycleOwner) {
                    myAlbumDataList.clear()
                    it.forEach { album ->
                        val myAlbumData = MyAlbumData(album.albumTitle,
                            album.coverUrlMiddle,
                            album.albumIntro,
                            album.playCount,
                            album.includeTrackCount)
                        myAlbumData.id = album.id.toString()
                        myAlbumDataList.add(myAlbumData)
                    }
                    albumAdapter.setData(myAlbumDataList)
                }

                albumLiveList.observeStatus(viewLifecycleOwner) {
                    when (it) {
                        LiveDataStatus.RESET -> {
                            mViewModel?.getRecommendData()
                        }
                        else -> {}
                    }
                }

                recommendStatusLive.observe(this@RecommendFragment) {
                    when (it) {
                        RecommendViewModel.RecommendStatus.LOADING -> {
                            dispatchViewState(FragmentStatus.LOADING)
                        }

                        RecommendViewModel.RecommendStatus.SUCCESS -> {
                            dispatchViewState(FragmentStatus.SUCCESS)
                        }

                        RecommendViewModel.RecommendStatus.EMPTY -> {
                            dispatchViewState(FragmentStatus.EMPTY)
                        }

                        RecommendViewModel.RecommendStatus.ERROR -> {
                            dispatchViewState(FragmentStatus.ERROR)
                        }
                        RecommendViewModel.RecommendStatus.REFRESH_SUCCESS -> {
                            Toast.makeText(requireContext(), "刷新成功", Toast.LENGTH_SHORT).show()
                        }
                        RecommendViewModel.RecommendStatus.REFRESH_ERROR -> {
                            Toast.makeText(requireContext(), "刷新失败", Toast.LENGTH_SHORT).show()
                        }
                        else -> {}
                    }
                }
            }

            subscribeViewModel.queryAlbums().observe(this@RecommendFragment) {
                dbTitleList.clear()
                it.forEach {
                    dbTitleList.add(it.albumTitle)
                }
            }

            playViewModel.playPositionAndTrackLiveData.observe(this@RecommendFragment) {

                recommendPlayerTitle.text = it.track.trackTitle
                recommendPlayerNickname.text = it.track.announcer.nickname
                val requestOptions = RequestOptions.bitmapTransform(RoundedCorners(10))
                Glide.with(requireContext()).load(it.track.coverUrlMiddle).apply(requestOptions)
                    .into(recommendPlayerCover)

                playViewModel.playState.observe(this@RecommendFragment) { playState ->
                    if (playState) {
                        recommendPlayerPlayOrPause.setImageResource(R.mipmap.player_paus_click)
                    } else {
                        recommendPlayerPlayOrPause.setImageResource(R.mipmap.player_button)
                    }
                }
            }


            detailViewModel.albumLiveData.observe(this@RecommendFragment) {
                fromDetailAlbum = it
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun initListener() {
        super.initListener()
        dataBinding.apply {
            recommendSmartRefresh.setOnRefreshLoadMoreListener(object :
                OnRefreshLoadMoreListener {
                override fun onRefresh(refreshLayout: RefreshLayout) {

                    mViewModel?.getRecommendData()
                    refreshLayout.finishRefresh()
                }

                override fun onLoadMore(refreshLayout: RefreshLayout) {

                }

            })

            albumAdapter.setOnRecommendItemClickListener(object :
                AlbumAdapter.OnRecommendItemClickListener {
                override fun onRecommendItemClick(albumId: Int, album: MyAlbumData) {

                    val bundle = Bundle()
                    bundle.putInt(Constant.PUT_ALBUM_ID, albumId)
                    bundle.putParcelable(Constant.PUT_ALBUM, album)

                    findNavController().navigate(R.id.detail_fragment, bundle)

                }

                override fun onRecommendLongClick(album: MyAlbumData) {

                    val albumData =
                        AlbumData(
                            album.albumTitle,
                            album.id.toInt(),
                            album.coverUrlMiddle,
                            album.albumIntro,
                            album.playCount.toString(),
                            album.includeTrackCount.toString()
                        )

                    xmDialog.apply {
                        if (dbTitleList.isEmpty()) {
                            upDateDialogState(DialogControl.DialogStatus.ADD)
                            setOnDialogAddClickListener(object :
                                DialogControl.OnDialogAddClickListener {
                                override fun onAddClick() {
                                    subscribeViewModel.insertAlbum(albumData)
                                    Toast.makeText(requireContext(), "添加成功", Toast.LENGTH_SHORT)
                                        .show()
                                }
                            })
                        } else {
                            if (dbTitleList.contains(album.albumTitle)) {
                                upDateDialogState(DialogControl.DialogStatus.IS_ADD)
                            } else {
                                upDateDialogState(DialogControl.DialogStatus.ADD)
                                setOnDialogAddClickListener(object :
                                    DialogControl.OnDialogAddClickListener {
                                    override fun onAddClick() {
                                        subscribeViewModel.insertAlbum(albumData)
                                        Toast.makeText(requireContext(), "添加成功", Toast.LENGTH_SHORT)
                                            .show()
                                    }
                                })
                            }
                        }
                        show()
                    }
                }
            })

            recommendPlayerPlayOrPause.setOnTouchListener { v, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        recommendPlayerButtonColor =
                            ContextCompat.getColor(requireContext(), R.color.secondColor)
                    }

                    MotionEvent.ACTION_MOVE -> {
                        recommendPlayerButtonColor =
                            ContextCompat.getColor(requireContext(), R.color.secondColor)
                    }

                    MotionEvent.ACTION_UP -> {
                        recommendPlayerButtonColor =
                            ContextCompat.getColor(requireContext(), R.color.sienna)
                    }
                }
                false
            }
            playViewModel.apply {
                recommendPlayerPlayOrPause.setOnClickListener {
                    if (!playViewModel.hasPlayList()) {
                        Toast.makeText(requireContext(), "快去播放一首音乐吧！", Toast.LENGTH_SHORT).show()
                    } else {
                        if (getPlaying()) {
                            playPause()
                            recommendPlayerPlayOrPause.setImageResource(R.mipmap.player_button)
                        } else {
                            playStart()
                            recommendPlayerPlayOrPause.setImageResource(R.mipmap.player_paus_click)

                        }
                    }
                }

                recommendPlayerContainer.setOnClickListener {
                    if (hasPlayList()) {
                        if (this@RecommendFragment::fromDetailAlbum.isInitialized) {
                            val bundle = Bundle()
                            bundle.putInt(Constant.PUT_ALBUM_ID, fromDetailAlbum.id.toInt())
                            bundle.putParcelable(Constant.PUT_ALBUM, fromDetailAlbum)
                            findNavController().navigate(R.id.detail_fragment, bundle)
                        }
                    } else {
                        Toast.makeText(requireContext(), "快去播放音乐吧", Toast.LENGTH_SHORT).show()
                    }

                }
            }

        }
    }

    override fun onResume() {
        super.onResume()
        requireActivity().window.apply {
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

            statusBarColor = context.getColor(R.color.mainColor)
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE;

        }
    }

    override fun onPause() {
        super.onPause()
        mViewModel = null
    }


}