package tests;

import base.BaseTest;
import pages.LoginPage;
import org.testng.annotations.Test;
import org.testng.annotations.DataProvider;
import org.testng.Assert;

public class LoginTestInvalid extends BaseTest {
    private LoginPage loginPage;

    @Test(priority = 1, description = "Verify login fails with invalid email and invalid password", dataProvider = "invalidCredentials")
    public void testLoginWithInvalidCredentials(String email, String password) throws Exception {
        loginPage = new LoginPage(driver);
        
        try {
            loginPage.performLogin(email, password);
            boolean errorDisplayed = loginPage.isErrorMessageDisplayed() || loginPage.isErrorMessageSpanDisplayed();
            Assert.assertTrue(errorDisplayed, "Error message should be displayed for invalid credentials");
        } catch (AssertionError e) {
            throw new Exception("Invalid credentials test failed: " + e.getMessage(), e);
        }
    }

    @Test(priority = 2, description = "Verify login fails with empty email and valid password")
    public void testLoginWithEmptyEmail() throws Exception {
        loginPage = new LoginPage(driver);
        
        try {
            loginPage.enterEmail("");
            loginPage.enterPassword("ValidPassword123");
            loginPage.clickLoginButton();
            boolean errorDisplayed = loginPage.isErrorMessageDisplayed() || loginPage.isErrorMessageSpanDisplayed();
            Assert.assertTrue(errorDisplayed, "Error message should be displayed for empty email");
        } catch (AssertionError e) {
            throw new Exception("Empty email test failed: " + e.getMessage(), e);
        }
    }

    @Test(priority = 3, description = "Verify login fails with valid email and empty password")
    public void testLoginWithEmptyPassword() throws Exception {
        loginPage = new LoginPage(driver);
        
        try {
            loginPage.enterEmail("test@salesforce.com");
            loginPage.enterPassword("");
            loginPage.clickLoginButton();
            boolean errorDisplayed = loginPage.isErrorMessageDisplayed() || loginPage.isErrorMessageSpanDisplayed();
            Assert.assertTrue(errorDisplayed, "Error message should be displayed for empty password");
        } catch (AssertionError e) {
            throw new Exception("Empty password test failed: " + e.getMessage(), e);
        }
    }

    @Test(priority = 4, description = "Verify login fails with both email and password empty")
    public void testLoginWithBothFieldsEmpty() throws Exception {
        loginPage = new LoginPage(driver);
        
        try {
            loginPage.enterEmail("");
            loginPage.enterPassword("");
            loginPage.clickLoginButton();
            boolean errorDisplayed = loginPage.isErrorMessageDisplayed() || loginPage.isErrorMessageSpanDisplayed();
            Assert.assertTrue(errorDisplayed, "Error message should be displayed when both fields are empty");
        } catch (AssertionError e) {
            throw new Exception("Both fields empty test failed: " + e.getMessage(), e);
        }
    }

    @Test(priority = 5, description = "Verify login fails with invalid email format", dataProvider = "invalidEmails")
    public void testLoginWithInvalidEmailFormat(String email) throws Exception {
        loginPage = new LoginPage(driver);
        
        try {
            loginPage.enterEmail(email);
            loginPage.enterPassword("SomePassword123");
            loginPage.clickLoginButton();
            boolean errorDisplayed = loginPage.isErrorMessageDisplayed() || loginPage.isErrorMessageSpanDisplayed();
            Assert.assertTrue(errorDisplayed, "Error message should be displayed for invalid email format: " + email);
        } catch (AssertionError e) {
            throw new Exception("Invalid email format test failed: " + e.getMessage(), e);
        }
    }

    @Test(priority = 6, description = "Verify login fails with valid email but wrong password", dataProvider = "wrongPasswords")
    public void testLoginWithWrongPassword(String email, String password) throws Exception {
        loginPage = new LoginPage(driver);
        
        try {
            loginPage.performLogin(email, password);
            boolean errorDisplayed = loginPage.isErrorMessageDisplayed() || loginPage.isErrorMessageSpanDisplayed();
            Assert.assertTrue(errorDisplayed, "Error message should be displayed for wrong password");
        } catch (AssertionError e) {
            throw new Exception("Wrong password test failed: " + e.getMessage(), e);
        }
    }

