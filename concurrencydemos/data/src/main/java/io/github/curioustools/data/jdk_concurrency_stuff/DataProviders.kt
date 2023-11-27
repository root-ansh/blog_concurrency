package io.github.curioustools.data.jdk_concurrency_stuff

import io.github.curioustools.data.DataProviders
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.TimeUnit
import java.util.stream.Stream
import java.util.zip.GZIPInputStream


class ConcurrentDataProviders:DataProviders() {
    open fun immediateDataAsStream(): Stream<Char> = Stream.of(*immediateData().toTypedArray())
    open fun immediateDataAsSequence(): Sequence<Char> = Sequence { immediateData().iterator() }


    // todo data via streams, java flows,future,

}

