package work.curioustools.basic

import org.junit.Test

import org.junit.Assert.*
import work.curioustools.basic.concurrency_scenarios.BasicNetworkConnection
import work.curioustools.basic.concurrency_scenarios.f

/**
 * Example local unit work.curioustools.basic.utils.test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        //BasicNetworkConnection.connect()
        assertEquals(4, 2 + 2)

        val f = f()
//        while (!f.isDone || !f.isCancelled){
//            println("waiting for future at t= ${System.currentTimeMillis()}")
//            Thread.sleep(100)
//        }
        println("start @ ${System.currentTimeMillis()}")
        println(f.get())
        println("end @ ${System.currentTimeMillis()}")


    }
}