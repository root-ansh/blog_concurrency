package io.github.curioustools.data

import org.json.JSONObject
import java.util.concurrent.TimeUnit

/**
 * What is Concurrency, why is it needed?
 * - todo
 *
 * Threads, Executors, ThreadPools, oh my!
 * - todo
 *
 * Coroutines Intro: its a different approach to concurrency
 * - todo
 *
 * Coroutines Piece by Piece Series
 * - CPP1 : marking any function as suspend will not make it a suspendable function.
 *   it will just not allow any other non suspendable function to call this function, thereby
 *   creating a useless function if you don't add the external coroutines dependency
 * - CPP2 : Main kotlin library just consists of suspend function and a few coroutine interfaces
 *   which again, is not enough to create actual coroutine functions
 * - CPP3 : To make coroutines, we must add coroutines dependency and use one of the context switcher function
 *
 *
 */
class  SuspendableDataProviders:DataProviders() {
    suspend fun sNetworkConnection(url: String = "https://reqres.in/api/users?page=2", headers: Map<String, String> = hashMapOf("Connection" to "Keep-Alive", "Accept-Encoding" to "gzip")): JSONObject? {
        return super.networkConnection(url, headers) //todo make suspendible
    }

    suspend fun sLongRunningTask(duration: Long = TimeUnit.SECONDS.toMillis(3)): JSONObject {
        return super.longRunningTask(duration)
    }

    suspend fun sLongRunningTaskThreadSleep(duration: Long = TimeUnit.SECONDS.toMillis(3)): JSONObject {
        return super.longRunningTaskThreadSleep(duration)
    }

    suspend fun sLongRunningTaskDelay(duration: Long = TimeUnit.SECONDS.toMillis(3)): JSONObject {
        System.currentTimeMillis().also { log("starting task at $it") }

        System.currentTimeMillis().also { log("finished task at $it") }
        return JSONObject().also { it.put("result", "success") }
    }

}