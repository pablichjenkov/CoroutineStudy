package com.pablich.co

import kotlinx.coroutines.*
import printOnComplete

/**
 * Using a SupervisorJob() a Cancellable child coroutine throws an Exception.
 * In this case all siblings coroutines in the same scope where the SupervisorJob is installed complete execution.
 * Notice that the inner siblings inside the launch {} coroutine will be cancelled all, and that is because their
 * Job is not the SupervisorJob() in the scope but a regular Job() created as a SupervisorJob -> ChildJob when
 * launch {} is executed.
 * See runWithSupervisorScope() example to create a SupervisorJob() for sibling launch {}
 * See runWithCoroutineScope() example to create a Job() for sibling launch {}
 * */
object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        run()
        //runWithSupervisorScope()
        //runWithCoroutineScope() /* Same behavior as run() */
    }

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private fun run(): Unit = runBlocking {
        // Main Job
        scope.launch {
            // Child 1 Cancellable
            launch {
                /*while (isActive) {
                    // runs forever
                }*/
                delay(1000)
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

    private fun runWithSupervisorScope(): Unit = runBlocking {
        // Main Job
        scope.launch {
            val result = supervisorScope {
                // Child 1 Cancellable
                launch {
                    /*while (isActive) {
                        // runs forever
                    }*/
                    delay(1000)
                }.printOnComplete("Child 1 completed!", "Child 1 failed")

                // Child 2 Cancellable
                launch {
                    delay(500)
                    println("Here goes boom...")
                    throw IllegalStateException("Uncaught Exception!")
                }.printOnComplete("Child 2 completed!", "Child 2 failed")

                "Any Result"
            }
            println("Got result: $result")
        }.printOnComplete("Main Job completed!", "Main Job failed")

        // Random Cancellable coroutine on the same scope
        scope.launch {
            delay(2500)
        }.printOnComplete("Random coroutine completed!", "Random coroutine failed")

        delay(5000)
    }

    private fun runWithCoroutineScope(): Unit = runBlocking {
        // Main Job
        scope.launch {
            val result = coroutineScope {
                // Child 1 Cancellable
                launch {
                    /*while (isActive) {
                        // runs forever
                    }*/
                    delay(1000)
                }.printOnComplete("Child 1 completed!", "Child 1 failed")

                // Child 2 Cancellable
                launch {
                    delay(500)
                    println("Here goes boom...")
                    throw IllegalStateException("Uncaught Exception!")
                }.printOnComplete("Child 2 completed!", "Child 2 failed")

                "Any Result"
            }
            println("Got result: $result")
        }.printOnComplete("Main Job completed!", "Main Job failed")

        // Random Cancellable coroutine on the same scope
        scope.launch {
            delay(2500)
        }.printOnComplete("Random coroutine completed!", "Random coroutine failed")

        delay(5000)
    }

}