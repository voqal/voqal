class InlineMathOperations:

	def complexCalculation(self, a, b):
		sum_ = a + b
		product = a * b
		difference = a - b
		return sum_ + product - difference

	def calculateResult1(self, x, y):
		return self.complexCalculation(x, y) + 10

	def calculateResult2(self, p, q):
		return 2 * self.complexCalculation(p, q)
