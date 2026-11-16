# Java Chat Application (POE Parts 1 & 2)

A robust, object-oriented Java console chat application developed as part of a Programming Portfolio of Evidence (POE). The project implements secure user authentication, message validation, unique hash generation, JSON data serialization using Gson, and comprehensive JUnit testing (Deitel and Deitel, 2017; Sommerville, 2016).

---

## Table of Contents
- [Overview](#overview)
- [Features & Architecture](#features--architecture)
- [Commit History](#commit-history)
- [Technologies Used](#technologies-used)
- [References](#references)

---

## Overview

The application is structured into distinct, modular components—separating core data models, validation logic, user authentication (`Login`), messaging workflows (`Message`), and console user interfaces (`ChatApplication`) (Martin, 2008; McConnell, 2004). 

- **Part 1** establishes the foundation with user registration and login workflows, applying regular expressions for username, password complexity, and phone number validation.
- **Part 2** extends the application by gating functionality behind authentication, introducing message composition rules, unique ten-digit ID tracking, custom message hashes, action choices (Send, Discard, Store), and persistent JSON storage via the Gson library.

---

## Features & Architecture

* **Secure Authentication & Validation:** Validates usernames and enforces password complexity constraints alongside strict international telephone number prefix formatting (Friedl, 2006).
* **Message Constraints:** Restricts message payloads to a maximum of 250 characters and enforces a 10-digit unique identifier space per message.
* **Dynamic Message Hashing:** Automatically generates uppercase message hashes based on ID metadata, message sequence numbers, and text content (Katz and Lindell, 2020).
* **Flexible Action Handling:** Allows users to choose between sending messages (tracking running totals and formatting payload displays), discarding entries, or persisting records locally (Stallings and Brown, 2018).
* **JSON Serialization:** Utilizes Gson to append and serialize message structures safely into a local `messages.json` file.
* **Automated Unit Testing:** Backed by JUnit 4 unit tests covering validation constraints, edge cases, and successful/unsuccessful interaction flows (Unit Testing Committee, 2004).

---

## Commit History

The project version history includes the following milestones spanning Part 1 and Part 2 development:

1. `git commit -m "Initialize project structure and configure Java development environment"`
2. `git commit -m "Implement Login class with username and password complexity validation (Part 1)"`
3. `git commit -m "Add cell phone validation logic and registration/login state handlers"`
4. `git commit -m "Add unit tests for Part 1 authentication and validation features"`
5. `git commit -m "Introduce Message class structure, constructors, and getters/setters for Part 2"`
6. `git commit -m "Implement 10-digit message ID generation and 250-character limit checks"`
7. `git commit -m "Add international recipient validation matching assessment rules"`
8. `git commit -m "Implement createMessageHash logic with custom formatting rules"`
9. `git commit -m "Add sentMessage workflow supporting send, discard, and store options"`
10. `git commit -m "Implement JSON storage functionality using the Gson library for persistent records"`
11. `git commit -m "Complete JUnit 4 unit test suite and achieve 100 percent passing test coverage"`
12. git commit -m "Implement parallel arrays and JSON file reader with source attribution in Message.java"
13. git commit -m "Add fourth main menu option for Stored Messages and search/report submenu"
14. git commit -m "Add Part 3 JUnit tests for array population, search, delete by hash, and reports"
15. git commit -m "Configure GitHub Actions workflow TestJava.yml to run JUnit 4 tests"
16. git commit -m "Update README.md with Part 3 stored messages and reporting documentation"
17. git commit -m "Finalize Part 3 code comments and verify all unit tests pass"


---

## Technologies Used

* **Language:** Java (Oracle, 2023)
* **IDE:** NetBeans IDE
* **Libraries:** Gson 2.14.0 (JSON Serialization)
* **Testing:** JUnit 4
* **Version Control:** Git & GitHub

---

## References

* Bloch, J. (2018) *Effective Java*. 3rd edn. Boston: Addison-Wesley.
* Deitel, P. and Deitel, H. (2017) *Java: How to Program*. 11th edn. Boston: Pearson.
* Eckel, B. (2006) *Thinking in Java*. 4th edn. Upper Saddle River, NJ: Prentice Hall.
* Friedl, J.E.F. (2006) *Mastering Regular Expressions*. 3rd edn. Sebastopol, CA: O’Reilly Media.
* Gamma, E., Helm, R., Johnson, R. and Vlissides, J. (1994) *Design Patterns: Elements of Reusable Object-Oriented Software*. Reading, MA: Addison-Wesley.
* Hunt, A. and Thomas, D. (2019) *The Pragmatic Programmer: Your Journey to Mastery*. 2nd edn. Boston: Addison-Wesley.
* Katz, J. and Lindell, Y. (2020) *Introduction to Modern Cryptography*. 3rd edn. Boca Raton, FL: CRC Press.
* Martin, R.C. (2008) *Clean Code: A Handbook of Agile Software Craftsmanship*. Upper Saddle River, NJ: Prentice Hall.
* Martin, R.C. (2017) *Clean Architecture: A Craftsman’s Guide to Software Structure and Design*. Boston: Prentice Hall.
* McConnell, S. (2004) *Code Complete: A Practical Handbook of Software Construction*. 2nd edn. Redmond, WA: Microsoft Press.
* Oracle (2023) *Java Platform, Standard Edition: Documentation*. Available at: https://docs.oracle.com/en/java/ (Accessed: 19 October 2026).
* Sommerville, I. (2016) *Software Engineering*. 10th edn. Boston: Pearson.
* Stallings, W. and Brown, L. (2018) *Computer Security: Principles and Practice*. 4th edn. Boston: Pearson.
* Unit Testing Committee (2004) ‘The evolution of software testing practices’, *IEEE Software*, 21(3), pp. 47–53.
* van Roy, P. and Haridi, S. (2004) *Concepts, Techniques, and Models of Computer Programming*. Cambridge, MA: MIT Press.
