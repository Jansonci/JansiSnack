package com.example.jetsnack.ui.snackdetail

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.jetsnack.api.DessertService
import com.example.jetsnack.data.DessertRepository
import com.example.jetsnack.model.Dessert
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SnackDetailViewModel @Inject constructor(private val dessertRepository: DessertRepository): ViewModel() {
    private val _dessertDetail = mutableStateOf<Dessert?>(null)
    val dessertDetail: State<Dessert?> = _dessertDetail

    private val viewModelScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    @RequiresApi(Build.VERSION_CODES.O)
    fun getDessertDetail(dessertId: Long, isRefresh: Boolean = false) {
        viewModelScope.launch {
            if (isRefresh || dessertRepository.getDessert(dessertId) == null) {
                try {
                    val response = DessertService.create().getDessertDetail(dessertId)
                    val dessert = response.body()
                    if (dessert != null) {
                        dessertRepository.insertDessert(dessert)
                    }
                    if (response.isSuccessful) {
                        // 更新_dessertDetail状态
                        _dessertDetail.value = response.body()
                    } else {
                        // 处理错误情况，例如显示错误信息
                        // ...
                        val errorMessage = when (response.code()) {
                            404 -> "甜品详情未找到。"
                            500 -> "服务器错误，请稍后再试。"
                            else -> "未知错误，请稍后再试。"
                        }
                    }
                } catch (e: Exception) {
                    // 处理异常，例如网络错误
                    // ...
                }
            }
            else {
                _dessertDetail.value = dessertRepository.getDessert(dessertId)
            }
        }
    }
    companion object {
        fun provideFactory(
            dessertRepository: DessertRepository
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return SnackDetailViewModel(dessertRepository) as T
            }
        }
    }
}