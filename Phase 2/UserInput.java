import java.util.Scanner;
import java.util.Set;
import java.util.HashSet;

/*
 * This class will have static methods to get the user input and validate it
 * if it fails the validation it should keep propting untill the correct input
 * is entered
 */
/**
 *
 * @author Daler
 */
public class UserInput {

    /**
     * Get a validate int. Keep prompting till int enteredd
     *
     * @param message message that need to be displayed while prompting
     * @return
     */
    public static int getInt(String message) {
        Scanner scanner = new Scanner(System.in);
        System.out.print(message);
        while (!scanner.hasNextInt()) {
            scanner.nextLine();
            System.out.print(message);
        }
        return scanner.nextInt();
    }

    public static String getID() {
        String userID;
        Scanner scanner = new Scanner(System.in);
        int size;
        String input;
        do {
            System.out.print("Enter ID: (<20 chars) >");
            input = scanner.next();
            size = input.length();
        } while (size > 20);
        return input;
    }

    public static String getUserName() {
        String userID;
        Scanner scanner = new Scanner(System.in);
        int size;
        String input;
        do {
            System.out.print("Enter Name: (<20 chars) >");
            input = scanner.next();
            size = input.length();
        } while (size > 20);
        return input;
    }

    public static String getEmail() {
        String userID;
        Scanner scanner = new Scanner(System.in);
        int size;
        String input;
        do {
            System.out.print("Enter Email: (<20 chars) >");
            input = scanner.next();
            size = input.length();
        } while (size > 20);
        return input;
    }

    public static String getBDay() {
        String userID;
        Scanner scanner = new Scanner(System.in);
        int size;
        String input;
        do {
            System.out.print("Enter Birthday: (<20 chars) >");
            input = scanner.next();
            size = input.length();
        } while (size > 20);
        return input;
    }

    public static String getPassword() {
        String userID;
        Scanner scanner = new Scanner(System.in);
        int size;
        String input;
        do {
            System.out.print("Enter Password: (<20 chars) >");
            input = scanner.next();
            size = input.length();
        } while (size > 20);
        return input;
    }

    public static String getMessage() {
        String msg;
        Scanner scanner = new Scanner(System.in);
        int size;
        String input;

        do {
            System.out.print("Enter your message (<200 chars, \n for new line) >");
            input = scanner.nextLine();
            size = input.length();
        } while (size > 200 || size == 0);
        return input;
    }

    /**
     * Get line that is less than 20 chars
     *
     * @param message message for the user
     * @return input
     */
    public static String getLine20(String message) {
        String msg;
        Scanner scanner = new Scanner(System.in);
        int size;
        String input;

        do {
            System.out.print(message + "(<20) >");
            input = scanner.nextLine();
            size = input.length();
        } while (size > 20 || size == 0);
        return input;
    }

    /**
     * Get line that is less than 20 chars
     *
     * @param message message for the user
     * @return input
     */
    public static String getLine200(String message) {
        String msg;
        Scanner scanner = new Scanner(System.in);
        int size;
        String input;

        do {
            System.out.print(message + "(<200) >");
            input = scanner.nextLine();
            size = input.length();
        } while (size > 200 || size == 0);
        return input;
    }

    public static String[] getSearch(String message){
        Set<String> searches = new HashSet<String>();
        Scanner scanner = new Scanner(System.in);
        System.out.print(message);
        String line = scanner.nextLine();
        String[] arr = line.split(" ");
        return arr;
    }

}