
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import com.example.jetsnack.data.AppDatabase
import com.example.jetsnack.data.DessertDao
import com.example.jetsnack.model.Dessert
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class DessertDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var dessertDao: DessertDao

    @Before
    fun createDb() {
        // 使用内存数据库，测试结束后数据库内容会消失
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries() // 允许在主线程上查询，只在测试中使用
            .build()
        dessertDao = database.dessertDao()
    }

    @After
    fun closeDb() {
        database.close()
    }

    @Test
    fun testFindDessertsByCategory() = runBlocking {
        // 准备数据
        val dessert1 = Dessert(1, "Chocolate Cake", "Cake","","",1.0,"",1,1,"","","",1,1.0,"","",
            listOf()
        )
        val dessert2 = Dessert(2, "Apple Pie", "Pie","","",1.0,"",1,1,"","","",1,1.0,"","",
            listOf())
        dessertDao.upsertAllDesserts(listOf(dessert1,dessert2))

        // 执行测试的方法
        val result = dessertDao.findDessertsByCategory("Cake")

        // 验证结果
        assertNotNull(result)
        if (result != null) {
            assertEquals(1, result.size)
        }
        assertEquals("Chocolate Cake", result?.get(0)?.name ?: "")
    }
}

