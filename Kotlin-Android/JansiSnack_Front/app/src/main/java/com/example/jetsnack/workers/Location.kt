
import android.util.Log
import com.baidu.mapapi.model.LatLng
import com.baidu.mapapi.search.core.SearchResult
import com.baidu.mapapi.search.geocode.GeoCodeResult
import com.baidu.mapapi.search.geocode.GeoCoder
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult


interface GeoCodeResultListener {
    fun onResult(city: String)
    fun onError(error: String)
}

fun reverseGeoCode(latitude: Double, longitude: Double, listener: GeoCodeResultListener) {
    val geoCoder = GeoCoder.newInstance()
    val reverseGeoCodeOption = ReverseGeoCodeOption()
    reverseGeoCodeOption.location(LatLng(latitude, longitude)).newVersion(1)
    geoCoder.setOnGetGeoCodeResultListener(object : OnGetGeoCoderResultListener {
        override fun onGetReverseGeoCodeResult(result: ReverseGeoCodeResult?) {
            if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                // 没有找到检索结果
                Log.i("GeoCoder", result.toString())

            } else {
                // 获取反向地理编码结果
                val city = result.addressDetail.city
                Log.i("GeoCoder", "City: $city")
                listener.onResult(city)
            }
        }

        override fun onGetGeoCodeResult(p0: GeoCodeResult?) {
            // 此处为正向地理编码的回调，一般不需要实现
        }
    })
    geoCoder.reverseGeoCode(reverseGeoCodeOption)

}