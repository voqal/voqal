package main

import "fmt"

type RFunctions struct{}

func (self RFunctions) factorialRecursive(n int) int {
    if n <= 1 {
        return 1
    } else {
        return n * self.factorialRecursive(n - 1)
    }
}

func (self RFunctions) findMaxIterative(elements []int) int {
    max := elements[0]
    for _, element := range elements {
        if element > max {
            max = element
        }
    }
    return max
}
