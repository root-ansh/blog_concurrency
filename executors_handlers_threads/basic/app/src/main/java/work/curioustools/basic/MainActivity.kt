package work.curioustools.basic

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import work.curioustools.basic.concurrency_scenarios.BasicNetworkConnection
import work.curioustools.basic.utils.EditTextCustomWatchers
import work.curioustools.basic.utils.addTextChangedListenerDebounced
import work.curioustools.basic.utils.addTextChangedListenerThrottled
import work.curioustools.basic.utils.withCustomTextWatcher

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        BasicNetworkConnection.connectSync()

    }

    fun test(et: EditText) {
        //et.inputsACharacter().debounceFirst(100).subscribe { println("processing word: $it ") }
        et.addTextChangedListenerDebounced(100) { println("processing word: $it ") }
        et.addTextChangedListenerThrottled(1000) { println("processing word $it") }
    }


    fun test2(et: EditText){
        et.withCustomTextWatcher(EditTextCustomWatchers.Default).onWordAvailable { println("user typed $it") }
        et.withCustomTextWatcher(EditTextCustomWatchers.ThrottlingWatcher(300)).onWordAvailable { println("user typed $it") }
        et.withCustomTextWatcher(EditTextCustomWatchers.DebouncingWatcher(50) { println("user is typing") }).onWordAvailable { println("user typed $it") }
    }

}
