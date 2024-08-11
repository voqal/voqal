class AdventureGame {

    private var playerName: String = ""
    private var playerHealth: Int = 100
    private var playerGold: Int = 0
    private var isGameRunning: Boolean = true
    private var playerInventory: MutableList<String> = mutableListOf()

    fun startGame() {
        println("Welcome to the Extended Adventure Game!")
        println("Enter your name:")
        playerName = readLine() ?: "Adventurer"
        println("Hello, $playerName!")
        gameLoop()
    }

    private fun gameLoop() {
        while (isGameRunning) {
            showStatus()
            showOptions()
            handlePlayerChoice()
            checkGameOver()
        }
    }

    private fun showStatus() {
        println("------------")
        println("Name: $playerName")
        println("Health: $playerHealth")
        println("Gold: $playerGold")
        println("Inventory: ${playerInventory.joinToString(", ")}")
        println("------------")
    }

    private fun showOptions() {
        println("Choose an action:")
        println("1. Explore")
        println("2. Rest")
        println("3. Visit the Shop")
        println("4. View Inventory")
        println("5. Craft Items")
        println("6. Complete Quests")
        println("7. Exit Game")
    }

    private fun handlePlayerChoice() {
        when (readLine()?.toIntOrNull()) {
            1 -> explore()
            2 -> rest()
            3 -> visitShop()
            4 -> viewInventory()
            5 -> craftItems()
            6 -> completeQuests()
            7 -> exitGame()
            else -> println("Invalid choice. Please choose again.")
        }
    }

    private fun explore() {
        println("You venture into the wild...")
        val encounter = (1..5).random()
        when (encounter) {
            1 -> findGold()
            2 -> encounterEnemy()
            3 -> findNothing()
            4 -> findItem()
            5 -> triggerRandomEvent()
        }
    }

    private fun findGold() {
        val goldFound = (10..50).random()
        playerGold += goldFound
        println("You found $goldFound gold!")
    }

    private fun encounterEnemy() {
        println("An enemy appears!")
        val enemyHealth = (20..50).random()
        var enemyAlive = true
        while (enemyAlive && playerHealth > 0) {
            println("Enemy Health: $enemyHealth")
            println("Do you want to (1) Attack, (2) Use Item, or (3) Run?")
            when (readLine()?.toIntOrNull()) {
                1 -> {
                    val damageToEnemy = (5..20).random()
                    val damageToPlayer = (1..15).random()
                    println("You hit the enemy for $damageToEnemy damage.")
                    playerHealth -= damageToPlayer
                    println("The enemy hits you for $damageToPlayer damage.")
                    if (damageToEnemy >= enemyHealth) {
                        enemyAlive = false
                        println("You defeated the enemy!")
                    } else {
                        println("The enemy is still alive.")
                    }
                }
                2 -> useItemInCombat()
                3 -> {
                    println("You managed to run away!")
                    return
                }
                else -> println("Invalid choice.")
            }
        }
        if (playerHealth <= 0) {
            println("You have been defeated!")
            isGameRunning = false
        }
    }

    private fun findNothing() {
        println("You find nothing of interest.")
    }

    private fun findItem() {
        val items = listOf("Potion", "Sword", "Shield", "Amulet")
        val foundItem = items.random()
        playerInventory.add(foundItem)
        println("You found a $foundItem!")
    }

    private fun triggerRandomEvent() {
        println("A mysterious event occurs...")
        val event = (1..3).random()
        when (event) {
            1 -> println("You feel a strange force, but nothing happens.")
            2 -> {
                println("You fall into a trap!")
                val damage = (10..30).random()
                playerHealth -= damage
                println("You lose $damage health.")
            }
            3 -> {
                println("A wandering merchant offers you a trade.")
                val tradeSuccess = (1..2).random() == 1
                if (tradeSuccess) {
                    val tradeItem = "Magic Ring"
                    playerInventory.add(tradeItem)
                    println("You traded successfully and received a $tradeItem!")
                } else {
                    println("The trade failed, and you lost 10 gold.")
                    playerGold -= 10
                }
            }
        }
    }

    private fun rest() {
        println("You take a rest.")
        val healthRecovered = (10..30).random()
        playerHealth += healthRecovered
        if (playerHealth > 100) playerHealth = 100
        println("You recovered $healthRecovered health.")
    }

    private fun visitShop() {
        println("You enter the shop.")
        println("1. Buy Potion (20 gold)")
        println("2. Buy Sword (50 gold)")
        println("3. Buy Shield (40 gold)")
        println("4. Exit Shop")
        when (readLine()?.toIntOrNull()) {
            1 -> buyItem("Potion", 20)
            2 -> buyItem("Sword", 50)
            3 -> buyItem("Shield", 40)
            4 -> println("You leave the shop.")
            else -> println("Invalid choice.")
        }
    }

    private fun buyItem(item: String, cost: Int) {
        if (playerGold >= cost) {
            playerGold -= cost
            playerInventory.add(item)
            println("You bought a $item.")
        } else {
            println("You don't have enough gold.")
        }
    }

    private fun viewInventory() {
        println("Your inventory contains:")
        if (playerInventory.isEmpty()) {
            println("Nothing.")
        } else {
            playerInventory.forEach { println("- $it") }
        }
    }

    private fun craftItems() {
        println("You attempt to craft an item.")
        val craftingSuccess = (1..2).random() == 1
        if (craftingSuccess) {
            val craftedItem = "Elixir"
            playerInventory.add(craftedItem)
            println("You successfully crafted a $craftedItem!")
        } else {
            println("Crafting failed, and you lost some materials.")
        }
    }

    private fun completeQuests() {
        println("You embark on a quest...")
        val questOutcome = (1..3).random()
        when (questOutcome) {
            1 -> {
                println("You successfully completed the quest!")
                val rewardGold = (50..100).random()
                playerGold += rewardGold
                println("You earned $rewardGold gold.")
            }
            2 -> {
                println("The quest was tougher than expected.")
                val questDamage = (20..40).random()
                playerHealth -= questDamage
                println("You lost $questDamage health.")
            }
            3 -> println("The quest was uneventful.")
        }
    }

    private fun useItemInCombat() {
        println("Your inventory contains:")
        if (playerInventory.isEmpty()) {
            println("Nothing.")
        } else {
            playerInventory.forEachIndexed { index, item -> println("${index + 1}. $item") }
            println("Which item do you want to use?")
            val choice = readLine()?.toIntOrNull()
            if (choice != null && choice in 1..playerInventory.size) {
                val item = playerInventory[choice - 1]
                when (item) {
                    "Potion" -> {
                        playerHealth += 30
                        if (playerHealth > 100) playerHealth = 100
                        println("You used a Potion and recovered 30 health.")
                        playerInventory.remove(item)
                    }
                    "Sword" -> println("You equip the Sword. It doesn't have an immediate effect.")
                    "Shield" -> println("You equip the Shield. It doesn't have an immediate effect.")
                    "Amulet" -> println("You wear the Amulet. It doesn't have an immediate effect.")
                    "Magic Ring" -> println("You wear the Magic Ring. It doesn't have an immediate effect.")
                    else -> println("This item can't be used in combat.")
                }
            } else {
                println("Invalid choice.")
            }
        }
    }

    private fun exitGame() {
        println("Are you sure you want to exit? (y/n)")
        when (readLine()?.toLowerCase()) {
            "y", "yes" -> {
                isGameRunning = false
                println("Thanks for playing, $playerName!")
            }
            "n", "no" -> println("Continuing game...")
            else -> println("Invalid choice.")
        }
    }

    private fun checkGameOver() {
        if (playerHealth <= 0) {
            println("Game Over. You have died.")
            isGameRunning = false
        }
    }
}
