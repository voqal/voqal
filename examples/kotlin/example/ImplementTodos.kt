package example

class ImplementTodos {

    fun add(a: Int, b: Int): Int {
        // TODO: Handle possible overflow when adding large numbers
        return a + b
    }

    fun subtract(a: Int, b: Int): Int {
        // TODO: Handle possible underflow when subtracting large numbers
        return a - b
    }

    fun multiply(a: Int, b: Int): Int {
        // TODO: Handle possible overflow when multiplying large numbers
        return a * b
    }

    fun divide(a: Int, b: Int): Double {
        // TODO: Handle division by zero
        return a.toDouble() / b.toDouble()
    }
}
