import java.util.Scanner;

public class EqualArrays {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter Sizes for Array 1 & Array 2: ");
        int s = scanner.nextInt();
        int[] arr1 = new int[s];
        int[] arr2 = new int[s];


        System.out.println("Input integers for Array 1: ");
        for (int i=0; i<s; i++) {
            arr1[i] = scanner.nextInt();
        }

        System.out.println("Input integers for Array 2: ");
        for(int i=0; i<s; i++) {
            arr2[i] = scanner.nextInt();
        }

        boolean equal = true;
        for (int i=0; i<s; i++) {
            if (arr1[i] != arr2[i]) {
                System.out.println("Arrays are not the same at index " + i);
                equal = false;
                break;
            }
        }

        if (equal) {
            System.out.println("Arrays are equal!");
        }

        scanner.close();
    }
}
