
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableEmitter
import io.reactivex.rxjava3.core.ObservableOnSubscribe
import io.reactivex.rxjava3.functions.Supplier
import io.reactivex.rxjava3.observables.ConnectableObservable
import utils.Patterns
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

/**
 * Observerables.
 * Observerables are basically data streams. todo :complete definition
 *
 * Cold Observables : "Most data-driven Observables are cold, and this includes the Observable.just() and
 * Observable.fromIterable() factories" //todo definition
 */

fun getObservableFromJust():Observable<String>{
    return Observable.just("a","b","c","d","e","f","g","h","i","j")
}

// we can pass a complete instance of ObservableOnSubscribe class to Observable.create to create a custom observable
fun getObservableFromCreate(pIdx:Int=1): Observable<Char> {
    val pattern = Patterns.patters[pIdx]
    val patternEmitter = object : ObservableOnSubscribe<Char> {
        override fun subscribe(emitter: ObservableEmitter<Char>) {
            runCatching {
                pattern.forEach {
                    Thread.sleep(10)
                    emitter.onNext(it)
                }
                emitter.onComplete()
            }.getOrElse { emitter.onError(it) }
        }
    }
    return Observable.create { e ->
        runCatching {
            pattern.forEach {
                Thread.sleep(5)
                e.onNext(it)
            }
            e.onComplete()
        }.getOrElse { e.onError(it) }
    }

}

fun getObservableFromCreateNumbers(): Observable<String> {
    return Observable.create { emitter->
        (1..10).forEach {
            Thread.sleep(500)
            println("Thread@${Thread.currentThread().id} Processing...")
            emitter.onNext(it.toString())
        }
        emitter.onComplete()
    }
}

// an infinite Observable. this would keep on emitting for eternity. note : this runs in a "computation Scheduler" , which is not main thread. so main thread needs to wait for its subscriber to receive callbacks
fun getInfiniteObservableFromInterval(): Observable<String> {
    return Observable.interval(10,300, TimeUnit.MILLISECONDS).map { it.toString() }
}

// future is like a response of runnable task running somewhere(main thread, parallel thread ) . an observable from future will start the execution when subscribed, wait for its result, and return it in onNext when available
fun getObservableFromFuture():Observable<String>{
    val  future: Future<String> = Executors.newSingleThreadExecutor().submit<String> {
        Thread.sleep(2000)
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

    return Observable.fromFuture(future)
}

// Observable from empty emits  nothing and straight calls onComplete
fun getObservableFromEmpty():Observable<String>{
    return Observable.empty<String>()
}

// Observable from error emits nothing and straight calls Error
fun getObservableFromError():Observable<String>{
    return Observable.error(Exception("DO NOT SUBSCRIBE"))
}

// Observable from never emits anything and just exits. it never calls onComplete or onError
fun getObservableFromNever():Observable<String>{
    return Observable.never()
}

//range will emit numbers from start to end. iterable will emit data via an iterable
fun getObservableFromRangeAndIterable():Pair<Observable<Int>,Observable<Char>>{
    val obs1 = Observable.range(20,11)
    val obs2 = Observable.fromIterable("ansh_sachdeva".asIterable())
    return Pair(obs1,obs2)
}




// if the source for observable is constantly changing, we can use a Deferred observable that will automatically generate a new observable for every subscriber, and therefore every subscriber will get an observable with new source
fun observingDynamicStaticObservableVsDynamicDeferredObservable(){
    var source = "ansh_sachdeva"
    val observable = Observable.fromIterable(source.asIterable())

    run {
        print("\n subscriber1:")
        observable.subscribe{print("$it.")}
        source = "changed"
        print("\n subscriber2:")
        observable.subscribe{print("$it.")}
    }

    var ns = "ansh_sachdeva"
    val observableSupplier = Supplier { Observable.fromIterable(ns.asIterable()) }
    val deferredObservable = Observable.defer(observableSupplier)
   run {
       print("\n n_subscriber1: ")
       deferredObservable.subscribe{print("$it,")}
       ns = "changed"
       print("\n n_subscriber2: ")
       deferredObservable.subscribe{print("$it,")}
   }
}


// callable is a java interface similar to runnable, but it returns a value. so this is in essence similar to calling Observable.just, but with multiple codelines(enclosed in a callback) getting executed instead of 1 codeline. is  also a better way of calling than observable.just(..) since it can pass errors in callback as events instead of abruptly killing the code
fun getObservableFromCallable(erroneous:Boolean=false):Observable<Int>{
    val callable = Callable { if(erroneous)  error("WTF DUDE!") else 4/2 }
    return Observable.fromCallable(callable)
}


fun getObservableFromMerge():Observable<Int>{
    val obs1 = Observable.range(1,5)
    val obs2 = Observable.range(11,5)
    val obs3 = Observable.range(21,5)
    return Observable.merge(obs1,obs2,obs3)
}

//same to merge but unlike merge, it will retain the concatenation sequence and always return values in the same order
fun getObservableFromConcat():Observable<Int>{
    val obs1 = Observable.range(1,5)
    val obs2 = Observable.range(11,5)
    val obs3 = Observable.range(21,5)
    return Observable.concat(obs1,obs2,obs3)

}


// this is like creating a single observable from multiple observables, where the first observable is only used for emission, while others are discarded
fun getObservableFromAmbiguous():Observable<String>{
    class StringEmitter(private val delay:Long, private val char: String):ObservableOnSubscribe<String> {
        override fun subscribe(emitter: ObservableEmitter<String>) {
            runCatching {
                (1..10).forEach { _ ->
                    Thread.sleep(delay)
                    emitter.onNext(char)
                }
                emitter.onComplete()
            }.getOrElse { emitter.onError(it) }
        }
    }

    val obs1 = Observable.create(StringEmitter(300,"o1"))
    val obs2 =  Observable.create(StringEmitter(200,"o2"))
    val obs3 =  Observable.create(StringEmitter(100,"o3"))
    return Observable.amb(mutableListOf(obs1,obs2,obs3))
}

fun getObservableFromZipper():Observable<String>{
    val obs1 = Observable.range(1,5)
    val obs2 = Observable.range(11,5)
    val obs3 = Observable.range(21,5)
    return Observable.zip(obs1,obs2,obs3){a,b,c -> "$a|$b|$c" }

}




// todo defining+usecase
fun getConnectableObservable():ConnectableObservable<String>{
    return getInfiniteObservableFromInterval().publish()
}


// todo defining+usecase
fun singleMaybeAndCompletableObservables(erroneous: Boolean=false){}





fun main(){
    //consuming observers

    getObservableFromCreate().subscribe { print(it) }
    println()
    getObservableFromJust().subscribe { print(it) }
    println()

    run {
        getInfiniteObservableFromInterval().subscribe { print("$it ") }
        println()
        Thread.sleep(5000) //must wait for some time for this to actually run
    }


}