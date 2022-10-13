package com.pablich.co

import kotlinx.coroutines.*
import printOnComplete

/**
 * Using a regular Job() a child coroutine throws an exception, different from CancellationException.
 * */
object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        run()
    }

    private val scope = CoroutineScope(Job() + Dispatchers.Default)

    private fun run(): Unit = runBlocking {
        // Main Job
        scope.launch {
            // Child 1 Cancellable
            launch {
                while (isActive) {
                    // run forever
                }
            }.printOnComplete("Child 1 completed!", "Child 1 failed")

            // Child 2 Cancellable
            launch {
                delay(500)
                println("Here goes boom...")
                throw IllegalStateException("Uncaught Exception")
            }.printOnComplete("Child 2 completed!", "Child 2 failed")
        }.printOnComplete("Main Job completed!", "Main Job failed")

        // Random Cancellable coroutine on the same scope
        scope.launch {
            while (isActive) {
                // run forever
            }
        }.printOnComplete("Random coroutine completed!", "Random coroutine failed")

        delay(1000)
    }

}