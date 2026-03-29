package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.time.Duration;

public class LoginPage {
    private WebDriver driver;
    private WebDriverWait wait;

    @FindBy(xpath = "//input[@id='username']")
    private WebElement emailField;

    @FindBy(xpath = "//input[@id='password']")
    private WebElement passwordField;

    @FindBy(xpath = "//input[@id='Login']")
    private WebElement loginButton;

    @FindBy(xpath = "//input[@id='rememberUn']")
    private WebElement rememberMeCheckbox;

    @FindBy(xpath = "//a[contains(@href, 'forgot')]")
    private WebElement forgotPasswordLink;

    @FindBy(xpath = "//div[contains(@class, 'errorMessage')]")
    private WebElement errorMessage;

    @FindBy(xpath = "//span[@id='error']")
    private WebElement errorMessageSpan;

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        PageFactory.initElements(driver, this);
    }

    public void enterEmail(String email) throws Exception {
        try {
            wait.until(ExpectedConditions.visibilityOf(emailField));
            emailField.clear();
            emailField.sendKeys(email);
        } catch (Exception e) {
            throw new Exception("Failed to enter email: " + e.getMessage(), e);
        }
    }

    public void enterPassword(String password) throws Exception {
        try {
            wait.until(ExpectedConditions.visibilityOf(passwordField));
            passwordField.clear();
            passwordField.sendKeys(password);
        } catch (Exception e) {
            throw new Exception("Failed to enter password: " + e.getMessage(), e);
        }
    }

    public void clickLoginButton() throws Exception {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(loginButton));
            loginButton.click();
        } catch (Exception e) {
            throw new Exception("Failed to click login button: " + e.getMessage(), e);
        }
    }

    public void checkRememberMe() throws Exception {
        try {
            wait.until(ExpectedConditions.visibilityOf(rememberMeCheckbox));
            if (!rememberMeCheckbox.isSelected()) {
                rememberMeCheckbox.click();
            }
        } catch (Exception e) {
            throw new Exception("Failed to check remember me: " + e.getMessage(), e);
        }
    }

    public void performLogin(String email, String password) throws Exception {
        try {
            enterEmail(email);
            enterPassword(password);
            clickLoginButton();
        } catch (Exception e) {
            throw new Exception("Failed to perform login: " + e.getMessage(), e);
        }
    }

    public void performLoginWithRememberMe(String email, String password) throws Exception {
        try {
            enterEmail(email);
            enterPassword(password);
            checkRememberMe();
            clickLoginButton();
        } catch (Exception e) {
            throw new Exception("Failed to perform login with remember me: " + e.getMessage(), e);
        }
    }

    public boolean isErrorMessageDisplayed() throws Exception {
        try {
            wait.until(ExpectedConditions.visibilityOf(errorMessage));
            return errorMessage.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isErrorMessageSpanDisplayed() throws Exception {
        try {
            wait.until(ExpectedConditions.visibilityOf(errorMessageSpan));
            return errorMessageSpan.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public String getErrorMessageText() throws Exception {
        try {
            wait.until(ExpectedConditions.visibilityOf(errorMessage));
            return errorMessage.getText();
        } catch (Exception e) {
            throw new Exception("Failed to get error message: " + e.getMessage(), e);
        }
    }

    public boolean isEmailFieldPresent() throws Exception {
        try {
            wait.until(ExpectedConditions.visibilityOf(emailField));
            return emailField.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isPasswordFieldPresent() throws Exception {
        try {
            wait.until(ExpectedConditions.visibilityOf(passwordField));
            return passwordField.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isLoginButtonPresent() throws Exception {
        try {
            wait.until(ExpectedConditions.visibilityOf(loginButton));
            return loginButton.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isForgotPasswordLinkPresent() throws Exception {
        try {
            wait.until(ExpectedConditions.visibilityOf(forgotPasswordLink));
            return forgotPasswordLink.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}
