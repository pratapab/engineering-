Configuration Module Documentation

STRUCTURE:
==========
config/
├── Environment.java          - Enum for dev, staging, production
├── Credentials.java          - Immutable credentials holder
├── Timeouts.java             - Timeout configuration (default, navigation, assertion)
├── BrowserSettings.java      - Browser configuration (headless, viewport, type)
├── RetryConfig.java          - Retry and artifact configuration
├── TestConfig.java           - Interface for configuration
└── ConfigManager.java        - Singleton manager for configuration loading

USAGE:
======

1. Load Configuration:
   TestConfig config = ConfigManager.getInstance();  // Uses ENV or 'dev' by default
   TestConfig config = ConfigManager.getInstance(Environment.STAGING);  // Specific environment

2. Access Configuration:
   String baseUrl = config.getBaseUrl();
   Credentials admin = config.getAdminCredentials();
   Timeouts timeouts = config.getTimeouts();
   BrowserSettings browser = config.getBrowserSettings();
   RetryConfig retry = config.getRetryConfig();

3. Environment Setup:
   - Copy .env.example to .env
   - Fill in actual credentials
   - Set ENV variable: export ENV=staging (or dev/production)
   - Or use system property: -Denv=staging

ENVIRONMENT FILES:
==================

config-dev.properties:
- Headless: false
- Retries: 2
- Video: enabled
- Screenshot: enabled

config-staging.properties:
- Headless: true
- Retries: 3
- Video: enabled
- Screenshot: enabled

config-production.properties:
- Headless: true
- Retries: 1
- Video: disabled
- Screenshot: enabled

CREDENTIALS:
============
Set via:
1. Environment variables: ADMIN_USERNAME, ADMIN_PASSWORD, USER_USERNAME, USER_PASSWORD
2. .env file
3. Properties file (not recommended for production)

INTEGRATION WITH TESTS:
======================
In BaseTest.java:
   TestConfig config = ConfigManager.getInstance();
   driver.navigate().to(config.getBaseUrl());
   WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(config.getTimeouts().getDefaultTimeout()));

In Test Classes:
   Credentials admin = config.getAdminCredentials();
   loginPage.performLogin(admin.getUsername(), admin.getPassword());
