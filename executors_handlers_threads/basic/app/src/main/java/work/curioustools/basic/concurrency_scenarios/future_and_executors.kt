package work.curioustools.basic.concurrency_scenarios

import java.util.concurrent.Executors
import java.util.concurrent.Future

fun f(): Future<String>{
    val  future:Future<String> = Executors.newSingleThreadExecutor().submit<String> {
        Thread.sleep(5000)
        """
            ╔╦═════════╗
            ║║════┳════╣
            ║║════┻════╣
            ║║═════════╝
            ║║
            ║║
            ║║
            ╚╝
        """.trimIndent()

    }
    return future

}

