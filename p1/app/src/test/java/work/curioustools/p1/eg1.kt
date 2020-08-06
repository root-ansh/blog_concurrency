package work.curioustools.rxjava1

import io.reactivex.rxjava3.core.Observable //imp : this should be io.whatever.OurClasses, nothing else
import io.reactivex.rxjava3.core.ObservableEmitter
import io.reactivex.rxjava3.core.ObservableOnSubscribe
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import org.junit.Assert.*
import org.junit.Test


class eg1 {


    /* pre context :
     * reactive programming refers to dealing with data that is constantly changing. in
     * imperative programming, we treat data as a single source of truth : if i have say x=5
     * then after this line, any piece of code i write, from any thread, will believe x to be
     * always = 5.
     * but in functional programming, we believe that if there is a variable x, it might not
     * have the same value as we think when our line of code is executing. maybe x=5 is such a
     * huge task, that by the time line print(x) is executing, x does not have fully received
     * the value 5, and is currently null.
     *
     * in another scenario, imagine having a list in some global memory
     *  l=[1,2,3,4,5].
     * say an imperitive programming code print all nums of list l is as follows:
     *
     * val x = Gobal.get(l) // took a reference of l at say time t1
     * println("hello")
     * println("hello")
     * println("hello")
     * println("hello")
     * for(i in x.indices):
     *   println(i)  // say time t2,3,4,5,6
     *
     * now we wil expect this code to print all values in l, i.e 1,2,3,4,5. but notice that l is
     * only being accessed once at time t. what if l has received more data by the time the executor
     * reached line println(i) at time t2? it won't be really knowing since x has only accessed l
     * once and does not know about any updates
     *
     * this is where reactive programing achieves results. in reactive  programming, x would rather
     * be always observing the values of l and updating itself whenever a change occurs.
     * thus at any point of time whenever we access x, we would have the complete instance of l. we
     * would also be using functional list i.e x.foreach{it->print(it)}  which conveys a better
     * meaning
     *
     * Thus everything in rx is considered a stream. and since we are considering every data to be a
     *  stream of idk,a *variable changing data*, we  would be handling the callbacks we receive,
     *  when to subscribe and unsubscribe, which threads to use for subscription,
     *  which threads to use for recieving data, which threads to use for updating ui, etc
     * */

    @Test
    fun basicExample1() {
        // goal : to create an observable data source (i.e a data stream) ,and subscribe to it.

        //1 in this example, we have a string as data source, aka a place where our data is present
        val dataSource: String = "This is a string"

        //2 we use a function called `just` to convert our data source to observable data source,
        // meaning a data source which could be observed
        // when the code runs, rxjava will takes the data from source and sends it to observers via
        // various callbacks. the observers are something  attach themselves to this observable
        // (basically livedata waala scene)

        val observableDataSource: Observable<String> = Observable.just(dataSource)


        //3 a temporary variable
        var receivedData = ""

        //4 an observable is an interface  which can be implemented in an anonymous class. it has 4
        // functions that would be called in their sequence: onSubscribe-> onNext-> onComplete or
        // onSubScribe-> onError(). we can write our data handling logic here and rxjava lib will
        // automatically execute those logics
        val dataSourceListener: Observer<String> = object : Observer<String> {
            override fun onComplete() {
                println("we are done!")
                println(receivedData)
            }

            override fun onSubscribe(d: Disposable?) {

            }

            override fun onNext(t: String?) {
                println("we received: $t")
                receivedData += t
            }

            override fun onError(e: Throwable?) {
                println(e?.message)
            }
        }

        //5 attaching observer to observable as written in point 2,4. Note: at this point only the
        // observable will start streaming/ throwing the data. before this, no data is being thrown by observable
        observableDataSource.subscribe(dataSourceListener)

        assertEquals(4, 3 + 1)

        /* 6. Note about the Threading :
         * in rx java, we have schedulers subscribing threads, which are not taken into consideration
         * in this example.todo : please explain it
         *
         * */


    }


