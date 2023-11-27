import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

object TestUtils {

    fun longRunningOperation(millis: Int = 1000): JSONObject {
        val json = JSONObject()
        val data = JSONArray()
        repeat(millis*10){
            val obj = JSONObject()
            obj.put("id","${it+1}")
            obj.put("name", randomString())
            data.put(obj)
        }
        json.put("data",data)
        return json
    }


    private fun randomString(): String {
        return UUID.randomUUID()?.toString()?.replace("-", "")?.substring(0..8) ?: "12345678"
    }

}