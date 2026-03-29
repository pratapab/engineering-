package tests;

import base.BaseTest;
import pages.LoginPage;
import org.testng.annotations.Test;
import org.testng.annotations.DataProvider;
import org.testng.Assert;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.support.ui.WebDriverWait;


public class LoginTestValid extends BaseTest {
    private LoginPage loginPage;

    @Test(priority = 1, description = "Verify login page UI elements are displayed")
    public void testLoginPageUIElements() throws Exception {
        loginPage = new LoginPage(driver);
        
        try {
            Assert.assertTrue(loginPage.isEmailFieldPresent(), "Email field should be present");
            Assert.assertTrue(loginPage.isPasswordFieldPresent(), "Password field should be present");
            Assert.assertTrue(loginPage.isLoginButtonPresent(), "Login button should be present");
            Assert.assertTrue(loginPage.isForgotPasswordLinkPresent(), "Forgot password link should be present");
        } catch (AssertionError e) {
            throw new Exception("UI Elements verification failed: " + e.getMessage(), e);
        }
    }

    @Test(priority = 2, description = "Verify valid login credentials are accepted", dataProvider = "validCredentials")
    public void testValidLogin(String email, String password) throws Exception {
        loginPage = new LoginPage(driver);
        
        try {
            loginPage.performLogin(email, password);
            String currentUrl = driver.getCurrentUrl();
            Assert.assertFalse(currentUrl.contains("login.salesforce.com"), 
                "User should not remain on login page after successful login");
        } catch (AssertionError e) {
            throw new Exception("Valid login test failed: " + e.getMessage(), e);
        }
    }

    @Test(priority = 3, description = "Verify login with remember me functionality", dataProvider = "validCredentials")
    public void testLoginWithRememberMe(String email, String password) throws Exception {
        loginPage = new LoginPage(driver);
        
        try {
            loginPage.performLoginWithRememberMe(email, password);
            String currentUrl = driver.getCurrentUrl();
            Assert.assertFalse(currentUrl.contains("login.salesforce.com"), 
                "User should not remain on login page after successful login with remember me");
        } catch (AssertionError e) {
            throw new Exception("Login with remember me test failed: " + e.getMessage(), e);
        }
    }

    @Test(priority = 4, description = "Verify email field accepts valid email format", dataProvider = "validEmails")
    public void testEmailFieldValidation(String email) throws Exception {
        loginPage = new LoginPage(driver);
        
        try {
            loginPage.enterEmail(email);
            String enteredValue = ((JavascriptExecutor) driver).executeScript("return arguments[0].value;", 
                driver.findElement(org.openqa.selenium.By.xpath("//input[@id='username']"))).toString();
            Assert.assertEquals(enteredValue, email, "Email field should accept the entered email");
        } catch (AssertionError e) {
            throw new Exception("Email validation test failed: " + e.getMessage(), e);
        }
    }

    @Test(priority = 5, description = "Verify password field accepts input")
    public void testPasswordFieldAcceptsInput() throws Exception {
        loginPage = new LoginPage(driver);
        String testPassword = "TestPassword123";
        
        try {
            loginPage.enterPassword(testPassword);
            String enteredValue = ((JavascriptExecutor) driver).executeScript("return arguments[0].value;", 
                driver.findElement(org.openqa.selenium.By.xpath("//input[@id='password']"))).toString();
            Assert.assertEquals(enteredValue, testPassword, "Password field should accept the entered password");
        } catch (AssertionError e) {
            throw new Exception("Password field test failed: " + e.getMessage(), e);
        }
    }

    @Test(priority = 6, description = "Verify login button is clickable")
    public void testLoginButtonClickability() throws Exception {
        loginPage = new LoginPage(driver);
        
        try {
            org.openqa.selenium.WebElement loginBtn = driver.findElement(
                org.openqa.selenium.By.xpath("//input[@id='Login']"));
            Assert.assertTrue(loginBtn.isEnabled(), "Login button should be enabled and clickable");
        } catch (AssertionError e) {
            throw new Exception("Login button clickability test failed: " + e.getMessage(), e);
        }
    }

    @DataProvider(name = "validCredentials")
    public Object[][] getValidCredentials() {
        return new Object[][]{
            {"user@example.com", "ValidPassword123"}
        };
    }

    @DataProvider(name = "validEmails")
    public Object[][] getValidEmails() {
        return new Object[][]{
            {"test@salesforce.com"},
            {"user.name@salesforce.com"},
            {"user+tag@salesforce.com"}
        };
    }
}
