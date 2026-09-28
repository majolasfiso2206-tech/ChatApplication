/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package chatapp;

/**
 *
 * @author 27723
 */
import java.util.Scanner;

/**
 * Runs the registration and login demonstration.
 */
public class ChatApplication {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        System.out.println("=== Java Chat Application ===");

        System.out.print("Enter your first name: ");
        String firstName = scanner.nextLine();

        System.out.print("Enter your last name: ");
        String lastName = scanner.nextLine();

        Login login = new Login(firstName, lastName);

        System.out.println("\n=== Registration ===");

        System.out.print("Enter a username: ");
        String username = scanner.nextLine();

        System.out.print("Enter a password: ");
        String password = scanner.nextLine();

        System.out.print("Enter a South African cell-phone number: ");
        String cellPhoneNumber = scanner.nextLine();

        String registrationMessage = login.registerUser(
                username,
                password,
                cellPhoneNumber
        );

        System.out.println("\n" + registrationMessage);

        if (login.getUsername() != null) {
            System.out.println("\n=== Login ===");

            System.out.print("Enter your username: ");
            String loginUsername = scanner.nextLine();

            System.out.print("Enter your password: ");
            String loginPassword = scanner.nextLine();

            System.out.println(
                    login.returnLoginStatus(loginUsername, loginPassword)
            );
        } else {
            System.out.println(
                    "\nRegistration was unsuccessful. "
                    + "Please correct the supplied details."
            );
        }

        scanner.close();
    }
}
