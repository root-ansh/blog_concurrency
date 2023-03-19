import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable

/**
 * We have been using subscribe method to test the various observables.It takes a lambda and somehow the lambda
 * receives everything an observable emits.
 * Actually the lambda is a convenience method. what we actually pass is an observer.
 *
 * an observer can receive 4 types of emits: onSubscribe,onError,onComplete and onNext.
 * but we pass a callback for only onNext.
 *
 * Note that the onSubscribe() returns a Disposable. disposables allow us to disconnect an Observable from an
 * Observer so emissions are terminated early, which is critical for infinite or long-running Observables.
 *
 */


fun basicSubscribeWithObserver(){
    val patternObserver = object :Observer<Char>{
        override fun onSubscribe(d: Disposable) { println("subscribed to data stream") }
        override fun onError(e: Throwable) { println("something went wrong $e") }
        override fun onComplete() { println("emission finished from observer") }
        override fun onNext(t: Char) { print(t) }
    }
    getObservableFromCreate(0).subscribe(patternObserver)
}




//we can subscribe multiple times to the same subscriber, and it will restart from initial emission
fun multipleSubscribe(){
    var show = true

    val observer = getInfiniteObservableFromInterval()
    observer.subscribe { if(show) println("observer1 : $it") }

    Thread.sleep(2000)
    observer.subscribe { if(show) println("observer2 : $it") }

    Thread.sleep(5000)
    show = false
}

//connectable observable allows us to receive only remaining emissions. todo more on connectable observable
fun subscribingToConnectableObservable(){
    val connectableObserver = getConnectableObservable()
    connectableObserver.subscribe { println("subs observer1 : $it") }
    connectableObserver.connect()

    Thread.sleep(2000)
    connectableObserver.subscribe { println("subs observer2 : $it") }

    Thread.sleep(5000)
}

fun <T : Any> getNewSubscriber(id:String="",showDate:Boolean=true,showThread: Boolean=false):Observer<T>{
    return object : Observer<T> {
        private fun d() :String{
            val date = if(showDate) System.currentTimeMillis().toString()+":" else ""
            val tid = if(showThread) "Thread@${Thread.currentThread().id}:" else ""
            return "$date$tid"
        }
        val observableID = id
        override fun onSubscribe(d: Disposable) { println("${d()}$observableID:subscribed") }
        override fun onError(e: Throwable) { println("${d()}$observableID:error:${e.message}") }
        override fun onComplete() { println("${d()}$observableID:Completed") }
        override fun onNext(t: T) { println("${d()}$observableID:onNext received: $t") }
    }
}




fun main(){
    //basicSubscribeWithObserver()
    //multipleSubscribe()
    //println("normal infinite observer multi subscribe=======")
    //multipleSubscribe()
    //println("connectable infinite observer multi subscribe=======")
    //subscribingToConnectableObservable()


    //getObservableFromFuture().subscribe(getNewObserver("future"))
    //getObservableFromEmpty().subscribe(getNewObserver("empty"))
    //getObservableFromError().subscribe(getNewObserver("error"))
    //getObservableFromNever().subscribe(getNewObserver("never"))
    //println("finished")

    //observingDynamicStaticObservableVsDynamicDeferredObservable()
//    getObservableFromCallable().subscribe(getNewSubscriber("callable"))
//    getObservableFromCallable(true).subscribe(getNewSubscriber("callable_ep"))

//    getObservableFromMerge().subscribe(getNewSubscriber("merge observer"))
//    getObservableFromConcat().subscribe(getNewSubscriber("concat observer"))
    //getObservableFromAmbiguous().subscribe(getNewSubscriber("ambigous observer"))
    getObservableFromZipper().subscribe(getNewSubscriber("zipper"))

}


//Disposables
// 1. When we subscribe() to an Observable to receive emissions, a stream is created to
//     process these emissions through the Observable chain. and Of course, this uses resources.
//
// 2. When we are done, we want to dispose of these resources so that they can be garbage-collected.
//
// 3. Thankfully, the finite Observables that call onComplete() will and usually dispose of themselves
//    safely when done.
//
// 4. But if we are working with infinite or long-running Observables, we might want to have a handle to explicitly
//    stop the emissions and dispose of everything associated with that subscription. otherwise the garbage collector
//    will probably not collect infinite running subscribers in the background and will cause a memory leak.
//
// 5. The Disposable is the handle we want and is available in onSubscribe of an observer. we can use it to call
//    dispose() , which will stop emissions and dispose off all resources used for that Observer. It also has an
//    isDisposed() method, indicating whether it has been disposed off already.
//
// 6. For multiple infinite observables, we can use CompositeDispose instance, which is like a hashmap of disposables.
//    it can store multiple disposable and can be used to dispose off all the disposables at once


fun infiniteSubscription(){
    val observable = getInfiniteObservableFromInterval()
    val subscriber = object : Observer<String> {
        val observableID = ""
        override fun onSubscribe(d: Disposable) { println("$observableID:subscribed") }
        override fun onError(e: Throwable) { println("$observableID:error:${e.message}") }
        override fun onComplete() { println("$observableID:Completed") }
        override fun onNext(t: String) { println("$observableID:onNext received: $t") }
    }
    observable.subscribe(subscriber)
    while (true){}
}

fun infiniteSubscriptionWithTimeoutUsingDisposable(){
    var disposable:Disposable? = null
    val observable = getInfiniteObservableFromInterval()
    val subscriber = object : Observer<String> {
        val observableID = ""
        override fun onSubscribe(d: Disposable) {
            println("$observableID:subscribed")
            disposable = d
        }
        override fun onError(e: Throwable) { println("$observableID:error:${e.message}") }
        override fun onComplete() { println("$observableID:Completed") }
        override fun onNext(t: String) { println("$observableID:onNext received: $t") }
    }
    observable.subscribe(subscriber)
    val startTime = System.currentTimeMillis()
    while (true){
        val current = System.currentTimeMillis()
        if(current-startTime>=4000){
            println("disposing off the subscription")
            disposable?.dispose()
            break
        }
    }
    println("process is still running")
    while (true){}
}



fun multipleInfiniteSubscriptionsWithTimeoutUsingCompositeDisposable(){
    val disposable1 = getInfiniteObservableFromInterval().subscribe { println("subscriber 1 received: $it") }
    val disposable2 = getInfiniteObservableFromInterval().subscribe { println("subscriber 2 received: $it") }
    val disposable3 = getInfiniteObservableFromInterval().subscribe { println("subscriber 3 received: $it") }

    val compositeDisposable = CompositeDisposable(disposable1,disposable2,disposable3)

    val startTime = System.currentTimeMillis()
    while (true){
        val current = System.currentTimeMillis()
        if(current-startTime>=4000){
            println("disposing off all the subscriptions")
            compositeDisposable.dispose()
            break
        }
    }
    println("process is still running")
    while (true){}


}


