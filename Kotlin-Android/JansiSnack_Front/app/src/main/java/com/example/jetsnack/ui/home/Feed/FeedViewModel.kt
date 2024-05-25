package com.example.jetsnack.ui.home.Feed

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.jetsnack.api.DessertService
import com.example.jetsnack.data.DessertRepository
import com.example.jetsnack.model.Category
import com.example.jetsnack.model.CollectionType
import com.example.jetsnack.model.Dessert
import com.example.jetsnack.model.DessertCollection
import com.example.jetsnack.model.Lifestyle
import com.example.jetsnack.model.OrderLine
import com.example.jetsnack.model.SnackRepo
import com.example.jetsnack.model.snacks
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class FeedViewModel @Inject constructor(private val dessertRepository: DessertRepository): ViewModel(){
    val filters = SnackRepo.getFilters()

    private val _desserts = mutableStateOf<List<Dessert>?>(null)
    val desserts: State<List<Dessert>?> = _desserts

    private val _categories = mutableStateOf<List<Category>?>(null)
    val categories: State<List<Category>?> = _categories

    private val _lifestyles = mutableStateOf<List<Lifestyle>?>(null)
    val lifestyles: State<List<Lifestyle>?> = _lifestyles

    private val _sortedDesserts = mutableStateOf<List<Dessert>?>(null)
    val sortedDesserts: State<List<Dessert>?> = _sortedDesserts

    init {
        refreshData(true)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun refreshData(isRefresh: Boolean = false) {
        viewModelScope.launch {
            if (isRefresh || dessertRepository.getDesserts()?.isEmpty() == true) {
                try {
                    val response1 = DessertService.create().getAllDesserts()
                    val dessertss = response1.body()

                    val response2 = DessertService.create().getAllCategories()
                    val categoriess = response2.body()

                    val response3 = DessertService.create().getAllLifestyles()
                    val lifestyless = response3.body()

                    if (dessertss != null) {
                        dessertRepository.upsertAllDesserts(dessertss)
                    }
                    if (categoriess != null) {
                        dessertRepository.upsertAllCategories(categoriess)
                    }
                    if (lifestyless != null) {
                        dessertRepository.upsertAllLifestyles(lifestyless)
                    }
                    if (response1.isSuccessful) {
                        // 更新_dessertDetail状态
                        _desserts.value = response1.body()
                    } else {
                        // 处理错误情况，例如显示错误信息
                        // ...
                        val errorMessage = when (response1.code()) {
                            404 -> "甜品详情未找到。"
                            500 -> "服务器错误，请稍后再试。"
                            else -> "未知错误，请稍后再试。"
                        }
                    }
                    if (response2.isSuccessful) {
                        // 更新_dessertDetail状态
                        _categories.value = response2.body()
                        Log.i("Dessertsss", "Response: ${categories.value?.get(0)?.imageUrl}")

                    } else {
                        // 处理错误情况，例如显示错误信息
                        // ...
                        val errorMessage = when (response2.code()) {
                            404 -> "甜品详情未找到。"
                            500 -> "服务器错误，请稍后再试。"
                            else -> "未知错误，请稍后再试。"
                        }
                    }
                    if (response3.isSuccessful) {
                        // 更新_dessertDetail状态
                        _lifestyles.value = response3.body()
                        Log.i("Dessertsss", "Response: ${lifestyles.value?.size}")

                    } else {
                        // 处理错误情况，例如显示错误信息
                        // ...
                        val errorMessage = when (response3.code()) {
                            404 -> "甜品详情未找到。"
                            500 -> "服务器错误，请稍后再试。"
                            else -> "未知错误，请稍后再试。"
                        }
                    }
                } catch (e: Exception) {
                    // 处理异常，例如网络错误
                    // ...
                }
            } else {
                _desserts.value = dessertRepository.getDesserts()
            }
        }
    }

    fun findDessertsByCategory(categoryName: String) {
        viewModelScope.launch {
            _sortedDesserts.value=dessertRepository.findDessertsByCategory(categoryName)
        }
    }

    private val tastyTreats = derivedStateOf {
        desserts.value?.let {
            DessertCollection(
                id = 1L,
                name = "Android's picks",
                type = CollectionType.Highlight,
                snacks = it.subList(0, minOf(13, it.size))
            )
        }
    }

    private val popular = derivedStateOf {
        desserts.value?.let {
            DessertCollection(
                id = 2L,
                name = "Popular on KotlinSnack",
                snacks = it.subList(14, 19)
            )
        }
    }

    private val wfhFavs = derivedStateOf {
        tastyTreats.value?.copy(
            id = 3L,
            name = "XHL's favourites"
        )
    }

    private val newlyAdded = derivedStateOf {
        popular.value?.copy(
            id = 4L,
            name = "Newly Added"
        )
    }

    private val exclusive = derivedStateOf {
        tastyTreats.value?.copy(
            id = 5L,
            name = "Only on Jetsnack"
        )
    }

    private val also = derivedStateOf {
        tastyTreats.value?.copy(
            id = 6L,
            name = "Customers also bought"
        )
    }

    private val inspiredByCart = derivedStateOf {
        tastyTreats.value?.copy(
            id = 7L,
            name = "Inspired by your cart"
        )
    }
    private val dessertCollections = derivedStateOf {
        listOf(
            tastyTreats,
            popular,
            wfhFavs,
            newlyAdded,
            exclusive
        )
    }

    val snackCollections = derivedStateOf {
        dessertCollections.value
    }

    private val related = listOf(
        also,
        popular
    )

    private val cart = listOf(
        OrderLine(snacks[4], 2),
        OrderLine(snacks[6], 3),
        OrderLine(snacks[8], 1)
    )

    companion object {
        fun provideFactory(
            dessertRepository: DessertRepository
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return FeedViewModel(dessertRepository) as T
            }
        }
    }
}