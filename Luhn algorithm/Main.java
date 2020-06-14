package banking;

import java.math.BigInteger;
import java.util.*;

public class Main
{
    static final Scanner sc = new Scanner(System.in);
    static final Random random = new Random();
    static final int IIN = 400000;
    static int checksum = 5;
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
        String stringCardNumber = "400000";

        for (int i = 0; i < 8; i++)
        {
            stringCardNumber = stringCardNumber.concat(String.valueOf(random.nextInt(10)));
        }
        luhnAlgo(stringCardNumber);
        stringCardNumber = stringCardNumber.concat(String.valueOf(checksum));

        cardNumber = Long.parseLong(stringCardNumber);

        pin = random.nextInt(9000) + 1000;

        System.out.println("Your card has been created\nYour card number:\n" + cardNumber + "\nYour card PIN:\n" + pin);
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
            System.out.println("\n1. Balance\n2. Log out\n0. Exit");
            int action = sc.nextInt();

            switch (action)
            {
                case 1:
                    System.out.println("Balance: " + balance);
                    break;
                case 2:
                    loggedOut = true;
                    System.out.println("You have successfully logged out!");
                    break;
                case 0:
                    loggedOut = true;
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

    private static void luhnAlgo(String accountNumber)
    {
        int[] numArr = new int[13];
        int sum = 0;

        for (int i = 0; i < numArr.length; i++)
        {
            numArr[i] = Integer.parseInt(String.valueOf(accountNumber.charAt(i)));

            if ((i+1) % 2 != 0)
            {
                numArr[i] *= 2;
            }
            if (numArr[i] > 9)
            {
                numArr[i] -= 9;
            }

            sum += numArr[i];
        }

        for (int i = 0; i < 10; i++)
        {
            if ((i + sum) % 10 == 0)
            {
                checksum = i;
                break;
            }
        }
    }
}
