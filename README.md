# Java-bases-Software-using-Java-Swing-Virtual-Study-management-System-

This application is a desktop-based learning management system (LMS) built using Java Swing, SQLite, and REST API integration. It is designed to replicate core features of Google Classroom while remaining simple enough to run on any personal computer without a server setup. The app combines offline functionality (local database, classroom management) with online features (web-based API access such as Google Books).
The goal is to allow teachers and students to interact in a structured learning environment similar to Google Classroom.
Users can:

Log in with a username and password

Access class materials

Create, view, and manage classes

Search external educational resources (Books) through the Internet

The application is intended for academic use, desktop-based learning tools, and educational project demonstrations.
This is a standalone Java desktop application built with:

Swing UI for the graphical interface

SQLite for local persistent storage

Java HTTP networking for internet access

Google Books API for retrieving online book information

Maven for dependency management

It does not require a web server—everything runs locally—while still being able to fetch online resources
🖥 Overview of the System

This is a standalone Java desktop application built with:

Swing UI for the graphical interface

SQLite for local persistent storage

Java HTTP networking for internet access

Google Books API for retrieving online book information

Maven for dependency management

It does not require a web server—everything runs locally—while still being able to fetch online resources.

🧩 Key Features
✔ 1. User Login

Simple authentication system using SQLite

Users have roles: Teacher or Student

Login window verifies credentials before granting access

✔ 2. Main Dashboard

After login, users see a tabbed interface:

Classes tab

Google Books tab

Future expansion: Assignments, Profile, Submissions, etc.

✔ 3. Class Management (Teacher or Student)

Create new classes with a title and auto-generated class code

View all available classes in the system

Future-ready structure for joining classes, viewing assignments, etc.

✔ 4. Internet Integration (Google Books API)

The application can access external online information:

Search any keyword (e.g., “Java programming”)

Fetch book titles, authors, descriptions, and thumbnails

Display results in the GUI

Uses HttpURLConnection and Google's public REST API

This demonstrates integrating desktop Java applications with online web services.

✔ 5. Local Database Storage (SQLite)

All data is stored locally:

Users

Classes

Assignments (structure ready)
No need for MySQL or online hosting.

🛠 Technical Architecture
Frontend (UI Layer)

Java Swing

Frames, Panels, and TabbedPane for navigation

List views for classes and search results

Clean layout and beginner-friendly UI

Backend (Database Layer)

SQLite database file classroom.db

Tables: users, classes, assignments

DAOs (Data Access Objects) handle all queries

Network / API Layer

A dedicated utility class BooksApiClient

Handles REST calls to Google Books API

Parses JSON using Gson

🚀 What Makes This App Useful

Demonstrates full-stack desktop app development

Combines GUI, database, internet access, and APIs

Mimics real-world systems like Google Classroom

Usable offline + supports online search

Runs on any computer with Java installed

Excellent for academic projects and skill building

📚 Future Enhancements (Optional)

If you expand the project later, you can add:

Assignment posting & uploading files

Student-teacher communication

Notifications

Class codes for joining classes

File storage for materials

Enhanced UI with JavaFX or FlatLaf themes

Remote server backend for multi-user online access

📝 Short Summary (if you need a 5–6 line description)

This is a Java-based Google Classroom clone built using Swing and SQLite. It provides a login system, class creation, and a simple dashboard for teachers and students. The application also integrates with the Google Books API, allowing users to search books online directly from the desktop app. It works fully offline except for API features, requires no server, and demonstrates database management, GUI design, and API integration in one project.
