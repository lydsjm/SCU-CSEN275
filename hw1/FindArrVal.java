import java.util.Scanner;

public class FindArrVal {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Input the number of elements in the array: ");
        int s = scanner.nextInt();
        int[] arr = new int[s];
        int sum = 0;

        System.out.println("Input the elements in the array: ");
        for (int i = 0; i < s; i++) {
            arr[i] = scanner.nextInt();
            sum += arr[i];
        }

        int min = arr[0];
        int max = arr[0];

        for (int i = 1; i < s; i++) {
            if (arr[i] < min) min = arr[i];
            if (arr[i] > max) max = arr[i];
        }

        double avg = (double) sum / s;
        System.out.println("Minimum: " + min);
        System.out.println("Maximum: " + max);
        System.out.println("Average: " + avg);
        scanner.close();
    }
}
