package com.pablich.co

import kotlinx.coroutines.*
import printOnComplete

/**
 * Different coroutine builders treat exception propagation differently.
 * While launch automatically propagates exceptions when they are thrown, the async
 * coroutine builder is a bit special in that regard.
 * When an exception is thrown inside the async builder that is used as a root
 * coroutine it will rely on the user to consume the exception.
 * */
object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        //runAsyncNoException()
        //runAsyncWithException()
        runAsyncWithExceptionAndSupervisorJob()

    }

    private val scope by lazy {
        CoroutineScope(Job() + Dispatchers.Default)
    }
    private val supervisorScope by lazy {
        CoroutineScope(Job() + Dispatchers.Default)
    }

    private fun runNoException(): Unit = runBlocking {
        val deferred = scope.async {
            delay(50)
            throw IllegalStateException("Async Boom!")
        }

        delay(100)
        println("I'm done")
    }

    private fun runAsyncWithException(): Unit = runBlocking {
        val deferred = scope.async<Int> {
            delay(50)
            throw IllegalStateException("Async Boom!")
        }

        deferred.await() // Deferred is just a Job that returns a result
        println("I'm done")
    }

    /**
     * The async builder will also crash inside a supervisorScope, since a
     * supervisorScope will not notify its parent about exceptions and will rely on
     * children to handle them. In other words, coroutines inside a supervisorScope
     * can be treated as root coroutines
     * */
    private fun runAsyncWithExceptionAndSupervisorJob() = runBlocking {
        scope.launch {
            supervisorScope {
                println("I am the supervisor scope!")

                val deferred = async {
                    delay(50)
                    throw IllegalArgumentException("Async Boom!")
                }

                deferred.await()
                println("Supervisor scope done!")
            }
        }

        // Random Cancellable coroutine on the same scope
        scope.launch {
            delay(200)
        }.printOnComplete("Random coroutine completed!", "Random coroutine failed")

        delay(1000)
        println("Main is done!")
    }

}