package io.github.curioustools.data

import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.math.BigInteger
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.TimeUnit
import java.util.stream.Stream
import java.util.zip.GZIPInputStream


open class DataProviders {
    protected var log = { it: Any -> println(it) }
    open  fun updateLogCallBack(callback: (Any) -> Unit) {
        log = callback
    }

    open fun networkConnection(
        url: String = "https://reqres.in/api/users?page=2",
        headers: Map<String, String> = hashMapOf(
            "Connection" to "Keep-Alive",
            "Accept-Encoding" to "gzip"
        ),
    ): JSONObject? {
        log("connect() called with: url = $url,headers = $headers ")
        val urlObj = URL(url)

        val connection = urlObj.openConnection() as? HttpURLConnection ?: return null

        connection.apply {
            requestMethod = "GET"
            connectTimeout = TimeUnit.MINUTES.toMillis(1).toInt()
            readTimeout = TimeUnit.MINUTES.toMillis(1).toInt()
            headers.forEach { (t, u) -> setRequestProperty(t, u) }
            //connection.doOutput = true //android has a very strict library that generates requests in contrast to browsers. if we set doutput to true, connection will automatically make a post request, which in turn will fail on the server if a post server is not available
        }

        //-------
        val respCode = connection.responseCode
        val respHeaders = connection.headerFields
        var contentJson = JSONObject()


        //either make sure that server returns gzip data, or
        runCatching {
            val stream = GZIPInputStream(connection.inputStream)
            val streamReader = InputStreamReader(stream)
            val content = BufferedReader(streamReader).useLines { lines ->
                lines.fold(StringBuilder()) { acc, line -> acc.append(line) }
            }
            log("content string = $content")
            contentJson = JSONObject(content.toString())
        }.getOrElse { e -> e.printStackTrace() }

        log("response code: $respCode")
        log("response headers: $respHeaders")
        log("response string: $contentJson")
        return contentJson
    }

    open fun longRunningTask(duration: Long = TimeUnit.SECONDS.toMillis(3)): JSONObject {
        var currentTime = System.currentTimeMillis().also { log("starting task at $it") }
        val futureTime = currentTime + duration
        var lastLoggedTime = futureTime - currentTime
        while (true) {
            currentTime = System.currentTimeMillis()
            val difference = futureTime - currentTime
            if (difference != lastLoggedTime && difference % 1000 == 0L) {
                log("1 sec passed. current time:$currentTime")
                lastLoggedTime = difference
            }
            if (currentTime >= futureTime) break

        }
        log("task finished at $currentTime")
        return JSONObject().also { it.put("result", "success") }
    }
    open fun longRunningTaskCpuIntensive(num:Int):BigInteger{
        val start = System.currentTimeMillis().also { log("starting task at $it") }
        var fact:BigInteger = BigInteger.ONE
        for (i in 1..num){
            fact = fact.multiply(BigInteger.valueOf(i.toLong()))
        }
        var end = System.currentTimeMillis().also { log("finished task at $it. time taken = ${it-start} ms") }
        return fact
    }

    open fun longRunningTaskThreadSleep(duration: Long = TimeUnit.SECONDS.toMillis(3)): JSONObject {
        System.currentTimeMillis().also { log("starting task at $it") }
        Thread.sleep(duration)
        System.currentTimeMillis().also { log("finished task at $it") }
        return JSONObject().also { it.put("result", "success") }
    }

    open fun immediateData():List<Char>{
        return """
            ╔╦═════════╗
            ║║════┳════╣
            ║║════┻════╣
            ║║═════════╝
            ║║
            ║║
            ║║
            ╚╝
        """.trimIndent().toList()
    }
}

