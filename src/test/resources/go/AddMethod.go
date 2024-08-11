package main

import "fmt"

type AddMethod struct{}

func (self AddMethod) add(x int, y int) int {
    return x + y
}
