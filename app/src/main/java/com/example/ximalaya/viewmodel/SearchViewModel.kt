package com.example.ximalaya.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ximalaya.repository.SearchRepository
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack
import com.ximalaya.ting.android.opensdk.model.album.SearchAlbumList
import com.ximalaya.ting.android.opensdk.model.word.SuggestWords
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

class SearchViewModel : ViewModel() {
    private var currentKeyword: String = ""
    private val searchRepository by lazy {
        SearchRepository()
    }

    val suggestWordsListLiveData = MutableLiveData<SuggestWords>()

    val searchAlbumLiveData = MutableLiveData<SearchAlbumList>()
    private var currentPage = 1

    enum class SearchAlbumLoadStatus {
        LOADING, SUCCESS, EMPTY, ERROR
    }

    val searchAlbumLoadMoreLiveData = MutableLiveData<SearchAlbumList>()

    enum class SearchAlbumLoadMoreStatus {
        SUCCESS, EMPTY, ERROR
    }

    val searchAlbumLoadMoreStateLiveData = MutableLiveData<SearchAlbumLoadMoreStatus>()

    val searchAlbumStateLiveData = MutableLiveData<SearchAlbumLoadStatus>()
    fun getSuggestWords(keyword: String) {
        searchRepository.getSuggestWords(keyword, object : IDataCallBack<SuggestWords> {
            override fun onSuccess(p0: SuggestWords?) {
                viewModelScope.launch {
                    flow<SuggestWords?> {
                        emit(p0)
                    }.flowOn(Dispatchers.IO).collect {
                        suggestWordsListLiveData.postValue(it)
                    }
                }

            }

            override fun onError(p0: Int, p1: String?) {

            }
        })
    }


    fun getSearchAlbum(keyword: String) {
        currentKeyword = keyword
        searchAlbumStateLiveData.postValue(SearchAlbumLoadStatus.LOADING)
        searchRepository.getSearchAlbums(object : IDataCallBack<SearchAlbumList> {
            override fun onSuccess(p0: SearchAlbumList?) {
                viewModelScope.launch {
                    flow<SearchAlbumList?> {
                        emit(p0)
                    }.flowOn(Dispatchers.IO).catch {
                        searchAlbumStateLiveData.postValue(SearchAlbumLoadStatus.ERROR)
                    }.collect {
                        if (it != null && it.albums.isNotEmpty()) {
                            searchAlbumLiveData.postValue(it)
                            searchAlbumStateLiveData.postValue(SearchAlbumLoadStatus.SUCCESS)
                        } else {
                            searchAlbumStateLiveData.postValue(SearchAlbumLoadStatus.EMPTY)
                        }

                    }
                }
            }

            override fun onError(p0: Int, p1: String?) {
                searchAlbumStateLiveData.postValue(SearchAlbumLoadStatus.ERROR)
            }

        }, keyword, currentPage)
    }

    fun loadMoreSearchAlbum() {
        currentPage++
        if (currentPage == 4) {
            currentPage = 1
        }

        searchRepository.getSearchAlbums(object : IDataCallBack<SearchAlbumList> {
            override fun onSuccess(p0: SearchAlbumList?) {
                viewModelScope.launch {
                    flow<SearchAlbumList?> {
                        emit(p0)
                    }.flowOn(Dispatchers.IO).catch {
                        searchAlbumLoadMoreStateLiveData.postValue(SearchAlbumLoadMoreStatus.ERROR)
                    }.collect {
                        if (it != null && it.albums.isNotEmpty()) {
                            searchAlbumLoadMoreStateLiveData.postValue(SearchAlbumLoadMoreStatus.SUCCESS)
                            searchAlbumLoadMoreLiveData.postValue(it)
                        } else {
                            searchAlbumLoadMoreStateLiveData.postValue(SearchAlbumLoadMoreStatus.EMPTY)
                        }
                    }
                }
            }

            override fun onError(p0: Int, p1: String?) {
                searchAlbumLoadMoreStateLiveData.postValue(SearchAlbumLoadMoreStatus.SUCCESS)
            }

        }, currentKeyword, currentPage)
    }
}
