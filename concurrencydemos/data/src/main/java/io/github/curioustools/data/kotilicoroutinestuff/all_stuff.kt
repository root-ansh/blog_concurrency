package io.github.curioustools.data.kotilicoroutinestuff

import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.runInterruptible
import kotlinx.coroutines.selects.select
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import java.util.Optional

fun normalFunction(){
    // these are functions from coroutine classes which can be used in a normal function.
    // these are written in a manner val x:ReturnType = y so that it is easy to understand what will
    // be the result and how they are used.
    // gr means generic result since whatever the function returns is the variable's data type.
    // Add more coroutine functions in similar examples or correct them if they are wrong

    val gr: String = runBlocking {
        "hi"
    }
    val completableJob: CompletableJob = SupervisorJob()



}

suspend fun suspendFunction(){
    // these are functions from coroutine classes which can be ONLY used in a suspend function.
    // these are also written in a manner val x:ReturnType = y so that it is easy to understand what will
    // be the result and how they are used.
    // gr again means generic result
    // Add more coroutine functions in similar examples.

    val noResult = delay(250)
    val gr: String = runInterruptible {
        "Hi"
    }
    
    val gr2: String = coroutineScope { 
        "hi"
    }
    val gr3: String = supervisorScope {
        "hi"
    }
    val gr4: String = withTimeout(250){
        "hi"
    }
    val gr5:String = withContext(Dispatchers.IO){
       "hi" 
    }

    val deferredGR : Deferred<String>? = null
    val gr6: String? = deferredGR?.await()

    val gr7: String = select {}




}

fun extensionToScope() {
    // these are functions from coroutine classes which can be ONLY used in a coroutine scope
    // these are also written in a manner val x:ReturnType = y
    // gr again means generic result
    // Add more coroutine functions in similar examples
    runBlocking {
        val job: Job = launch(Dispatchers.IO){

        }

        val deferredGR: Deferred<String> = async {
            "hi"
        }



    }

}

fun extensionToScope2() {
    // these are functions from coroutine classes which can be ONLY used in a coroutine scope
    // these are also written in a manner val x:ReturnType = y
    // gr again means generic result
    // Add more coroutine functions in similar examples
    runBlocking {

        val producer = produce<String> {
            send("Value 1")
            send("Value 2")
        }
        val value1 = producer.receive()
        val value2 = producer.receive()

    }

}