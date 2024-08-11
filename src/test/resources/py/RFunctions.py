class RFunctions:

	def factorialRecursive(self, n):
		if n <= 1:
			return 1
		else:
			return n * self.factorialRecursive(n - 1)

	def findMaxIterative(self, elements):
		max_element = elements[0]
		for element in elements:
			if element > max_element:
				max_element = element
		return max_element
