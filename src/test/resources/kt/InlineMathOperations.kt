class InlineMathOperations {
    private fun complexCalculation(a: Int, b: Int): Int {
        val sum = a + b
        val product = a * b
        val difference = a - b
        return sum + product - difference
    }

    fun calculateResult1(x: Int, y: Int): Int {
        return complexCalculation(x, y) + 10
    }

    fun calculateResult2(p: Int, q: Int): Int {
        return 2 * complexCalculation(p, q)
    }
}
