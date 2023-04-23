package work.curioustools.basic.concurrency_scenarios

import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.zip.GZIPInputStream


object BasicNetworkConnection {
    fun connectSync(url :String =  "https://test-server-qn18.onrender.com/ok?delay=2000" , headers:Map<String,String> = hashMapOf("Connection" to "Keep-Alive", "Accept-Encoding" to "gzip"), log:(Any)->Unit = { println(it) }){
        log("connect() called with: url = $url,headers = $headers, log = $log")
        val urlObj = URL(url)

        val connection:HttpURLConnection? = urlObj.openConnection() as? HttpURLConnection
        if(connection==null) {
            log("connection failed")
            return
        }

        connection.requestMethod = "GET"
        connection.connectTimeout = 10_000
        connection.readTimeout = 10_000

        headers.forEach { (t, u) -> connection.setRequestProperty(t, u) }

        //connection.doOutput = true //android has very strict library that generates requests in contrast to browsers. if we set doutput to true, connection will automatically make a post request, which in turn will fail on the server if a post server is not available

        //-------
        val respCode = connection.responseCode
        val respHeaders = connection.headerFields
        var contentJson: JSONObject? = null


        //either make sure that server returns gzip data, or
        runCatching {
            val stream = GZIPInputStream(connection.inputStream)
            val streamReader = InputStreamReader(stream)
            val content = BufferedReader(streamReader).useLines { lines -> lines.fold(StringBuilder()) { acc, line -> acc.append(line) } }
            log("content string = $content")
            contentJson = JSONObject(content.toString())
        }.getOrElse { e -> e.printStackTrace() }

        log("response code: $respCode")
        log("response headers: $respHeaders")
        log("response string: $contentJson")




    }

}