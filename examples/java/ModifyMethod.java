public class ModifyMethod {
    public int add(int x, int y) {
        return x + y;
    }

    public int subtract(int x, int y) {
        return x - y;
    }

    public static void main(String[] args) {
        ModifyMethod calculator = new ModifyMethod();
        int sum = calculator.add(5, 3);
        int difference = calculator.subtract(10, 4);
        System.out.println("Sum: " + sum);
        System.out.println("Difference: " + difference);
    }
}