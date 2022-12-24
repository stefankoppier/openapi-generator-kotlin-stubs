import io.github.stefankoppier.generators.test.models.Item
import java.math.BigDecimal
import kotlin.test.Test
import kotlin.test.assertEquals

class ItemModelTest {

    @Test
    fun `item has fields`() {
        val item =
            Item.of {
                bigdecimal { constant(BigDecimal.ONE) }
                float { constant(2.0f) }
                double { constant(3.0) }
                integer { constant(4) }
                long { constant(5L) }
                string { constant("string") }
            }

        val expected = Item(BigDecimal.ONE, 2.0f, 3.0, 4, 5L, "string")

        assertEquals(expected, item)
    }
}
