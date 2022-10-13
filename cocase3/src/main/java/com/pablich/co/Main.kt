package com.pablich.co

import kotlinx.coroutines.*
import printOnComplete
import java.lang.IllegalStateException

/**
 * Using a regular Job() a child coroutine throws an Exception but there is a Non-Cancellable sibling running forever.
 * In this case the exception will never get thrown by the parent coroutine. As we have discussed before, a parent
 * coroutine always waits for its children to complete. However, because we have introduced an infinite loop inside the
 * Child 1, it will never complete and the parent will keep waiting indefinitely, or in this case while the main
 * is running.
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
            // Child 1 Non-Cancellable
            launch {
                while (true) {
                    // Makes this coroutine non-cancellable
                }
            }.printOnComplete("Child 1 completed!", "Child 1 failed")

            // Child 2 Cancellable
            launch {
                delay(500)
                println("Here goes boom...")
                throw IllegalStateException("Uncaught Exception!")
            }.printOnComplete("Child 2 completed!", "Child 2 failed")
        }.printOnComplete("Main Job completed!", "Main Job failed")

        // Random Cancellable coroutine on the same scope
        scope.launch {
            delay(2500)
        }.printOnComplete("Random coroutine completed!", "Random coroutine failed")

        delay(5000)
    }

}