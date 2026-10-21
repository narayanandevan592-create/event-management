# 🎟️ Eventora — Campus Event Management & Registration System

A desktop event management application developed in **Java Swing** with **MySQL & JDBC** relational database persistence. Designed and built by BTech Computer Science & Engineering students to demonstrate object-oriented software engineering principles, layered 3-tier architecture, and database-backed student event registration with offline payment verification.

---

## 👥 Student Team Project Roles

For college presentations and project evaluations, the modules are divided among the team:
* **Pillar 1 Lead (Database & JDBC):** MySQL Schema (`database/schema.sql`), JDBC connection management (`DBConnection.java`), account storage, PreparedStatement CRUD, and persistence verification (`VerifyPersistence.java`).
* **Pillar 2 Lead (Registration & Payments):** M:N association modeling (`Registration.java`), offline venue payment workflow, payment state machine, and duplicate registration prevention (`DuplicateRegistrationException`).
* **Pillar 3 Lead (Student Interface):** Student authentication & sign-up (`LoginFrame.java`), student portal dashboard (`StudentDashboardFrame.java`), event catalog search (`StudentBrowseEventsPanel.java`), schedule details, and service layer integration.
* **Pillar 4 Lead (Administrator Dashboard):** Separate admin portal (`AdminDashboardFrame.java`), Free vs Paid event creation (`EventPanel.java`), cascading event deletion, and the offline payment approvals queue (`ParticipantRegistrationPanel.java`).

---

## 🚀 Quick Start (Running in 10 Seconds)

### Option 1: Double-Click or Command Line (No Maven required)
1. Open PowerShell in this project folder:
   ```powershell
   .\run.ps1
   ```
   *Or in Windows Command Prompt (CMD):*
   ```cmd
   run.bat
   ```
2. The application compiles all source files and automatically launches the Java Swing GUI window on your desktop!

### Option 2: Using VS Code
1. Open Visual Studio Code:
   ```cmd
   code .
   ```
2. Open `src/main/java/com/eventmgmt/Main.java`.
3. Click the **Run** button (play icon) at the top-right of the editor, or press `F5`.

### Option 3: Using Maven (if Maven is installed)
```bash
mvn clean compile
mvn exec:java -Dexec.mainClass="com.eventmgmt.Main"
```

---

## 🛠️ Technology Stack & Environment Prerequisites

| Technology | Requirement / Version | Purpose |
| :--- | :--- | :--- |
| **Language** | Java 17 or Java 21 (LTS) | Core application code & OOP features |
| **GUI** | Java Swing (`JFrame`, `JTable`, `JTabbedPane`) | Native desktop graphical interface |
| **Database** | MySQL 8.x (or MariaDB / XAMPP) | Persistent relational database storage |
| **Connectivity** | JDBC (`mysql-connector-j-8.3.0.jar`) | Direct communication between Java and MySQL |
| **Build Tool** | Maven / Standard Java Compiler (`javac`) | Project structure and dependency resolution |
| **IDE** | Visual Studio Code | Recommended code editor |

### Installed Environment Status on this Machine:
* **Java:** OpenJDK 21.0.12 LTS (`C:\Program Files\Microsoft\jdk-21.0.12.101-hotspot\bin`) — **Installed & Verified**.
* **VS Code:** Version 1.142.0 with Java Extension Pack — **Installed & Verified**.
* **JDBC Driver:** Bundled standalone in `lib/mysql-connector-j-8.3.0.jar` — **Ready to use**.
* **Database Driver / Engine:** Dual-mode architecture. Operates with full persistent MySQL JDBC storage, or seamless In-Memory fallback if MySQL service is not currently running.

---

## 🗄️ Setting Up MySQL Database

