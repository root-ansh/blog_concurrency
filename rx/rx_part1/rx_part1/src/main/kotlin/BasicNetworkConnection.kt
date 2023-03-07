import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.zip.GZIPInputStream


object BasicNetworkConnection {
    private const val server = "https://test-server-qn18.onrender.com/ok" // checkout https://github.com/root-ansh/web_project_test_server for actual server url
    private  val queryParams = hashMapOf("delay" to "2000")
    private  val reqHeaders = hashMapOf(
        "Connection" to "Keep-Alive",
        "Accept-Encoding" to "gzip",
    )

    fun connect(url :String = server, query:Map<String,String> = queryParams, headers:Map<String,String> = reqHeaders, log:(Any)->Unit = { println(it) }){
        log("connect() called with: url = $url, query = $query, headers = $headers, log = $log")
        val finalUrl = buildString {
            append(url)
            append("?")
            append(query.map { "${it.key}=${it.value}"}.joinToString("&"))
        }
        log(finalUrl)
        val urlObj = URL(finalUrl)


        val connection:HttpURLConnection? = urlObj.openConnection() as? HttpURLConnection
        if(connection==null) {
            log("connection failed")
            return
        }

        connection.let {
            it.requestMethod = "GET"
            it.connectTimeout = 10_000
            it.readTimeout = 10_000

            headers.forEach { (t,u)-> connection.setRequestProperty(t,u) }

            //it.doOutput = true //android has very strict library that generates requests in contrast to browsers. if we set doutput to true, it will automatically make a post request, which in turn will fail on the server if a post server is not available

            //-------
            val respCode = connection.responseCode
            val respHeaders= connection.headerFields
            var contentJson : JSONObject? = null


            //either make sure that server returns gzip data, or
            runCatching {
               val content = BufferedReader(InputStreamReader(GZIPInputStream(connection.inputStream))).useLines { lines ->
                   lines.fold(StringBuilder()) { acc, line -> acc.append(line) }
               }
                log("content string = $content")
                contentJson = JSONObject(content.toString())
           }.getOrElse { e->e.printStackTrace() }

            log("response code: $respCode")
            log("response headers: $respHeaders")
            log("response string: $contentJson")
        }
    }
}

fun main(args: Array<String>) {
    BasicNetworkConnection.connect()
}