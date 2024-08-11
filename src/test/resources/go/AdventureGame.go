package main

import (
	"bufio"
	"fmt"
	"math/rand"
	"os"
	"strconv"
	"strings"
	"time"
)

type AdventureGame struct {
	playerName     string
	playerHealth   int
	playerGold     int
	isGameRunning  bool
	playerInventory []string
}

func (game *AdventureGame) startGame() {
	fmt.Println("Welcome to the Extended Adventure Game!")
	game.playerName = game.prompt("Enter your name:")
	if game.playerName == "" {
		game.playerName = "Adventurer"
	}
	fmt.Printf("Hello, %s!\n", game.playerName)
	game.gameLoop()
}

func (game *AdventureGame) gameLoop() {
	for game.isGameRunning {
		game.showStatus()
		game.showOptions()
		game.handlePlayerChoice()
		game.checkGameOver()
	}
}

func (game *AdventureGame) showStatus() {
	fmt.Println("------------")
	fmt.Printf("Name: %s\n", game.playerName)
	fmt.Printf("Health: %d\n", game.playerHealth)
	fmt.Printf("Gold: %d\n", game.playerGold)
	fmt.Printf("Inventory: %s\n", strings.Join(game.playerInventory, ", "))
	fmt.Println("------------")
}

func (game *AdventureGame) showOptions() {
	fmt.Println("Choose an action:")
	fmt.Println("1. Explore")
	fmt.Println("2. Rest")
	fmt.Println("3. Visit the Shop")
	fmt.Println("4. View Inventory")
	fmt.Println("5. Craft Items")
	fmt.Println("6. Complete Quests")
	fmt.Println("7. Exit Game")
}

func (game *AdventureGame) handlePlayerChoice() {
	choice := game.promptInt("Enter your choice:")
	switch choice {
	case 1:
		game.explore()
	case 2:
		game.rest()
	case 3:
		game.visitShop()
	case 4:
		game.viewInventory()
	case 5:
		game.craftItems()
	case 6:
		game.completeQuests()
	case 7:
		game.exitGame()
	default:
		fmt.Println("Invalid choice. Please choose again.")
	}
}

func (game *AdventureGame) explore() {
	fmt.Println("You venture into the wild...")
	encounter := rand.Intn(5) + 1
	switch encounter {
	case 1:
		game.findGold()
	case 2:
		game.encounterEnemy()
	case 3:
		game.findNothing()
	case 4:
		game.findItem()
	case 5:
		game.triggerRandomEvent()
	}
}

func (game *AdventureGame) findGold() {
	goldFound := rand.Intn(41) + 10
	game.playerGold += goldFound
	fmt.Printf("You found %d gold!\n", goldFound)
}

func (game *AdventureGame) encounterEnemy() {
	fmt.Println("An enemy appears!")
	enemyHealth := rand.Intn(31) + 20
	enemyAlive := true

	for enemyAlive && game.playerHealth > 0 {
		fmt.Printf("Enemy Health: %d\n", enemyHealth)
		choice := game.promptInt("Do you want to (1) Attack, (2) Use Item, or (3) Run?")

		switch choice {
		case 1:
			damageToEnemy := rand.Intn(16) + 5
			damageToPlayer := rand.Intn(15) + 1
			fmt.Printf("You hit the enemy for %d damage.\n", damageToEnemy)
			game.playerHealth -= damageToPlayer
			fmt.Printf("The enemy hits you for %d damage.\n", damageToPlayer)
			if damageToEnemy >= enemyHealth {
				enemyAlive = false
				fmt.Println("You defeated the enemy!")
			} else {
				enemyHealth -= damageToEnemy
				fmt.Println("The enemy is still alive.")
			}
		case 2:
			game.useItemInCombat()
		case 3:
			fmt.Println("You managed to run away!")
			return
		default:
			fmt.Println("Invalid choice.")
		}
	}

	if game.playerHealth <= 0 {
		fmt.Println("You have been defeated!")
		game.isGameRunning = false
	}
}

func (game *AdventureGame) findNothing() {
	fmt.Println("You find nothing of interest.")
}

func (game *AdventureGame) findItem() {
	items := []string{"Potion", "Sword", "Shield", "Amulet"}
	foundItem := items[rand.Intn(len(items))]
	game.playerInventory = append(game.playerInventory, foundItem)
	fmt.Printf("You found a %s!\n", foundItem)
}

func (game *AdventureGame) triggerRandomEvent() {
	fmt.Println("A mysterious event occurs...")
	event := rand.Intn(3) + 1
	switch event {
	case 1:
		fmt.Println("You feel a strange force, but nothing happens.")
	case 2:
		fmt.Println("You fall into a trap!")
		damage := rand.Intn(21) + 10
		game.playerHealth -= damage
		fmt.Printf("You lose %d health.\n", damage)
	case 3:
		fmt.Println("A wandering merchant offers you a trade.")
		if rand.Float32() < 0.5 {
			tradeItem := "Magic Ring"
			game.playerInventory = append(game.playerInventory, tradeItem)
			fmt.Printf("You traded successfully and received a %s!\n", tradeItem)
		} else {
			fmt.Println("The trade failed, and you lost 10 gold.")
			game.playerGold -= 10
		}
	}
}

