import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Timer;
import java.util.TimerTask;

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

public class ExamApp extends JFrame implements ActionListener {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> examOptions;
    private JLabel questionLabel, timerLabel, progressLabel;
    private JRadioButton[] answerOptions = new JRadioButton[4];
    private ButtonGroup answerGroup;
    private JButton loginButton, startExamButton, nextButton, previousButton, resultButton, feedbackButton, submitFeedbackButton;
    private JTextArea feedbackArea;
    private int currentQuestion = 0, score = 0, totalQuestions = 20, remainingTime = 1200; // 20 minutes in seconds
    private String username, selectedExam;
    private String[][] questions;
    private Timer timer;

    public ExamApp(String title) {
        super(title);
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        createLoginPage();
        createInstructionPage();
        createQuizPage();
        createResultPage();
        createFeedbackPage();

        add(mainPanel);
        cardLayout.show(mainPanel, "login");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void createLoginPage() {
        JPanel loginPanel = new JPanel(null);
        JLabel loginLabel = new JLabel("Enter Username (10 characters):");
        loginLabel.setBounds(50, 50, 250, 30);
        usernameField = new JTextField();
        usernameField.setBounds(50, 80, 200, 30);
        
        JLabel passwordLabel = new JLabel("Enter Password (10 characters):");
        passwordLabel.setBounds(50, 120, 250, 30);
        passwordField = new JPasswordField();
        passwordField.setBounds(50, 150, 200, 30);
        
        JLabel examLabel = new JLabel("Select Exam:");
        examLabel.setBounds(50, 190, 200, 30);
        String[] exams = {"C", "C++", "Java", "Python", "DSA", "HTML", "CSS", "JavaScript", "MongoDB", "Node.js"};
        examOptions = new JComboBox<>(exams);
        examOptions.setBounds(50, 220, 200, 30);
        
        loginButton = new JButton("Login");
        loginButton.setBounds(50, 270, 100, 30);
        loginButton.addActionListener(this);
        
        loginPanel.add(loginLabel);
        loginPanel.add(usernameField);
        loginPanel.add(passwordLabel);
        loginPanel.add(passwordField);
        loginPanel.add(examLabel);
        loginPanel.add(examOptions);
        loginPanel.add(loginButton);
        mainPanel.add(loginPanel, "login");
    }

    private void createInstructionPage() {
        JPanel instructionPanel = new JPanel(null);
        JLabel instructionLabel = new JLabel("<html><h1>Instructions</h1><p>Answer all the questions to the best of your knowledge. Each correct answer gives you 1 point.</p></html>");
        instructionLabel.setBounds(50, 50, 400, 100);
        startExamButton = new JButton("Start Exam");
        startExamButton.setBounds(50, 200, 100, 30);
        startExamButton.addActionListener(this);
        instructionPanel.add(instructionLabel);
        instructionPanel.add(startExamButton);
        mainPanel.add(instructionPanel, "instruction");
    }

    private void createQuizPage() {
        JPanel quizPanel = new JPanel(null);
        questionLabel = new JLabel();
        questionLabel.setBounds(30, 40, 500, 20);
        quizPanel.add(questionLabel);
        
        answerGroup = new ButtonGroup();
        for (int i = 0; i < 4; i++) {
            answerOptions[i] = new JRadioButton();
            answerOptions[i].setBounds(50, 80 + (i * 30), 400, 20);
            quizPanel.add(answerOptions[i]);
            answerGroup.add(answerOptions[i]);
        }

        timerLabel = new JLabel("Time Left: 20:00");
        timerLabel.setBounds(400, 10, 150, 20);
        quizPanel.add(timerLabel);
        
        progressLabel = new JLabel("Question: 1/20");
        progressLabel.setBounds(30, 10, 150, 20);
        quizPanel.add(progressLabel);

        nextButton = new JButton("Next");
        previousButton = new JButton("Previous");
        resultButton = new JButton("Results");

        nextButton.setBounds(50, 270, 100, 30);
        previousButton.setBounds(200, 270, 100, 30);
        resultButton.setBounds(350, 270, 100, 30);

        nextButton.addActionListener(this);
        previousButton.addActionListener(this);
        resultButton.addActionListener(this);
        
        quizPanel.add(nextButton);
        quizPanel.add(previousButton);
        quizPanel.add(resultButton);
        
        mainPanel.add(quizPanel, "quiz");
    }

    private void createResultPage() {
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
        feedbackArea.setBounds(50, 80, 300, 100);
        submitFeedbackButton = new JButton("Submit");
        submitFeedbackButton.setBounds(50, 200, 100, 30);
        submitFeedbackButton.addActionListener(this);
        feedbackPanel.add(feedbackLabel);
        feedbackPanel.add(feedbackArea);
        feedbackPanel.add(submitFeedbackButton);
        mainPanel.add(feedbackPanel, "feedback");
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginButton) {
            username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            if (username.length() == 10 && password.length() == 10) {
                selectedExam = (String) examOptions.getSelectedItem();
                loadQuestions(selectedExam);
                cardLayout.show(mainPanel, "instruction");
            } else {
                JOptionPane.showMessageDialog(this, "Username and password must be exactly 10 characters.");
            }
        }
        if (e.getSource() == startExamButton) {
            startTimer();
            displayQuestion();
            cardLayout.show(mainPanel, "quiz");
        }
        if (e.getSource() == nextButton) {
            if (currentQuestion < totalQuestions - 1) {
                if (checkAnswer()) score++;
                currentQuestion++;
                displayQuestion();
            }
        }
        if (e.getSource() == previousButton) {
            if (currentQuestion > 0) {
                currentQuestion--;
                displayQuestion();
            }
        }
        if (e.getSource() == resultButton) {
            if (checkAnswer()) score++;
            showResults();
            cardLayout.show(mainPanel, "result");
        }
        if (e.getSource() == feedbackButton) {
            cardLayout.show(mainPanel, "feedback");
        }
        if (e.getSource() == submitFeedbackButton) {
            String feedback = feedbackArea.getText();
            JOptionPane.showMessageDialog(this, "Thank you for your feedback, " + username + "!");
            System.exit(0);
        }
    }

