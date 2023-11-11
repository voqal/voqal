/**
 * Exercise: highlight different sections of the code and ask Voqal "what does this do?"
 */
package example

fun add(a: Int, b: Int): Int {
    return a + b
}

fun subtract(a: Int, b: Int): Int {
    return a - b
}

fun multiply(a: Int, b: Int): Int {
    return a * b
}

fun divide(a: Int, b: Int): Int {
    if (b == 0) {
        throw IllegalArgumentException("Cannot divide by zero")
    }
    return a / b
}

fun power(base: Int, exponent: Int): Int {
    return Math.pow(base.toDouble(), exponent.toDouble()).toInt()
}