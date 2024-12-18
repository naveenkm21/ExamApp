package javaapplication6;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class QuestionEntryPanel extends JFrame {
    private JTextField answer1Field, answer2Field, answer3Field, answer4Field;
    private JTextArea questionArea;
    private JComboBox<String> correctAnswerCombo;
    private JButton saveButton;
    private Connection connection;

    public QuestionEntryPanel() {
        // Set up JFrame properties
        setTitle("Add New Question");
        setSize(500, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Set up panel layout
        JPanel panel = new JPanel();
        panel.setLayout(null);

        // Question label and area
        JLabel questionLabel = new JLabel("Question:");
        questionLabel.setFont(new Font("Arial", Font.BOLD, 16));
        questionLabel.setBounds(20, 20, 100, 30);
        panel.add(questionLabel);

        questionArea = new JTextArea();
        questionArea.setLineWrap(true);
        questionArea.setWrapStyleWord(true);
        questionArea.setBounds(20, 50, 440, 80);
        panel.add(questionArea);

        // Answer fields
        JLabel answer1Label = new JLabel("Answer 1:");
        answer1Label.setBounds(20, 140, 100, 30);
        panel.add(answer1Label);

        answer1Field = new JTextField();
        answer1Field.setBounds(120, 140, 340, 30);
        panel.add(answer1Field);

        JLabel answer2Label = new JLabel("Answer 2:");
        answer2Label.setBounds(20, 180, 100, 30);
        panel.add(answer2Label);

        answer2Field = new JTextField();
        answer2Field.setBounds(120, 180, 340, 30);
        panel.add(answer2Field);

        JLabel answer3Label = new JLabel("Answer 3:");
        answer3Label.setBounds(20, 220, 100, 30);
        panel.add(answer3Label);

        answer3Field = new JTextField();
        answer3Field.setBounds(120, 220, 340, 30);
        panel.add(answer3Field);

        JLabel answer4Label = new JLabel("Answer 4:");
        answer4Label.setBounds(20, 260, 100, 30);
        panel.add(answer4Label);

        answer4Field = new JTextField();
        answer4Field.setBounds(120, 260, 340, 30);
        panel.add(answer4Field);

        // Correct answer dropdown
        JLabel correctAnswerLabel = new JLabel("Correct Answer:");
        correctAnswerLabel.setBounds(20, 300, 120, 30);
        panel.add(correctAnswerLabel);

        correctAnswerCombo = new JComboBox<>(new String[]{"Answer 1", "Answer 2", "Answer 3", "Answer 4"});
        correctAnswerCombo.setBounds(140, 300, 150, 30);
        panel.add(correctAnswerCombo);

        // Save button
        saveButton = new JButton("Save Question");
        saveButton.setBounds(180, 350, 150, 40);
        panel.add(saveButton);

        // Add panel to frame
        add(panel);

        // Set up database connection
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/examapp", "root", "N@y83889");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database connection error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Save button action
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveQuestionToDatabase();
            }
        });
    }

    // Method to save question to database
    private void saveQuestionToDatabase() {
        String question = questionArea.getText();
        String answer1 = answer1Field.getText();
        String answer2 = answer2Field.getText();
        String answer3 = answer3Field.getText();
        String answer4 = answer4Field.getText();
        String correctAnswer = correctAnswerCombo.getSelectedItem().toString();

        if (question.isEmpty() || answer1.isEmpty() || answer2.isEmpty() || answer3.isEmpty() || answer4.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Incomplete Data", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String sql = "INSERT INTO java__question (question, answer1, answer2, answer3, answer4, correct_answer) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, question);
            stmt.setString(2, answer1);
            stmt.setString(3, answer2);
            stmt.setString(4, answer3);
            stmt.setString(5, answer4);
            stmt.setString(6, correctAnswer);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Question added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            clearFields();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error saving question: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Method to clear input fields
    private void clearFields() {
        questionArea.setText("");
        answer1Field.setText("");
        answer2Field.setText("");
        answer3Field.setText("");
        answer4Field.setText("");
        correctAnswerCombo.setSelectedIndex(0);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            QuestionEntryPanel frame = new QuestionEntryPanel();
            frame.setVisible(true);
        });
    }
}
