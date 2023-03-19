import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableEmitter
import io.reactivex.rxjava3.core.ObservableOnSubscribe
import kotlin.random.Random
import kotlin.random.nextInt


// wap that takes sends 10_000 random integers, drops first 9900, then drops duplicate value then drops all odd values, then drops another 5 numbers then sorts them and converts them to strings
fun operatorsPart1(){
    val source = (1..10_000).map { Random.nextInt(1..9999) } //random list of 10k numbers
    val observable = Observable.fromIterable(source)
    val transformed =
        observable
            .takeLast(100)                           //suppressing operator
            .distinct()                                //suppressing operator
            .filter { it % 2 != 0  }                    //suppressing operator
            .skip(5)                             //suppressing operator
            .sorted()                                  //transforming operator
            .map { "'$it'" }                           //transforming operator
            //.toList()                                //collection operator. note : converts observable to single<t>

    println("data")
    transformed.subscribe {
        print("$it,")
        //println("error= ${t2.message}")
    }
}


fun  operators3(){

}

//buffer operator : will automatically collect a group of emissions and send in batches instead of 1
fun operatorsPart2(){
    val observable = Observable.create<Int> { emitter ->
        (1..43).forEach {
            Thread.sleep(100)
            emitter.onNext(it)
        }
        emitter.onComplete()
    }

    val transformed = observable.buffer(10)

    transformed.subscribe {
        println("${System.currentTimeMillis()}:received data= $it")
    }

}

fun main(){
    operatorsPart2()
}