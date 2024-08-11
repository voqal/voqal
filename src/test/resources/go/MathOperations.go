package main

import "fmt"

type MathOperations struct{}

func (self MathOperations) calculateResult1(x, y int) int {
	sum := x + y
	product := x * y
	difference := x - y
	return (sum + product - difference) + 10
}

func (self MathOperations) calculateResult2(p, q int) int {
	difference := p - q
	product := p * q
	sum := p + q
	return 2 * (sum + product - difference)
}
