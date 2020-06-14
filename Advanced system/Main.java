package banking;

import java.util.*;
import java.sql.*;

public class Main
{
    static final Scanner sc = new Scanner(System.in);
    static final Random random = new Random();
    static int checksum = 5;
    static int pin = 0;
    static boolean finished = false;
    static String url = "";

    public static void main(String[] args)
    {
        if (args.length != 0)
            createNewDatabase(args[1]);
        else
            createNewDatabase("card.s3db");
            url = "jdbc:sqlite:card.s3db";

        while (!finished)
        {
            System.out.println("\n1. Create account\n2. Log into account\n0. Exit");
            int action = Integer.parseInt(sc.nextLine());

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
        StringBuilder stringCardNumber = new StringBuilder("400000");

        for (int i = 0; i < 9; i++)
        {
            stringCardNumber.append(random.nextInt(10));
        }
        luhnAlgo(String.valueOf(stringCardNumber));
        stringCardNumber.append(checksum);

        pin = random.nextInt(9000) + 1000;

        System.out.println("\nYour card has been created\n" +
                            "Your card number:\n"
                            + stringCardNumber
                            + "\nYour card PIN:\n"
                            + pin);
        insert(String.valueOf(stringCardNumber), String.valueOf(pin));
    }

    private static void logIn()
    {
        System.out.println("Enter your card number: ");
        String inputNumber = sc.nextLine();
        System.out.println("Enter your pin: ");
        String inputPin = sc.nextLine();

        if (checkIfExists(inputNumber, inputPin))
            loggedIn(inputNumber);
        else
            System.out.println("Wrong card number or PIN!");
    }

    private static void loggedIn(String number)
    {
        boolean loggedOut = false;
        int balance = 0;

        System.out.println("You have successfully logged in!");

        while (!loggedOut)
        {
            System.out.println("\n1. Balance" +
                            "\n2. Add income" +
                            "\n3. Do transfer " +
                            "\n4. Close account " +
                            "\n5. Log out " +
                            "\n0. Exit");
            int action = Integer.parseInt(sc.nextLine());

            switch (action)
            {
                case 1:
                    System.out.println("Balance: " + getBalance(number));
                    break;
                case 2:
                    System.out.println("How much money to add?");
                    addIncome(number, Integer.parseInt(sc.nextLine()));
                    break;
                case 3:
                    System.out.println("Which account to transfer?");
                    String toNumber = sc.nextLine();
                    if (number.equals(toNumber))
                    {
                        System.out.println("You can't transfer money to the same account!");
                        break;
                    }
                    else if (!checkLuhnAlgo(toNumber))
                    {
                        System.out.println("You probably made a mistake in the card number. Please try again!");
                        break;
                    }
                    else if (!checkIfExists(number))
                    {
                        System.out.println("Such a card does not exist.");
                        break;
                    }
                    else
                    {
                        System.out.println("How much money to transfer?");
                        int money = Integer.parseInt(sc.nextLine());
                        makeTransaction(number, toNumber, money);
                        break;
                    }
                case 4:
                    closeAccount(number);
                    break;
                case 5:
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
        int[] numArr = new int[accountNumber.length()];
        int sum = 0;

        for (int i = 0; i < numArr.length; i++)
        {
            numArr[i] = Integer.parseInt(String.valueOf(accountNumber.charAt(i)));

            if ((i + 1) % 2 != 0)
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

    private static boolean checkLuhnAlgo(String accountNumber)
    {
        int[] numArr = new int[accountNumber.length()];
        int sum = 0;

        for (int i = 0; i < numArr.length; i++)
        {
            numArr[i] = Integer.parseInt(String.valueOf(accountNumber.charAt(i)));

            if ((i + 1) % 2 != 0)
            {
                numArr[i] *= 2;
            }
            if (numArr[i] > 9)
            {
                numArr[i] -= 9;
            }

            sum += numArr[i];
        }
        return sum % 10 == 0;
    }

    private static Connection connect()
    {
        // SQLite connection string
        Connection conn = null;
        try
        {
            conn = DriverManager.getConnection(url);
        }
        catch (SQLException e)
        {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    public static void createNewDatabase(String fileName)
    {

        // String url = "jdbc:sqlite:C:/Users/arav/IdeaProjects/Simple Banking System/" + fileName;
        url = "jdbc:sqlite:" + fileName;

        try (Connection conn = DriverManager.getConnection(url))
        {
            if (conn != null)
            {
                DatabaseMetaData meta = conn.getMetaData();
            }
        }
        catch (SQLException e)
        {
            System.out.println(e.getMessage());
        }

        String sql = "CREATE TABLE IF NOT EXISTS card (\n"
                + "	id INTEGER PRIMARY KEY,\n"
                + "	number TEXT NOT NULL,\n"
                + "	pin TEXT,\n"
                + "	balance INTEGER DEFAULT 0\n"
                + ");";

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement())
        {
            // create a new table
            stmt.execute(sql);
            stmt.close();
        }
        catch (SQLException e)
        {
            System.out.println(e.getMessage());
        }
    }

    private static void insert(String number, String pin)
    {
        String sql = "INSERT INTO card(number, pin) VALUES(?,?)";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql))
        {
            pstmt.setString(1, number);
            pstmt.setString(2, pin);
            pstmt.executeUpdate();
            pstmt.close();
        }
        catch (SQLException e)
        {
            System.out.println(e.getMessage());
        }
    }

    private static boolean checkIfExists(String number, String pin)
    {
        boolean exists = false;
        try
        {
            Statement statement = connect().createStatement();
            String sql = "SELECT * FROM card WHERE number = '" + number + "' AND pin = '" + pin + "'";

            ResultSet rs = statement.executeQuery(sql);
            exists = rs.next();
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
        return exists;
    }

    private static boolean checkIfExists(String number)
    {
        boolean exists = false;
        try
        {
            Statement statement = connect().createStatement();
            String sql = "SELECT * FROM card WHERE number = '" + number + "'";

            ResultSet rs = statement.executeQuery(sql);
            exists = rs.next();
            rs.close();
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
        return exists;
    }

    private static int getBalance (String number)
    {
        int balance = 0;
        try
        {
            Statement stmt = connect().createStatement();
            String sql = "SELECT balance FROM card WHERE number = '" + number + "'";

            ResultSet rs = stmt.executeQuery(sql);
            balance = rs.getInt(balance);
            rs.close();
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
        return balance;
    }

    private static void addIncome(String number, int income)
    {
        try
        {
            Statement stmt = connect().createStatement();
            String sql = "UPDATE card SET balance = balance + " + income + " WHERE number = '" + number + "'";

            stmt.execute(sql);
            stmt.close();
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
    }

    private static void makeTransaction(String number, String toNumber, int money)
    {
        try
        {
            Statement stmt = connect().createStatement();
            String sql = "UPDATE card SET balance = balance + " + money + " WHERE number = '" + toNumber + "'";

            stmt.execute(sql);
            stmt.close();
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
    }

    private static void closeAccount (String number)
    {
        try
        {
            Statement stmt = connect().createStatement();
            String sql = "DELETE FROM card WHERE number = '" + number + "'";

            stmt.execute(sql);
            stmt.close();
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
    }
}
