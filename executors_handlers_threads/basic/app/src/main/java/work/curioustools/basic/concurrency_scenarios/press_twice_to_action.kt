import kotlin.concurrent.thread

var hasPressedBackOnceOnStart = false
fun pressTwiceToAction(onBlocked:() ->Unit, action:()->Unit){
    if(!hasPressedBackOnceOnStart){
        hasPressedBackOnceOnStart = true
        thread {
            Thread.sleep(1000)
            hasPressedBackOnceOnStart=false
        }
        onBlocked.invoke()
    }
    else{
        action.invoke()
    }
}