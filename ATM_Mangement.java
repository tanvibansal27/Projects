import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.HashMap;


public class ATM_Mangement extends JFrame {
    //stores all users
    private HashMap<String, BankAccount> accounts = new HashMap<>();
    //represent the currently log in user which is null by default
    private BankAccount currentUser = null;
    // file used to save accounts
    private final String FILE_NAME = "accounts.dat";

    // Card help to switch btw multiple panels
    private CardLayout card;
    private JPanel main; // holds all subPanel

    //Input fields
    private JTextField amountField;
    private JTextField loginUserField;
    private JPasswordField loginPassField;

    // Constructor
    public ATM_Mangement() {
        setTitle("ATM Machine");
        setForeground(Color.red);
        setSize(800, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        card = new CardLayout();
        main = new JPanel(card);

        main.add(start(), "Startup");
        main.add(register(), "Register");
        main.add(login(), "Login");
        main.add(atm(), "ATM");

        add(main);
        card.show(main, "Startup");
        setVisible(true);
    }

    // --- Startup Panel ---
    private JPanel start() {
        //creates panel with 2 rows , 1 cols , 10 px spacing
        JPanel panel = new JPanel(new GridLayout(2, 1, 10, 10));
        // setting boundary with welcome label
        panel.setBorder(BorderFactory.createTitledBorder("Welcome"));
        panel.setFont(new Font("xyz",Font.ITALIC,28));

        // existing and new button to ask user if they have already acc. or want to make new one
        JButton existing = new JButton("I have an account");
        existing.setFont(new Font("xyz",Font.PLAIN , 30));
        JButton newBtn = new JButton("Create a new account");
        newBtn.setFont(new Font("xyz",Font.PLAIN , 30));

        // if user has acc. then loading details from file
        existing.addActionListener(e -> {
            loadAccountsFromFile();
            card.show(main, "Login");
        });

        // clears previous data and go to registration screen
        newBtn.addActionListener(e -> {
            accounts.clear();
            card.show(main, "Register");
        });

        panel.add(existing);
        panel.add(newBtn);
        return panel;
    }

    // --- Register Panel ---
    private JPanel register() {
        JPanel panel = new JPanel(new GridLayout(7, 2, 5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Create Account"));
        panel.setFont(new Font("xyz",Font.PLAIN,32));

        // input fields and password toggle
        JTextField userField = new JTextField();
        JPasswordField passField = new JPasswordField();
        JCheckBox showPassword = new JCheckBox("Show Password");
        JTextField balanceField = new JTextField();
        JLabel passwordHint = new JLabel("<html><small>Password must be at least 6 characters,<br>include letters and special characters.</small></html>");
        passwordHint.setForeground(Color.DARK_GRAY);

        JButton createBtn = new JButton("Create Account");
        createBtn.setFont(new Font("xyz",Font.PLAIN,28));

        // toggle password visibility
        showPassword.addActionListener(e -> {
            passField.setEchoChar(showPassword.isSelected() ? (char) 0 : '•');
        });

        panel.add(new JLabel("Username:"));
        panel.add(userField);
        panel.add(new JLabel("Password:"));
        panel.add(passField);
        panel.add(new JLabel(""));
        panel.add(showPassword);
        panel.add(new JLabel(""));
        panel.add(passwordHint);
        panel.add(new JLabel("Initial Balance:"));
        panel.add(balanceField);
        panel.add(new JLabel(""));
        panel.add(createBtn);

        // Logic of registration
        createBtn.addActionListener(e -> {
            String user = userField.getText();
            String pass = new String(passField.getPassword());
            double balance;

            if (user.isEmpty() || pass.isEmpty() || balanceField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required.");
                return;
            }

            if (!isPasswordValid(pass)) {
                JOptionPane.showMessageDialog(this,
                        "Password must be at least 6 characters long, contain at least one letter and one special character.");
                return;
            }

            try {
                balance = Double.parseDouble(balanceField.getText());
                if (balance < 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Enter a valid positive balance.");
                return;
            }

            if (accounts.containsKey(user)) {
                JOptionPane.showMessageDialog(this, "Username already exists.");
            } else {
                accounts.put(user, new BankAccount(user, pass, balance));
                saveAccountsToFile();
                JOptionPane.showMessageDialog(this, "Account created successfully!");
                card.show(main, "Login");
            }
        });

        return panel;
    }

    // --- Login Panel ---
    private JPanel login() {
        // 5 rows layout for logic panel
        JPanel panel = new JPanel(new GridLayout(5, 2, 5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Login"));

        //reusable fields
        loginUserField = new JTextField();
        loginPassField = new JPasswordField();

        // toggle for password visibility
        JCheckBox showPassword = new JCheckBox("Show Password");
        JButton loginBtn = new JButton("Login");

        showPassword.addActionListener(e -> {
            loginPassField.setEchoChar(showPassword.isSelected() ? (char) 0 : '•');
        });

        panel.add(new JLabel("Username:"));
        panel.add(loginUserField);
        panel.add(new JLabel("Password:"));
        panel.add(loginPassField);
        panel.add(new JLabel(""));
        panel.add(showPassword);
        panel.add(new JLabel(""));
        panel.add(loginBtn);

        // Logic for login
        loginBtn.addActionListener(e -> {
            String user = loginUserField.getText();
            String pass = new String(loginPassField.getPassword());

            if (!accounts.containsKey(user)) {
                JOptionPane.showMessageDialog(this, "User not found. Please register first.");
                loginUserField.setText("");
                loginPassField.setText("");
                card.show(main, "Startup"); // 👈 Go back to Startup screen
                return;
            }

            BankAccount account = accounts.get(user);
            if (account.getPassword().equals(pass)) {
                currentUser = account;
                JOptionPane.showMessageDialog(this, "Login successful!");
                card.show(main, "ATM");
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials.");
                loginUserField.setText("");
                loginPassField.setText("");
                card.show(main, "Startup"); // 👈 Back to Startup
            }
        });



        return panel;
    }

    // --- ATM Panel ---
    private JPanel atm() {
        // used for deposit , withdraw , balance check , logout
        JPanel panel = new JPanel(new GridLayout(5, 2, 5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("ATM Operations"));

        amountField = new JTextField();
        JButton depositBtn = new JButton("Deposit");
        depositBtn.setFont(new Font("xyz", Font.PLAIN , 28));
        JButton withdrawBtn = new JButton("Withdraw");
        withdrawBtn.setFont(new Font("xyz", Font.PLAIN , 28));
        JButton balanceBtn = new JButton("Check Balance");
        balanceBtn.setFont(new Font("xyz", Font.PLAIN , 28));
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setFont(new Font("xyz", Font.PLAIN , 28));

        panel.add(new JLabel("Amount:"));
        panel.add(amountField);
        panel.add(depositBtn);
        panel.add(withdrawBtn);
        panel.add(balanceBtn);
        panel.add(logoutBtn);

        // logic for deposit
        depositBtn.addActionListener(e -> {
            try {
                double amount = Double.parseDouble(amountField.getText());
                if (amount <= 0) {
                    JOptionPane.showMessageDialog(this, "Enter a positive amount.");
                } else {
                    currentUser.deposit(amount);
                    JOptionPane.showMessageDialog(this, "Deposited successfully. New Balance: ₹" + currentUser.getBalance());
                    amountField.setText("");
                    saveAccountsToFile();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid amount.");
            }
        });

        // logic for withdraw
        withdrawBtn.addActionListener(e -> {
            try {
                double amount = Double.parseDouble(amountField.getText());
                if (amount <= 0) {
                    JOptionPane.showMessageDialog(this, "Enter a positive amount.");
                } else if (currentUser.withdraw(amount)) {
                    JOptionPane.showMessageDialog(this, "Withdrawn successfully. New Balance: ₹" + currentUser.getBalance());
                    amountField.setText("");
                    saveAccountsToFile();
                } else {
                    JOptionPane.showMessageDialog(this, "Insufficient balance.");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid amount.");
            }
        });

        // balance display
        balanceBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Current Balance: ₹" + currentUser.getBalance());
        });

        // logic for logout
        logoutBtn.addActionListener(e -> {
            amountField.setText("");
            loginUserField.setText("");
            loginPassField.setText("");
            saveAccountsToFile();
            currentUser = null;
            card.show(main, "Login");
        });

        return panel;
    }

    // --- Password Validation ---
    // enforces basic password rules
    private boolean isPasswordValid(String password) {
        if (password.length() < 6) return false;
        boolean hasLetter = password.matches(".*[a-zA-Z].*");
        boolean hasSpecial = password.matches(".*[^a-zA-Z0-9].*");
        return hasLetter && hasSpecial;
    }

    // --- Save/Load Account Data ---
    private void saveAccountsToFile() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            out.writeObject(accounts);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Failed to save account data.");
        }
    }

    @SuppressWarnings("unchecked")
    private void loadAccountsFromFile() {
        File file = new File(FILE_NAME);

        if (!file.exists()) {
            JOptionPane.showMessageDialog(this, "No saved account file found.");
            accounts = new HashMap<>();
            return;
        }

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            Object obj = in.readObject();
            if (obj instanceof HashMap) {
                accounts = (HashMap<String, BankAccount>) obj;

                if (accounts.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Account file loaded but no user details found.");
                } else {

                }
            } else {
                JOptionPane.showMessageDialog(this, "File content is not valid.");
                accounts = new HashMap<>();
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error reading account data: " + e.getMessage());
            accounts = new HashMap<>();
        }
    }


    // --- BankAccount Class ---
    private static class BankAccount implements Serializable {
        private static final long serialVersionUID = 1L;
        private String username;
        private String password;
        private double balance;

        public BankAccount(String username, String password, double balance) {
            this.username = username;
            this.password = password;
            this.balance = balance;
        }

        public String getPassword() {
            return password;
        }

        public double getBalance() {
            return balance;
        }

        public void deposit(double amount) {
            balance += amount;
        }

        public boolean withdraw(double amount) {
            if (balance >= amount) {
                balance -= amount;
                return true;
            }
            return false;
        }
    }

    // --- Main ---
    public static void main(String[] args) {
        SwingUtilities.invokeLater(ATM_Mangement::new);
    }
}
