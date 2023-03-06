package work.curioustools.rxjava1

import io.reactivex.rxjava3.core.Observable //imp : this should be io.whatever.OurClasses, nothing else
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import org.junit.Assert.*
import org.junit.Test


class RxJava1 {

    @Test
    fun test() {


        val dataSource: String = "This is a string"
        val observableDataSource: Observable<String> = Observable.just(dataSource)

        var receivedData = ""

        val dataSourceListener:Observer<String> =object : Observer<String> {
            override fun onComplete() {
                println("we are done!")
                println(receivedData)
            }

            override fun onSubscribe(d: Disposable?) {

            }

            override fun onNext(t: String?) {
                receivedData += t
            }

            override fun onError(e: Throwable?) {
                println(e?.message)
            }
        }

        observableDataSource.subscribe(dataSourceListener)

        assertEquals(4, 3 + 1)

    }

}