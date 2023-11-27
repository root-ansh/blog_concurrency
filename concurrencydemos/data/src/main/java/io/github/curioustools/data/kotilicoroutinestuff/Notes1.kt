package io.github.curioustools.data.kotilicoroutinestuff

/**
 * What is Concurrency, why is it needed?
 *
 */

/**
 * Threads, Executors, ThreadPools, oh my!
 */


/**
 * Coroutines OverView: its a different approach to concurrency
 */

/**
 * Coroutines Piece by Piece Series Lessons
 * - CPP1 : marking any function as suspend will not make it a suspendable function.
 *   it will just not allow any other non suspendable function to call this function, thereby
 *   creating a useless function if you don't add the external coroutines dependency
 * - CPP2 : Main kotlin library just consists of suspend function and a few coroutine interfaces
 *   (package kotlin.coroutines), which again, is not enough to create actual coroutine functions.
 *   additional features are provided via kotlinx.coroutines-core-jvm and  kotlinx.coroutines-android
 *   package
 * - CPP3 : To make coroutines, we must add coroutines dependency and use one of the context
 *   switcher function. for eg , withContext()
 */

/** CPP4 : why suspend function can't be called by other functions and
 *   how is it different from other functions? to answer this we must understand the whole
 *   coroutine framework and how it modifies the functions
 *
 *
 */