### Step 1: Install MySQL (or XAMPP)
If MySQL is not yet installed on your system:
* Download **MySQL Community Server** from [https://dev.mysql.com/downloads/mysql/](https://dev.mysql.com/downloads/mysql/) (or install **XAMPP** from [https://www.apachefriends.org](https://www.apachefriends.org)).
* During installation, note down your root password (commonly `root` or left blank in XAMPP).

### Step 2: Create the Database & Schema
Open **MySQL Workbench**, **phpMyAdmin**, or the **MySQL Command Line Client**, and execute:
```sql
SOURCE database/schema.sql;
SOURCE database/sample_data.sql;
```
*(Or simply open `database/schema.sql` in MySQL Workbench and click the Execute lightning bolt icon).*

### Step 3: Configure `db.properties`
Open `src/main/resources/db.properties` (or create a local `db.properties` file in the project root directory):
```properties
db.enabled=true
db.url=jdbc:mysql://localhost:3306/event_management_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
db.username=root
db.password=your_mysql_password_here
db.driver=com.mysql.cj.jdbc.Driver
```

> [!IMPORTANT]
> **Safe Credential Handling**: Never commit passwords to Git! Notice that `.gitignore` contains `db.properties`. A template named `src/main/resources/db.properties.example` is committed so collaborators know the configuration format without exposing private passwords.

---

## 🏛️ Project Directory Structure

```
event-management-system/
├── .gitignore                          # Excludes compiled class files, target/, and db.properties
├── pom.xml                             # Maven build and dependency management file
├── build.ps1 / build.bat               # Compiles Java classes to bin/ directory
├── run.ps1 / run.bat                   # Launches the desktop Swing application
├── database/
│   ├── schema.sql                      # Relational schema (tables, primary/foreign keys)
│   └── sample_data.sql                 # Initial seed data for viva testing
├── lib/
│   └── mysql-connector-j-8.3.0.jar     # Standalone MySQL JDBC driver jar
└── src/
    └── main/
        ├── resources/
        │   ├── db.properties           # Local active MySQL credentials
        │   └── db.properties.example   # Version-controlled configuration template
        └── java/
            └── com/eventmgmt/
                ├── Main.java           # Entry point; launches GUI on Swing EDT
                ├── model/              # Domain entities
                │   ├── User.java       # [Abstract Superclass] id, name, email, phone
                │   ├── Participant.java# [Subclass] Attends events
                │   ├── Organizer.java  # [Subclass] Creates events
                │   ├── Event.java      # Manages sessions via Composition
                │   ├── Session.java    # Schedulable session belonging to an event
                │   ├── Registration.java # Association between Participant & Event
                │   └── interfaces/
                │       ├── Displayable.java # Abstraction interface for summary display
                │       └── Schedulable.java # Abstraction interface for schedule timing
                ├── dao/                # Data Access Object pattern
                │   ├── EventDAO.java
                │   ├── ParticipantDAO.java
                │   ├── SessionDAO.java
                │   ├── RegistrationDAO.java
                │   ├── DAOFactory.java # Singleton factory; toggles JDBC vs In-Memory
                │   ├── jdbc/           # MySQL JDBC implementations (PreparedStatement)
                │   │   ├── JdbcEventDAO.java
                │   │   ├── JdbcParticipantDAO.java
                │   │   ├── JdbcSessionDAO.java
                │   │   └── JdbcRegistrationDAO.java
                │   └── inmemory/       # In-Memory collection implementations
                │       ├── InMemoryEventDAO.java
                │       ├── InMemoryParticipantDAO.java
                │       ├── InMemorySessionDAO.java
                │       └── InMemoryRegistrationDAO.java
                ├── service/            # Business validation & rules
                │   ├── EventService.java
                │   ├── ParticipantService.java
                │   ├── SessionService.java
                │   └── RegistrationService.java
                ├── exception/          # Custom exceptions
                │   ├── EventManagementException.java
                │   ├── DuplicateRegistrationException.java
                │   ├── InvalidSessionTimeException.java
                │   ├── ValidationException.java
                │   └── DatabaseException.java
                ├── util/               # Helper utilities
                │   ├── DBConnection.java # Connection manager & test diagnostics
                │   └── DateTimeUtil.java # Date & Time parser/formatter
                └── ui/                 # Java Swing Graphical User Interface
                    ├── MainFrame.java  # Main application window (JTabbedPane)
                    ├── EventPanel.java # Feature 1: Event Management
                    ├── ParticipantRegistrationPanel.java # Feature 2: Participants & Registration
                    ├── SessionSchedulePanel.java         # Feature 3: Sessions & Schedules

```

---

## 🎯 Implementation of Required Features

### Feature 1: Event Management (`EventPanel.java`, `EventService.java`)
* **Create an Event:** Enter Event ID, Event Name, Description, Date (YYYY-MM-DD), Venue, and Organizer Name.
* **View Events List:** Formatted `JTable` displaying all active events with dates, venues, and session counts.
* **View Event Details:** Click "View Full Details" to inspect formatted details, description, and attached sessions.
* **Edit Event:** Click any event row in the table; the fields populate automatically. Edit the fields and click "Update Event".
* **Delete Event with Confirmation:** Select an event and click "Delete Event". Triggers a `JOptionPane.showConfirmDialog` asking for confirmation and explaining that child sessions and registrations will be removed.
* **Input Validation:** Required field checks, date format verification (`YYYY-MM-DD`), and user-friendly error dialogs.

### Feature 2: Participant Management & Registration (`ParticipantRegistrationPanel.java`)
* **Manage Participants:**
  * Add participant with Participant ID, Full Name, Email, and Phone number.
  * Formatted table of all participants.
  * Update participant info with email format validation (`@` and `.`).
  * Delete participant after confirmation dialog.
  * "View Enrolled Events" button: Shows all events a selected participant has registered for.
* **Event Registration (Association):**
  * Select an Event from dropdown.
  * Select a Participant from dropdown.
  * Click "Register Now" to link them together.
  * **Duplicate Prevention:** If the participant is already registered for that event, the application rejects the attempt and shows a clear warning alert via `DuplicateRegistrationException`.
  * Filter registrations table by event or view all registrations.
  * Cancel / remove individual registrations.

### Feature 3: Session & Schedule Management (`SessionSchedulePanel.java`)
* **Associate Sessions with Events:** Select an Event from the top dropdown; the table dynamically loads only the sessions belonging to that event (**Composition**).
* **Create Session:** Input Session ID, Title, Date, Start Time (`HH:mm`), End Time (`HH:mm`), and Room/Venue.
* **Strict Timing Validation:**
  * Ensures Start Time and End Time are valid 24-hr formats (`HH:mm`).
  * **Enforces End Time > Start Time:** If an end time is earlier than or equal to start time (e.g. Start 11:00, End 10:30), the system rejects it and displays an `InvalidSessionTimeException` alert.
* **Edit & Delete Sessions:** Update session room/timings or remove a session.
* **Chronological Schedule View:** Click "View Full Day Schedule" to see a formatted, sorted timeline of the entire day's events and session durations.

---

## 💡 Comprehensive OOP Concepts Guide (Viva Voce Cheat Sheet)

This project was intentionally designed so you can open any class during your examination and explain OOP with real, meaningful code:

| # | OOP Concept | Where It Is Used in Code | Viva Voce Explanation |
| :---: | :--- | :--- | :--- |
| **1** | **Classes & Objects** | `Event.java`, `Participant.java`, `Session.java`, `Registration.java` | A *class* is a blueprint; an *object* is an instance created with `new`. When adding an event, `Event e = new Event(...)` allocates memory with its own state. |
| **2** | **Encapsulation** | All model classes (private fields + getters/setters) | Fields (`eventId`, `startTime`, `email`) are `private`. In `Session.java`, `setScheduleTimes()` validates that `end.isAfter(start)` before setting values. |
| **3** | **Inheritance** | `User.java` (Superclass) $\to$ `Participant.java`, `Organizer.java` (Subclasses) | `Participant` extends `User` using `super(id, name, email, phone)`. Avoids code duplication for common user attributes. |
| **4** | **Polymorphism** | `User u = new Participant(...)`; `EventDAO dao = new JdbcEventDAO()` | Dynamic Method Dispatch: Calling `u.getRoleDescription()` executes `Participant`'s implementation at runtime. |
| **5** | **Abstraction** | `abstract class User`, `interface Displayable`, `interface Schedulable` | Defines *what* must be done without exposing *how*. Interfaces enforce contracts for display and duration calculation. |
| **6** | **Constructors & Overloading** | `Event.java`, `Participant.java`, `Registration.java` | `Event()` (default), `Event(name, desc, date, venue, org)` (auto ID), `Event(id, name, desc, date, venue, org)` (explicit ID). |
| **7** | **Method Overloading** | `EventService.searchEvents(keyword)` vs `searchEvents(keyword, date)` | Same method name in the same class with different parameter types/counts (Compile-Time Polymorphism). |
| **8** | **Method Overriding** | `@Override` in `Participant.java` (`getRoleDescription()`, `displayDetails()`, `toString()`) | Subclass provides its own customized behavior for a method inherited from superclass (Runtime Polymorphism). |
| **9** | **Composition** | `Event.java` contains `List<Session> sessions` | Strong HAS-A relationship. A session has no independent meaning outside an event; deleting an event deletes its sessions (`ON DELETE CASCADE`). |
| **10** | **Association** | `Registration.java` links `Participant` with `Event` | Represents a Many-to-Many association. A participant registers for multiple events, and an event has multiple participants. |
| **11** | **Access Modifiers** | `private` in models, `protected String id` in `User`, `public` in APIs | `private` restricts access to inside class, `protected` allows subclass access, `public` exposes interface to other packages. |
| **12** | **Exception Handling** | Custom exceptions: `DuplicateRegistrationException`, `InvalidSessionTimeException` | Checked and unchecked exceptions caught in UI using `try-catch` blocks to display understandable error dialogs. |
| **13** | **Packages** | `com.eventmgmt.model`, `dao`, `service`, `ui`, `exception`, `util` | Modular organization preventing namespace collisions and enforcing clean layer separation. |
| **14** | **Collections** | `ArrayList<Session>`, `List<Event>`, `ConcurrentHashMap` | Storing and manipulating groups of objects dynamically. Using `Collections.sort()` with `Comparable<Session>` for chronological sorting. |

---

## 🗄️ Relational Database & JDBC Concepts for Viva

### 1. Relational Database Concepts
* **Primary Key (PK):** `event_id` in `events`, `participant_id` in `participants`. Uniquely identifies every row in a table.
* **Foreign Key (FK):** `event_id` in `sessions` references `events(event_id)`. Enforces referential integrity.
* **One-to-Many Relationship:** One Event has Many Sessions (`events` $1 \to N$ `sessions`).
* **Many-to-Many Relationship:** Participants attend Events. Mediated by `registrations` table (`participants` $M \leftrightarrow N$ `events`).
* **Cascading Deletes:** Defined with `ON DELETE CASCADE`. When an event is deleted, MySQL automatically deletes all its sessions and registrations.

### 2. What is SQL Injection and Why Use `PreparedStatement`?
* **SQL Injection:** Occurs when untrusted user input is directly concatenated into a SQL string:
  ```java
  // DANGEROUS CODE (Vulnerable to SQL Injection):
  String sql = "SELECT * FROM events WHERE event_name = '" + userInput + "'";
  // If userInput is: ' OR '1'='1
  // The query becomes: SELECT * FROM events WHERE event_name = '' OR '1'='1'
  // Returning ALL rows and bypassing security!
  ```
* **Why PreparedStatement is Superior:**
  ```java
  // SECURE CODE (Used throughout this project):
  String sql = "SELECT * FROM events WHERE event_name = ?";
  PreparedStatement ps = conn.prepareStatement(sql);
  ps.setString(1, userInput);
  ```
  The database engine compiles the query structure *first*. The parameter `?` is treated strictly as literal data, never as executable SQL commands, completely preventing injection.

### 3. What is `try-with-resources`?
Introduced in Java 7, `try-with-resources` automatically closes any resource that implements `java.lang.AutoCloseable` (such as `Connection`, `PreparedStatement`, `ResultSet`):
```java
try (Connection conn = DBConnection.getConnection();
     PreparedStatement ps = conn.prepareStatement(sql);
     ResultSet rs = ps.executeQuery()) {
    // Process results
} catch (SQLException e) {
    // Handle error
} // conn, ps, and rs are GUARANTEED to be closed here even if an exception is thrown!
```
This prevents **database connection leaks** and **memory leaks**.

---

## 🖥️ Graphical User Interface (Swing) Walkthrough

* **`LoginFrame.java`:** Entry authentication window hosting separate tabs for Student Login, Student Sign-Up, and Admin Login.
* **`StudentDashboardFrame.java`:** Dedicated student portal with tabbed views for:
  1. *Browse Available Events & Register* (`StudentBrowseEventsPanel.java`): Catalog search, session schedule inspection, and one-click enrollment / offline payment claims.
  2. *My Registered Events* (`StudentMyRegistrationsPanel.java`): Personal enrollment tracking and payment verification status.
* **`AdminDashboardFrame.java`:** Dedicated administrator control center with tabs for:
  1. *Events Management* (`EventPanel.java`): Add (Free or Paid with custom fee), edit, and delete events.
  2. *Sessions & Schedules* (`SessionSchedulePanel.java`): Schedule session tracks and itineraries.
  3. *Registrations & Students* (`ParticipantRegistrationPanel.java`): Manage participants, view enrollments, and process the Offline Payment Approvals queue.
* **`JTable` & `DefaultTableModel`:** Tabular display for listing events, participants, sessions, and registrations with single-row selection.
* **`JOptionPane`:** Used for interactive user feedback (confirmation dialogs, validation notices, payment receipts).
* **Delegation Event Model:** Event sources (buttons, tables, menus) dispatch to listeners implemented via Anonymous Inner Classes (`new ActionListener() { ... }`).

---