    private void startTimer() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (remainingTime > 0) {
                    remainingTime--;
                    int minutes = remainingTime / 60;
                    int seconds = remainingTime % 60;
                    timerLabel.setText(String.format("Time Left: %02d:%02d", minutes, seconds));
                } else {
                    timer.cancel();
                    JOptionPane.showMessageDialog(mainPanel, "Time's up! Submitting your answers.");
                    showResults();
                    cardLayout.show(mainPanel, "result");
                }
            }
        }, 1000, 1000);
    }

    private void loadQuestions(String exam) {
        switch (exam) {
            case "C":
                questions = new String[][]{
                    {"What is the output of printf(\"%d\", (5>2)?5:2);?", "5", "2", "Error", "None", "5"},
                    {"Which header file is used for standard input/output functions?", "conio.h", "stdio.h", "iostream", "math.h", "stdio.h"},
                    // Add more questions for C (total 20)
                };
                break;
            case "C++":
                questions = new String[][]{
                    {"Which of the following is not a valid C++ variable name?", "intVar", "2Var", "Var_1", "variable", "2Var"},
                    {"Which of the following is a valid C++ loop?", "for", "loop", "repeat", "run", "for"},
                    // Add more questions for C++ (total 20)
                };
                break;
            case "Java":
                questions = new String[][]{
                    {"Which of the following is not a primitive datatype in Java?", "int", "float", "String", "boolean", "String"},
                    {"Which of the following is used to handle exceptions in Java?", "throws", "try-catch", "final", "error", "try-catch"},
                    // Add more questions for Java (total 20)
                };
                break;
            case "Python":
                questions = new String[][]{
                    {"Which of the following is used to define a block of code in Python?", "Indentation", "Brackets", "Parentheses", "Semicolons", "Indentation"},
                    {"Which one is not a core datatype in Python?", "List", "Tuple", "Dictionary", "Class", "Class"},
                    // Add more questions for Python (total 20)
                };
                break;
            case "DSA":
                questions = new String[][]{
                    {"Which data structure uses LIFO?", "Queue", "Stack", "Array", "List", "Stack"},
                    {"Which algorithm is used in merge sort?", "Divide and conquer", "Greedy", "Backtracking", "Dynamic programming", "Divide and conquer"},
                    // Add more questions for DSA (total 20)
                };
                break;
            case "HTML":
                questions = new String[][]{
                    {"What does HTML stand for?", "HyperText Markup Language", "Hyper Transfer Markup Language", "HyperText Machine Language", "None of these", "HyperText Markup Language"},
                    {"Which tag is used for the largest heading?", "<h1>", "<h6>", "<head>", "<title>", "<h1>"},
                    // Add more questions for HTML (total 20)
                };
                break;
            case "CSS":
                questions = new String[][]{
                    {"Which of the following is not a CSS property?", "color", "background-color", "font-size", "align", "align"},
                    {"Which tag is used to link a CSS file to HTML?", "<link>", "<style>", "<script>", "<meta>", "<link>"},
                    // Add more questions for CSS (total 20)
                };
                break;
            case "JavaScript":
                questions = new String[][]{
                    {"Which company developed JavaScript?", "Netscape", "Google", "Microsoft", "Apple", "Netscape"},
                    {"What is the correct syntax to refer to an external script called 'script.js'?", "<script src='script.js'>", "<script href='script.js'>", "<script link='script.js'>", "<script url='script.js'>", "<script src='script.js'>"},
                    // Add more questions for JavaScript (total 20)
                };
                break;
            case "MongoDB":
                questions = new String[][]{
                    {"What type of database is MongoDB?", "Relational", "Document", "Graph", "None of these", "Document"},
                    {"Which language is used to query MongoDB?", "SQL", "NoSQL", "Mongo Query Language", "None", "Mongo Query Language"},
                    // Add more questions for MongoDB (total 20)
                };
                break;
            case "Node.js":
                questions = new String[][]{
                    {"Node.js is a runtime built on which engine?", "JavaScript V8 Engine", "SpiderMonkey", "Java VM", "None of these", "JavaScript V8 Engine"},
                    {"Which function is used to create a server in Node.js?", "createServer()", "newServer()", "httpServer()", "startServer()", "createServer()"},
                    // Add more questions for Node.js (total 20)
                };
                break;
        }
        totalQuestions = questions.length;
    }

    private void displayQuestion() {
        answerGroup.clearSelection();
        questionLabel.setText("Question " + (currentQuestion + 1) + ": " + questions[currentQuestion][0]);
        for (int i = 0; i < 4; i++) {
            answerOptions[i].setText(questions[currentQuestion][i + 1]);
        }
        progressLabel.setText("Question: " + (currentQuestion + 1) + "/" + totalQuestions);
    }

    private boolean checkAnswer() {
        return (answerOptions[0].isSelected() && questions[currentQuestion][5].equals(questions[currentQuestion][1])) ||
               (answerOptions[1].isSelected() && questions[currentQuestion][5].equals(questions[currentQuestion][2])) ||
               (answerOptions[2].isSelected() && questions[currentQuestion][5].equals(questions[currentQuestion][3])) ||
               (answerOptions[3].isSelected() && questions[currentQuestion][5].equals(questions[currentQuestion][4]));
    }

    private void showResults() {
        JLabel resultLabel = (JLabel) ((JPanel) mainPanel.getComponent(3)).getComponent(0);
        resultLabel.setText("Congratulations " + username + ", your score is: " + score + "/" + totalQuestions);
        timer.cancel();
    }

    public static void main(String[] args) {
        new ExamApp("Exam Application");
    }
}