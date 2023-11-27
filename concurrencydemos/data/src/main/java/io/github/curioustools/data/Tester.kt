package io.github.curioustools.data

object Tester {

    @JvmStatic
    fun main(args: Array<String>) {
        DataProviders().run {
            //longRunningTaskThreadSleep().also { println("received data: $it") }
            //getDataAsStream().forEach { print(it) }
            //getDataAsSequence().onEach { print(it)  }
        }


    }
}