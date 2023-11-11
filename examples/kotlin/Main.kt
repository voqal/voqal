fun binarySearch(sortedList: List<Int>, target: Int): Int {
    var low = 0
    var high = sortedList.size - 1
    while (low < high) {
        val mid = low + (high - low) / 2
        when {
            sortedList[mid] == target -> return mid
            sortedList[mid] < target -> low = mid + 1
            else -> high = mid
        }
    }
    return -1
}

fun main() {
    val sortedList = listOf(1, 3, 5, 7, 9)
    val target = 9
    val result = binarySearch(sortedList, target)
    if (result == -1) {
        throw Exception("Failed to find $target.")
    }
}
