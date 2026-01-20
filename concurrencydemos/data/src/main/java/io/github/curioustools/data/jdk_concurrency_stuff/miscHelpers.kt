package io.github.curioustools.data.jdk_concurrency_stuff

fun threadId():String{
    return "(T-${Thread.currentThread().id}):"
}