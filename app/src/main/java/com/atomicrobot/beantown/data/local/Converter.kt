import androidx.room.TypeConverter
import kotlinx.serialization.json.Json

/**
 * Helper class containing "converter" functions for for serializing/deserializing
 * data to be stored and read from a SQLite database.
 */
class Converters {

    /**
     * Serializes a [List] of strings to JSON string.
     * @param value [List] of string to be serialized
     * @return String containing the JSON representation of the [value].
     */
    @TypeConverter
    fun fromList(value : List<String>) = Json.encodeToString<List<String>>(value)

    /**
     * Deserializes a JSON string to a [List] of strings
     * @param value JSON string containing a JSON encoded array of strings.
     * @return Deserialized [List] of strings contained in [value].
     */
    @TypeConverter
    fun toList(value: String) = Json.decodeFromString<List<String>>(value)
}
