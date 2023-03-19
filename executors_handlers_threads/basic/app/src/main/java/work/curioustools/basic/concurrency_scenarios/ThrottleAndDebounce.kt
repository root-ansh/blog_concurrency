package work.curioustools.basic.concurrency_scenarios

import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

/*
 * Throttling and debouncing.
 * - Throttling and debouncing are technically NOT concurrency scenarios, but flow handling scenarios
 * - Assume that a network connection is made from client to server and response is received.
 * - The throttling will actually happen either during the time when a request is made, or when the
 *   response is received. It happens on the main thread itself and is not  concerned about a
 *   background thread
 * - A typical example scenario 1 :
 *   A button when pressed will start a request for data. if user presses the button multiple times,
 *   multiple request will be made. thus to prevent multiple clicks by the user, we can ensure that
 *   subsequent clicks does not cause a network request and thereby write a custom onclick listener,
 *   that will trigger a request only at a certain time or user click. check throttling and
 *   debouncing for more info
 *
 * - A typical example scenario 2 :
 *   Same as 1, but with an edittext, when user presses a key, a search request is made. to prevent
 *   unnecessary search requests, we should either write a custom text watcher, that uses
 *   throttling/debouncing to not make a search request on every character
 */


/**
 * a basic implementation of how a simple debounced watcher can be implemented
 * debouncing = cancel all old calls + make a new call that will be executed after n seconds
 * ps: a need for handler arrives because we have to cancel currently executing tasks
*/
fun addTextChangedListenerDebounced(editText: EditText, waitingTime: Long, callback: (debouncedWord: String?) -> Unit) {
    val handler: Handler? = editText.handler
    if(handler==null){
        println("EditText(${editText.id}) has not got its handler set. It might not be connected to window. returning")
        return
    }
    var task = Runnable { callback(null) }
    val listener = object :TextWatcher{
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            handler.removeCallbacks(task)
            task = Runnable { callback(s?.toString()) }
            handler.postDelayed(task, waitingTime)
        }
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun afterTextChanged(s: Editable?) {}
    }
    editText.addTextChangedListener(listener)
}

/**
 * a basic/advanced implementation of Throttling TextWatcher.
 * will keep on making calls, but will not make a call until x time has passed in between subsequent calls.
 * highly unuseful unless your goal is to make a few calls instead of all.
 * (in debouncing its mostly 1 call instead of all, but here it will be more than 1)
 */
fun addTextChangedListenerThrottled(editText: EditText, interval: Long = 200, onWordAvailable: (debouncedWord: String?) -> Unit) {
    var lastExecutionTime = 0L
    val listener = object :TextWatcher{
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            val latestTime = System.currentTimeMillis()
            if (latestTime - lastExecutionTime >= interval) {
                lastExecutionTime = latestTime
                onWordAvailable.invoke(s?.toString())
            }
        }
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun afterTextChanged(s: Editable?) {}
    }

    editText.addTextChangedListener(listener)
}


//----------------------This is how Rx observable wrappers are written for android sdk----------------------

class RxObservable(private val origEditText: EditText){

    private var debounceDelay: Long = 200
    private val editTextHandler: Handler = origEditText.handler
    private var onTextAvailable: ((String?) -> Unit)? = null

    private var currentInput:String? = null
    private val task: Runnable = Runnable { onTextAvailable?.invoke(currentInput) }

    fun debounceFirst(delay:Long):RxObservable{
        debounceDelay = delay
        origEditText.addTextChangedListener(
            object : TextWatcher {
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    currentInput = s?.toString()
                    editTextHandler.removeCallbacks(task)
                    editTextHandler.postDelayed(task, debounceDelay)
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun afterTextChanged(s: Editable?) {}
            }
        )
        return this
    }

    private var lastExecutionTime = 0L
    fun throttleFirst(waitingTime: Long):RxObservable{
        origEditText.addTextChangedListener(
            object :TextWatcher{
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val latestTime = System.currentTimeMillis()
                if (latestTime - lastExecutionTime >= waitingTime) {
                    lastExecutionTime = latestTime
                    onTextAvailable?.invoke(s?.toString())
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}

        }

        )
        return this
    }

    fun subscribe(callback: (debouncedWord: String?) -> Unit){
        onTextAvailable = callback
    }
}

fun EditText.inputsACharacter():RxObservable{
    return RxObservable(this)
}







