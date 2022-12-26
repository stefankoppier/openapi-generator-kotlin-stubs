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
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

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

        val body = extract(200)
        val type = (Types.newParameterizedType(List::class.java, Item::class.java))
        val adapter: JsonAdapter<List<Item>> = moshi.adapter(type)
        assertEquals(listOf(item), adapter.fromJson(body))
    }

    @Test
    fun `stub 'items GET not found' calls WireMock`() {
        stub.itemsIdGet(0).notFound("Not found")

        val body = extract(404)
        assertEquals("Not found", moshi.adapter(String::class.java).fromJson(body))
    }

    @ParameterizedTest
    @CsvSource("500", "501", "502")
    fun `stub 'items GET default' calls WireMock`(code: String) {
        stub.itemsIdGet(0).response(code.toInt(), code)

        val body = extract(code.toInt())
        assertEquals(code, moshi.adapter(String::class.java).fromJson(body))
    }

    private fun extract(code: Int): String {
        return Given { port(8080) } When
            {
                get("/items/0")
            } Then
            {
                statusCode(code)
            } Extract
            {
                response().body.print()
            }
    }
}
