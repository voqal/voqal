class RFunctions {
    fun factorialRecursive(n: Int): Int {
        return if (n <= 1) {
            1
        } else {
            n * factorialRecursive(n - 1)
        }
    }

    fun findMaxIterative(elements: List<Int>): Int {
        var max = elements[0]
        for (element in elements) {
            if (element > max) {
                max = element
            }
        }
        return max
    }
}
