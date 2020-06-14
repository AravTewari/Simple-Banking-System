package banking;

import java.math.BigInteger;
import java.util.*;

public class Main
{
    static final Scanner sc = new Scanner(System.in);
    static final Random random = new Random();
    static final int IIN = 400000;
    static final int checksum = 5;
    static int pin = 0;
    static long customerAccountNumber = 0;
    static long cardNumber = 0;
    static boolean finished = false;

    public static void main(String[] args)
    {
        while(!finished)
        {
            System.out.println("\n1. Create account\n2. Log into account\n0. Exit");
            int action = sc.nextInt();

            switch (action)
            {
                case 1:
                    createAccount();
                    break;
                case 2:
                    logIn();
                    break;
                case 0:
                    finish();
                    break;
                default:
                    System.out.println("Not an action");
                    break;
            }
        }
    }

    private static void createAccount()
    {
        String result = "";

        for (int i = 0; i < 9; i++)
        {
            result = result.concat(String.valueOf(random.nextInt(10)));
        }
        customerAccountNumber = Long.parseLong(result);

        String stringCardNumber = String.valueOf(IIN)
                                + String.valueOf(customerAccountNumber)
                                + String.valueOf(checksum);
        cardNumber = Long.parseLong(stringCardNumber);

        pin = random.nextInt(9000) + 1000;

        System.out.println("\nYour card has been created\nYour card number:\n"
                            + cardNumber + "\nYour card PIN:\n" + pin);
    }

    private static void logIn()
    {
        System.out.println("Enter your card number: ");
        long inputNumber = sc.nextLong();
        System.out.println("Enter your pin: ");
        int inputPin = sc.nextInt();

        if ((inputNumber == cardNumber) && inputPin == pin)
        {
            loggedIn();
        }
        else
        {
            System.out.println("Wrong card number or PIN!");
            return;
        }
    }

    private static void loggedIn()
    {
        boolean loggedOut = false;
        int balance = 0;

        System.out.println("You have successfully logged in!");

        while (!loggedOut)
        {
            System.out.println("\n1. Balance:\n2. Log out\n0. Exit");
            int action = sc.nextInt();

            switch (action)
            {
                case 1:
                    System.out.println("Balance " + balance);
                    break;
                case 2:
                    loggedOut = true;
                    System.out.println("You have successfully loggeed out!");
                    break;
                case 0:
                    finish();
                    break;
                default:
                    System.out.println("Not an action.");
                    break;
            }
        }
    }

    private static void finish()
    {
        finished = true;
        System.out.println("Bye!");
    }
}
