class InlineMathOperations {
    complexCalculation(a, b) {
        const sum = a + b;
        const product = a * b;
        const difference = a - b;
        return sum + product - difference;
    }

    calculateResult1(x, y) {
        return this.complexCalculation(x, y) + 10;
    }

    calculateResult2(p, q) {
        return 2 * this.complexCalculation(p, q);
    }
}