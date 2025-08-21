import java.util.Scanner;
import javax.swing.*; // GUI components
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;

public class Grade_Calculator extends JFrame {
    // Panel number 1 to get input how many sub
    private JPanel First;
    private JTextField Number_Of_Sub;
    private JButton next;
    //panel 2 to calculate grade
    private JPanel Second;
    private JButton Cal_Grade;
    private JLabel Result;
    private JTextField Sub_Count[];

    // Constructor
    public Grade_Calculator(){
        setTitle("Grade Calculator");
        JLabel Title = new JLabel("Grade Calculator",JLabel.CENTER);
        Title.setFont(new Font("xyz",Font.ITALIC,28));
        Title.setForeground(Color.RED);
        add(Title,BorderLayout.NORTH);
        setSize(800, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        FirstScreen();
    }

    // set up first Panel
    private void FirstScreen(){
        First = new JPanel(new GridLayout(3,1,10,10));
        First.setBorder(BorderFactory.createEmptyBorder(30,50,30,50));
        JLabel Text = new JLabel("Enter number of subjects:",JLabel.CENTER);
        Text.setFont(new Font("xyz",Font.BOLD,28));
        Number_Of_Sub = new JTextField();
        Number_Of_Sub.setHorizontalAlignment(JTextField.CENTER);
        Number_Of_Sub.setFont(new Font("xyz",Font.PLAIN,28));

        next = new JButton("Next");
        next.setFont(new Font("xyz",Font.BOLD,28));
        First.add(Text);
        First.add(Number_Of_Sub);
        First.add(next);
        add(First,BorderLayout.CENTER);

        next.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    int sub_count = Integer.parseInt(Number_Of_Sub.getText());
                    if(sub_count <= 0) throw new NumberFormatException() ;
                    remove(First);
                    createInput(sub_count);
                }catch(NumberFormatException a){
                    JOptionPane.showMessageDialog(Grade_Calculator.this,"Please enter a valid positive Number.","Input Error,",JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }
    //step 2
    // when next hit if valid remove first panel,then second panel comes to work
    private void createInput(int count){
        Sub_Count = new JTextField[count];
        // adding 2 extra rows one for button and one for result
        Second = new JPanel(new GridLayout(count + 2 , 2 ,10,10));
        TitledBorder title2 = BorderFactory.createTitledBorder("Enter marks for each subjects");
        title2.setTitleFont(new Font("xyz",Font.ITALIC,32));
        title2.setTitleColor(Color.RED);
        Second.setBorder(title2);
        // for each subject creating label and text field
        for(int i=0 ; i<count ; i++) {
            Second.add(new JLabel("Subject " + (i + 1) + ":"));
            Sub_Count[i] = new JTextField();
            Sub_Count[i].setFont(new Font("xyz",Font.PLAIN,28));
            Sub_Count[i].setHorizontalAlignment(JTextField.CENTER);
            Second.add(Sub_Count[i]);
        }
        Cal_Grade = new JButton("Calculate Grade");
        Cal_Grade.setFont(new Font("xyz",Font.BOLD,28));
        Result = new JLabel("", JLabel.CENTER);
        Result.setFont(new Font("xyz", Font.BOLD, 30));

        Second.add(Cal_Grade);
        Second.add(Cal_Grade);
        Second.add(Result);
        add(Second, BorderLayout.CENTER);
        // refresh the frame after removing the previous panel
        revalidate();
        repaint();
        Cal_Grade.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Cal_Result();
            }
        });
    }

    // step 3
    private void Cal_Result() {
        try {
            int total = 0;
            for (JTextField field : Sub_Count) {
                int marks = Integer.parseInt(field.getText());
                if (marks < 0 || marks > 100) throw new NumberFormatException();
                total += marks;
            }

            double average = total / (double) Sub_Count.length;
            String grade = getGrade(average);

            Result.setText(String.format("Total: %d, Average: %.2f%%, Grade: %s", total, average, grade));
            Result.setFont(new Font("xyz",Font.BOLD,32));
            Result.setForeground(Color.red);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Enter valid marks (0–100) in all fields.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String getGrade(double avg) {
        if (avg >= 90) return "A+";
        else if (avg >= 80) return "A";
        else if (avg >= 70) return "B";
        else if (avg >= 60) return "C";
        else if (avg >= 50) return "D";
        else return "F";
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Grade_Calculator().setVisible(true);
        });
    }
}


