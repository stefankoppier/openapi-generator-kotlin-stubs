import com.github.tomakehurst.wiremock.WireMockServer
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import io.github.stefankoppier.generators.test.ItemsApiStub
import io.github.stefankoppier.generators.test.models.Item
import io.restassured.module.kotlin.extensions.Extract
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import kotlin.test.*

class ItemsApiTest {

    private val wiremock = WireMockServer(8080)

    private val moshi =
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .add(BigDecimalAdapter)
            .add(URIAdatapter)
            .add(LocalDateAdapter)
            .add(OffsetDateTimeAdapter)
            .build()

    private val stub = ItemsApiStub()

    @BeforeTest
    fun setup() {
        wiremock.start()
    }

    @AfterTest
    fun teardown() {
        wiremock.stop()
    }

    @Test
    fun `stub has 'items GET'`() {
        assertIs<ItemsApiStub.ItemsIdGetStubBuilder>(stub.itemsIdGet(0))
    }

    @Test
    fun `stub 'items GET ok' calls WireMock`() {
        val item = Item.of()

        stub.itemsIdGet(0).ok(listOf(item))

        val body =
            Given { port(8080) } When { get("/items/0") } Then { statusCode(200) } Extract { response().body.print() }
        val type = (Types.newParameterizedType(List::class.java, Item::class.java))
        val adapter: JsonAdapter<List<Item>> = moshi.adapter(type)
        assertEquals(listOf(item), adapter.fromJson(body))
    }

    @Test
    fun `stub 'items GET not found' calls WireMock`() {
        stub.itemsIdGet(0).notFound("Not found")

        val body =
            Given { port(8080) } When { get("/items/0") } Then { statusCode(404) } Extract { response().body.print() }
        assertEquals("Not found", moshi.adapter(String::class.java).fromJson(body))
    }
}
