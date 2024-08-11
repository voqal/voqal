public class MathOperations {
    public int calculateResult1(int x, int y) {
        int sum = x + y;
        int product = x * y;
        int difference = x - y;
        return (sum + product - difference) + 10;
    }

    public int calculateResult2(int p, int q) {
        int difference = p - q;
        int product = p * q;
        int sum = p + q;
        return 2 * (sum + product - difference);
    }
}
