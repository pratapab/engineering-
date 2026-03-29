package config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigManager implements TestConfig {
    private static ConfigManager instance;
    private final Properties properties;
    private final Environment environment;
    private final Timeouts timeouts;
    private final BrowserSettings browserSettings;
    private final RetryConfig retryConfig;
    private final Credentials adminCredentials;
    private final Credentials standardUserCredentials;

    private ConfigManager(Environment environment) throws IOException {
        this.environment = environment;
        this.properties = loadProperties(environment);
        this.timeouts = loadTimeouts();
        this.browserSettings = loadBrowserSettings();
        this.retryConfig = loadRetryConfig();
        this.adminCredentials = loadAdminCredentials();
        this.standardUserCredentials = loadStandardUserCredentials();
    }

    public static synchronized ConfigManager getInstance(Environment environment) throws IOException {
        if (instance == null) {
            instance = new ConfigManager(environment);
        }
        return instance;
    }

    public static synchronized ConfigManager getInstance() throws IOException {
        String env = System.getProperty("env", System.getenv("ENV"));
        if (env == null) {
            env = "dev";
        }
        return getInstance(Environment.fromString(env));
    }

    public static synchronized void reset() {
        instance = null;
    }

    private Properties loadProperties(Environment environment) throws IOException {
        Properties props = new Properties();
        String resourceName = String.format("config-%s.properties", environment.getName());
        
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(resourceName)) {
            if (input == null) {
                throw new IOException("Configuration file not found: " + resourceName);
            }
            props.load(input);
        }
        return props;
    }

    private Timeouts loadTimeouts() {
        long defaultTimeout = getLongProperty("timeout.default", 30);
        long navigationTimeout = getLongProperty("timeout.navigation", 60);
        long assertionTimeout = getLongProperty("timeout.assertion", 10);
        return new Timeouts(defaultTimeout, navigationTimeout, assertionTimeout);
    }

    private BrowserSettings loadBrowserSettings() {
        boolean headlessMode = getBooleanProperty("browser.headless", false);
        int viewportWidth = getIntProperty("browser.viewport.width", 1920);
        int viewportHeight = getIntProperty("browser.viewport.height", 1080);
        String browserType = getStringProperty("browser.type", "chrome");
        return new BrowserSettings(headlessMode, viewportWidth, viewportHeight, browserType);
    }

    private RetryConfig loadRetryConfig() {
        int maxRetries = getIntProperty("retry.max", 2);
        boolean screenshotOnFailure = getBooleanProperty("retry.screenshot.enabled", true);
        boolean videoRecordingOnFirstRetry = getBooleanProperty("retry.video.enabled", true);
        return new RetryConfig(maxRetries, screenshotOnFailure, videoRecordingOnFirstRetry);
    }

    private Credentials loadAdminCredentials() {
        String username = getStringProperty("admin.username", System.getenv("ADMIN_USERNAME"));
        String password = getStringProperty("admin.password", System.getenv("ADMIN_PASSWORD"));
        
        if (username == null || password == null) {
            throw new IllegalStateException("Admin credentials not configured. Set admin.username and admin.password in properties file or ADMIN_USERNAME/ADMIN_PASSWORD environment variables");
        }
        return new Credentials(username, password);
    }

    private Credentials loadStandardUserCredentials() {
        String username = getStringProperty("user.username", System.getenv("USER_USERNAME"));
        String password = getStringProperty("user.password", System.getenv("USER_PASSWORD"));
        
        if (username == null || password == null) {
            throw new IllegalStateException("Standard user credentials not configured. Set user.username and user.password in properties file or USER_USERNAME/USER_PASSWORD environment variables");
        }
        return new Credentials(username, password);
    }

    private String getStringProperty(String key, String defaultValue) {
        String value = properties.getProperty(key);
        return value != null && !value.isEmpty() ? value : defaultValue;
    }

    private int getIntProperty(String key, int defaultValue) {
        try {
            String value = properties.getProperty(key);
            return value != null ? Integer.parseInt(value) : defaultValue;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private long getLongProperty(String key, long defaultValue) {
        try {
            String value = properties.getProperty(key);
            return value != null ? Long.parseLong(value) : defaultValue;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private boolean getBooleanProperty(String key, boolean defaultValue) {
        String value = properties.getProperty(key);
        return value != null ? Boolean.parseBoolean(value) : defaultValue;
    }

    @Override
    public String getBaseUrl() {
        return getStringProperty("base.url", "https://login.salesforce.com/?locale=in");
    }

    @Override
    public String getApiUrl() {
        return getStringProperty("api.url", null);
    }

    @Override
    public Credentials getAdminCredentials() {
        return adminCredentials;
    }

    @Override
    public Credentials getStandardUserCredentials() {
        return standardUserCredentials;
    }

    @Override
    public Timeouts getTimeouts() {
        return timeouts;
    }

    @Override
    public BrowserSettings getBrowserSettings() {
        return browserSettings;
    }

    @Override
    public RetryConfig getRetryConfig() {
        return retryConfig;
    }

    @Override
    public Environment getEnvironment() {
        return environment;
    }

    @Override
    public String toString() {
        return "ConfigManager{" +
                "environment=" + environment +
                ", baseUrl='" + getBaseUrl() + '\'' +
                ", timeouts=" + timeouts +
                ", browserSettings=" + browserSettings +
                ", retryConfig=" + retryConfig +
                '}';
    }
}