func (game *AdventureGame) rest() {
	fmt.Println("You take a rest.")
	healthRecovered := rand.Intn(21) + 10
	game.playerHealth += healthRecovered
	if game.playerHealth > 100 {
		game.playerHealth = 100
	}
	fmt.Printf("You recovered %d health.\n", healthRecovered)
}

func (game *AdventureGame) visitShop() {
	fmt.Println("You enter the shop.")
	fmt.Println("1. Buy Potion (20 gold)")
	fmt.Println("2. Buy Sword (50 gold)")
	fmt.Println("3. Buy Shield (40 gold)")
	fmt.Println("4. Exit Shop")

	choice := game.promptInt("Enter your choice:")
	switch choice {
	case 1:
		game.buyItem("Potion", 20)
	case 2:
		game.buyItem("Sword", 50)
	case 3:
		game.buyItem("Shield", 40)
	case 4:
		fmt.Println("You leave the shop.")
	default:
		fmt.Println("Invalid choice.")
	}
}

func (game *AdventureGame) buyItem(item string, cost int) {
	if game.playerGold >= cost {
		game.playerGold -= cost
		game.playerInventory = append(game.playerInventory, item)
		fmt.Printf("You bought a %s.\n", item)
	} else {
		fmt.Println("You don't have enough gold.")
	}
}

func (game *AdventureGame) viewInventory() {
	fmt.Println("Your inventory contains:")
	if len(game.playerInventory) == 0 {
		fmt.Println("Nothing.")
	} else {
		for _, item := range game.playerInventory {
			fmt.Printf("- %s\n", item)
		}
	}
}

func (game *AdventureGame) craftItems() {
	fmt.Println("You attempt to craft an item.")
	if rand.Float32() < 0.5 {
		craftedItem := "Elixir"
		game.playerInventory = append(game.playerInventory, craftedItem)
		fmt.Printf("You successfully crafted a %s!\n", craftedItem)
	} else {
		fmt.Println("Crafting failed, and you lost some materials.")
	}
}

func (game *AdventureGame) completeQuests() {
	fmt.Println("You embark on a quest...")
	questOutcome := rand.Intn(3) + 1
	switch questOutcome {
	case 1:
		fmt.Println("You successfully completed the quest!")
		rewardGold := rand.Intn(51) + 50
		game.playerGold += rewardGold
		fmt.Printf("You earned %d gold.\n", rewardGold)
	case 2:
		fmt.Println("The quest was tougher than expected.")
		questDamage := rand.Intn(21) + 20
		game.playerHealth -= questDamage
		fmt.Printf("You lost %d health.\n", questDamage)
	case 3:
		fmt.Println("The quest was uneventful.")
	}
}

func (game *AdventureGame) useItemInCombat() {
	fmt.Println("Your inventory contains:")
	if len(game.playerInventory) == 0 {
		fmt.Println("Nothing.")
		return
	}

	for i, item := range game.playerInventory {
		fmt.Printf("%d. %s\n", i+1, item)
	}

	choice := game.promptInt("Which item do you want to use?")
	if choice >= 1 && choice <= len(game.playerInventory) {
		item := game.playerInventory[choice-1]
		switch item {
		case "Potion":
			game.playerHealth += 30
			if game.playerHealth > 100 {
				game.playerHealth = 100
			}
			fmt.Println("You used a Potion and recovered 30 health.")
			game.playerInventory = append(game.playerInventory[:choice-1], game.playerInventory[choice:]...)
		case "Sword", "Shield", "Amulet", "Magic Ring":
			fmt.Printf("You used the %s. It doesn't have an immediate effect.\n", item)
		default:
			fmt.Println("This item can't be used in combat.")
		}
	} else {
		fmt.Println("Invalid choice.")
	}
}

func (game *AdventureGame) exitGame() {
	choice := strings.ToLower(game.prompt("Are you sure you want to exit? (y/n)"))
	if choice == "y" || choice == "yes" {
		game.isGameRunning = false
		fmt.Printf("Thanks for playing, %s!\n", game.playerName)
	} else if choice == "n" || choice == "no" {
		fmt.Println("Continuing game...")
	} else {
		fmt.Println("Invalid choice.")
	}
}

func (game *AdventureGame) checkGameOver() {
	if game.playerHealth <= 0 {
		fmt.Println("Game Over. You have died.")
		game.isGameRunning = false
	}
}

func (game *AdventureGame) prompt(text string) string {
	fmt.Print(text)
	scanner := bufio.NewScanner(os.Stdin)
	scanner.Scan()
	return strings.TrimSpace(scanner.Text())
}

func (game *AdventureGame) promptInt(text string) int {
	for {
		input := game.prompt(text)
		if number, err := strconv.Atoi(input); err == nil {
			return number
		}
		fmt.Println("Invalid input. Please enter a number.")
	}
}
