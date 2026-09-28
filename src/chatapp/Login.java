package chatapp;

/**
 * Stores registration details and provides validation and login methods.
 */
public class Login {

    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String cellPhoneNumber;

    public Login(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Login() {
    }

    public boolean checkUserName(String username) {
        if (username == null) {
            return false;
        }
        return username.contains("_") && username.length() <= 5;
    }

    public boolean checkPasswordComplexity(String password) {
        if (password == null) {
            return false;
        }
        boolean hasUppercase = !password.equals(password.toLowerCase());
        boolean hasDigit = password.matches(".*\\d.*");
        boolean hasSpecial = password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*");
        boolean hasMinLength = password.length() >= 8;

        return hasUppercase && hasDigit && hasSpecial && hasMinLength;
    }

    /**
     * Reference: Standard regex pattern matching international dialing codes 
     * followed by phone digits (e.g., +27838968976).
     */
    public boolean checkCellPhoneNumber(String cellPhoneNumber) {
        if (cellPhoneNumber == null) {
            return false;
        }
        return cellPhoneNumber.matches("^\\+\\d{1,3}\\d{9,10}$");
    }

    public String registerUser(String username, String password, String cellPhoneNumber) {
        if (!checkUserName(username)) {
            return "Username is not correctly formatted; please ensure that your username contains an underscore and is no more than five characters in length.";
        }

        if (!checkPasswordComplexity(password)) {
            return "Password is not correctly formatted; please ensure that the password contains at least eight characters, a capital letter, a number, and a special character.";
        }

        if (!checkCellPhoneNumber(cellPhoneNumber)) {
            return "Cell number is incorrectly formatted or does not contain an international code; please correct the number and try again.";
        }

        this.username = username;
        this.password = password;
        this.cellPhoneNumber = cellPhoneNumber;

        return "Username successfully captured.\n"
                + "Password successfully captured.\n"
                + "Cell number successfully captured.";
    }

    public boolean loginUser(String username, String password) {
        return this.username != null
                && this.password != null
                && this.username.equals(username)
                && this.password.equals(password);
    }

    public String returnLoginStatus(String username, String password) {
        if (loginUser(username, password)) {
            return "Welcome " + firstName + ", " + lastName + " it is great to see you again.";
        }
        return "Username or password incorrect, please try again.";
    }

    public String getUsername() {
        return username;
    }

    public String getCellPhoneNumber() {
        return cellPhoneNumber;
    }
}