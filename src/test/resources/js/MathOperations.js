class MathOperations {
    calculateResult1(x, y) {
        const sum = x + y;
        const product = x * y;
        const difference = x - y;
        return (sum + product - difference) + 10;
    }

    calculateResult2(p, q) {
        const difference = p - q;
        const product = p * q;
        const sum = p + q;
        return 2 * (sum + product - difference);
    }
}