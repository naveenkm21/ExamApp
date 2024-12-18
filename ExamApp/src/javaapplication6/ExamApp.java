package javaapplication6;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Collections;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.border.TitledBorder;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ExamApp extends JFrame implements ActionListener {
    public static void main(String[] args) {
        new ExamApp("Exam Application");
    }
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel questionLabel, timerLabel, progressLabel;
    private JRadioButton[] answerOptions = new JRadioButton[4];
    private ButtonGroup answerGroup;
    private JButton loginButton, startExamButton, nextButton, previousButton, resultButton, feedbackButton, submitFeedbackButton;
    private JTextArea feedbackArea;
    private int currentQuestion = 0, score = 0, totalQuestions = 20, remainingTime = 1200; // 20 minutes in seconds
    private String username;
    private String[][] questions;
    private JButton registerButton, submitRegistrationButton;
    private JTextField regUsernameField;
    private JPasswordField regPasswordField;

    private Timer timer;
    private Connection connection;
	private Component resultLabel;

    public ExamApp(String title) {
        super(title);
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        
        
        connectToDatabase();
        createLoginPage();
        createInstructionPage();
        createQuizPage();
        //createResultPage();
        //createFeedbackPage();
        createResultAndFeedbackPage();
        createRegistrationPage();
        
        add(mainPanel);
        cardLayout.show(mainPanel, "login");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void connectToDatabase() {
        try {
            String url = "jdbc:mysql://localhost:3306/ExamApp"; // Change the URL as needed
            String user = "root"; // Your MySQL username
            String password = "N@y83889"; // Your MySQL password
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Database connected successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to connect to the database.");
        }
    }
    
    private void loadQuestionsFromDatabase() {
        List<String[]> questionList = new ArrayList<>();
        
        if (connection == null) {
            System.out.println("Connection is null, unable to load questions.");
            return;
        }
        
        String sql = "SELECT question, answer1, answer2, answer3, answer4, correct_answer FROM java__question"; // Replace with your actual table name

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                String question = resultSet.getString("question");
                String answer1 = resultSet.getString("answer1");
                String answer2 = resultSet.getString("answer2");
                String answer3 = resultSet.getString("answer3");
                String answer4 = resultSet.getString("answer4");
                String correctAnswer = resultSet.getString("correct_answer");
                
                questionList.add(new String[] { question, answer1, answer2, answer3, answer4, correctAnswer });
            }
            
            if (questionList.isEmpty()) {
                System.out.println("No questions found in the database.");
            } else {
                System.out.println("Questions loaded successfully.");
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        questions = new String[questionList.size()][];
        questions = questionList.toArray(questions);
        totalQuestions = questions.length;

        System.out.println("Total questions loaded: " + totalQuestions);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginButton) {
            username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            if (validateLogin(username, password)) {
                loadQuestionsFromDatabase();
                cardLayout.show(mainPanel, "instruction");
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password.");
            	}
        }
        else if (e.getSource() == registerButton) {
                cardLayout.show(mainPanel, "register");
         }else if (e.getSource() == submitRegistrationButton) {
                String newUsername = regUsernameField.getText();
                String newPassword = new String(regPasswordField.getPassword());
                if (newUsername.length() < 10 && newPassword.length() < 10) {
                    registerUser(newUsername, newPassword);
                    JOptionPane.showMessageDialog(this, "Registration successful! Please log in.");
                    cardLayout.show(mainPanel, "login");
                }else {
                    JOptionPane.showMessageDialog(this, "Username and password must be less than 10 characters.");
                }
        }
        if (e.getSource() == startExamButton) {
        	shuffleQuestions(); //
            startTimer();
            displayQuestion();
            cardLayout.show(mainPanel, "quiz");
        }
        if (e.getSource() == nextButton) {
        	if (checkAnswer()) score++;  // Increment score for the current question
            if (currentQuestion < totalQuestions - 1) {
                currentQuestion++;
                displayQuestion();
            }//else {
              //  showResults();  // Show results when reaching the last question
               // cardLayout.show(mainPanel, "result_feedback");  // Go to result and feedback page
           // }
        }
        if (e.getSource() == previousButton) {
            if (currentQuestion > 0) {
                currentQuestion--;
                displayQuestion();
            }
        }
        if (e.getSource() == resultButton) {
            
            showResults();
            cardLayout.show(mainPanel, "result_feedback");
        }
        if (e.getSource() == feedbackButton) {
            cardLayout.show(mainPanel, "result_feedback");
        }
        if (e.getSource() == submitFeedbackButton) {
            String feedback = feedbackArea.getText();
            if(feedback == null || feedback.trim().isEmpty()) {
            	JOptionPane.showMessageDialog(this, "Please enter the feedback");
            }
            else {
            	
            JOptionPane.showMessageDialog(this, "Thank you for your feedback, " + username + "!");
            System.exit(0);

            }
        }
    }

    private void createRegistrationPage() {
        JPanel registerPanel = new JPanel(null);
        
        JLabel titleLabel = new JLabel("Online Exam Application");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 33));
        titleLabel.setBounds(600, 30, 1000, 50);
        
        // Create a box panel similar to loginBoxPanel for registration
        JPanel registerBoxPanel = new JPanel(null);
        registerBoxPanel.setBounds(400, 150, 800, 500); // Position and size of the box panel
        
        TitledBorder registerTitleBorder = BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), 
            "Registration Page",
            TitledBorder.CENTER, 
            TitledBorder.TOP
        );
        registerTitleBorder.setTitleFont(new Font("Arial", Font.BOLD, 24));
        registerBoxPanel.setBorder(registerTitleBorder);

        // Create Username field
        JLabel regUsernameLabel = new JLabel("Create Username (Less than 10 characters):");
        regUsernameLabel.setFont(new Font("Arial", Font.BOLD, 24));
        regUsernameLabel.setBounds(150, 100, 1000, 30);
        regUsernameField = new JTextField();
        regUsernameField.setFont(new Font("Arial", Font.PLAIN, 20));
        regUsernameField.setBounds(150, 150, 300, 40);

        // Create Password field
        JLabel regPasswordLabel = new JLabel("Create Password (Less than 10 characters):");
        regPasswordLabel.setFont(new Font("Arial", Font.BOLD, 24));
        regPasswordLabel.setBounds(150, 200, 1000, 40);
        regPasswordField = new JPasswordField();
        regPasswordField.setFont(new Font("Arial", Font.PLAIN, 20));
        regPasswordField.setBounds(150, 250, 300, 40);

        // Submit button
        submitRegistrationButton = new JButton("Submit");
        submitRegistrationButton.setFont(new Font("Arial", Font.BOLD, 24));
        submitRegistrationButton.setBounds(150, 350, 150, 50);
        submitRegistrationButton.addActionListener(this);

        // Add components to registerBoxPanel
        registerBoxPanel.add(regUsernameLabel);
        registerBoxPanel.add(regUsernameField);
        registerBoxPanel.add(regPasswordLabel);
        registerBoxPanel.add(regPasswordField);
        registerBoxPanel.add(submitRegistrationButton);

        // Add titleLabel and registerBoxPanel to registerPanel
        registerPanel.add(titleLabel);
        registerPanel.add(registerBoxPanel);
        
        mainPanel.add(registerPanel, "register");
    }

    // Modify the login page to include a "Register" button
    private void createLoginPage() {
    
        JPanel loginPanel = new JPanel(null);
        
        
        JLabel titleLabel=new JLabel("Online Exam Application");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 33));
        titleLabel.setBounds(600, 30, 1000, 50);
        
        JPanel loginBoxPanel = new JPanel(null);
        loginBoxPanel.setBounds(400, 150, 800, 500); // Position and size of the box panel
        TitledBorder titledBorder=(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), 
            "Login Page",
            TitledBorder.CENTER, 
            TitledBorder.TOP
            ));
        titledBorder.setTitleFont(new Font("Arial", Font.BOLD, 24));
       
        loginBoxPanel.setBorder(titledBorder);
        
        JLabel loginLabel = new JLabel("Enter Username (Less than 10 characters):");
        loginLabel.setFont(new Font("Arial", Font.BOLD, 24));
        loginLabel.setBounds(150, 100, 1000, 30);//250
        
        usernameField = new JTextField();
        usernameField.setFont(new Font("Arial", Font.PLAIN, 20));
        usernameField.setBounds(150, 150, 300, 40);
        
        JLabel passwordLabel = new JLabel("Enter Password (Less than 10 characters):");
        passwordLabel.setFont(new Font("Arial", Font.BOLD, 24));
        passwordLabel.setBounds(150, 200, 1000, 40);
        
        
        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Arial", Font.PLAIN, 20));
        passwordField.setBounds(150, 250, 300, 40);
        
        loginButton = new JButton("Login");
        loginButton.setFont(new Font("Arial", Font.BOLD, 24));
        loginButton.setBounds(150, 300, 150, 50);
        loginButton.addActionListener(this);
        
        JLabel donthave=new JLabel("Don't have an account?");
        donthave.setFont(new Font("Arial", Font.PLAIN, 20));
        donthave.setBounds(150, 360, 300, 40);

        registerButton = new JButton("Register");
        registerButton.setFont(new Font("Arial", Font.BOLD, 24));
        registerButton.setBounds(150, 400, 150, 50);
        registerButton.addActionListener(this);
        
        
        loginBoxPanel.add(loginLabel);
        loginBoxPanel.add(usernameField);
        loginBoxPanel.add(passwordLabel);
        loginBoxPanel.add(passwordField);
        loginBoxPanel.add(loginButton);
        loginBoxPanel.add(donthave);
        loginBoxPanel.add(registerButton);
        
        loginPanel.add(titleLabel);
        loginPanel.add(loginBoxPanel);
        
        mainPanel.add(loginPanel, "login");
    }


    private void createInstructionPage() {
        JPanel instructionPanel = new JPanel(null);
        
        JLabel titleLabel = new JLabel("Online Exam Application");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 33));
        titleLabel.setBounds(600, 30, 1000, 50);

        // Create a box panel for instructions
        JPanel instructionBoxPanel = new JPanel(null);
        instructionBoxPanel.setBounds(400, 150, 800, 500); // Position and size of the box panel

        TitledBorder instructionTitleBorder = BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), 
            "Instructions Page",
            TitledBorder.CENTER, 
            TitledBorder.TOP
        );
        instructionTitleBorder.setTitleFont(new Font("Arial", Font.BOLD, 24));
        instructionBoxPanel.setBorder(instructionTitleBorder);

        // Instruction label with HTML content
        JLabel instructionLabel = new JLabel("<html><h1>Instructions</h1><p>Answer all the questions to the best of your knowledge. Each correct answer gives you 1 point.</p></html>");
        instructionLabel.setFont(new Font("Arial", Font.BOLD, 26));
        instructionLabel.setBounds(50, 50, 700, 200); // Adjust the position and size as needed

        // Start Exam button
        startExamButton = new JButton("Start Exam");
        startExamButton.setFont(new Font("Arial", Font.BOLD, 24));
        startExamButton.setBounds(50, 250, 200, 50);
        startExamButton.addActionListener(this);
        
        // Add components to instructionBoxPanel
        instructionBoxPanel.add(instructionLabel);
        instructionBoxPanel.add(startExamButton);
        
        // Add titleLabel and instructionBoxPanel to instructionPanel
        instructionPanel.add(titleLabel);
        instructionPanel.add(instructionBoxPanel);

        mainPanel.add(instructionPanel, "instruction");
    }


    private void createQuizPage() {
        JPanel quizPanel = new JPanel(null);

        JLabel titleLabel = new JLabel("Online Exam Application");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 33));
        titleLabel.setBounds(600, 30, 1000, 50);

        // Create a box panel for the quiz
        JPanel quizBoxPanel = new JPanel(null);
        quizBoxPanel.setBounds(400, 150, 800, 500); // Position and size of the box panel

        TitledBorder quizTitleBorder = BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "Quiz",
            TitledBorder.CENTER,
            TitledBorder.TOP
        );
        quizTitleBorder.setTitleFont(new Font("Arial", Font.BOLD, 24));
        quizBoxPanel.setBorder(quizTitleBorder);

        // Question label
        questionLabel = new JLabel();
        questionLabel.setFont(new Font("Arial", Font.BOLD, 24));
        questionLabel.setBounds(50, 90, 1000, 50);

        // Answer options
        answerGroup = new ButtonGroup();
        for (int i = 0; i < 4; i++) {
            answerOptions[i] = new JRadioButton();
            answerOptions[i].setFont(new Font("Arial", Font.PLAIN, 22));
            answerOptions[i].setBounds(50, 150 + i * 50, 500, 40);
            quizBoxPanel.add(answerOptions[i]);
            answerGroup.add(answerOptions[i]);
        }

        // Timer label (ensure it is added to the correct panel)
        timerLabel = new JLabel("Time Left: 20:00");
        timerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        timerLabel.setBounds(570, 20, 200, 50);  // Make sure it fits inside the quizBoxPanel

        // Progress label
        progressLabel = new JLabel("Question: 1/20");
        progressLabel.setFont(new Font("Arial", Font.BOLD, 24));
        progressLabel.setBounds(40, 20, 200, 50);

        // Navigation buttons
        nextButton = new JButton("Next");
        previousButton = new JButton("Previous");
        resultButton = new JButton("Results");

        nextButton.setFont(new Font("Arial", Font.BOLD, 24));
        previousButton.setFont(new Font("Arial", Font.BOLD, 24));
        resultButton.setFont(new Font("Arial", Font.BOLD, 24));

        nextButton.setBounds(320, 400, 150, 50);
        previousButton.setBounds(20, 400, 150, 50);
        resultButton.setBounds(630, 400, 150, 50);

        nextButton.addActionListener(this);
        previousButton.addActionListener(this);
        resultButton.addActionListener(this);

        // Add components to quizBoxPanel
        quizBoxPanel.add(questionLabel);
        quizBoxPanel.add(timerLabel);  // Add the timer label here
        quizBoxPanel.add(progressLabel);
        quizBoxPanel.add(nextButton);
        quizBoxPanel.add(previousButton);
        quizBoxPanel.add(resultButton);

        // Add the quizBoxPanel to the main quizPanel
        quizPanel.add(titleLabel);
        quizPanel.add(quizBoxPanel);

        // Add the quizPanel to the mainPanel with the "quiz" name
        mainPanel.add(quizPanel, "quiz");
    }

    private void shuffleQuestions() {
    List<String[]> questionList = Arrays.asList(questions); // Convert array to list
    Collections.shuffle(questionList); // Shuffle the list
    
    // Trim the list to the first 20 questions if there are more than 20 questions
    if (questionList.size() > 20) {
        questionList = questionList.subList(0, 20);
    }
    
    questions = questionList.toArray(new String[questionList.size()][]); // Convert back to array
    totalQuestions = questions.length; // Update totalQuestions to 20 (or fewer if the question set was smaller)
}

   /* private void createResultPage() {
        JPanel resultPanel = new JPanel(null);
        JLabel resultLabel = new JLabel();
        resultLabel.setBounds(50, 50, 400, 100);
        feedbackButton = new JButton("Give Feedback");
        feedbackButton.setBounds(50, 150, 150, 30);
        feedbackButton.addActionListener(this);
        resultPanel.add(resultLabel);
        resultPanel.add(feedbackButton);
        mainPanel.add(resultPanel, "result");
    }

    private void createFeedbackPage() {
        JPanel feedbackPanel = new JPanel(null);
        JLabel feedbackLabel = new JLabel("Your Feedback:");
        feedbackLabel.setBounds(50, 50, 200, 30);
        feedbackArea = new JTextArea();
        feedbackArea.setBounds(50, 80, 500, 200);
        submitFeedbackButton = new JButton("Submit");
        submitFeedbackButton.setBounds(50, 300, 100, 30);
        submitFeedbackButton.addActionListener(this);
        feedbackPanel.add(feedbackLabel);
        feedbackPanel.add(feedbackArea);
        feedbackPanel.add(submitFeedbackButton);
        mainPanel.add(feedbackPanel, "feedback");
    }*/
    
    private void createResultAndFeedbackPage() {
        JPanel resultFeedbackPanel = new JPanel(null);

        // Title label
        JLabel titleLabel = new JLabel("Feedback Page");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 33));
        titleLabel.setBounds(650, 30, 1000, 50);

        // Create a box panel for the result and feedback section
        JPanel resultFeedbackBoxPanel = new JPanel(null);
        resultFeedbackBoxPanel.setBounds(400, 150, 800, 500); // Position and size of the result/feedback box panel

        TitledBorder resultFeedbackTitleBorder = BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "Feedback",
            TitledBorder.CENTER,
            TitledBorder.TOP
        );
        resultFeedbackTitleBorder.setTitleFont(new Font("Arial", Font.BOLD, 24));
        resultFeedbackBoxPanel.setBorder(resultFeedbackTitleBorder);

        // Result label (displaying the result summary)
        JLabel resultLabel = new JLabel("<html><body style='width: 700px'>You scored"+score+"</body></html>");
        resultLabel.setFont(new Font("Arial", Font.BOLD, 24));
        resultLabel.setBounds(50, 50, 700, 100);

        // Feedback button
        feedbackButton = new JButton("Give Feedback");
        feedbackButton.setFont(new Font("Arial", Font.BOLD, 22));
        feedbackButton.setBounds(50, 150, 200, 40);
        feedbackButton.addActionListener(this);

        // Feedback label (for entering feedback)
        JLabel feedbackLabel = new JLabel("Your Feedback:");
        feedbackLabel.setFont(new Font("Arial", Font.BOLD, 24));
        feedbackLabel.setBounds(50, 120, 200, 30);

        // Text area for feedback
        feedbackArea = new JTextArea();
        feedbackArea.setFont(new Font("Arial", Font.PLAIN, 18));
        feedbackArea.setBounds(50, 180, 700, 200);
        feedbackArea.setLineWrap(true);
        feedbackArea.setWrapStyleWord(true);

        // Submit feedback button
        submitFeedbackButton = new JButton("Submit");
        submitFeedbackButton.setFont(new Font("Arial", Font.BOLD, 22));
        submitFeedbackButton.setBounds(50, 400, 150, 40);
        submitFeedbackButton.addActionListener(this);

        // Add components to the result/feedback box panel
       // resultFeedbackBoxPanel.add(resultLabel);
        //resultFeedbackBoxPanel.add(feedbackButton);
        resultFeedbackBoxPanel.add(feedbackLabel);
        resultFeedbackBoxPanel.add(feedbackArea);
        resultFeedbackBoxPanel.add(submitFeedbackButton);

        // Add the result/feedback box panel to the resultFeedback panel
        resultFeedbackPanel.add(titleLabel);
        resultFeedbackPanel.add(resultFeedbackBoxPanel);

        // Add the resultFeedbackPanel to the mainPanel with the "result_feedback" name
        mainPanel.add(resultFeedbackPanel, "result_feedback");
    }


    private void startTimer() {
        timer = new Timer(1000, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                remainingTime--;
                int minutes = remainingTime / 60;
                int seconds = remainingTime % 60;
                timerLabel.setText(String.format("Time Left: %02d:%02d", minutes, seconds));
                if (remainingTime <= 0) {
                    timer.stop();
                    showResults();
                    cardLayout.show(mainPanel, "result");
                }
            }
        });
        timer.start();
    }
    private int correctAnswerIndex;  
    private void displayQuestion() {
    answerGroup.clearSelection();
    if (currentQuestion < totalQuestions) { // Check to make sure the index is within the limit
        String[] currentQ = questions[currentQuestion];
        String questionText = currentQ[0];
        
        questionLabel.setText("<html><body style='width: 550px'>" + questionText + "</body></html>");
        
        ArrayList<String> options = new ArrayList<>();
        for (int i = 1; i <= 4; i++) {
            options.add(currentQ[i]);
        }
        
        Collections.shuffle(options);
        
        for (int i = 0; i < 4; i++) {
            answerOptions[i].setText(options.get(i));  // Set the shuffled option text
            answerOptions[i].setSelected(false);       // Deselect all options initially
        }
        
        String correctAnswer = currentQ[5];
        for (int i = 0; i < 4; i++) {
            if (answerOptions[i].getText().equals(correctAnswer)) {
                correctAnswerIndex = i;  // Store the index of the correct answer
                break;
            }
        }
        
        progressLabel.setText("Question: " + (currentQuestion + 1) + "/" + totalQuestions);
    }
}


    private boolean checkAnswer() {
        String selectedAnswer = null;
        for (JRadioButton option : answerOptions) {
            if (option.isSelected()) {
                selectedAnswer = option.getText();
                break;
            }
        }
        return selectedAnswer != null && selectedAnswer.equals(questions[currentQuestion][5]);
    }

    private void showResults() {
        String resultMessage = "You scored " + score + " out of " + totalQuestions;
        JOptionPane.showMessageDialog(this, resultMessage);
        
    }

    private boolean validateLogin(String username, String password) {
        String sql = "SELECT * FROM users_examapp WHERE username = ? AND password = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Register a new user by inserting username and password into the database
    private void registerUser(String username, String password) {
        String sql = "INSERT INTO users_examapp (username, password) VALUES (?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
