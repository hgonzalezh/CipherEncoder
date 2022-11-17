import java.util.NoSuchElementException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        String message;
        boolean exit = false;

        while (!exit) {
            System.out.println("Please input operation (encode/decode/exit):");
            String option = scanner.nextLine();

            switch (option) {

                case "encode":
                    System.out.println("Input string:");
                    message = scanner.nextLine();
                    encode(message);
                    break;

                case "decode":
                    System.out.println("Input encoded string:");
                    message = scanner.nextLine();

                    try {
                        decode(message);
                    } catch (InvalidEncodedStringException | NoSuchElementException exception) {
                        // NoSuchElementException covers the invalid case: The number of blocks is odd
                        System.out.println("Encoded string is not valid" + "\n");
                    }
                    break;

                case "exit":
                    System.out.println("Bye!");
                    exit = true;
                    break;

                default:
                    System.out.printf("There is no '%s' operation\n\n", option);
                    break;
            }

        }
        scanner.close();

    }

    public static void encode(String message) {

        char[] charArray = message.toCharArray();
        String binaryString = convertInputToBinary(charArray);
        String encodedArray = zeroEncryption(binaryString);

        System.out.println("Encoded string:");
        System.out.println(encodedArray + "\n");
    }

    public static String convertInputToBinary(char[] charArray) {
        String binaryString = "";

        for (char element : charArray) {
            binaryString += String.format(
                            "%7s", Integer.toBinaryString(element))
                    .replace(" ", "0"
                    );
        }

        return binaryString;
    }

    public static String zeroEncryption(String binaryString) {

        String encodedString = "";

        for (int i = 0; i < binaryString.length(); i++) {

            int counter = 1;

            while (i + 1 < binaryString.length() && binaryString.charAt(i) == binaryString.charAt(i + 1)) {
                counter++;
                i++;
            }

            if (binaryString.charAt(i) == '0') {
                encodedString += "00" + " " + "0".repeat(counter);
            } else {
                encodedString += "0" + " " + "0".repeat(counter);
            }
            encodedString += " ";
        }

        return encodedString;
    }

    public static void decode(String message) {

        /* Invalid case: The encoded message includes characters other than 0 or spaces */
        if (message == null || message.isBlank()|| message.matches("[^0 ]+")) {
            throw new InvalidEncodedStringException();
        }

        Scanner scanner = new Scanner(message);
        message = readEncodedInput(scanner);
        message = binaryToText(message);
        System.out.println("Decoded string:");
        System.out.println(message + "\n");
        scanner.close();

    }

    public static String readEncodedInput(Scanner scanner) {
        String binaryMessage = "";

        while (scanner.hasNext()) {
            String firstBlock = scanner.next();
            String secondBlock= scanner.next();

            if ("0".equals(firstBlock)) {
                binaryMessage += "1".repeat(secondBlock.length());
            } else if ("00".equals(firstBlock)) {
                binaryMessage += "0".repeat(secondBlock.length());
            } else {
                /* Invalid case: The first block of each sequence is not 0 or 00 */
                scanner.close();
                throw new InvalidEncodedStringException();
            }
        }

        /* The length of the decoded binary string is not a multiple of 7 */
        if (binaryMessage.length() % 7 != 0) {
            throw new InvalidEncodedStringException();
        }

        return binaryMessage;
    }

    public static String binaryToText(String message) {
        String[] letters = message.split("(?<=\\G.{" + 7 + "})");
        message = "";
        for (int i = 0; i < letters.length; i++) {
            int sum = 0;
            for (int j = 0; j < letters[i].length(); j++) {
                if (letters[i].charAt(j) == '1') {
                    sum += (int) Math.pow(2,letters[i].length() - (j + 1));
                }
            }
            letters[i] = Character.toString(sum);
            message += letters[i];
        }
        return message;
    }
}