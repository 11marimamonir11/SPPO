package util;

import java.util.Scanner;

/**
 * Utility class for reading user input safely.
 */
public class InputManager {

    private Scanner scanner;

    public InputManager(Scanner scanner) {
        this.scanner = scanner;
    }

    public String readNonEmptyString(String prompt) {
        while (true) {
            System.out.print(prompt + ": ");
            String line = scanner.nextLine().trim();
            if (!line.isEmpty()) {
                return line;
            }
            System.out.println("Value cannot be empty. Try again.");
        }
    }

    public String readStringAllowEmpty(String prompt) {
        System.out.print(prompt + " (empty = null): ");
        String line = scanner.nextLine().trim();
        return line; // may be empty
    }

    public int readInt(String prompt) {
        while (true) {
            System.out.print(prompt + ": ");
            String line = scanner.nextLine().trim();
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("Please enter an integer number.");
            }
        }
    }

    public Integer readIntNullable(String prompt) {
        while (true) {
            System.out.print(prompt + " (empty = null): ");
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) return null;
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("Please enter an integer number or empty.");
            }
        }
    }

    public double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt + ": ");
            String line = scanner.nextLine().trim();
            try {
                return Double.parseDouble(line);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a decimal number.");
            }
        }
    }

    public boolean readBoolean(String prompt) {
        while (true) {
            System.out.print(prompt + " (true/false): ");
            String line = scanner.nextLine().trim().toLowerCase();
            if (line.equals("true")) return true;
            if (line.equals("false")) return false;
            System.out.println("Please type true or false.");
        }
    }

    public Boolean readBooleanNullable(String prompt) {
        while (true) {
            System.out.print(prompt + " (true/false, empty = null): ");
            String line = scanner.nextLine().trim().toLowerCase();
            if (line.isEmpty()) return null;
            if (line.equals("true")) return true;
            if (line.equals("false")) return false;
            System.out.println("Please type true, false, or empty.");
        }
    }

    public <E extends Enum<E>> E readEnum(String prompt, Class<E> enumClass) {
        E[] values = enumClass.getEnumConstants();

        while (true) {
            System.out.print(prompt + " (choose one: ");
            for (int i = 0; i < values.length; i++) {
                System.out.print(values[i]);
                if (i < values.length - 1) System.out.print(", ");
            }
            System.out.print("): ");

            String line = scanner.nextLine().trim();

            try {
                return Enum.valueOf(enumClass, line);
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid value. Please type exactly one of the listed constants.");
            }
        }
    }

    public Scanner getScanner() {
        return scanner;
    }

    public void setScanner(Scanner scanner) {
        this.scanner = scanner;
    }


}
