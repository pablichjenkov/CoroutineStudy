import kotlinx.coroutines.Job

fun Job.printOnComplete(
    successCompletionMsg: String,
    exceptionMsg: String = "",
) {
    invokeOnCompletion {
        if (it != null) {
            println("$exceptionMsg. Th = $it")
        } else {
            println(successCompletionMsg)
        }
    }
}