package work.curioustools.basic.utils

import android.app.Activity
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.util.Log
import androidx.annotation.MainThread
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

/*
 * - Android provides handler and HandlerThread class for a simple parallel execution.
 *
 * - As taught by java frameworks, creating multiple threads is bad and consumes large memory.
 *
 * - So, a HandlerThread is simply a thread that will keep on running forever as it has an
 *   instance of Looper inside it. Looper does not allow thread to finish.
 *
 * - And a Handler is a like an interface for HandlerThread. an instance of handler can be used
 *   to pass runnables onto this infinitely running thread to be executed
 *
 * - we also create a clean() function that will forcefully quit the thread and remove any
 *   tasks that needed to be executed by looper
 * */
class ParallelLane{
    private val parallelThread = HandlerThread("parallelThread").also { it.start() }
    private val parallelThreadHandler = Handler(parallelThread.looper)

    fun execute(delay: Long,task:Runnable){
        parallelThreadHandler.postDelayed(task,delay)
    }

    fun clean(){
        parallelThreadHandler.removeCallbacksAndMessages(null)
        parallelThread.quitSafely()
    }
}

// - this is similar to Parallel Lane, but we cannot get access to main thread as its
//   construction/destruction is controlled by os. Thus we just attach a handler to main looper and
//   pass our tasks to it.
// - this also means that this is a MEMORY LEAK, as activity can get destroyed at any time
// - better is to use activity.runonUiThread{} / activity.postDelayed{} directly
class MainLane(activity: Activity){
    private val mainThreadHandler = Handler(activity.mainLooper)

    fun execute(delay: Long,task:Runnable){
        mainThreadHandler.postDelayed(task,delay)
    }

    fun clean(){
        mainThreadHandler.removeCallbacksAndMessages(null)
    }
}


class QueueTask<T>(
    val run: () -> T,
    val timeout:Long? = null,
    val onSuccess:((T)->Unit)? = null,
    val onFailure: ((Throwable)->Unit)? = null
)

class AppExecutor private constructor(){

    private fun <T> executeWithTimeoutInParallelAndRunCallbacksOnMainThread(task: QueueTask<T>) {
        val t = Thread{
            val result: Result<T> = kotlin.runCatching { task.run() }

            Handler(Looper.getMainLooper()).post {
                if (result.isSuccess) task.onSuccess?.invoke(result.getOrThrow())
                else task.onFailure?.invoke(result.exceptionOrNull() ?: Exception("null"))
            }

            println("thread:${Looper.myLooper()?.thread?.id} started with  ${if(this== singleton) "singleton" else "new"} finished")

        }

        println("starting thread:${t.id} with ${if(this== singleton) "singleton" else "new"} instance")
        t.start()

    }

    fun <T> execute(task:QueueTask<T>){
        executeWithTimeoutInParallelAndRunCallbacksOnMainThread(task)
    }

    fun <T> execute(task: ()->T){
        val queueTask = QueueTask(task)
        execute(queueTask)
    }

    companion object{
        private var singleton: AppExecutor? = null


        fun sharedQueueTask(): AppExecutor {

            if(singleton !=null) return singleton!!
            synchronized(AppExecutor::class.java){
                if(singleton !=null)return singleton!!
                else{
                    singleton = AppExecutor()
                    singleton
                    return singleton!!
                }
            }
        }

        fun ioTask() = AppExecutor()

    }
}


// an executor which asks for either to execute task in main thread parallel thread
// or parallel+sequential threadpool and returns result in main thread
class AppExecutor2 {
    private var sharedPoolExecutor: ThreadPoolExecutor? = ThreadPoolExecutor(1, 4, 10, TimeUnit.SECONDS, LinkedBlockingQueue(4), ThreadPoolExecutor.DiscardOldestPolicy())

    private val mainThreadExecutor: Handler = Handler(Looper.getMainLooper())

    private var executeOnMainThread = false
    private var executeOnIOThread = false
    private var executeOnSharedThread = true

    fun inMainThread():AppExecutor2{
        executeOnMainThread = true
        executeOnIOThread = false
        executeOnSharedThread = false
        return this
    }

    fun inIOThread():AppExecutor2{
        executeOnMainThread = false
        executeOnIOThread = true
        executeOnSharedThread = false
        return this
    }

    fun inSharedThread():AppExecutor2{
        executeOnMainThread = false
        executeOnIOThread = false
        executeOnSharedThread = true
        return this
    }

    fun <T> execute(task: Task<T>) {
        kotlin.runCatching {
            when(true){
                executeOnMainThread -> {
                    mainThreadExecutor.post {
                        val result = task.action()
                        task.onSuccess(result)
                    }
                }
                executeOnSharedThread ->{
                    sharedPoolExecutor?.execute {
                        val result = task.action()
                        mainThreadExecutor.post { task.onSuccess(result) }
                    }
                }
                executeOnIOThread ->{
                    thread {
                        val result = task.action()
                        mainThreadExecutor.post { task.onSuccess(result) }
                    }
                }
                else ->{}
            }
        }.exceptionOrNull()?.let {
            it.printStackTrace()
            Log.e(TAG, "job execute error: ${it.message}")
            mainThreadExecutor.post { task.onError(it.message) }
        }
    }

    fun destroy() {
        sharedPoolExecutor?.shutdown()
        sharedPoolExecutor = null
    }

    interface Task<T> {
        fun action(): T?

        @MainThread
        fun onSuccess(result: T?)

        @MainThread
        fun onError(msg: String?){}

    }

    companion object {

        private const val TAG = "JobExecutor"

        @JvmStatic
        fun main(args: Array<String>) {
            // Test Case 1

            val jobExecutor = AppExecutor2()
            jobExecutor.inMainThread().execute(object : Task<String> {
                override fun action(): String {
                    Thread.sleep(2000)
                    return "Hello World!" }
                override fun onSuccess(result: String?) { Log.d(TAG, "Result: $result") }
            })
            jobExecutor.destroy()


            val jobExecutor2 = AppExecutor2()
            jobExecutor2.inSharedThread().execute(object : Task<String> {
                override fun action(): String {
                    Thread.sleep(2000)
                    return "Hello World!" }
                override fun onSuccess(result: String?) { Log.d(TAG, "Result: $result") }
            })
            jobExecutor2.destroy()



            val jobExecutor3 = AppExecutor2()
            jobExecutor3.inIOThread().execute(object : Task<String> {
                override fun action(): String {
                    Thread.sleep(2000)
                    return "Hello World!" }
                override fun onSuccess(result: String?) { Log.d(TAG, "Result: $result") }
            })
            jobExecutor3.destroy()




        }
    }
}

