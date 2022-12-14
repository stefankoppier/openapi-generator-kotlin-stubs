import com.github.tomakehurst.wiremock.WireMockServer
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import io.github.stefankoppier.generators.test.DefaultApiStub
import io.github.stefankoppier.generators.test.models.Item
import io.restassured.module.kotlin.extensions.Extract
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import kotlin.test.*

class ItemApiTest {

    private val wiremock = WireMockServer(8083)

    private val moshi =
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .add(BigDecimalAdapter)
            .add(URIAdatapter)
            .add(LocalDateAdapter)
            .add(OffsetDateTimeAdapter)
            .build()

    private var stub = DefaultApiStub("localhost", 8083)

    @BeforeTest
    fun setup() {
        wiremock.start()
    }

    @AfterTest
    fun teardown() {
        wiremock.stop()
    }

    @Test
    fun `stub has 'item GET'`() {
        assertIs<DefaultApiStub.ItemGetStubBuilder>(stub.itemGet())
    }

    @Test
    fun `stub 'items GET' calls WireMock`() {
        val item = Item.of()

        stub.itemGet().ok(item)

        val body = extract()
        assertEquals(item, moshi.adapter(Item::class.java).fromJson(body))
    }

    private fun extract(): String {
        return Given { port(8083) } When { get("/item") } Then { statusCode(200) } Extract { response().body.print() }
    }
}
