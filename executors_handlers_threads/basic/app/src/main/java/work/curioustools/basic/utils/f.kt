package work.curioustools.basic.utils

import android.widget.EditText
import work.curioustools.basic.utils.edittext.CustomWatchers
import work.curioustools.basic.utils.edittext.addTextChangedListenerDebounced
import work.curioustools.basic.utils.edittext.addTextChangedListenerThrottled
import work.curioustools.basic.utils.edittext.withCustomTextWatcher

fun test(et: EditText){
    //et.inputsACharacter().debounceFirst(100).subscribe { println("processing word: $it ") }
    et.addTextChangedListenerDebounced(100) { println("processing word: $it ")  }
    et.addTextChangedListenerThrottled(1000) { println("processing word $it") }
}


fun test2(et:EditText){
    et.withCustomTextWatcher(CustomWatchers.Default).onWordAvailable { println("user typed $it") }
    et.withCustomTextWatcher(CustomWatchers.ThrottlingWatcher(300)).onWordAvailable { println("user typed $it") }
    et.withCustomTextWatcher(CustomWatchers.DebouncingWatcher(50) { println("user is typing") }).onWordAvailable { println("user typed $it") }
}
