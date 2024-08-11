class MissingImports {
    private val random = Random()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd")
    private val file = File("example.txt")
    private val localDate = LocalDate.now()
    private val zoneId = ZoneId.systemDefault()

    fun printRandomNumber() {
        println(random.nextInt(100))
    }

    fun printCurrentDate() {
        println(dateFormat.format(Date()))
    }

    fun printFileExists() {
        println(file.exists())
    }

    fun printLocalDate() {
        println(localDate)
    }

    fun printZoneId() {
        println(zoneId)
    }
}
