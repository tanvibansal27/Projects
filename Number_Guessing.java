import javax.swing.*; // GUI components
import java.awt.*;
import java.awt.event.*;// Allow event handling like buttons

public class Number_Guessing extends JFrame implements ActionListener {
    // game state variables store game logic
    private int Random_Number;
    private int Remaining_Attempts;
    private int TotalRoundWon = 0;
    private int Attempts = 0;

    // GUi Components
    private final JTextField Guess_Field;
    private final JButton Guess_Button;
    private final JButton New_Button;
    private final JLabel Info_Label;
    private JLabel Attempts_Label;

    // Gui elements for user input
    private final JLabel rounds_won_Label;
    private JLabel Total_Attempts_Label;
    private final JLabel average_attempts;

    // constructor
    public Number_Guessing() {
        // frame setup
        setTitle("----Number Game----");
        setSize(800, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE); // ends app when close
        setLayout(new BorderLayout());

        JPanel main = new JPanel(new GridLayout(6, 1));// panel with 6 rows
        Info_Label = new JLabel("Guess a number...", SwingConstants.CENTER);
        Info_Label.setFont(new Font("xyz",Font.PLAIN,25));
        Guess_Field = new JTextField();
        Guess_Field.setFont(new Font("xyz",Font.PLAIN,30));
        Guess_Field.setHorizontalAlignment(JTextField.CENTER);
        Guess_Button = new JButton("Guess");
        Guess_Button.setFont(new Font("xyz",Font.PLAIN,21));
        New_Button = new JButton("Want to play new game");
        New_Button.setFont(new Font("xyz",Font.PLAIN,21));
        Attempts_Label = new JLabel("You have remaining Attempts : 10", SwingConstants.CENTER);
        Attempts_Label.setFont(new Font("xyz",Font.PLAIN,25));

        Guess_Button.addActionListener(this);
        New_Button.addActionListener(e -> startNewGame());
        New_Button.setEnabled(false);
        // Adding components to main panel
        main.add(Info_Label);
        main.add(Guess_Field);
        main.add(Guess_Button);
        main.add(New_Button);
        main.add(Attempts_Label);

        // Score panel
        JPanel Score = new JPanel(new GridLayout(1, 3));

        // adding 3 horizontal panel
        rounds_won_Label = new JLabel("Rounds won : 0", SwingConstants.CENTER);
        rounds_won_Label.setFont(new Font("xyz",Font.PLAIN,19));
        Attempts_Label = new JLabel("Total Attempts : 0", SwingConstants.CENTER);
        Attempts_Label.setFont(new Font("xyz",Font.PLAIN,19));
        average_attempts = new JLabel("Average Attempts : 0.0", SwingConstants.CENTER);
        average_attempts.setFont(new Font("xyz",Font.PLAIN,19));

        // display current score
        Score.add(rounds_won_Label);
        Score.add(Attempts_Label);
        Score.add(average_attempts);

        add(main, BorderLayout.CENTER);
        add(Score, BorderLayout.SOUTH);

        startNewGame();
        setVisible(true);
    }

    private void startNewGame() {
        Random_Number = (int) (Math.random() * 100 + 1);
        Remaining_Attempts = 10;
        Guess_Button.setEnabled(true);
        New_Button.setEnabled(false);
        Guess_Field.setText("");
        Info_Label.setText("Guess a number between 1 and 100 . You have 10 attempts.");
        Attempts_Label.setText("Remaining Attempts : " + Remaining_Attempts);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String input = Guess_Field.getText();
        try {
            int guess = Integer.parseInt(input);
            Remaining_Attempts--;

            if (guess == Random_Number) {
                TotalRoundWon++;
                int attemptsUsed = 10 - Remaining_Attempts;

                Attempts += attemptsUsed;
                Info_Label.setText("Congratulations , You guess correct in " + attemptsUsed + "attempts\n If you want to play again then press New game.");
                Guess_Button.setEnabled(false);
                New_Button.setEnabled(true);
                updateScorePanel();
            } else if (guess > Random_Number) Info_Label.setText("oops sorry !!! you guess too high ... try again.");
            else Info_Label.setText("oops sorry !!! You guess too low ... try again.");

            if (Remaining_Attempts == 0 && guess != Random_Number) {
                Info_Label.setText("Game over ! Try again ... The correct number was " + Random_Number);
                Guess_Button.setEnabled(false);
                New_Button.setEnabled(true);
            }
            Attempts_Label.setText("Remaining attempts : " + Remaining_Attempts);
            Guess_Field.setText("");
        } catch (NumberFormatException ex) {
            Info_Label.setText("Please enter a Valid number !!!!");
        }
    }

    private void updateScorePanel() {
        double averageAttempts = TotalRoundWon > 0 ? (double) Attempts / TotalRoundWon :0;
        rounds_won_Label.setText("Rounds won : " + TotalRoundWon);
        Total_Attempts_Label.setText("Total Attempts : " + Attempts);
        average_attempts .setText("Avg attempts/Win : "+ String.format("%.1f",average_attempts));
    }

    public static void main(String[] args) {
        new Number_Guessing();
    }
}
