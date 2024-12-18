# ExamApp: Online Examination Application

## Overview
ExamApp is a Java-based online examination platform that provides a secure, user-friendly interface for conducting assessments. Built with Java Swing and JDBC, it offers features like user authentication, quiz management, timed tests, real-time score calculation, and feedback collection. ExamApp is designed to meet the needs of educational institutions, corporate training programs, and certification bodies.

---

## Features
- **User Authentication**: Secure login and registration for students and instructors.
- **Quiz Management**: Instructors can create, edit, and delete quizzes with multiple question types.
- **Timed Exams**: Countdown timer for quizzes with automatic submission upon expiration.
- **Real-Time Results**: Immediate score calculation and feedback upon quiz completion.
- **Feedback Collection**: Users can provide feedback to improve future exams.
- **Database Integration**: Secure data storage for user accounts, quiz questions, and results using MySQL.

---

## Technologies Used
- **Java Swing**: For building the graphical user interface.
- **JDBC**: For database connectivity.
- **MySQL**: Backend database to store user credentials, quiz questions, and scores.
- **HTML/CSS**: Supplementary frontend for exam selection.

---

## Key Components
1. **Login and Registration**:
   - Students and instructors can register and log in securely.
   - Role-based access ensures proper permissions.

2. **Quiz Interface**:
   - Displays multiple-choice questions.
   - Timer tracks remaining time.
   - Progress indicators for navigation.

3. **Result and Feedback**:
   - Detailed score breakdown upon completion.
   - Feedback option for users to share their experience.

4. **Admin Panel**:
   - Allows instructors to add, update, or delete quiz questions.

---

## Database Schema
### `users_examapp` Table
- `id` (INT): Primary key.
- `username` (VARCHAR): User's unique username.
- `password` (VARCHAR): Encrypted password.

### `java__question` Table
- `id` (INT): Primary key.
- `question` (TEXT): Question text.
- `answer1`, `answer2`, `answer3`, `answer4` (VARCHAR): Answer options.
- `correct_answer` (VARCHAR): The correct answer.

---

## Setup Instructions
1. **Clone the Repository**:
   ```bash
   git clone [https://github.com/naveenkm21/ExamApp.git]
