package main

import "fmt"

type InlineMathOperations struct{}

func (self InlineMathOperations) complexCalculation(a, b int) int {
	sum := a + b
	product := a * b
	difference := a - b
	return (sum + product - difference)
}

func (self InlineMathOperations) calculateResult1(x, y int) int {
	return self.complexCalculation(x, y) + 10
}

func (self InlineMathOperations) calculateResult2(p, q int) int {
	return 2 * self.complexCalculation(p, q)
}
