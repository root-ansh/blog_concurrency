package work.curioustools.basic.utils

import android.app.Activity
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper

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
