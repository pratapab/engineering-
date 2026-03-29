package base;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.AfterTest;
import config.ConfigManager;
import config.TestConfig;
import config.BrowserSettings;
import java.util.concurrent.TimeUnit;

public class BaseTest {
    protected WebDriver driver;
    protected TestConfig config;

    @BeforeSuite
    public void initializeSuite() throws Exception {
        System.out.println("=== Test Suite Started ===");
        config = ConfigManager.getInstance();
        System.out.println("Configuration Loaded: " + config);
    }

    @BeforeTest
    public void setUp() throws Exception {
        driver = initializeDriver();
        driver.manage().timeouts().implicitlyWait(config.getTimeouts().getDefaultTimeout(), TimeUnit.SECONDS);
        driver.manage().timeouts().pageLoadTimeout(config.getTimeouts().getNavigationTimeout(), TimeUnit.SECONDS);
        driver.manage().window().maximize();
        navigateToApplication();
    }

    @AfterTest
    public void tearDown() {
        if (driver != null) {
            try {
                driver.quit();
            } catch (Exception e) {
                System.err.println("Error closing driver: " + e.getMessage());
            }
        }
    }

    @AfterSuite
    public void terminateSuite() {
        System.out.println("=== Test Suite Completed ===");
    }

    protected WebDriver initializeDriver() throws Exception {
        try {
            BrowserSettings browserSettings = config.getBrowserSettings();
            String browserType = browserSettings.getBrowserType();
            
            if (browserType.equalsIgnoreCase("firefox")) {
                WebDriverManager.firefoxdriver().setup();
                FirefoxOptions firefoxOptions = new FirefoxOptions();
                if (browserSettings.isHeadlessMode()) {
                    firefoxOptions.addArguments("--headless");
                }
                firefoxOptions.addArguments("--width=" + browserSettings.getViewportWidth(),
                                          "--height=" + browserSettings.getViewportHeight());
                return new FirefoxDriver(firefoxOptions);
            } else {
                WebDriverManager.chromedriver().setup();
                ChromeOptions chromeOptions = new ChromeOptions();
                chromeOptions.setAcceptInsecureCerts(true);
                
                if (browserSettings.isHeadlessMode()) {
                    chromeOptions.addArguments("--headless=new");
                }
                chromeOptions.addArguments("--window-size=" + browserSettings.getViewportWidth() + 
                                        "," + browserSettings.getViewportHeight());
                return new ChromeDriver(chromeOptions);
            }
        } catch (Exception e) {
            throw new Exception("Failed to initialize WebDriver: " + e.getMessage(), e);
        }
    }

    protected void navigateToApplication() throws Exception {
        try {
            driver.navigate().to(config.getBaseUrl());
        } catch (Exception e) {
            throw new Exception("Failed to navigate to application: " + e.getMessage(), e);
        }
    }

    protected TestConfig getConfig() {
        return config;
    }
}
