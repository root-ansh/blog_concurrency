import io.reactivex.rxjava3.core.*
import java.util.concurrent.TimeUnit


class MyObservable:Observable<Char>(){
    override fun subscribeActual(observer: Observer<in Char>) {

    }



}



fun t3(){
    //Observable<Long> secondIntervals =
    //Observable.interval(1, TimeUnit.SECONDS);
    //secondIntervals.subscribe(s -> System.out.println(s))
    val intervalObservable: Observable<Long> = Observable.interval(1,TimeUnit.SECONDS)
    intervalObservable.subscribe { println("fired: $it") }
    Thread.sleep(5000)
}

fun t2Observable(){

    // can take atmost 10 items (why?), so better to use for just few items. Observable.just("thing1","thing2","thing3","thing4","thing5", "thing6","thing7","thing8","thing9","thing10",)
    val observable: Observable<String> = Observable.just("hello","h1")
    val transformedObservable: Observable<Int> = observable.map { it.length }
    val observer = {it:Int ->  println(it) }
    transformedObservable.subscribe(observer)
}
fun t1(){
    Flowable.just("Hello").subscribe {it: String -> println(it) }
}

fun main(args: Array<String>) {
    //Flowable.just("Hello world").subscribe(System.out::println);
    println("Hello World!")
    t3()

}