package com.example.jetsnack.api
//import com.example.jetsnack.data.BuidConfig
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.jetsnack.model.CartDetail
import com.example.jetsnack.model.Category
import com.example.jetsnack.model.Dessert
import com.example.jetsnack.model.Lifestyle
import com.example.jetsnack.model.OrderForCheck
import com.example.jetsnack.model.Post
import com.example.jetsnack.ui.home.Cart.gson
import com.google.gson.JsonElement
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import java.math.BigDecimal

/**
 * Used to connect to the Unsplash API to fetch photos
 */
interface DessertService {

    @GET("desserts/{id}")
    suspend fun getDessertDetail(@Path("id") dessertId: Long): Response<Dessert>

    @GET("desserts/all")
    suspend fun getAllDesserts(): Response<List<Dessert>>

    @GET("desserts/allCategories")
    suspend fun getAllCategories(): Response<List<Category>>

    @GET("desserts/allLifestyles")
    suspend fun getAllLifestyles(): Response<List<Lifestyle>>

    @GET("users/findUser/{userId}")
    suspend fun getUser(@Path("userId") userId: Long):Response<JsonElement>

    @POST("users/signUp")
    @Headers("Content-Type: application/json")
    suspend fun signUp(@Body request: SignUpRequest): Response<JsonElement>

    @PUT("users/update")
    @Headers("Content-Type: application/json")
    suspend fun update(@Body request: UpdateRequest)

    @POST("users/login")
    @Headers("Content-Type: application/json")
    suspend fun login(@Body request: LoginRequest): Response<JsonElement>

    @GET("article/{postId}")
    suspend fun getAnArticle(@Path("postId") postId: String): Response<Post>

    @GET("article/all")
    suspend fun getAllArticles(): Response<List<Post>>

    @GET("users/findCollections/{userId}")
    suspend fun findCollections(@Path("userId") userId: Long): Response<List<Post>>

    @POST("order/createWithKafkaa")
    suspend fun createOrderWithKafkaa(@Query("userId") userId: Long, @Query("address") address: String, @Query("requireTime")requireTime: Long,  @Query("shippingCost")shippingCost: BigDecimal ): Response<Long>

    @POST("order/submitWithKafka")
    suspend fun submitPaymentWithKafka(@Query("orderId") orderId: Long, @Query("paymentMethod") paymentMethod: String)

    @GET("order/getCartt/{userId}")
    suspend fun getCartt(@Path("userId") userId: Long): Response<Set<CartDetail>>

    @GET("order/getOrderr/{userId}")
    suspend fun getOrders(@Path("userId") userId: Long): Response<List<OrderForCheck>>

    @PUT("order/updateCart")
    suspend fun updateCartRequest(@Body updateCartRequest: UpdateCartRequest)

    companion object {
        private const val BASE_URL = "http://10.0.2.2:9527/"
        private const val ARTICLE_BASE_URL = "http://10.0.2.2:8080/"

        @RequiresApi(Build.VERSION_CODES.O)
        fun create(): DessertService {
            val logger = HttpLoggingInterceptor().apply { level = Level.BASIC }

            val client = OkHttpClient.Builder()
                .addInterceptor(logger)
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))  // 使用自定义的Gson实例
                .build()
                .create(DessertService::class.java)
        }

        fun createFoeArticle(): DessertService {
            val logger = HttpLoggingInterceptor().apply { level = Level.BASIC }

            val client = OkHttpClient.Builder()
                .addInterceptor(logger)
                .build()

            return Retrofit.Builder()
                .baseUrl(ARTICLE_BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(DessertService::class.java)
        }

    }
}

data class SignUpRequest(val username: String, val password: String)

data class LoginRequest(val username: String, val password: String)

data class UpdateRequest(val userId: Long, val fieldName: String, val fieldValue: Any)

data class UpdateCartRequest(val userId: Long,val dessertId: Long, val operation: Int )