    /* Pre context: a single observable can also be created from multiple fixed data sources*/
    @Test
    fun basicExample2() {
        // goal : to create an observable data source from multiple data sources ,and subscribe to it.

        val i1 = 'a';
        val i3 = "Hello";
        val i2 = 7
        val i4 = 22.5;
        val i5 = false

        //2 we pass multiple sources here
        val observableDataSource: Observable<Any> = Observable.just(i1, i2, i3, i4, i5)

        //3 same  as above
        var receivedData = "final output: "

        //4 same as above
        val dataSourceListener = object : Observer<Any> {
            override fun onComplete() {
                println("we are done!")
                println(receivedData)
            }

            override fun onSubscribe(d: Disposable?) {

            }

            override fun onNext(t: Any?) {
                println("we received: $t")
                receivedData += " $t"
            }

            override fun onError(e: Throwable?) {
                println(e?.message)
            }
        }

        //5 same  as above
        observableDataSource.subscribe(dataSourceListener)

        assertEquals(4, 3 + 1)
    }

    /*
     * DIFFERENT TYPES OF OBSERVABLES
     * https://medium.com/better-programming/rxjava-different-ways-of-creating-observables-7ec3204f1e23
     *
     * pre context :
     * >> based on types of datastream (not really an actual difference, just a theoritical point of
     *    view) : hot, cold and collected observables
     * >> based on different purposes :Observable,Single,Maybe,Completable,Flowable(todo)
     *
     * - hot and cold observables: simply a data stream which is changing very rapidly vs
     * a data stream which is not very rapidly changing. this effects in a manner that if you start
     * observing a hot observable late, you miss a lot of data changes
     *
     * - collected observables : collected observables are simply the usual cold or hot observables
     *   but are *made* to be cold, unless a particular function collect() is called. thus if we have
     *   a rapidly changing weather api as data source on which we wrap an observable, and attach
     *   3 obsrvers, they will still not be getting any data until we add a call to .collect(). the
     *   observable doesn't even start throwing data unless this fuction is called.
     *
     * - single, maybe and completeable observers: these are subcategories of observers which does
     *   not bind you to complete observer callbacks. These are used in place of word Observables,
     *   aka like this: Single.just(datasource).subscribe(...), etc
     *
     *   - in single: we only get a callback for onError, onComplete and first change for data stream
     *     ( and that too in onSuccess)
     *
     *   - in maybe, idk, i think almost like single : https://medium.com/tompee/rxjava-ninja-single-maybe-and-completable-b5907dddc5e4
     *
     *   - in completeable, we get only a callback for onComplete and error
     *
     * */

    fun testHotColdCollectedObservableAndSingleMaybeAndCompletableTypes() {
        // TODO: 06-08-2020
    }


    /*
     * pretext: 10 ways to create observables in rx java
     * https://medium.com/better-programming/rxjava-different-ways-of-creating-observables-7ec3204f1e23
     *
     * >> create() ,just() ,defer() ,empty(), never() ,error() ,range() ,interval() ,timer() ,fromX()
     *
     * Note: these would be still incompletely implemented since we are using kotlin here, and
     * kotlin does not allow throwing errors Also, in rxkotlin and rxandroid there are another lot
     * of extension functions and easy to create syntactical sugars for creation of observables
     *
     * >> some of them are not available for either of Obsevrable() Single(),Maybe,Completable, or
     *   Flowable types
     * */
    fun testObservableTypes() {
        val dataSource = arrayOf(1, 2, 3, 4, 5)

        //Observable.just()
        // emits whatever is present inside the just function. It can take between
        // two and nine parameters. If you pass a list or array in just() it will emit the list or
        // array only. Not availa for completable type

        val observableMadeFromJust = Observable.just(dataSource)// prints array obj directly
        //val observableMadeFromJust = Observable.just(11,12,13,14,15)// prints each number in seperate line
        observableMadeFromJust.subscribe { emittedVal -> println(emittedVal) }

        //----------------------------------------------------------------------------------------

        //Oservable.FromX(data source)
        //here we can easily create an observable from a particlar type: array, iterable,
        // action(idk), callable(idk) or future(idk, todo)
        // note: some of them are not available to single, maybe and completable
        val observableDataSource2 = Observable.fromIterable(dataSource.asList())
        val onserVableForArray = Observable.fromArray(dataSource)


        //-----------------------------------------------------------------------------------------
        //Observable.create()
        
        val obsOnSubscribe = object : ObservableOnSubscribe<Int> {
            override fun subscribe(emitter: ObservableEmitter<Int>?) {
                val data = dataSource
                data.forEach { item ->
                    emitter?.onNext(item)
                }
                emitter?.onComplete()
            }

        }
        val observableDirect = Observable.create(obsOnSubscribe)
        //-----------------------------------------------------------------------------------------


    }

}