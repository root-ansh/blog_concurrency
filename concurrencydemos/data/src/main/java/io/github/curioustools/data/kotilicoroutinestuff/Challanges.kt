package io.github.curioustools.data.kotilicoroutinestuff

import io.github.curioustools.data.jdk_concurrency_stuff.threadId
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.json.JSONObject
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext


/**
 * Challenge 1. Create Api Call to get list of users. for the last user, create a
 *              request to get its color, using its id and second api. Don't worry about threads,
 *              even calling all of them on main thread works
 * Challenge 2. Do same as Challenge 1 , but ensure that api calls happen in common parallel thread,
 *              while request is raised on main thread response is received on main thread as well
 * Challenge 3. Do same as Challenge 2 , but ensure that api calls happen in individual threads,
 *              ensuring that api calls happen parallel to main thread , in different threads, but
 *              in sequence
 * Challenge 4. this time use the allColors api to remove dependency and need for individual
 *              thread sequence thus the logic becomes : Parallel call1 for getting users,
 *              parallel call2 for gettign all colors, then joining on main thread for finding
 *              the color via user id
 * Challenge 5. same as step 4 , but do the searching and joining logic in parallel thread too. thus
 *              logic becomes: p1 for getting users , p2 for getting colors, p3 : which gets called
 *              after p1 and p2 for joining and searching, and then returning the answer on main thread
 *
 *
 * Challenge 3. Do same as Challenge 1 , but Cancel The Api Calls if any request takes more than
 *              1 sec to return response
 * Challenge 4. Do same as Challenge 1 , but Cancel The Api Calls if both request takes more than
 *              1 second combined
 * Challenge 5. Do same as Challenge 1 , but Cancel The Api Calls if user has left the screen or
 *              pressed cancel
 *
 */




suspend fun challenge1(api: CoroutineDataProviders):String{


    val users = api.getUsers()
    val (userName, userId) = (users.last() as JSONObject).let {
        it.getString("first_name") to it.getInt("id")
    }
    val color = api.getColorForUser(userId)
    val colorID = color.getString("name")
    return "$userName:$colorID"


}

suspend fun challenge2(api: CoroutineDataProviders): String {
    return withContext(context = Dispatchers.IO) { challenge1(api) }
}

suspend fun challenge3(api: CoroutineDataProviders):String{
    val users = withContext(Dispatchers.IO) { api.getUsers() }

    println("${threadId()} received user data, now trying to get user name and id")
    val user = (users.last() as JSONObject)
    val userName = user.getString("first_name")
    val userId = user.getInt("id")

    val color: JSONObject = withContext(Dispatchers.IO) { api.getColorForUser(userId) }

    println("${threadId()} received color obj, now trying to get color name")
    val colorID = color.getString("name")
    return "$userName:$colorID"
}

suspend fun challenge5(api: CoroutineDataProviders):String{
    val users = withContext(Dispatchers.IO) { api.getUsers() }
    val colors = withContext(Dispatchers.IO) { api.getColors() }

    println("${threadId()} received user data, now trying to get user name and id")
    println("${threadId()} received color obj, now trying to get color name")

    val finalData = withContext(Dispatchers.IO){
        val user = (users.last() as JSONObject)
        val userName = user.getString("first_name")
        val userId = user.getInt("id")

        val colorObj = colors.first { (it as JSONObject).getInt("id") == userId } as JSONObject
        val colorName = colorObj.getString("name")
        "$userName:$colorName"
    }
    return finalData
}

suspend fun challenge4(api: CoroutineDataProviders):String{
    println("${threadId()} requesting for data in async")
    val users = withContext(Dispatchers.IO) { api.getUsers() }
    val colors = withContext(Dispatchers.IO) { api.getColors() }
    println("${threadId()} received user data, now trying to get user name and id")
    println("${threadId()} received color obj, now trying to get color name")

    val user = (users.last() as JSONObject)
    val userName = user.getString("first_name")
    val userId = user.getInt("id")

    val colorObj = colors.first { (it as JSONObject).getInt("id")==userId } as JSONObject
    val colorName = colorObj.getString("name")
    val finalData = "$userName:$colorName"
    return finalData
}

fun challenge6(api: CoroutineDataProviders, scope: CoroutineScope) {
    scope.launch(scope.coroutineContext) {
        println("${threadId()} requesting for data in async")
        val usersReq = async(Dispatchers.IO) { api.getUsers() }
        val colorsReq = async(Dispatchers.IO) { api.getColors() }
        val users = usersReq.await()
        val colors = colorsReq.await()
        println("${threadId()} received user data, now trying to get user name and id")
        println("${threadId()} received color obj, now trying to get color name")
        val user = (users.last() as JSONObject)
        val userName = user.getString("first_name")
        val userId = user.getInt("id")

        val colorObj = colors.first { (it as JSONObject).getInt("id") == userId } as JSONObject
        val colorName = colorObj.getString("name")
        val finalData = "$userName:$colorName"
        println("${threadId()} $finalData")

    }


}

//fun main(){
//    val api = CoroutineDataProviders()
//    runBlocking {
//         println("${threadId()}:"+challenge1(api))
//        // println("${threadId()}:"+challenge2(api))
//        // println("${threadId()}:"+challenge3(api))
//
//
//
//    }
//    println("${threadId()} main finished")
//
//}


suspend fun longRunningTask(delayMs:Long,id:Int):String{

    println("${Thread.currentThread().id}:longRunningTask() called with: delayMs = $delayMs, id = $id")
    delay(delayMs)
    return "completed#$id"
}
//fun main(){
//    runBlocking {
//        println(longRunningTask(200,3))
//        //1:longRunningTask() called with: delayMs = 200, id = 3
//        //completed#3
//    }
//}
suspend fun longRunningTaskAsync(delay:Long,id:Int):String{
    val context:CoroutineContext = Dispatchers.IO
    return withContext(Dispatchers.IO){ longRunningTask(delay,id) }
}
//fun main(){
//    runBlocking {
//        println(longRunningTaskAsync(200,200))
//        println(longRunningTaskAsync(150,150))
//        //output
//        //15:longRunningTask() called with: delayMs = 200, id = 200
//        //completed#200
//        //15:longRunningTask() called with: delayMs = 150, id = 150
//        //completed#150
//    }
//}

suspend fun longRunningTaskLaunch(scope:CoroutineScope){
    scope.launch(Dispatchers.IO) { println(longRunningTask(150,150)) }
    scope.launch(Dispatchers.IO) { println(longRunningTask(100,100)) }
}

fun main(){
    runBlocking {
        longRunningTaskLaunch(this)
    }
}

suspend fun launcher(){

    coroutineScope {
        longRunningTaskLaunch(this)
    }
}


