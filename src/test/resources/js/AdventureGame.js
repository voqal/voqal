class AdventureGame {
    constructor() {
        this.playerName = "";
        this.playerHealth = 100;
        this.playerGold = 0;
        this.isGameRunning = true;
        this.playerInventory = [];
    }

    startGame() {
        console.log("Welcome to the Extended Adventure Game!");
        this.playerName = prompt("Enter your name:") || "Adventurer";
        console.log(`Hello, ${this.playerName}!`);
        this.gameLoop();
    }

    gameLoop() {
        while (this.isGameRunning) {
            this.showStatus();
            this.showOptions();
            this.handlePlayerChoice();
            this.checkGameOver();
        }
    }

    showStatus() {
        console.log("------------");
        console.log(`Name: ${this.playerName}`);
        console.log(`Health: ${this.playerHealth}`);
        console.log(`Gold: ${this.playerGold}`);
        console.log(`Inventory: ${this.playerInventory.join(", ")}`);
        console.log("------------");
    }

    showOptions() {
        console.log("Choose an action:");
        console.log("1. Explore");
        console.log("2. Rest");
        console.log("3. Visit the Shop");
        console.log("4. View Inventory");
        console.log("5. Craft Items");
        console.log("6. Complete Quests");
        console.log("7. Exit Game");
    }

    handlePlayerChoice() {
        const choice = parseInt(prompt("Enter your choice:"));
        switch (choice) {
            case 1:
                this.explore();
                break;
            case 2:
                this.rest();
                break;
            case 3:
                this.visitShop();
                break;
            case 4:
                this.viewInventory();
                break;
            case 5:
                this.craftItems();
                break;
            case 6:
                this.completeQuests();
                break;
            case 7:
                this.exitGame();
                break;
            default:
                console.log("Invalid choice. Please choose again.");
                break;
        }
    }

    explore() {
        console.log("You venture into the wild...");
        const encounter = Math.floor(Math.random() * 5) + 1;
        switch (encounter) {
            case 1:
                this.findGold();
                break;
            case 2:
                this.encounterEnemy();
                break;
            case 3:
                this.findNothing();
                break;
            case 4:
                this.findItem();
                break;
            case 5:
                this.triggerRandomEvent();
                break;
        }
    }

    findGold() {
        const goldFound = Math.floor(Math.random() * 41) + 10;
        this.playerGold += goldFound;
        console.log(`You found ${goldFound} gold!`);
    }

    encounterEnemy() {
        console.log("An enemy appears!");
        let enemyHealth = Math.floor(Math.random() * 31) + 20;
        let enemyAlive = true;
        while (enemyAlive && this.playerHealth > 0) {
            console.log(`Enemy Health: ${enemyHealth}`);
            const choice = parseInt(prompt("Do you want to (1) Attack, (2) Use Item, or (3) Run?"));
            switch (choice) {
                case 1:
                    const damageToEnemy = Math.floor(Math.random() * 16) + 5;
                    const damageToPlayer = Math.floor(Math.random() * 15) + 1;
                    console.log(`You hit the enemy for ${damageToEnemy} damage.`);
                    this.playerHealth -= damageToPlayer;
                    console.log(`The enemy hits you for ${damageToPlayer} damage.`);
                    if (damageToEnemy >= enemyHealth) {
                        enemyAlive = false;
                        console.log("You defeated the enemy!");
                    } else {
                        console.log("The enemy is still alive.");
                    }
                    break;
                case 2:
                    this.useItemInCombat();
                    break;
                case 3:
                    console.log("You managed to run away!");
                    return;
                default:
                    console.log("Invalid choice.");
                    break;
            }
        }
        if (this.playerHealth <= 0) {
            console.log("You have been defeated!");
            this.isGameRunning = false;
        }
    }

    findNothing() {
        console.log("You find nothing of interest.");
    }

    findItem() {
        const items = ["Potion", "Sword", "Shield", "Amulet"];
        const foundItem = items[Math.floor(Math.random() * items.length)];
        this.playerInventory.push(foundItem);
        console.log(`You found a ${foundItem}!`);
    }

    triggerRandomEvent() {
        console.log("A mysterious event occurs...");
        const event = Math.floor(Math.random() * 3) + 1;
        switch (event) {
            case 1:
                console.log("You feel a strange force, but nothing happens.");
                break;
            case 2:
                console.log("You fall into a trap!");
                const damage = Math.floor(Math.random() * 21) + 10;
                this.playerHealth -= damage;
                console.log(`You lose ${damage} health.`);
                break;
            case 3:
                console.log("A wandering merchant offers you a trade.");
                const tradeSuccess = Math.random() < 0.5;
                if (tradeSuccess) {
                    const tradeItem = "Magic Ring";
                    this.playerInventory.push(tradeItem);
                    console.log(`You traded successfully and received a ${tradeItem}!`);
                } else {
                    console.log("The trade failed, and you lost 10 gold.");
                    this.playerGold -= 10;
                }
                break;
        }
    }

    rest() {
        console.log("You take a rest.");
        const healthRecovered = Math.floor(Math.random() * 21) + 10;
        this.playerHealth += healthRecovered;
        if (this.playerHealth > 100) this.playerHealth = 100;
        console.log(`You recovered ${healthRecovered} health.`);
    }

    visitShop() {
        console.log("You enter the shop.");
        console.log("1. Buy Potion (20 gold)");
        console.log("2. Buy Sword (50 gold)");
        console.log("3. Buy Shield (40 gold)");
        console.log("4. Exit Shop");
        const choice = parseInt(prompt("Enter your choice:"));
        switch (choice) {
            case 1:
                this.buyItem("Potion", 20);
                break;
            case 2:
                this.buyItem("Sword", 50);
                break;
            case 3:
                this.buyItem("Shield", 40);
                break;
            case 4:
                console.log("You leave the shop.");
                break;
            default:
                console.log("Invalid choice.");
                break;
        }
    }

    buyItem(item, cost) {
        if (this.playerGold >= cost) {
            this.playerGold -= cost;
            this.playerInventory.push(item);
            console.log(`You bought a ${item}.`);
        } else {
            console.log("You don't have enough gold.");
        }
    }

    viewInventory() {
        console.log("Your inventory contains:");
        if (this.playerInventory.length === 0) {
            console.log("Nothing.");
        } else {
            this.playerInventory.forEach(item => console.log(`- ${item}`));
        }
    }

    craftItems() {
        console.log("You attempt to craft an item.");
        const craftingSuccess = Math.random() < 0.5;
        if (craftingSuccess) {
            const craftedItem = "Elixir";
            this.playerInventory.push(craftedItem);
            console.log(`You successfully crafted a ${craftedItem}!`);
        } else {
            console.log("Crafting failed, and you lost some materials.");
        }
    }

    completeQuests() {
        console.log("You embark on a quest...");
        const questOutcome = Math.floor(Math.random() * 3) + 1;
        switch (questOutcome) {
            case 1:
                console.log("You successfully completed the quest!");
                const rewardGold = Math.floor(Math.random() * 51) + 50;
                this.playerGold += rewardGold;
                console.log(`You earned ${rewardGold} gold.`);
                break;
            case 2:
                console.log("The quest was tougher than expected.");
                const questDamage = Math.floor(Math.random() * 21) + 20;
                this.playerHealth -= questDamage;
                console.log(`You lost ${questDamage} health.`);
                break;
            case 3:
                console.log("The quest was uneventful.");
                break;
        }
    }

    useItemInCombat() {
        console.log("Your inventory contains:");
        if (this.playerInventory.length === 0) {
            console.log("Nothing.");
        } else {
            this.playerInventory.forEach((item, index) => console.log(`${index + 1}. ${item}`));
            const choice = parseInt(prompt("Which item do you want to use?"));
            if (choice >= 1 && choice <= this.playerInventory.length) {
                const item = this.playerInventory[choice - 1];
                switch (item) {
                    case "Potion":
                        this.playerHealth += 30;
                        if (this.playerHealth > 100) this.playerHealth = 100;
                        console.log("You used a Potion and recovered 30 health.");
                        this.playerInventory.splice(this.playerInventory.indexOf(item), 1);
                        break;
                    case "Sword":
                        console.log("You equip the Sword. It doesn't have an immediate effect.");
                        break;
                    case "Shield":
                        console.log("You equip the Shield. It doesn't have an immediate effect.");
                        break;
                    case "Amulet":
                        console.log("You wear the Amulet. It doesn't have an immediate effect.");
                        break;
                    case "Magic Ring":
                        console.log("You wear the Magic Ring. It doesn't have an immediate effect.");
                        break;
                    default:
                        console.log("This item can't be used in combat.");
                        break;
                }
            } else {
                console.log("Invalid choice.");
            }
        }
    }

    exitGame() {
        const choice = prompt("Are you sure you want to exit? (y/n)").toLowerCase();
        if (choice === "y" || choice === "yes") {
            this.isGameRunning = false;
            console.log(`Thanks for playing, ${this.playerName}!`);
        } else if (choice === "n" || choice === "no") {
            console.log("Continuing game...");
        } else {
            console.log("Invalid choice.");
        }
    }

    checkGameOver() {
        if (this.playerHealth <= 0) {
            console.log("Game Over. You have died.");
            this.isGameRunning = false;
        }
    }
}
