package config;

public interface TestConfig {
    String getBaseUrl();
    String getApiUrl();
    Credentials getAdminCredentials();
    Credentials getStandardUserCredentials();
    Timeouts getTimeouts();
    BrowserSettings getBrowserSettings();
    RetryConfig getRetryConfig();
    Environment getEnvironment();
}
