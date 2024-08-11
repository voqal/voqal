import random

class AdventureGame:
	def __init__(self):
		self.player_name = ""
		self.player_health = 100
		self.player_gold = 0
		self.is_game_running = True
		self.player_inventory = []

	def start_game(self):
		print("Welcome to the Extended Adventure Game!")
		self.player_name = input("Enter your name: ") or "Adventurer"
		print(f"Hello, {self.player_name}!")
		self.game_loop()

	def game_loop(self):
		while self.is_game_running:
			self.show_status()
			self.show_options()
			self.handle_player_choice()
			self.check_game_over()

	def show_status(self):
		print("------------")
		print(f"Name: {self.player_name}")
		print(f"Health: {self.player_health}")
		print(f"Gold: {self.player_gold}")
		print(f"Inventory: {', '.join(self.player_inventory) if self.player_inventory else 'None'}")
		print("------------")

	def show_options(self):
		print("Choose an action:")
		print("1. Explore")
		print("2. Rest")
		print("3. Visit the Shop")
		print("4. View Inventory")
		print("5. Craft Items")
		print("6. Complete Quests")
		print("7. Exit Game")

	def handle_player_choice(self):
		try:
			choice = int(input("Enter your choice: "))
		except ValueError:
			choice = 0

		if choice == 1:
			self.explore()
		elif choice == 2:
			self.rest()
		elif choice == 3:
			self.visit_shop()
		elif choice == 4:
			self.view_inventory()
		elif choice == 5:
			self.craft_items()
		elif choice == 6:
			self.complete_quests()
		elif choice == 7:
			self.exit_game()
		else:
			print("Invalid choice. Please choose again.")

	def explore(self):
		print("You venture into the wild...")
		encounter = random.randint(1, 5)
		if encounter == 1:
			self.find_gold()
		elif encounter == 2:
			self.encounter_enemy()
		elif encounter == 3:
			self.find_nothing()
		elif encounter == 4:
			self.find_item()
		elif encounter == 5:
			self.trigger_random_event()

	def find_gold(self):
		gold_found = random.randint(10, 50)
		self.player_gold += gold_found
		print(f"You found {gold_found} gold!")

	def encounter_enemy(self):
		print("An enemy appears!")
		enemy_health = random.randint(20, 50)
		enemy_alive = True

		while enemy_alive and self.player_health > 0:
			print(f"Enemy Health: {enemy_health}")
			try:
				choice = int(input("Do you want to (1) Attack, (2) Use Item, or (3) Run? "))
			except ValueError:
				choice = 0

			if choice == 1:
				damage_to_enemy = random.randint(5, 20)
				damage_to_player = random.randint(1, 15)
				print(f"You hit the enemy for {damage_to_enemy} damage.")
				self.player_health -= damage_to_player
				print(f"The enemy hits you for {damage_to_player} damage.")
				if damage_to_enemy >= enemy_health:
					enemy_alive = False
					print("You defeated the enemy!")
				else:
					enemy_health -= damage_to_enemy
					print("The enemy is still alive.")
			elif choice == 2:
				self.use_item_in_combat()
			elif choice == 3:
				print("You managed to run away!")
				return
			else:
				print("Invalid choice.")

		if self.player_health <= 0:
			print("You have been defeated!")
			self.is_game_running = False

	def find_nothing(self):
		print("You find nothing of interest.")

	def find_item(self):
		items = ["Potion", "Sword", "Shield", "Amulet"]
		found_item = random.choice(items)
		self.player_inventory.append(found_item)
		print(f"You found a {found_item}!")

	def trigger_random_event(self):
		print("A mysterious event occurs...")
		event = random.randint(1, 3)
		if event == 1:
			print("You feel a strange force, but nothing happens.")
		elif event == 2:
			print("You fall into a trap!")
			damage = random.randint(10, 30)
			self.player_health -= damage
			print(f"You lose {damage} health.")
		elif event == 3:
			print("A wandering merchant offers you a trade.")
			if random.random() < 0.5:
				trade_item = "Magic Ring"
				self.player_inventory.append(trade_item)
				print(f"You traded successfully and received a {trade_item}!")
			else:
				print("The trade failed, and you lost 10 gold.")
				self.player_gold -= 10

	def rest(self):
		print("You take a rest.")
		health_recovered = random.randint(10, 30)
		self.player_health += health_recovered
		if self.player_health > 100:
			self.player_health = 100
		print(f"You recovered {health_recovered} health.")

	def visit_shop(self):
		print("You enter the shop.")
		print("1. Buy Potion (20 gold)")
		print("2. Buy Sword (50 gold)")
		print("3. Buy Shield (40 gold)")
		print("4. Exit Shop")

		try:
			choice = int(input("Enter your choice: "))
		except ValueError:
			choice = 0

		if choice == 1:
			self.buy_item("Potion", 20)
		elif choice == 2:
			self.buy_item("Sword", 50)
		elif choice == 3:
			self.buy_item("Shield", 40)
		elif choice == 4:
			print("You leave the shop.")
		else:
			print("Invalid choice.")

	def buy_item(self, item, cost):
		if self.player_gold >= cost:
			self.player_gold -= cost
			self.player_inventory.append(item)
			print(f"You bought a {item}.")
		else:
			print("You don't have enough gold.")

	def view_inventory(self):
		print("Your inventory contains:")
		if not self.player_inventory:
			print("Nothing.")
		else:
			for item in self.player_inventory:
				print(f"- {item}")

	def craft_items(self):
		print("You attempt to craft an item.")
		if random.random() < 0.5:
			crafted_item = "Elixir"
			self.player_inventory.append(crafted_item)
			print(f"You successfully crafted a {crafted_item}!")
		else:
			print("Crafting failed, and you lost some materials.")

	def complete_quests(self):
		print("You embark on a quest...")
		quest_outcome = random.randint(1, 3)
		if quest_outcome == 1:
			print("You successfully completed the quest!")
			reward_gold = random.randint(50, 100)
			self.player_gold += reward_gold
			print(f"You earned {reward_gold} gold.")
		elif quest_outcome == 2:
			print("The quest was tougher than expected.")
			quest_damage = random.randint(20, 40)
			self.player_health -= quest_damage
			print(f"You lost {quest_damage} health.")
		elif quest_outcome == 3:
			print("The quest was uneventful.")

	def use_item_in_combat(self):
		print("Your inventory contains:")
		if not self.player_inventory:
			print("Nothing.")
		else:
			for index, item in enumerate(self.player_inventory):
				print(f"{index + 1}. {item}")

			try:
				choice = int(input("Which item do you want to use? "))
			except ValueError:
				choice = 0

			if 1 <= choice <= len(self.player_inventory):
				item = self.player_inventory[choice - 1]
				if item == "Potion":
					self.player_health += 30
					if self.player_health > 100:
						self.player_health = 100
					print("You used a Potion and recovered 30 health.")
					self.player_inventory.remove(item)
				elif item == "Sword":
					print("You equip the Sword. It doesn't have an immediate effect.")
				elif item == "Shield":
					print("You equip the Shield. It doesn't have an immediate effect.")
				elif item == "Amulet":
					print("You wear the Amulet. It doesn't have an immediate effect.")
				elif item == "Magic Ring":
					print("You wear the Magic Ring. It doesn't have an immediate effect.")
				else:
					print("This item can't be used in combat.")
			else:
				print("Invalid choice.")

	def exit_game(self):
		choice = input("Are you sure you want to exit? (y/n): ").lower()
		if choice in ["y", "yes"]:
			self.is_game_running = False
			print(f"Thanks for playing, {self.player_name}!")
		elif choice in ["n", "no"]:
			print("Continuing game...")
		else:
			print("Invalid choice.")

	def check_game_over(self):
		if self.player_health <= 0:
			print("Game Over. You have died.")
			self.is_game_running = False
