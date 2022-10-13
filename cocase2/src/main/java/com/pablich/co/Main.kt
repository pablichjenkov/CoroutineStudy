package com.pablich.co

import kotlinx.coroutines.*
import printOnComplete

/**
 * Using a regular Job() a child coroutine throws CancellationException.
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
                delay(2000)
            }.printOnComplete("Child 1 completed!", "Child 1 failed")

            // Child 2 Cancellable
            launch {
                delay(500)
                println("Here goes boom...")
                throw CancellationException("Cancelled in Purpose!")
            }.printOnComplete("Child 2 completed!", "Child 2 failed")
        }.printOnComplete("Main Job completed!", "Main Job failed")

        // Random Cancellable coroutine on the same scope
        scope.launch {
            /*while (isActive) {
                // run forever
            }*/
            delay(2500)
        }.printOnComplete("Random coroutine completed!", "Random coroutine failed")

        delay(5000)
    }

}