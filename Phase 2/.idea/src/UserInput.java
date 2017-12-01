
import java.util.Scanner;

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
     * @param message message that need to be displayed  while prompting 
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

    public static String getID(String message) {
        String userID;
        Scanner scanner = new Scanner(System.in);
        int size;
        String input;
        do {
            System.out.print("Enter id (<20 chars) >");
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
            System.out.print("Enter you message (<200 chars, \n for new line) >");
            input = scanner.nextLine();
            size = input.length();
        } while (size > 200 || size == 0);
        return input;
    }

}
