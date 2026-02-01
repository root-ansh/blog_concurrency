package io.github.curioustools.learn_coroutines

import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import io.github.curioustools.learn_coroutines.CreateMaggieInfo.CreateMaggieBlock.MB1
import io.github.curioustools.learn_coroutines.CreateMaggieInfo.CreateMaggieBlock.MB2
import io.github.curioustools.learn_coroutines.CreatePastaInfo.CreatePastaBlock.PB1
import io.github.curioustools.learn_coroutines.CreatePastaInfo.CreatePastaBlock.PB2
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.supervisorScope
import java.util.Date

// should be synced to system_design


suspend fun  test(x: String,a: Long= 1000){
    println(" $x started")
    delay(a)
    if (x=="j2") error("j2 is bad") else println("$x:is completed")
}
private fun MainActivity.evaluation() {
    val h = CoroutineExceptionHandler { context, throwable -> println("error is observed: ${throwable.message}") }// only runs in roo
    // should be used in root coroutine // used for observability

    lifecycleScope.launch(Dispatchers.IO) {
        try {
            supervisorScope {
                println("starting:${Date()}")
                val j1 = launch{test("j1",500)}
                val j2 = launch { test("j2",1000)}
                val j3 = launch{test("j3",1500)}
                joinAll(j1,j2,j3)
                println("ending:${Date()}")
            }
        }catch (t: Throwable){
            println("error is caught:${t.message}")
        }
    }
}



fun parent(){
    GlobalScope.launch {
        val j1 = launch { createPasta() }
        val j2 = launch { createMaggie() }
        joinAll(j1,j2)
        println("we are done")
    }
}
suspend fun createPasta(){
    println("starting pasta")
    deley(1000)
    println("pasta is ready")
}
suspend fun createMaggie(){
    println("starting maggie")
    deley(900)
    println("maggie is ready")
}
suspend fun deley(l: Long): Boolean{
    return true
}

// ----decoded

fun createPastaDecoded(info: CreatePastaInfo):Res{
    when(info.curr){
        PB1 -> {
            println("starting pasta")
            info.curr = PB2;if (MagicalClock.deley(1000)) return Res.SUSPENDED
        }
        PB2 -> {
            println("pasta is ready")
            return Res.COMPLETE
        }
    }
    return Res.SUSPENDED
}
fun createMaggieDecoded(info: CreateMaggieInfo):Res{
    when(info.curr){
        MB1 -> {
            println("starting maggie")
            info.curr = MB2;if (MagicalClock.deley(900)) {
                return Res.SUSPENDED
            }
        }
        MB2 -> {
            println("maggie is ready")
            return Res.COMPLETE
        }
    }
    return Res.SUSPENDED
}
data class CreatePastaInfo(var curr: CreatePastaBlock= PB1){ enum class CreatePastaBlock{PB1,PB2} }
data class CreateMaggieInfo(var curr: CreateMaggieBlock = MB1){ enum class CreateMaggieBlock{MB1,MB2} }
enum class Res{SUSPENDED,COMPLETE}


var parentQueue = mutableListOf<Any>()
var finishedJobs = mutableListOf<Any>()
fun coroutineStarter(){
    parentQueue.add(CreatePastaInfo())
    parentQueue.add(CreateMaggieInfo())
    parentJobDecoded()
}
fun parentJobDecoded(){

    while (parentQueue.isNotEmpty()) {
        val task = parentQueue.remove(0)

        val res = when(task) {
            is CreatePastaInfo -> createPastaDecoded(task)
            is CreateMaggieInfo -> createMaggieDecoded(task)
            else -> Res.COMPLETE
        }
        if (res != Res.SUSPENDED) finishedJobs.add(task)
        else {}
        // move on to next item in queue
        //  We don't put THIS task back in the queue YET.  we want the parent to finish
    }
    println("we are done")
}

object MagicalClock{
    fun deley(l: Long): Boolean{
        return true
    }
}


/**

SUMMARY
the parent tracks the execution states of coroutines in queue and executes specific blocks ,
based on current queue element, its execution position and the returned result.
this is called CONTINUATION_PASSING_STYLE

1. the children code is mapped into states i.e an enum representing  a before suspend call and after suspend call, etc
2. these codeblocks when executed are made to return a state , like suspended or completed
3. parent has access to an outside queue object having states already being added.
4. it takes out the states and accordingly calls the function, consumes their result and continues the queue exection, then finishes
5. when a codeblock(say l1) is returning suspend, this means that its suspended function call(say s1) is running on some other coroutine/thread. once its completed, it will be responsibility of that s1 associated "manager" to add l1 back to its parent queue and starting the parent call again
6. this way multiple code blocks gets simontanously executed, because parent did not wait for l1 to complete s1's call and simply started to execute next parallel block
7. the coroutine uses finished jobs to identify if the complete execution is done or the whole system still needs to remain alive
SMALLER SUMMARY :
the coroutine switches back and forth between multiple parallel blocks of a suspend function when it reaches a suspension point to achieve concurrency

------

suspend function : functions which can be paused and resumed without blocking their parent thread. they are compiled into multiple smaller continuation blocks which emit either the result or "suspended" state, which lets their manager , the "coroutine context" to switch to executing continuation blocks of other suspend functions
continuation :  a partial block of a code that represents the lines that has to be excuted before/after suspension point.
suspension points: are suspend function calls made inside a suspend function. the compiler is able to split a function into mini executable blocks or contuations by identifying these.
coroutine context : a class consiting of dispatcher, job and coroutine exception handler. it mainly is responsible for the exact coroutine's structured concurrency behavior
job : a class that holds the suspend functions  represents its lifecycle/management hooks . it exposes start/cancel functions for starting/cancelling the exeution of a suspend function. it also handles the  exceptions and propagates it to the parent where excetpion handler are present(is that correct ?? who is the parent??). it can be deferred job, job or supervisor job
job states :NEW ,ACTIVE, COMPLETING, COMPLETED, CANCELLING, CANCELLED (represented by 3 booleans is completing, is actve, is cancelled)
scope = a class that ties the coroutines to external lifecycle managers like viewmodel, activity ,etc. it is a bridge : multiple continuation blocks representing different functions are being executed concurrently in a structred manner inside of it, using context, while the whole block is kept alive based on the external lifecycle
dispatcher :  a class that provides external thread to context for starting these concurrent continuation execution. at every suspension point, the context decides which thread to use based on dispatcher


 */