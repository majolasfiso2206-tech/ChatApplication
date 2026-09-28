package chatapp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for the Login class.
 */
public class LoginTest {

    private Login login;

    @Before
    public void setUp() {
        login = new Login("John", "Smith");

        login.registerUser(
                "kyl_1",
                "Ch&se@k99!",
                "+27838968976"
        );
    }

    @Test
    public void testUsernameCorrectlyFormatted() {
        assertTrue(login.checkUserName("kyl_1"));
    }

    @Test
    public void testUsernameIncorrectlyFormatted() {
        assertFalse(login.checkUserName("kyle!!!!!!!"));
    }

    @Test
    public void testPasswordMeetsComplexityRequirements() {
        assertTrue(login.checkPasswordComplexity("Ch&se@k99!"));
    }

    @Test
    public void testPasswordDoesNotMeetComplexityRequirements() {
        assertFalse(login.checkPasswordComplexity("password"));
    }

    @Test
    public void testCellPhoneCorrectlyFormatted() {
        assertTrue(login.checkCellPhoneNumber("+27838968976"));
    }

    @Test
    public void testCellPhoneIncorrectlyFormatted() {
        assertFalse(login.checkCellPhoneNumber("08966553"));
    }

    @Test
    public void testSuccessfulLogin() {
        assertTrue(login.loginUser("kyl_1", "Ch&se@k99!"));
    }

    @Test
    public void testFailedLogin() {
        assertFalse(login.loginUser("kyl_1", "wrongPassword"));
    }

    @Test
    public void testSuccessfulLoginMessage() {
        assertEquals(
                "Welcome John, Smith it is great to see you again.",
                login.returnLoginStatus("kyl_1", "Ch&se@k99!")
        );
    }

    @Test
    public void testFailedLoginMessage() {
        assertEquals(
                "Username or password incorrect, please try again.",
                login.returnLoginStatus("kyl_1", "wrongPassword")
        );
    }

    @Test
    public void testRegistrationSuccessMessages() {
        Login newLogin = new Login("John", "Smith");

        assertEquals(
                "Username successfully captured.\n"
                + "Password successfully captured.\n"
                + "Cell number successfully captured.",
                newLogin.registerUser(
                        "kyl_1",
                        "Ch&se@k99!",
                        "+27838968976"
                )
        );
    }

    @Test
    public void testInvalidUsernameRegistrationMessage() {
        Login newLogin = new Login("John", "Smith");

        assertEquals(
                "Username is not correctly formatted; please ensure that your username contains an underscore and is no more than five characters in length.",
                newLogin.registerUser(
                        "kyle!!!!!!!",
                        "Ch&se@k99!",
                        "+27838968976"
                )
        );
    }

    @Test
    public void testInvalidPasswordRegistrationMessage() {
        Login newLogin = new Login("John", "Smith");

        assertEquals(
                "Password is not correctly formatted; please ensure that the password contains at least eight characters, a capital letter, a number, and a special character.",
                newLogin.registerUser(
                        "kyl_1",
                        "password",
                        "+27838968976"
                )
        );
    }

    @Test
    public void testInvalidCellPhoneRegistrationMessage() {
        Login newLogin = new Login("John", "Smith");

        assertEquals(
                "Cell number is incorrectly formatted or does not contain an international code; please correct the number and try again.",
                newLogin.registerUser(
                        "kyl_1",
                        "Ch&se@k99!",
                        "08966553"
                )
        );
    }
}