package main

import "fmt"

type ModifyBetween struct{}

func (self ModifyBetween) add(x int, y int) int {
    return x + y
}

func (self ModifyBetween) divide(a int, b int) int {
    return a / b
}

func (self ModifyBetween) subtract(a int, b int) int {
    return a - b
}
