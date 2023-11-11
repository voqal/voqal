/**
 * Ask Voqal to rewrite this code so that "callFibImplementation" calls
 * either "slowFib" or "fastFib" by adding a new param called "goFast".
 */

val slowResult = callFibImplementation(10)
val fastResult = callFibImplementation(10)

fun callFibImplementation(n: Int): Long {
    return fastFib(n)
}

fun slowFib(n: Int, a: Long = 0, b: Long = 1): Long {
    if (n == 0) return a
    Thread.sleep(100) //slow down performance
    return slowFib(n - 1, b, a + b)
}

fun fastFib(n: Int, a: Long = 0, b: Long = 1): Long {
    if (n == 0) return a
    return fastFib(n - 1, b, a + b)
}
