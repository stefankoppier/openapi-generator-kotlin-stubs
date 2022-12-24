import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.math.BigDecimal
import java.net.URI
import java.time.LocalDate
import java.time.OffsetDateTime

object BigDecimalAdapter {
    @FromJson fun fromJson(string: String) = BigDecimal(string)
    @ToJson fun toJson(value: BigDecimal) = value.toString()
}

object URIAdatapter {
    @FromJson fun fromJson(string: String) = URI(string)
    @ToJson fun toJson(value: URI) = value.toString()
}

object LocalDateAdapter {
    @FromJson fun fromJson(string: String) = LocalDate.parse(string)
    @ToJson fun toJson(value: LocalDate) = value.toString()
}

object OffsetDateTimeAdapter {
    @FromJson fun fromJson(string: String) = OffsetDateTime.parse(string)
    @ToJson fun toJson(value: OffsetDateTime) = value.toString()
}
