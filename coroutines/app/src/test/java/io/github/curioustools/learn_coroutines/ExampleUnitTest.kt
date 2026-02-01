package io.github.curioustools.learn_coroutines

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.supervisorScope
import org.junit.Test
import kotlin.concurrent.thread

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    fun parentT(){
        thread { createPasta()  }
        thread { createMaggie()  }
        println("we are done")
        Thread.sleep(4000)
    }
     fun createPasta(){
        println("starting pasta")
         Thread.sleep(1000)
        println("pasta is ready")
    }
     fun createMaggie(){
        println("starting maggie")
         Thread.sleep(900)
        println("maggie is ready")
    }

    @Test
    fun wuthUGC() {
        val ugc = Thread.UncaughtExceptionHandler { _, p1 -> println("UGC caught: ${p1.message}") }
        thread { Thread.sleep(500);println("a") }.also { it.uncaughtExceptionHandler = ugc }
        thread { Thread.sleep(1000);error("b fucked up") }.also { it.uncaughtExceptionHandler = ugc }
        thread { Thread.sleep(1500);println("c") }.also { it.uncaughtExceptionHandler = ugc }
        println("done")
        Thread.sleep(4000)
    }
    @Test
    fun noUGC() {
        thread { Thread.sleep(500);println("a") }
        thread { Thread.sleep(1000);error("b fucked up");println("b") }
        thread { Thread.sleep(1500);println("c") }
        println("done")
        Thread.sleep(4000)
        //done
        //a
        //Exception in thread "Thread-4" java.lang.IllegalStateException: b fucked up
        //c
    }

    suspend fun task(s: String,  d: Long = 200,s2: String = "",){
        if (s=="j1") error("something went wrong for $s $s2")
        delay(d)
        println("success: $s $s2 after $d millis")
    }

    fun println(s: String){
        System.out.println(">>> $s")
    }

    @Test
    fun supervisorScopeCheck(){
        runBlocking {
            val hdlr = CoroutineExceptionHandler { _, err -> println("Handler caught: ${err.message}") }
            launch(hdlr) {
                supervisorScope {
                    val jasync = async { task("j1",200,"async") }
                    val j1 = launch { task("j1",200,"launch") }
                    val j2 = launch { task("j2",200) }
                    runCatching {  jasync.await() }.getOrElse { println("try catch Caught  error: ${it.message}") }
                    joinAll(j1,j2)
                    println("all ran")
                }
                println("scope based builder jobs fineshed")
            }//.join()
            println("launch is parallel so this will run before launch's internal stuff is completed")
        }
        println("run blocking finished")
    }
    @Test
    fun wrongSmallCoroutineScopeCheck(){
        runBlocking {
            val hdlr = CoroutineExceptionHandler { _, err -> println("Handler caught: ${err.message}") }
            launch(hdlr) {
                try {
                    coroutineScope {
                        val jasync = async { task("j1",200,"async") }
                        val j1 = launch { task("j1",200,"launch") }
                        val j2 = launch { task("j2",200) }
                        runCatching {  jasync.await() }.getOrElse { println("try catch Caught  error: ${it.message}") }
                        joinAll(j1,j2)
                        println("all ran")
                    }
                }catch (t: Throwable){
                    println("coroutine scope  try catch Caught  error: ${t.message}")
                }
                println("scope based builder jobs fineshed")
            }//.join()
            println("launch is parallel so this will run before launch's internal stuff is completed")
        }
        println("run blocking finished")
    }

    @Test
    fun capitalCoroutineScopeAndJobCheck() {
        runBlocking {
            val hdlr = CoroutineExceptionHandler { _, err -> println("Handler caught: ${err.message}") }
            CoroutineScope(this.coroutineContext + SupervisorJob() + hdlr).launch {
                val jasync = async { task("j1",200,"async") }
                val j1 = launch { task("j1",200,"launch") }
                val j2 = launch { task("j2",200) }
                runCatching {  jasync.await() }.getOrElse { println("try catch Caught  error: ${it.message}") }
                joinAll(j1,j2)
                println("all ran")
            }
            println("launch is parallel so this will run before launch's internal stuff is completed")
        }
        println("run blocking finished")
    }

    @Test
    fun capitalCoroutineScopeAndJobCheck2()  {
         val hdlr = CoroutineExceptionHandler { _, err -> println("Handler caught: ${err.message}") }
         val manualJob = SupervisorJob()
         val scope = CoroutineScope(manualJob + hdlr)
         runBlocking {
             val jasync = scope.async { task("j1",200,"async") }
             val j1 = scope.launch { task("j1",200,"launch") }
             val j2 = scope.launch { task("j2",200) }
             runCatching {  jasync.await() }.getOrElse { println("try catch Caught  error: ${it.message}") }
             joinAll(j1,j2)
             println("all ran")
             manualJob.complete()
         }
         println("run blocking finished")
    }

}