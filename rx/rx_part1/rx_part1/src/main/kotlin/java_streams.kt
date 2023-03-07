import java.io.ByteArrayInputStream
import java.util.stream.Stream



fun patternGenerator(i:Int,pat:Int):Char{
    val pattern = Patterns.patters[pat]
    Thread.sleep(20)
    return pattern[i]
}

fun main(){
    val pattern = Patterns.patters[0]
    val stream0: ByteArrayInputStream =pattern.byteInputStream()
    val stream: Stream<Char> = pattern.toList().stream()

    pattern.forEach { print(it) }
}
