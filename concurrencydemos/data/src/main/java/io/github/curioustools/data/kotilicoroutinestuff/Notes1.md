
## What is Concurrency, why is it needed?
```text
```

## Threads, Executors, ThreadPools, oh my!
```text
```

## Coroutines OverView: its a different approach to concurrency

By now , we have an idea of what why coroutines are needed. In a nutshell:
1. concurrency is needed for running software programs at scale, and with robustness/smoothness
2. every language has some concurrency frameworks. java has threads/executors. kotlin has 
   threads/executors and coroutines
3. thread framework achieves concurrency by executing blocks of code in different processors, while sharing resources
4. coroutines framework achieves concurrency by creating cooperative blocks of codes which can 
   be suspended/resumed during execution and are managed by small user defined subthreads

### Coroutines  Lessons

1. Marking any function as suspend will not make it a suspendable function. 
   - it will just not allow any other non suspendable function to call this function, thereby creating a useless function if you don't add any external coroutines dependency/
2. Main kotlin library just consists of suspend function and a few coroutine interfaces (package kotlin.coroutines)
   - which again, is not enough to create actual coroutine functions. 
   - additional features are provided via kotlinx.coroutines-core-jvm and  kotlinx.coroutines-android package



### suspend, Coroutine Context,  Dispatcher, Scope, Builders, `runBlocking` and `suspend CoroutineScope.()->Unit = {}` blocks 

the first 5 (suspend, context, dispatchers, scope and builders) form the basic architecture of 
coroutines which helps us achieving concurrent programming using this framework. So, explaining them 
in individual is easier thant to explain how they work together and what is the role of each component.
(Spoiler Alert: CoroutineScope is the main hero of the plot and does everything)

**suspend**  
suspend is a system keyword which marks a particular function as having special "suspendable" characterstic .
The functionality of suspension is achieved using other coroutine classes. if a function is marked suspend:
1. it can only be called from other suspend function or particular suspendable lambdas
2. the code written inside a suspendable function can be paused and resumed before final execution
3. like usual function calls, the suspend function calls are also executed sequentially. notice the sequential icon though
```kotlin
suspend fun apiCall(urlPath:String,query:List<String> = listOf(),resp:Any,millis:Int=500):Any{
   delay(millis.toLong())
   return resp
}
suspend fun task():String{
   val users = (apiCall("https://reqres.in", resp = listOf("1" to "anuj")))
   val (fuId,firstUserName) = users.first()
   val favColor = apiCall("https://reqres.in/color=$fuId", resp = "green")
   return "$firstUserName:$favColor"
}
```
here, the Api calls happen sequentially once the data is returned . also checkout
[challange#1](Challanges.kt).  


**Dispatchers(IO,Main,Default,UnConfined)**

Dispatchers are enum like singleton instances of `CoroutineContext` interface whose main task is to 
inform the executor scope about the threadpool to be used for executing a particular block of code. 
it can be used with a Coroutine Scope Extension, or a scope builder
there are 4 main dispatchers:
1. Main : indicating the codeblock is supposed to be executed on a thread from mainThreadPool
2. IO : indicating the codeblock is supposed to be executed on a thread from ioThreadPool
3. Default : indicating the codeblock is supposed  to be executed on a thread from defaultThreadPool. 
             note that in most cases, it is not main or io , but a seperate threadpool of even more 
             efficient threads and should be instead used to run long running, non-io codeblocks
4. Unconfined : indicating the codeblock is supposed to be executed in the same threadpool that was
                defined/used by its caller.
eg :  

- no dispatcher used (important for next examples):
```kotlin
suspend fun longRunningTask(delayMs:Long,id:Int):String{
    println("${Thread.currentThread().id}:longRunningTask() called with: delayMs = $delayMs, id = $id")
    delay(delayMs)
    return "completed#$id"
}
```

- using a scope builder:
```kotlin
suspend fun longRunningTaskAsync(delay:Long,id:Int):String{
    return withContext(Dispatchers.IO){ longRunningTask(delay,id) }
}
//usage
fun main(){
    runBlocking {
        println(longRunningTaskAsync(200,200))
        println(longRunningTaskAsync(150,150))
    }
}
//output
/*
 * 15:longRunningTask() called with: delayMs = 200, id = 200
 * completed#200
 * 15:longRunningTask() called with: delayMs = 150, id = 150
 * completed#150
 */
```

- using with a scope extension
```kotlin
suspend fun longRunningTaskLaunch(scope:CoroutineScope){
    scope.launch(Dispatchers.IO) { println(longRunningTask(150,150)) }
    scope.launch(Dispatchers.IO) { println(longRunningTask(100,100)) }
}
//usage
fun main(){
    runBlocking {
        longRunningTaskLaunch(this)
    }
}

//output
/*
 * 1:longRunningTaskLaunch() called
 * 15:longRunningTask() called with: delayMs = 150, id = 150
 * 17:longRunningTask() called with: delayMs = 100, id = 100
 * completed#100
 * completed#150
 * */

```


**Coroutine Context **

- It was important to discuss dispatchers before Context itself, as this makes it easy to understand
  now.
- a Coroutine Context is an interface class, which tells the coroutine scope about the designated 
  threadpool for executing a block of code. It has 4 major implementations in the form of Dispatchers
  (IO,Main,Default,UnConfined).
- Honestly a good name for this class would be BaseDispatcher or DispatcherRepo

**Coroutine Scopes(GlobalScope,coroutineScope,viewmodelScope,lifecycleScope..etc)**
- todo

**Coroutine Scope Constructors(withContext(){}, coroutineScope{}  )**
- ~`withContext(newCtx,block)` simply tells the parentscope to switch context of execution to
  for current block to another context~ it creates a new coroutine scope and executes the code in it


**Coroutine Scope Extensions(launch(),async(),produce()) , Deferred<T> and await()**

**misc:delay()**
- `delay()` is simply a coroutine based alternative to Thread.sleep, where it is going to request
  the current codeblock scope/executor to ignore/pause the execution of the said codeblock for some millis
- its a suspend function, meaning its only callable 








**`suspend CoroutineScope.()->Unit = {}` block.**  
this is a weird looking code, but in its essence its just a cobination of a lambda and extension 
function. this block is known as a scope block and represents a codeblock that is being called with 
an instance of a coroutine scope in a suspendable function. checkout this example

```kotlin

// a basic class
class MyClass(val name:String)

// a basic class extension
fun MyClass.printInfo(){
   print(this.name)
}

// a weird looking function which takes an extension function callback as a param
fun runSmoking(body: MyClass.()->Unit){
   val myObj = MyClass("myname")
   body.invoke(myObj)
}

// a normal looking function which takes an  callback  as a param
fun runNonSmoking(body: (MyClass) -> Unit){
   val myObj = MyClass("myname")
   body.invoke(myObj)
}

// calling each of them
fun main(){
   // notice how it is able to use MyClass's variables and functions directly using this
   runSmoking {
      print(this.name)
      print(this.printInfo())
   }

   // notice how it is usin MyClass's variables and functions directly using it and requires an 
   // additional apply call
   runNonSmoking {
      print(it.printInfo())
      print(it.name)
       it.apply {
          print(this.name)
          print(this.printInfo())
       }
   }
}

```
so in a nutshell, this block is known as a scope block and represents a code that is 
being called with an instance of a coroutine scope in a suspendable function