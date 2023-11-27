package io.github.curioustools.data.kotilicoroutinestuff

import io.github.curioustools.data.DataProviders
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class  CoroutineDataProviders: DataProviders() {
    suspend fun sNetworkConnection(url: String = "https://reqres.in/api/users?page=2", headers: Map<String, String> = hashMapOf("Connection" to "Keep-Alive", "Accept-Encoding" to "gzip")): JSONObject? {
        return withContext(Dispatchers.Default){super.networkConnection(url, headers) }
    }

    suspend fun sLongRunningTask(duration: Long = TimeUnit.SECONDS.toMillis(3)): JSONObject {
        return withContext(Dispatchers.Default){super.longRunningTask(duration)}
    }

    suspend fun sLongRunningTaskThreadSleep(duration: Long = TimeUnit.SECONDS.toMillis(3)): JSONObject {
        return withContext(Dispatchers.Default){super.longRunningTaskThreadSleep(duration)}
    }

    suspend fun sLongRunningTaskDelay(duration: Long = TimeUnit.SECONDS.toMillis(3)): JSONObject {
        return withContext(Dispatchers.Default) {
            System.currentTimeMillis().also { log("starting task at $it") }
            delay(duration)
            System.currentTimeMillis().also { log("finished task at $it") }
            return@withContext JSONObject().also { it.put("result", "success") }
        }
    }

}