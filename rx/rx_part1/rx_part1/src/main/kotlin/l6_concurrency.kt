
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.Executors

/**blocking subscribe allows us to wait for observers that are emitting in parallel thread to complete. for eg, check [infiniteSubscription]. instead of running our main thread forever, we can just run it for as long as the subscription is going on**/
fun testBlockingSubscribe1(){
    //getInfiniteObservableFromInterval().subscribe(getNewSubscriber("usual subscribe"))// this will not emit anything since its running parallel thread which gets destroyed immediately when main thread is finished

    getInfiniteObservableFromInterval().blockingSubscribe(getNewSubscriber("blockingSubscribe"))
    println("finished receiving emissions")
}

// Schedulers are like dispatchers for rxjava. they are essentially threadpools which a user can use to perform
// an operation on.  some Schedulers are fix thread pools, while others create threads on the fly
//todo : Schedulers.computation and  observerOn


fun mainThreadScheduler():Scheduler{
    return Schedulers.from(Executors.newSingleThreadExecutor())
}


// Schedulers.io will use one thread from a large, optimised reusable thread pool. if all threads are exhausted, new task will have to wait
fun testSchedulersIOAndComputation(){
   val observer = getObservableFromCreateNumbers()

    observer.subscribeOn(Schedulers.io()).observeOn(Schedulers.computation())
        .blockingSubscribe(getNewSubscriber("scheduler",showDate = false,showThread = true))
    println("Thread@${Thread.currentThread().id}: finished")
}


//this shows that if multiple subscribers are there, computation happens on a new thread whenever called/ todo : not sure if observer is working correctly
fun testSchedulersIO2(){
    val observer = getObservableFromCreateNumbers()

    val configuredObserver = observer.subscribeOn(Schedulers.io()).observeOn(mainThreadScheduler())
    val sub1 = getNewSubscriber<String>("subscriber1",showDate = false,showThread = true)
    val sub2 = getNewSubscriber<String>("subscriber2",showDate = false,showThread = true)
    val sub3 = getNewSubscriber<String>("subscriber3",showDate = false,showThread = true)
    val sub4 = getNewSubscriber<String>("subscriber4",showDate = false,showThread = true)
    configuredObserver.subscribe(sub1)
    configuredObserver.subscribe(sub2)
    configuredObserver.subscribe(sub3)
    configuredObserver.subscribe(sub4)
    println("Thread@${Thread.currentThread().id}: finished. please close app")
    while (true){}

}




// Schedulers.io will reuse one thread for all the subscribers. lateral subscribers will have to wait for previous subscribers to receive onComplete before getting new emissions
fun testSchedulersSingle(){
    val observer = getObservableFromCreateNumbers()

    val configuredObserver = observer.subscribeOn(Schedulers.single()).observeOn(Schedulers.computation())

    configuredObserver.subscribe(getNewSubscriber("subscriber1",showDate = false,showThread = true))
    configuredObserver.subscribe(getNewSubscriber("subscriber2",showDate = false,showThread = true))
    configuredObserver.subscribe(getNewSubscriber("subscriber3",showDate = false,showThread = true))
    configuredObserver.subscribe(getNewSubscriber("subscriber4",showDate = false,showThread = true))
    println("Thread@${Thread.currentThread().id}: finished. please close app")
    while (true){}
}


// todo
fun testSchedulersNewThread(){


    val observer = getObservableFromCreateNumbers()

    val configuredObserver = observer.subscribeOn(Schedulers.newThread()).observeOn(Schedulers.computation())

    configuredObserver.subscribe(getNewSubscriber("subscriber1",showDate = false,showThread = true))
    configuredObserver.subscribe(getNewSubscriber("subscriber2",showDate = false,showThread = true))
    configuredObserver.subscribe(getNewSubscriber("subscriber3",showDate = false,showThread = true))
    configuredObserver.subscribe(getNewSubscriber("subscriber4",showDate = false,showThread = true))
    println("Thread@${Thread.currentThread().id}: finished. please close app")
    while (true){}
}

//todo
fun testSchedulersTrampoline(){
    val observer = getObservableFromCreateNumbers()

    val configuredObserver = observer.subscribeOn(Schedulers.trampoline())//.observeOn(Schedulers.computation())

    configuredObserver.subscribe(getNewSubscriber("subscriber1",showDate = false,showThread = true))
    configuredObserver.subscribe(getNewSubscriber("subscriber2",showDate = false,showThread = true))
    configuredObserver.subscribe(getNewSubscriber("subscriber3",showDate = false,showThread = true))
    configuredObserver.subscribe(getNewSubscriber("subscriber4",showDate = false,showThread = true))
    println("Thread@${Thread.currentThread().id}: finished. please close app")
    while (true){}
}




fun main(){
    testSchedulersTrampoline()
}