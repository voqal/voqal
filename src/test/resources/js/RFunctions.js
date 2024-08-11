class RFunctions {
    factorialRecursive(n) {
        if (n <= 1) {
            return 1;
        } else {
            return n * this.factorialRecursive(n - 1);
        }
    }

    findMaxIterative(elements) {
        let max = elements[0];
        for (let element of elements) {
            if (element > max) {
                max = element;
            }
        }
        return max;
    }
}
