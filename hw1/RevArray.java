import java.util.Scanner;

public class RevArray {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Input the number of elements in the array: ");
        int s = scanner.nextInt();
        int[] arr = new int[s];

        System.out.println("Input the elements in the array: ");
        for(int i = 0; i < s; i++) {
            arr[i] = scanner.nextInt();
        }

        System.out.print("Original Array: ");
        for (int num : arr) {
            System.out.print(num + " ");
        }
        System.out.println();

       for (int i = 0; i < s / 2; i++) {
           int temp = arr[i];
           arr[i] = arr[s - i - 1];
           arr[s - i - 1] = temp;
       }

        System.out.print("Reversed Array: ");
        for(int num : arr) {
            System.out.print(num + " ");
        }

        scanner.close();
    }
}