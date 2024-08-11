class MathOperations {
    fun calculateResult1(x: Int, y: Int): Int {
        val sum = x + y
        val product = x * y
        val difference = x - y
        return (sum + product - difference) + 10
    }

    fun calculateResult2(p: Int, q: Int): Int {
        val difference = p - q
        val product = p * q
        val sum = p + q
        return 2 * (sum + product - difference)
    }
}
