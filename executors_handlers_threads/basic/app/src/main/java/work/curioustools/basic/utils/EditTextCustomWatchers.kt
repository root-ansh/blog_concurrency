package work.curioustools.basic.utils

import android.annotation.SuppressLint
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

abstract class EditTextCustomWatchers: TextWatcher {
    private var editText: EditText? = null
    protected var editTextHandler: Handler?=null
    protected var wordAvailableCallback: ((String?) -> Unit)? = null

    fun init(et: EditText, fallbackHandler: Handler?=null){
        this.editText = et
        this.editTextHandler = et.handler?:fallbackHandler
    }
    fun onWordAvailable(callback: (debouncedWord: String?) -> Unit){
        this.wordAvailableCallback = callback
        editText?.addTextChangedListener(this)
    }

    @SuppressLint("StaticFieldLeak")
    object Default: EditTextCustomWatchers(){

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { wordAvailableCallback?.invoke(s?.toString()) }
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun afterTextChanged(s: Editable?) {}

    }
    class ThrottlingWatcher(private val waitingTime: Long = 200 ): EditTextCustomWatchers(){

        private var lastExecutionTime = 0L

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            val latestTime = System.currentTimeMillis()
            if (latestTime - lastExecutionTime >= waitingTime) {
                lastExecutionTime = latestTime
                wordAvailableCallback?.invoke(s?.toString())
            }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun afterTextChanged(s: Editable?) {}

    }
    class DebouncingWatcher(private val debounceDelay: Long = 200, private val onUserStartedTypingAgain: () -> Unit = {}) : EditTextCustomWatchers() {
        /**
         * an advanced implementation of debounced watcher. supports
         * 1. customising via extension(open class)
         * 2. not creating a runnable multiple times
         * 3. supporting onUserStartedTypingAgain callback
         * 4. clears text for spaces
         */
        private var currentInput:String? = null
        private val task: Runnable = Runnable { wordAvailableCallback?.invoke(currentInput) }
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            onUserStartedTypingAgain()
            currentInput = s?.toString()
            editTextHandler?.removeCallbacks(task)
            editTextHandler?.postDelayed(task,debounceDelay)
        }
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun afterTextChanged(s: Editable?) { if(s?.isBlank()==true) currentInput = null }
    }
}