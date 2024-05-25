import org.junit.Test
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface BaiduMapService {
    @GET("reverse_geocoding/v3/")
    fun reverseGeocode(
        @Query("ak") apiKey: String,
        @Query("output") output: String = "json",
        @Query("coordtype") coordType: String = "wgs84ll",
        @Query("location") location: String
    ): Call<BaiduGeocodeResponse>
}

data class BaiduGeocodeResponse(
    val status: Int,
    val result: BaiduGeocodeResult
)

data class BaiduGeocodeResult(
    val formatted_address: String
)



fun testReverseGeocode1(latitude: Double, longitude: Double) {
    val retrofit = Retrofit.Builder()
        .baseUrl("http://api.map.baidu.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val service = retrofit.create(BaiduMapService::class.java)
    val call = service.reverseGeocode("rLZk89YFyY0d8EhLHp0rrR8wkh9dJvmV", location = "$latitude,$longitude")

    call.enqueue(object : retrofit2.Callback<BaiduGeocodeResponse> {
        override fun onResponse(call: Call<BaiduGeocodeResponse>, response: retrofit2.Response<BaiduGeocodeResponse>) {
            if (response.isSuccessful) {
                val geocodeResponse = response.body()
                println("Formatted Address: ${geocodeResponse?.result?.formatted_address}")
            } else {
                println("Response was not successful")
            }
        }

        override fun onFailure(call: Call<BaiduGeocodeResponse>, t: Throwable) {
            println("Failed to retrieve data: ${t.message}")
        }
    })
}

class BaiduMapAPITest {

    private val retrofit = Retrofit.Builder()
        .baseUrl("http://api.map.baidu.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val service = retrofit.create(BaiduMapService::class.java)

    @Test
    fun testReverseGeocode() {
//        val response = service.reverseGeocode("rLZk89YFyY0d8EhLHp0rrR8wkh9dJvmV", location = "39.915,116.404").execute()
        reverseGeoCode(31.8468583,117.2984183)

        // 判断响应是否成功
//        assert(response1.isSuccessful)
//
//        // 获取并打印出格式化的地址
//        val formattedAddress = response.body()?.result?.formatted_address
//        println("Formatted Address: $formattedAddress")
//
//        // 断言，确保获取到的地址不为空
//        assert(formattedAddress != null)
    }
}

