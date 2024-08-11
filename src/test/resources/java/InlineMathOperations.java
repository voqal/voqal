public class InlineMathOperations {
    private int complexCalculation(int a, int b) {
        int sum = a + b;
        int product = a * b;
        int difference = a - b;
        return sum + product - difference;
    }

    public int calculateResult1(int x, int y) {
        return complexCalculation(x, y) + 10;
    }

    public int calculateResult2(int p, int q) {
        return 2 * complexCalculation(p, q);
    }
}
