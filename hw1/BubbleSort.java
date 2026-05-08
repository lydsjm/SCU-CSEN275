import java.util.Scanner;

public class BubbleSort {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("Enter the number of elements in the array: ");
        int s = sc.nextInt();
        int[] arr = new int[s];

        System.out.println("Enter the elements in the array: ");
        for (int i = 0; i < s; i++) {
            arr[i] = sc.nextInt();
        }

        for (int i = 0; i < s - 1; i++) {
            for (int j = s - 1; j > i; j--) {
                if (arr[j] < arr[j - 1]) {
                    int temp = arr[j];
                    arr[j] = arr[j - 1];
                    arr[j - 1] = temp;
                }
            }
        }

        System.out.println("Sorted Array: ");
        for (int num : arr) {
            System.out.print(num + " ");
        }
        sc.close();

    }
}