    @Test(priority = 7, description = "Verify login fails with special characters in email", dataProvider = "specialCharacterEmails")
    public void testLoginWithSpecialCharacterEmail(String email) throws Exception {
        loginPage = new LoginPage(driver);
        
        try {
            loginPage.enterEmail(email);
            loginPage.enterPassword("Password123");
            loginPage.clickLoginButton();
            boolean errorDisplayed = loginPage.isErrorMessageDisplayed() || loginPage.isErrorMessageSpanDisplayed();
            Assert.assertTrue(errorDisplayed, "Error message should be displayed for special character email: " + email);
        } catch (AssertionError e) {
            throw new Exception("Special character email test failed: " + e.getMessage(), e);
        }
    }

    @Test(priority = 8, description = "Verify login fails with spaces in credentials", dataProvider = "credentialsWithSpaces")
    public void testLoginWithSpaces(String email, String password) throws Exception {
        loginPage = new LoginPage(driver);
        
        try {
            loginPage.performLogin(email, password);
            Thread.sleep(2000);
            boolean errorDisplayed = loginPage.isErrorMessageDisplayed() || loginPage.isErrorMessageSpanDisplayed();
            Assert.assertTrue(errorDisplayed, "Error message should be displayed for credentials with spaces");
        } catch (AssertionError e) {
            throw new Exception("Credentials with spaces test failed: " + e.getMessage(), e);
        }
    }

    @Test(priority = 9, description = "Verify login maintains URL on failed attempt")
    public void testURLRemainsOnFailedLogin() throws Exception {
        loginPage = new LoginPage(driver);
        String initialUrl = driver.getCurrentUrl();
        
        try {
            loginPage.performLogin("invalid@example.com", "invalidpassword");
            String currentUrl = driver.getCurrentUrl();
            Assert.assertTrue(currentUrl.contains("login.salesforce.com"), 
                "User should remain on login page after failed login");
        } catch (AssertionError e) {
            throw new Exception("URL check test failed: " + e.getMessage(), e);
        }
    }

    @Test(priority = 10, description = "Verify multiple failed login attempts")
    public void testMultipleFailedLoginAttempts() throws Exception {
        loginPage = new LoginPage(driver);
        
        try {
            for (int i = 0; i < 3; i++) {
                loginPage.performLogin("invalid@test.com", "wrongpassword");
                boolean errorDisplayed = loginPage.isErrorMessageDisplayed() || loginPage.isErrorMessageSpanDisplayed();
                Assert.assertTrue(errorDisplayed, "Error message should be displayed on attempt " + (i + 1));
                
                if (i < 2) {
                    loginPage = new LoginPage(driver);
                }
            }
        } catch (AssertionError e) {
            throw new Exception("Multiple failed attempts test failed: " + e.getMessage(), e);
        }
    }

    @DataProvider(name = "invalidCredentials")
    public Object[][] getInvalidCredentials() {
        return new Object[][]{
            {"invalid@example.com", "InvalidPassword123"},
            {"wronguser@salesforce.com", "WrongPassword456"},
            {"notauser@test.com", "NoPassword789"}
        };
    }

    @DataProvider(name = "invalidEmails")
    public Object[][] getInvalidEmails() {
        return new Object[][]{
            {"invalidemail"},
            {"invalid@"},
            {"@invalid.com"},
            {"invalid.email@"},
            {"invalid email@test.com"}
        };
    }

    @DataProvider(name = "wrongPasswords")
    public Object[][] getWrongPasswords() {
        return new Object[][]{
            {"test@salesforce.com", "WrongPassword123"},
            {"user@example.com", "IncorrectPassword456"},
            {"admin@salesforce.com", "WrongPass789"}
        };
    }

    @DataProvider(name = "specialCharacterEmails")
    public Object[][] getSpecialCharacterEmails() {
        return new Object[][]{
            {"user@@@example.com"},
            {"user##name@test.com"},
            {"user$%@example.com"}
        };
    }

    @DataProvider(name = "credentialsWithSpaces")
    public Object[][] getCredentialsWithSpaces() {
        return new Object[][]{
            {" invalid@example.com ", "Password123"},
            {"invalid@example.com", " Password123 "},
            {" invalid@example.com ", " Password123 "}
        };
    }
}
