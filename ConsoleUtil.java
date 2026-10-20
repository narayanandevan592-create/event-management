package com.eventmgmt.util;
import java.util.Scanner;
public final class ConsoleUtil {
    private static final Scanner scanner = new Scanner(System.in);

    private ConsoleUtil = null; 
    private ConsoleUtil() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated.");
    }
    private static String repeatChar(char ch, int count) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < count; i++) {
            builder.append(ch);
        }
        return builder.toString();
    }
    public static void printHeader(String title) {
        int length = Math.max(60, title.length() + 8);
        String borderLine = repeatChar('=', length);
        System.out.println("\n" + borderLine);
        System.out.println("   * " + title.toUpperCase() + " *");
        System.out.println(borderLine);
    }
    public static void printSubHeader(String title) {
        int length = Math.max(10, 50 - title.length());
        System.out.println("\n--- [ " + title + " ] " + repeatChar('-', length));
    }

    public static void printSuccess(String message) {
        System.out.println("[SUCCESS] " + message);
    }

    public static void printInfo(String message) {
        System.out.println("[INFO] " + message);
    }

    public static void printWarning(String message) {
        System.out.println("[WARNING] " + message);
    }

    public static void printError(String message) {
        System.out.println("[ERROR] " + message);
    }

    public static String readString(String prompt) {
        System.out.print(prompt + ": ");
        return scanner.nextLine().trim();
    }

    public static int readInt(String prompt, int defaultVal) {
        System.out.print(prompt + " [" + defaultVal + "]: ");
        String inputLine = scanner.nextLine().trim();
        
        if (inputLine.isEmpty()) {
            return defaultVal;
        }
        try {
            return Integer.parseInt(inputLine);
        } catch (NumberFormatException e) {
            System.out.println("Invalid number format. Falling back to default: " + defaultVal);
            return defaultVal;
        }
    }

    public static double readDouble(String prompt, double defaultVal) {
        System.out.printf("%s [%.2f]: ", prompt, defaultVal);
        String inputLine = scanner.nextLine().trim();
        
        if (inputLine.isEmpty()) {
            return defaultVal;
        }
        try {
            return Double.parseDouble(inputLine);
        } catch (NumberFormatException e) {
            System.out.printf("Invalid monetary value. Falling back to default: %.2f%n", defaultVal);
            return defaultVal;
        }
    }

    public static boolean readBoolean(String prompt, boolean defaultVal) {
        String defaultIndicator = defaultVal ? "y" : "n";
        System.out.print(prompt + " (y/n) [" + defaultIndicator + "]: ");
        String inputLine = scanner.nextLine().trim();
        
        if (inputLine.isEmpty()) {
            return defaultVal;
        }
        return inputLine.equalsIgnoreCase("y") || inputLine.equalsIgnoreCase("yes");
    }
}