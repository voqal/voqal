fun main() {
    // Declare a variable
    var num: Int = 5

    // Print the current value
    println("Original value of num: $num")
    // Modify the value
    num = addOne(num)

    // Print the updated value
    println("Updated value of num: $num")
}

fun addOne(x: Int): Int {
    x + 1
}