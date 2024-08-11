package main

import "fmt"

type RemoveMethod struct{}

func (self RemoveMethod) add(x int, y int) int {
    return x + y
}

func (self RemoveMethod) subtract(a int, b int) int {
    return a - b
}
