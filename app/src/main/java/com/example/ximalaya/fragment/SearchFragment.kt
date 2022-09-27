package com.example.ximalaya.fragment

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ximalaya.R
import com.example.ximalaya.adapter.AlbumAdapter
import com.example.ximalaya.adapter.SuggestAdapter
import com.example.ximalaya.base.BaseViewModelFragment

import com.example.ximalaya.databinding.FragmentSearchBinding
import com.example.ximalaya.databinding.FragmentSearchSuccessBinding

import com.example.ximalaya.domian.MyAlbumData
import com.example.ximalaya.room.AlbumData
import com.example.ximalaya.utils.Constant
import com.example.ximalaya.utils.KeyboardUtil
import com.example.ximalaya.view.DialogControl
import com.example.ximalaya.view.SearchFlowLayout
import com.example.ximalaya.viewmodel.SearchViewModel
import com.example.ximalaya.viewmodel.SubscribeViewModel
import com.scwang.smart.refresh.footer.ClassicsFooter
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class SearchFragment : BaseViewModelFragment<SearchViewModel>() {
    private lateinit var searchSuccessBinding: FragmentSearchSuccessBinding
    private lateinit var searchBinding: FragmentSearchBinding

    private val flowList = mutableListOf<String>()

    private val sharedPreferences by lazy {
        requireActivity().getSharedPreferences(Constant.SEARCH_SP_KEY, Context.MODE_PRIVATE)
    }

    private val xmDialog by lazy {
        DialogControl(requireContext())
    }

    override fun getSuccessFragmentView(rootFrameLayout: FrameLayout): View {
        searchSuccessBinding = DataBindingUtil.inflate(LayoutInflater.from(requireContext()),
            R.layout.fragment_search_success,
            rootFrameLayout,
            false)
        return searchSuccessBinding.root
    }

    

    override fun getRootView(inflater: LayoutInflater, container: ViewGroup?): View {
        searchBinding = DataBindingUtil.inflate(LayoutInflater.from(requireContext()),
            R.layout.fragment_search,
            container,
            false)
        return searchBinding.root
    }

    private val suggestAdapter by lazy {
        SuggestAdapter()
    }

    private val searchAlbumAdapter by lazy {
        AlbumAdapter()
    }

    private val subscribeViewModel by lazy {
        ViewModelProvider(this)[SubscribeViewModel::class.java]
    }

    private val subTitleList = mutableListOf<String>()

    private lateinit var backPressedCallback: OnBackPressedCallback

    override fun initView() {
        dispatchViewState(FragmentStatus.SUCCESS)


        val stringSet = sharedPreferences.getStringSet(Constant.SEARCH_HISTORY, null)

        // Log.d("取得容器内容-->", it)
        // Log.d("=========", "================")
        flowTextListCanBeAdd(null, stringSet?.toMutableList()?.reversed())


        searchSuccessBinding.apply {
            if (stringSet != null) {
                searchFirstContent.visibility = View.GONE
                searchFlowLayout.visibility = View.VISIBLE

            } else {
                searchFirstContent.visibility = View.VISIBLE
                searchFlowLayout.visibility = View.GONE
            }

            searchSuggestWordRv.layoutManager = LinearLayoutManager(requireContext())
            searchSuggestWordRv.adapter = suggestAdapter


            searchSmartRefresh.apply {
                setEnableRefresh(false)
                setEnableLoadMore(true)
                setRefreshFooter(ClassicsFooter(requireContext()))
            }

            searchContentRv.layoutManager = LinearLayoutManager(requireContext())
            searchContentRv.adapter = searchAlbumAdapter
        }

        backPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.home_fragmment)
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(backPressedCallback)

    }

  
    @OptIn(ExperimentalCoroutinesApi::class)
    private fun TextView.textWatchFlow() = callbackFlow<String> {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                searchSuccessBinding.apply {
                    searchBinding.apply {
                        if (s.toString().isNotEmpty()) {
                            searchCancelIv.visibility = View.VISIBLE
                            searchSuggestWordRv.visibility = View.VISIBLE
                            searchFirstContent.visibility = View.GONE
                            searchFlowLayout.visibility = View.GONE
                            searchSmartRefresh.visibility = View.GONE
                            trySend(s.toString())
                        } else {
                            searchCancelIv.visibility = View.GONE
                            searchSuggestWordRv.visibility = View.GONE
                            searchSmartRefresh.visibility = View.GONE
                            dispatchViewState(FragmentStatus.SUCCESS)
                            if (flowList.size != 0) {
                                searchFlowLayout.visibility = View.VISIBLE
                            } else {
                                searchFirstContent.visibility = View.VISIBLE
                            }
                        }
                    }
                }
            }
        }
        addTextChangedListener(textWatcher)
        awaitClose { removeTextChangedListener(textWatcher) }
    }.catch {
        throw it
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun searchFlow(text: String) = callbackFlow<String> {
        trySend(text)
        awaitClose { }
    }.catch {
        throw it
    }

    override fun initListener() {
        super.initListener()
        searchBinding.apply {
            searchSuccessBinding.apply {

                searchToolbar.setNavigationOnClickListener {

                    findNavController().navigate(R.id.home_fragmment)
                }
                searchInputEt.setOnEditorActionListener { v, actionId, event ->
                    if (v.text.toString().isNotEmpty()) {
                        flowTextListCanBeAdd(v.text.toString(), null)

                        KeyboardUtil.hideKeyboard(requireContext(), searchInputEt)

                        lifecycleScope.launch {
                            searchFlow(v.text.toString()).collect {
                                mViewModel?.getSearchAlbum(it)
                            }
                        }

                        searchSuggestWordRv.visibility = View.GONE
                        searchSmartRefresh.visibility = View.VISIBLE
                    }
                    return@setOnEditorActionListener true
                }

                searchHistoryClearIm.setOnClickListener {
                    xmDialog.upDateDialogState(DialogControl.DialogStatus.SEARCH)
                    xmDialog.setOnDialogClearClickListener(object :
                        DialogControl.OnDialogClearClickListener {
                        override fun onClearClick() {
                            sharedPreferences.edit().clear().apply()
                            flowList.clear()
                            searchFlowLayout.clearDataList()
                            searchFirstContent.visibility = View.VISIBLE
                        }

                    })
                    xmDialog.show()
                }

                lifecycleScope.launch {
                    searchInputEt.textWatchFlow().collect {
                        mViewModel?.getSuggestWords(it)
                    }

                }

                searchFlowLayout.setOnSearchFlowItemClickListener(object :
                    SearchFlowLayout.OnSearchFlowItemClickListener {
                    override fun onFlowItemClick(text: String) {
                        searchInputEt.setText(text)
                        searchInputEt.setSelection(text.length)
                        lifecycleScope.launch {
                            searchFlow(text).collect {
                                mViewModel?.getSearchAlbum(it)
                            }
                        }

                        searchSmartRefresh.visibility = View.VISIBLE
                        searchFlowLayout.visibility = View.GONE
                        searchSuggestWordRv.visibility = View.GONE
                        searchFirstContent.visibility = View.GONE
                    }
                })

                searchCancelIv.setOnClickListener {
                    searchInputEt.setText("")
                    dispatchViewState(FragmentStatus.SUCCESS)
                }

                suggestAdapter.setOnSuggestItemClickListener(object :
                    SuggestAdapter.OnSuggestItemClickListener {
                    override fun onSuggestClick(text: String) {
                        KeyboardUtil.hideKeyboard(requireContext(), searchSuggestWordRv)
                        searchInputEt.setText(text)
                        searchInputEt.setSelection(text.length)
                        lifecycleScope.launch {
                            searchFlow(text).collect {
                                mViewModel?.getSearchAlbum(it)
                            }
                        }

                        searchSmartRefresh.visibility = View.VISIBLE
                        searchFlowLayout.visibility = View.GONE
                        searchSuggestWordRv.visibility = View.GONE
                        searchFirstContent.visibility = View.GONE
                    }

                })

                        searchAlbumAdapter.setOnRecommendItemClickListener(object :
                            AlbumAdapter.OnRecommendItemClickListener {
                            override fun onRecommendItemClick(albumId: Int, album: MyAlbumData) {
                                val bundle = Bundle()

                                bundle.putInt(Constant.PUT_ALBUM_ID, albumId)
                                bundle.putParcelable(Constant.PUT_ALBUM, album)
                                bundle.putString(Constant.INTO_DETAIL,"search")
                                findNavController().navigate(R.id.detail_fragment, bundle)



                            }

                            override fun onRecommendLongClick(album: MyAlbumData) {
                                xmDialog.apply {
                                    if (subTitleList.size == 0) {

                                        upDateDialogState(DialogControl.DialogStatus.ADD)
                                        setOnDialogAddClickListener(object :
                                            DialogControl.OnDialogAddClickListener {
                                            override fun onAddClick() {
                                                val albumData = AlbumData(album.albumTitle,
                                                    album.id.toInt(),
                                                    album.coverUrlMiddle,
                                                    album.albumIntro,
                                                    album.playCount.toString(),
                                                    album.includeTrackCount.toString())
                                                subscribeViewModel.insertAlbum(albumData)

                                                Toast.makeText(requireContext(),
                                                    "添加成功",
                                                    Toast.LENGTH_SHORT).show()
                                            }

                                        })
                                    } else {
                                        if (subTitleList.contains(album.albumTitle)) {
                                            upDateDialogState(DialogControl.DialogStatus.IS_ADD)
                                        } else {
                                            upDateDialogState(DialogControl.DialogStatus.ADD)
                                            setOnDialogAddClickListener(object :
                                                DialogControl.OnDialogAddClickListener {
                                                override fun onAddClick() {
                                                    val albumData = AlbumData(album.albumTitle,
                                                        album.id.toInt(),
                                                        album.coverUrlMiddle,
                                                        album.albumIntro,
                                                        album.playCount.toString(),
                                                        album.includeTrackCount.toString())
                                                    subscribeViewModel.insertAlbum(albumData)
                                                    Toast.makeText(requireContext(),
                                                        "添加成功",
                                                        Toast.LENGTH_SHORT).show()
                                                }

                                            })
                                        }
                                    }

                                    show()
                                }
                            }
                })
            }
        }
    }


    override fun initDataListener() {
        super.initDataListener()
        mViewModel?.apply {
            suggestWordsListLiveData.observe(this@SearchFragment) {
                suggestAdapter.setData(it.keyWordList)
            }

            searchAlbumLiveData.observe(this@SearchFragment) {
                val myAlbumList: MutableList<MyAlbumData> = mutableListOf()
                it.albums.forEach { album ->
                    val myAlbumData = MyAlbumData(album.albumTitle,
                        album.coverUrlLarge,
                        album.albumIntro,
                        album.playCount,
                        album.includeTrackCount)
                    myAlbumData.id = album.id.toString()
                    myAlbumList.add(myAlbumData)
                }

                searchAlbumAdapter.setData(myAlbumList)
            }

            searchAlbumStateLiveData.observe(this@SearchFragment) {
                when (it) {
                    SearchViewModel.SearchAlbumLoadStatus.LOADING -> {
                        dispatchViewState(FragmentStatus.LOADING)
                    }

                    SearchViewModel.SearchAlbumLoadStatus.SUCCESS -> {
                        dispatchViewState(FragmentStatus.SUCCESS)
                    }

                    SearchViewModel.SearchAlbumLoadStatus.EMPTY -> {
                        dispatchViewState(FragmentStatus.EMPTY)
                    }

                    SearchViewModel.SearchAlbumLoadStatus.ERROR -> {
                        dispatchViewState(FragmentStatus.ERROR)
                    }
                    else -> {}
                }
            }

            searchAlbumLoadMoreLiveData.observe(this@SearchFragment) {

                val myAlbumList: MutableList<MyAlbumData> = mutableListOf()
                it.albums.forEach { album ->
                    val myAlbumData = MyAlbumData(album.albumTitle,
                        album.coverUrlLarge,
                        album.albumIntro,
                        album.playCount,
                        album.includeTrackCount)
                    myAlbumData.id = album.id.toString()
                    myAlbumList.add(myAlbumData)
                }

                searchAlbumAdapter.setLoadMoreData(myAlbumList)
            }

            searchAlbumLoadMoreStateLiveData.observe(this@SearchFragment) {
                when (it) {
                    SearchViewModel.SearchAlbumLoadMoreStatus.SUCCESS -> {
                        Toast.makeText(requireContext(), "成功加载数据", Toast.LENGTH_SHORT).show()
                    }

                    SearchViewModel.SearchAlbumLoadMoreStatus.ERROR -> {
                        Toast.makeText(requireContext(), "加载失败", Toast.LENGTH_SHORT).show()
                    }

                    else -> {
                        Toast.makeText(requireContext(), "没有更多数据了", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        subscribeViewModel.queryAlbums().observe(this) {
            subTitleList.clear()
            it.forEach {
                subTitleList.add(it.albumTitle)
            }
        }
    }

    private fun flowTextListCanBeAdd(text: String?, historyList: List<String>?) {
        searchSuccessBinding.searchFirstContent.visibility = View.GONE



        if (text != null && historyList == null) {
            if (!flowList.contains(text)) {
                flowList.add(text)
            }
        }

        if (text == null && historyList != null) {
            flowList.clear()
            flowList.addAll(historyList)
        }

        flowList.reverse()

        if (flowList.size >= 10) {
            flowList.removeAt(flowList.size - 1)
        }


        val edit = sharedPreferences.edit()
        edit.clear()
        edit.putStringSet(Constant.SEARCH_HISTORY, flowList.toSet())
        edit.apply()



        searchSuccessBinding.searchFlowLayout.setData(flowList)
    }

    override fun getViewModelClass() = SearchViewModel::class.java


    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden){
            requireActivity().window.apply {
                clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

                statusBarColor = context.getColor(R.color.mainColor)
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE;
            }
        }

        backPressedCallback.isEnabled = !hidden
    }

}