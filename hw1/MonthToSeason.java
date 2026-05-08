import java.util.Scanner;

public class MonthToSeason {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter a month in any of the following forms: January, january, Jan, jan");
        String inputMonth = scanner.nextLine().toLowerCase();

        String season;

        switch(inputMonth) {
            case "december":
            case "dec":
            case "january":
            case "jan":
            case "february":
            case "feb":
                season = "Winter";
                break;

            case "march":
            case "mar":
            case "april":
            case "apr":
            case "may":
                season = "Spring";
                break;

            case "june":
            case "jun":
            case "july":
            case "jul":
            case "august":
            case "aug":
                season = "Summer";
                break;

            case "september":
            case "sep":
            case "october":
            case "oct":
            case "november":
            case "nov":
                season = "Fall";
                break;

            default:
                season = "error: not a valid input for month";
        }

        System.out.println("The month inputted is during the following season: "+season);
        scanner.close();
    }
}
