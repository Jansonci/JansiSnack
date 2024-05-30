package com.example.jetsnack.ui.home.Profile

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.jetsnack.api.DessertService
import com.example.jetsnack.model.OrderForCheck
import com.example.jetsnack.model.Post
import com.google.gson.JsonElement
import kotlinx.coroutines.launch

var loggedInUser: Long = 10

@RequiresApi(Build.VERSION_CODES.O)
class ProfileViewModel : ViewModel() {
    private val _userInfo = mutableStateOf<JsonElement?>(null)
    val userInfo: State<JsonElement?> = _userInfo

    private val _collections = mutableStateOf<List<Post>?>(null)
    val collections: State<List<Post>?> = _collections

    private val _orderForChecks = mutableStateOf<List<OrderForCheck>?>(null)
    val orderForChecks: State<List<OrderForCheck>?> = _orderForChecks

    init {
        refreshData()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun refreshData() {
        viewModelScope.launch {
            try {
                val response = DessertService.create().getUser(loggedInUser)
                val collections = DessertService.create().findCollections(loggedInUser)
                val orderForChecks = DessertService.create().getOrders(loggedInUser)
                if (response.isSuccessful) {
                    _userInfo.value = response.body()  // 这里你直接得到 JsonElement
                } else {
                    // 处理错误响应
                }
                if (collections.isSuccessful) {
                    val updatedPosts = collections.body()?.map { post ->
                        post.copy(isCollected = post.likes.contains(loggedInUser))
                    }
                    _collections.value = updatedPosts  // 这里你直接得到 JsonElement
                } else {
                    // 处理错误响应
                }
                if (orderForChecks.isSuccessful) {
                    _orderForChecks.value = orderForChecks.body()  // 这里你直接得到 JsonElement
                } else {
                    // 处理错误响应
                }
            } catch (e: Exception) {
                // 处理网络异常等
            }
        }
    }

    companion object {
        fun provideFactory(
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ProfileViewModel() as T
            }
        }
    }
}