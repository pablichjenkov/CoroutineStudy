package com.pablich.co

import kotlinx.coroutines.*
import printOnComplete

/**
 * This case shows the best practices to catch exceptions.
 * Notice that if we don't use scope.launch{...} and use launch{...}
 * in the runBlocking{} scope lambda directly instead. The exception won't be caught.
 * That is because runBlocking{} is a special type of coroutine builder that just
 * bubbles up the exception.
 * */
object Main {

    private val scope by lazy {
        CoroutineScope(Job() + Dispatchers.Default)
    }

    @JvmStatic
    fun main(args: Array<String>) {
        //runLaunch()
        //runAsync()
        runLaunchCatching()
    }

    fun runLaunch() = runBlocking {
        val job = scope.launch {
            delay(10)

            runCatching<Int> {
                throw IllegalArgumentException("An absolute disaster!")
            }.onSuccess {
                println("I got: $it")
            }.onFailure { th ->
                // In this case we must map the Exception type to our Domain Error type.
                // If the exception is not within our Domain of Known Errors type then:
                // 1- Mapped to an UnKnowError type and reported it to the App crash collector
                //    system.
                // 2*- "Let it crash" and the App crash collector system will capture it.
                println("I have recovered from: ${th?.message}")
            }
        }

        delay(100)

        job.invokeOnCompletion { throwable ->
            when (throwable) {
                is CancellationException -> println("Job was cancelled!")
                is Throwable -> println("Job failed with exception!")
                null -> println("Job completed normally!")
            }
        }

        println("Main is done!")
    }

    fun runAsync() = runBlocking {
        val deferred = scope.async<Int> {
            delay(10)
            throw IllegalArgumentException("An absolute disaster!")
        }

        delay(100)

        val result = runCatching {
            deferred.await()
        }

        deferred.invokeOnCompletion { throwable ->
            when (throwable) {
                is CancellationException -> println("Job was cancelled!")
                is Throwable -> println("Job failed with exception!")
                null -> println("Job completed normally!")
            }
        }

        when {
            result.isSuccess -> println("I got: ${result.getOrNull()}")
            result.isFailure -> println("I have recovered from: ${result.exceptionOrNull()?.message}")
        }

        println("Main is done!")
    }

    fun runLaunchCatching() = runBlocking {
        val job = scope.launch {
            launchCatching(
                onException = { ex ->
                    println("I have recovered from: ${ex.message}")
                },
                onClean = {
                    println("Should cleanup any open resource")
                }
            ) {
                delay(50)
                throw IllegalArgumentException("An absolute disaster!")
            }

        }

        job.cancelAndJoin()
        // Cancel happens asynchronously, if not using job.cancelAndJoin() or job.join()
        // we need a delay to see the logs
        // delay(100)

        job.invokeOnCompletion { throwable ->
            when (throwable) {
                is CancellationException -> println("Job was cancelled!")
                is Throwable -> println("Job failed with exception!")
                null -> println("Job completed normally!")
            }
        }

        println("Main is done!")
    }

    // In general a coroutine body should look like bellow
    fun CoroutineScope.generalCoroutinePattern() {
        launch {
            // Open cancelable resources here
            try {
                // Do work here
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                // Handle Exception here
            } finally {
                withContext(NonCancellable) {
                    // Clean opened resources here
                }
            }
        }
    }

}