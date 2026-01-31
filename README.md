🏆 Kronos — Tournament & League Management System

Kronos is a desktop tournament and league management application built with Java and JavaFX. It helps organizers manage leagues, tournaments, teams, brackets, matches, referees, and standings through a modern GUI connected to a REST API.

This project was originally developed collaboratively by four classmates in a private GitHub repository. This public repository was created later to showcase the project for portfolio and resume purposes.

📌 Project Background

👥 Team Size: 4 Developers

🔒 Original Repository: Private (Course Project)

🌐 Public Repository: Created for learning, documentation, and portfolio visibility

🎯 Purpose: Academic + Real-world tournament management system

We followed real software engineering practices including version control, task division, code reviews, and modular architecture.

🚀 Features
🔐 Authentication & Roles

Secure login with JWT authentication

Role-based access control:

Admin

Tournament Organizer

Referee

User

Admin panel for user management

🏟️ Leagues & Tournaments

Create and manage leagues

Create and assign tournaments

View tournament details

League standings

👥 Team Management

Add/remove teams

Prevent duplicate entries

Assign teams to tournaments

🏆 Brackets & Matches

Supported formats:

Single Elimination

Seeded Elimination

Round Robin

Interactive bracket visualization

Zoom & pan support

Match detail popup

Winner selection and propagation

📊 Standings & Points

Automatic point generation from brackets

Manual point editing

Sync standings with API

League-wide rankings

🧑‍⚖️ Resources

Court management

Referee assignment

Validation and conflict handling

💻 UI/UX

Responsive JavaFX interface

Dark theme styling

Async API calls

Loading & error feedback

Consistent layout across views

🛠️ Tech Stack
Category	Technologies
Language	Java 24
GUI	JavaFX 24
Build Tool	Maven
API Client	Retrofit, OkHttp
JSON	Gson
Database	SQLite, jOOQ
Testing	JUnit 5, Mockito, TestFX
Auth	JWT
Config	.env, config.properties
🏗️ Architecture

MVC (Model-View-Controller) pattern

Central AppController for navigation

Singleton API client

DTO layer for API communication

Observable data binding

Modular view controllers

Asynchronous networking

Design patterns used:

Singleton

Observer

Dependency Injection

MVC

📂 Project Structure (Simplified)
src/
 ├── controllers/
 ├── views/
 ├── models/
 ├── dto/
 ├── api/
 ├── utils/
 └── main/

⚙️ Setup & Installation
Prerequisites

Java 24+

Maven

Internet connection (API access)

Steps
# Clone the repo
git clone https://github.com/your-username/kronos-public.git

# Enter directory
cd kronos-public

# Build project
mvn clean install

# Run application
mvn javafx:run


Make sure your .env or config.properties file is configured with the correct API endpoint.

🧪 Testing

Run tests using:

mvn test


Includes:

Unit tests

UI tests

Mocked API tests

📖 What We Learned

Through this project, we gained experience in:

Building large-scale JavaFX applications

Working with REST APIs

JWT authentication

Team collaboration with Git

MVC architecture

Asynchronous programming

UI/UX design

Database code generation

Software documentation

👥 Team Collaboration

This project was built by a team of four students. We:

Used GitHub for version control

Followed feature branching

Conducted code reviews

Held regular planning sessions

Divided work by modules

Maintained shared documentation

The original development was done in a private repository for academic integrity. This public version is shared for learning and demonstration purposes.

📌 Resume Summary

Built a Java/JavaFX tournament management system with REST API integration, JWT authentication, role-based access control, and interactive bracket visualization using MVC architecture and asynchronous networking.

⚠️ Disclaimer

This repository is a public showcase version of an academic project.
Sensitive credentials, private APIs, and internal data have been removed.

📬 Contact

For questions or collaboration:

Name: Nitai Mahat
Email: your-email@example.com

GitHub: https://github.com/your-username
