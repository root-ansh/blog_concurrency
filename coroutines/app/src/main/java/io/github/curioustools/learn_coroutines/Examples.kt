package io.github.curioustools.learn_coroutines

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentSkipListSet

fun testContextSwitching(){
    GlobalScope.launch {
        val threadNames = ConcurrentSkipListSet<String>()
        repeat(500){ i->
            val j2 = launch(Dispatchers.Default) {
                delay(10)
                val name = Thread.currentThread().name
                val x = if(threadNames.contains(name)) "Default Job loop $i : REUSED thread: $name" else "loop $i : used thread: $name"
                threadNames.add(name)
                println(x)
            }

            val j1 = launch(Dispatchers.IO) {
                delay(10)
                val name = Thread.currentThread().name
                val x = if(threadNames.contains(name)) "IO Job loop $i : REUSED thread: $name" else "loop $i : used thread: $name"
                threadNames.add(name)
                println(x)
            }
            joinAll(j1,j2)
            println(" all tasks executed : names(${threadNames.size} : $threadNames")
        }
    }
}

fun main(){
    testContextSwitching()
}