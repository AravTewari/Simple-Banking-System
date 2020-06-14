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
 
        while (!finished)
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
 
        for (int i = 0; i < 9; i++)
        {
            stringCardNumber = stringCardNumber.concat(String.valueOf(random.nextInt(10)));
        }
        luhnAlgo(stringCardNumber);
        stringCardNumber = stringCardNumber.concat(String.valueOf(checksum));
 
        pin = random.nextInt(9000) + 1000;
        sc.nextLine();
 
        System.out.println("\nYour card has been created\n" +
                "Your card number:\n"
                + stringCardNumber
                + "\nYour card PIN:\n"
                + pin);
        insert(stringCardNumber, String.valueOf(pin));
    }
 
    private static void logIn()
    {
        sc.nextLine();
 
        System.out.println("Enter your card number: ");
        String inputNumber = sc.nextLine();
        System.out.println("Enter your pin: ");
        String inputPin = sc.nextLine();
 
        if (checkIfExists(inputNumber, inputPin))
            loggedIn();
        else
            System.out.println("Wrong card number or PIN!");
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
                + " id INTEGER PRIMARY KEY,\n"
                + " number TEXT NOT NULL,\n"
                + " pin TEXT,\n"
                + " balance INTEGER DEFAULT 0\n"
                + ");";
 
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement())
        {
            // create a new table
            stmt.execute(sql);
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
        }
        catch (SQLException e)
        {
            System.out.println(e.getMessage());
        }
    }
 
    private static void insert(int id, String number, String pin, int balance)
    {
        String sql = "INSERT INTO card(id, number, pin, balance) VALUES(?,?,?,?)";
 
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql))
        {
            pstmt.setInt(1, id);
            pstmt.setString(2, number);
            pstmt.setString(3, pin);
            pstmt.setInt(4, balance);
            pstmt.executeUpdate();
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
}
