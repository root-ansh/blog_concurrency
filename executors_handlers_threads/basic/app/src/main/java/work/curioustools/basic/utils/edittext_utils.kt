package work.curioustools.basic.utils

import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText


//add watchers using builder pattern
fun EditText.withCustomTextWatcher(customWatcher: EditTextCustomWatchers): EditTextCustomWatchers {
    customWatcher.init(this)
    return customWatcher
}


/**
 * a basic implementation of how a simple debounced watcher can be implemented
 * debouncing = cancel all old calls + make a new call that will be executed after n seconds
 * ps: a need for handler arrives because we have to cancel currently executing tasks
 */
fun EditText.addTextChangedListenerDebounced(waitingTime:Long, callback: (debouncedWord: String?) -> Unit) {
    val handler: Handler? = this.handler
    if(handler==null){
        println("EditText($id) has not got its handler set. It might not be connected to window. returning")
        return
    }
    var task = Runnable { callback(null) }
    val listener = object : TextWatcher {
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            handler.removeCallbacks(task)
            task = Runnable { callback(s?.toString()) }
            handler.postDelayed(task, waitingTime)
        }
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun afterTextChanged(s: Editable?) {}
    }
    addTextChangedListener(listener)
}

/**
 * a basic/advanced implementation of Throttling TextWatcher.
 * will keep on making calls, but will not make a call until x time has passed in between subsequent calls.
 * highly unuseful unless your goal is to make a few calls instead of all.
 * (in debouncing its mostly 1 call instead of all, but here it will be more than 1)
 */
fun EditText.addTextChangedListenerThrottled(interval: Long = 200, onWordAvailable: (debouncedWord: String?) -> Unit) {
    var lastExecutionTime = 0L
    val listener = object : TextWatcher {
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

    addTextChangedListener(listener)
}






