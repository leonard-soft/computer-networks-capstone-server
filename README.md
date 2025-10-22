# Computer Networks Capstone Server

## Description
This project implements the server-side logic for a computer networks capstone project. It handles user authentication, registration, game session management, and real-time communication using TCP and UDP protocols. It also incorporates encryption and hashing for secure data handling.

## Technologies Used

*   **Programming Language:** Java
*   **Build Automation & Dependency Management:** Apache Maven
*   **Data Persistence:** JPA (Java Persistence API), likely with Hibernate, for database interactions.
*   **Network Communication:** TCP and UDP sockets for client-server communication.
*   **Security:** Custom implementations for AES, RSA encryption, and password hashing.
*   **Data Structuring:** Data Transfer Objects (DTOs) for efficient data exchange.
*   **Email Services:** For sending notifications and verification emails.
*   **Logging:** Custom logging management for system monitoring and debugging.

## Prerequisites

Before running the server, ensure you have the following installed:

*   **Java Development Kit (JDK):** Version 17 or higher.
*   **Apache Maven:** Version 3.6.3 or higher.
*   **Database:** A compatible database configured for JPA (e.g., MySQL, PostgreSQL). Ensure your `persistence.xml` is correctly configured.

## How to Run

Follow these steps to get the server up and running:

1.  **Clone the repository:**
    ```bash
    git clone <repository_url>
    cd computer-networks-capstone-server
    ```

2.  **Build the project using Maven:**
    This command compiles the source code and packages it into a JAR file.
    ```bash
    mvn clean install
    ```

3.  **Run the server:**
    After a successful build, you can run the server using the generated JAR file. Replace `your-server-jar-name.jar` with the actual name of the JAR file found in the `target/` directory (e.g., `computer-networks-capstone-server-1.0-SNAPSHOT.jar`).
    ```bash
    java -jar target/your-server-jar-name.jar
    ```

    Alternatively, you can run the main class directly using Maven:
    ```bash
    mvn exec:java -Dexec.mainClass="org.example.Main"
    ```

    The server should now be running and listening for client connections. Check the console output for any errors or status messages.