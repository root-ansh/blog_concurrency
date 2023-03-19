package work.curioustools.basic.utils

import android.os.Build
import androidx.annotation.RequiresApi
import java.util.stream.Stream

@RequiresApi(Build.VERSION_CODES.N)
fun <T> Stream<T>.withBuffering(size: Int): Stream<List<T>> {
    val list = mutableListOf<T>()
    return flatMap { t ->
        list.add(t)
        if (list.size != size) Stream.empty()
        else {
            val result = list.toList()
            list.clear()
            Stream.of(result)
        }
    }.onClose {
        if (list.isEmpty()) Stream.empty()
        else {
            val result = list.toList()
            list.clear()
            Stream.of(result)
        }
    }
}


@RequiresApi(Build.VERSION_CODES.N)
fun main(){
    val x = Stream.of(1,2,3,4,5,6,7,8,9).withBuffering(2).forEach { println(it) }
}
