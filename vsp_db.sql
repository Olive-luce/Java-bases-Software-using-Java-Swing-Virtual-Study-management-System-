CREATE DATABASE IF NOT EXISTS vsp_db CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE vsp_db;

-- USERS TABLE
CREATE TABLE IF NOT EXISTS users (
  id INT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(255),
  email VARCHAR(255) UNIQUE,
  password VARCHAR(255),
  role ENUM('teacher','student','admin') DEFAULT 'student'
);

INSERT INTO users (name, email, password, role) VALUES
('Admin User', 'admin@example.com', 'admin123', 'admin'),
('Alice Teacher', 'alice.teacher@example.com', 'teacher123', 'teacher'),
('Bob Teacher', 'bob.teacher@example.com', 'teacher123', 'teacher'),
('Charlie Student', 'charlie.student@example.com', 'student123', 'student'),
('Diana Student', 'diana.student@example.com', 'student123', 'student'),
('Edward Student', 'edward.student@example.com', 'student123', 'student');


-- CLASSROOM TABLE
CREATE TABLE IF NOT EXISTS classroom (
  classId INT PRIMARY KEY AUTO_INCREMENT,
  className VARCHAR(255),
  teacherId INT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (teacherId) REFERENCES users(id) ON DELETE SET NULL
);

INSERT INTO classroom (className, teacherId) VALUES
('Mathematics 101', 2),
('History 202', 3);


-- CLASS STUDENTS
CREATE TABLE IF NOT EXISTS class_students (
  id INT PRIMARY KEY AUTO_INCREMENT,
  classId INT,
  studentId INT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (classId) REFERENCES classroom(classId) ON DELETE CASCADE,
  FOREIGN KEY (studentId) REFERENCES users(id) ON DELETE CASCADE
);

INSERT INTO class_students (classId, studentId) VALUES
(1, 4),
(1, 5),
(2, 5),
(2, 6);


-- MATERIALS TABLE
CREATE TABLE IF NOT EXISTS materials (
  materialId INT PRIMARY KEY AUTO_INCREMENT,
  classId INT,
  title VARCHAR(255),
  filePath VARCHAR(2000),
  uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (classId) REFERENCES classroom(classId) ON DELETE CASCADE
);

INSERT INTO materials (classId, title, filePath) VALUES
(1, 'Algebra Basics', '/files/algebra.pdf'),
(1, 'Trigonometry Notes', '/files/trigonometry.pdf'),
(2, 'World War II Overview', '/files/ww2.pdf');


-- MARKS TABLE
CREATE TABLE IF NOT EXISTS marks (
  studentId INT,
  classId INT,
  marks INT,
  PRIMARY KEY (studentId, classId),
  FOREIGN KEY (studentId) REFERENCES users(id) ON DELETE CASCADE,
  FOREIGN KEY (classId) REFERENCES classroom(classId) ON DELETE CASCADE
);

INSERT INTO marks (studentId, classId, marks) VALUES
(4, 1, 85),
(5, 1, 92),
(5, 2, 88),
(6, 2, 75);

SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE class_students;
TRUNCATE TABLE materials;
TRUNCATE TABLE marks;
TRUNCATE TABLE classroom;
TRUNCATE TABLE users;
SET FOREIGN_KEY_CHECKS = 1;
