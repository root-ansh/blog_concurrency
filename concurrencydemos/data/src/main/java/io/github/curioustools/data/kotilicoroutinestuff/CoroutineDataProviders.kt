package io.github.curioustools.data.kotilicoroutinestuff

import io.github.curioustools.data.DataProviders
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class CoroutineDataProviders: DataProviders() {
    val dispatcher = Dispatchers.Unconfined
    suspend fun sNetworkConnection(url: String): JSONObject? {
        return withContext(dispatcher){super.networkConnection(url, defHeaders) }
    }

    suspend fun sLongRunningTask(duration: Long = TimeUnit.SECONDS.toMillis(3)): JSONObject {
        return withContext(dispatcher){super.longRunningTask(duration)}
    }

    suspend fun sLongRunningTaskThreadSleep(duration: Long = TimeUnit.SECONDS.toMillis(3)): JSONObject {
        return withContext(dispatcher){super.longRunningTaskThreadSleep(duration)}
    }

    suspend fun longRunningTask(duration: Long = TimeUnit.SECONDS.toMillis(3),op:String="Success"): String {
        return withContext(dispatcher) {
            System.currentTimeMillis().also { log("starting task at $it") }
            delay(duration)
            System.currentTimeMillis().also { log("finished task at $it") }
            op
        }
    }

    suspend fun getUsers(): JSONArray {
        return withContext(Dispatchers.Unconfined){super.ncGetUsers() }
    }
    suspend fun getColors(): JSONArray {
        return withContext(dispatcher){super.ncGetColors() }
    }

    suspend fun getColorForUser(id:Int): JSONObject {
        return withContext(dispatcher){super.ncGetColor(id) }
    }


}