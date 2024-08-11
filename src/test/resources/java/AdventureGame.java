import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class AdventureGame {

    private String playerName = "";
    private int playerHealth = 100;
    private int playerGold = 0;
    private boolean isGameRunning = true;
    private List<String> playerInventory = new ArrayList<>();
    private final Scanner scanner = new Scanner(System.in);
    private final Random random = new Random();

    public void startGame() {
        System.out.println("Welcome to the Extended Adventure Game!");
        System.out.println("Enter your name:");
        playerName = scanner.nextLine();
        if (playerName.isEmpty()) {
            playerName = "Adventurer";
        }
        System.out.println("Hello, " + playerName + "!");
        gameLoop();
    }

    private void gameLoop() {
        while (isGameRunning) {
            showStatus();
            showOptions();
            handlePlayerChoice();
            checkGameOver();
        }
    }

    private void showStatus() {
        System.out.println("------------");
        System.out.println("Name: " + playerName);
        System.out.println("Health: " + playerHealth);
        System.out.println("Gold: " + playerGold);
        System.out.println("Inventory: " + String.join(", ", playerInventory));
        System.out.println("------------");
    }

    private void showOptions() {
        System.out.println("Choose an action:");
        System.out.println("1. Explore");
        System.out.println("2. Rest");
        System.out.println("3. Visit the Shop");
        System.out.println("4. View Inventory");
        System.out.println("5. Craft Items");
        System.out.println("6. Complete Quests");
        System.out.println("7. Exit Game");
    }

    private void handlePlayerChoice() {
        int choice = scanner.nextInt();
        scanner.nextLine();  // Consume newline
        switch (choice) {
            case 1 -> explore();
            case 2 -> rest();
            case 3 -> visitShop();
            case 4 -> viewInventory();
            case 5 -> craftItems();
            case 6 -> completeQuests();
            case 7 -> exitGame();
            default -> System.out.println("Invalid choice. Please choose again.");
        }
    }

    private void explore() {
        System.out.println("You venture into the wild...");
        int encounter = random.nextInt(5) + 1;
        switch (encounter) {
            case 1 -> findGold();
            case 2 -> encounterEnemy();
            case 3 -> findNothing();
            case 4 -> findItem();
            case 5 -> triggerRandomEvent();
        }
    }

    private void findGold() {
        int goldFound = random.nextInt(41) + 10;
        playerGold += goldFound;
        System.out.println("You found " + goldFound + " gold!");
    }

    private void encounterEnemy() {
        System.out.println("An enemy appears!");
        int enemyHealth = random.nextInt(31) + 20;
        boolean enemyAlive = true;
        while (enemyAlive && playerHealth > 0) {
            System.out.println("Enemy Health: " + enemyHealth);
            System.out.println("Do you want to (1) Attack, (2) Use Item, or (3) Run?");
            int choice = scanner.nextInt();
            scanner.nextLine();  // Consume newline
            switch (choice) {
                case 1 -> {
                    int damageToEnemy = random.nextInt(16) + 5;
                    int damageToPlayer = random.nextInt(15) + 1;
                    System.out.println("You hit the enemy for " + damageToEnemy + " damage.");
                    playerHealth -= damageToPlayer;
                    System.out.println("The enemy hits you for " + damageToPlayer + " damage.");
                    if (damageToEnemy >= enemyHealth) {
                        enemyAlive = false;
                        System.out.println("You defeated the enemy!");
                    } else {
                        System.out.println("The enemy is still alive.");
                    }
                }
                case 2 -> useItemInCombat();
                case 3 -> {
                    System.out.println("You managed to run away!");
                    return;
                }
                default -> System.out.println("Invalid choice.");
            }
        }
        if (playerHealth <= 0) {
            System.out.println("You have been defeated!");
            isGameRunning = false;
        }
    }

    private void findNothing() {
        System.out.println("You find nothing of interest.");
    }

    private void findItem() {
        String[] items = {"Potion", "Sword", "Shield", "Amulet"};
        String foundItem = items[random.nextInt(items.length)];
        playerInventory.add(foundItem);
        System.out.println("You found a " + foundItem + "!");
    }

    private void triggerRandomEvent() {
        System.out.println("A mysterious event occurs...");
        int event = random.nextInt(3) + 1;
        switch (event) {
            case 1 -> System.out.println("You feel a strange force, but nothing happens.");
            case 2 -> {
                System.out.println("You fall into a trap!");
                int damage = random.nextInt(21) + 10;
                playerHealth -= damage;
                System.out.println("You lose " + damage + " health.");
            }
            case 3 -> {
                System.out.println("A wandering merchant offers you a trade.");
                boolean tradeSuccess = random.nextBoolean();
                if (tradeSuccess) {
                    String tradeItem = "Magic Ring";
                    playerInventory.add(tradeItem);
                    System.out.println("You traded successfully and received a " + tradeItem + "!");
                } else {
                    System.out.println("The trade failed, and you lost 10 gold.");
                    playerGold -= 10;
                }
            }
        }
    }

    private void rest() {
        System.out.println("You take a rest.");
        int healthRecovered = random.nextInt(21) + 10;
        playerHealth += healthRecovered;
        if (playerHealth > 100) playerHealth = 100;
        System.out.println("You recovered " + healthRecovered + " health.");
    }

    private void visitShop() {
        System.out.println("You enter the shop.");
        System.out.println("1. Buy Potion (20 gold)");
        System.out.println("2. Buy Sword (50 gold)");
        System.out.println("3. Buy Shield (40 gold)");
        System.out.println("4. Exit Shop");
        int choice = scanner.nextInt();
        scanner.nextLine();  // Consume newline
        switch (choice) {
            case 1 -> buyItem("Potion", 20);
            case 2 -> buyItem("Sword", 50);
            case 3 -> buyItem("Shield", 40);
            case 4 -> System.out.println("You leave the shop.");
            default -> System.out.println("Invalid choice.");
        }
    }

    private void buyItem(String item, int cost) {
        if (playerGold >= cost) {
            playerGold -= cost;
            playerInventory.add(item);
            System.out.println("You bought a " + item + ".");
        } else {
            System.out.println("You don't have enough gold.");
        }
    }

    private void viewInventory() {
        System.out.println("Your inventory contains:");
        if (playerInventory.isEmpty()) {
            System.out.println("Nothing.");
        } else {
            for (String item : playerInventory) {
                System.out.println("- " + item);
            }
        }
    }

    private void craftItems() {
        System.out.println("You attempt to craft an item.");
        boolean craftingSuccess = random.nextBoolean();
        if (craftingSuccess) {
            String craftedItem = "Elixir";
            playerInventory.add(craftedItem);
            System.out.println("You successfully crafted a " + craftedItem + "!");
        } else {
            System.out.println("Crafting failed, and you lost some materials.");
        }
    }

    private void completeQuests() {
        System.out.println("You embark on a quest...");
        int questOutcome = random.nextInt(3) + 1;
        switch (questOutcome) {
            case 1 -> {
                System.out.println("You successfully completed the quest!");
                int rewardGold = random.nextInt(51) + 50;
                playerGold += rewardGold;
                System.out.println("You earned " + rewardGold + " gold.");
            }
            case 2 -> {
                System.out.println("The quest was tougher than expected.");
                int questDamage = random.nextInt(21) + 20;
                playerHealth -= questDamage;
                System.out.println("You lost " + questDamage + " health.");
            }
            case 3 -> System.out.println("The quest was uneventful.");
        }
    }

    private void useItemInCombat() {
        System.out.println("Your inventory contains:");
        if (playerInventory.isEmpty()) {
            System.out.println("Nothing.");
        } else {
            for (int i = 0; i < playerInventory.size(); i++) {
                System.out.println((i + 1) + ". " + playerInventory.get(i));
            }
            System.out.println("Which item do you want to use?");
            int choice = scanner.nextInt();
            scanner.nextLine();  // Consume newline
            if (choice >= 1 && choice <= playerInventory.size()) {
                String item = playerInventory.get(choice - 1);
                switch (item) {
                    case "Potion" -> {
                        playerHealth += 30;
                        if (playerHealth > 100) playerHealth = 100;
                        System.out.println("You used a Potion and recovered 30 health.");
                        playerInventory.remove(item);
                    }
                    case "Sword" -> System.out.println("You equip the Sword. It doesn't have an immediate effect.");
                    case "Shield" -> System.out.println("You equip the Shield. It doesn't have an immediate effect.");
                    case "Amulet" -> System.out.println("You wear the Amulet. It doesn't have an immediate effect.");
                    case "Magic Ring" -> System.out.println("You wear the Magic Ring. It doesn't have an immediate effect.");
                    default -> System.out.println("This item can't be used in combat.");
                }
            } else {
                System.out.println("Invalid choice.");
            }
        }
    }

    private void exitGame() {
        System.out.println("Are you sure you want to exit? (y/n)");
        String choice = scanner.nextLine().toLowerCase();
        if (choice.equals("y") || choice.equals("yes")) {
            isGameRunning = false;
            System.out.println("Thanks for playing, " + playerName + "!");
        } else if (choice.equals("n") || choice.equals("no")) {
            System.out.println("Continuing game...");
        } else {
            System.out.println("Invalid choice.");
        }
    }

    private void checkGameOver() {
        if (playerHealth <= 0) {
            System.out.println("Game Over. You have died.");
            isGameRunning = false;
        }
    }
}
