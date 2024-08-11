class MathOperations:

	def calculateResult1(self, x, y):
		sum_ = x + y
		product = x * y
		difference = x - y
		return (sum_ + product - difference) + 10

	def calculateResult2(self, p, q):
		difference = p - q
		product = p * q
		sum_ = p + q
		return 2 * (sum_ + product - difference)
