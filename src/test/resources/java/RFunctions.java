import java.util.List;

public class RFunctions {

    public int factorialRecursive(int n) {
        if (n <= 1) {
            return 1;
        } else {
            return n * factorialRecursive(n - 1);
        }
    }

    public int findMaxIterative(List<Integer> elements) {
        int max = elements.get(0);
        for (int element : elements) {
            if (element > max) {
                max = element;
            }
        }
        return max;
    }
}
