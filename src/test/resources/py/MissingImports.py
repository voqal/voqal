class MissingImports:
	def print_random_number(self):
		print(random.randint(0, 100))

	def print_current_date(self):
		print(datetime.now().strftime("%Y-%m-%d"))

	def print_file_exists(self):
		print(os.path.exists("example.txt"))

	def print_local_date(self):
		print(date.today())

	def print_zone_id(self):
		print(time.tzname)
