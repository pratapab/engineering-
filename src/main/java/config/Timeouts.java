package config;

public class Timeouts {
    private final long defaultTimeout;
    private final long navigationTimeout;
    private final long assertionTimeout;

    private static final long DEFAULT_TIMEOUT = 30;
    private static final long NAVIGATION_TIMEOUT = 60;
    private static final long ASSERTION_TIMEOUT = 10;

    public Timeouts() {
        this(DEFAULT_TIMEOUT, NAVIGATION_TIMEOUT, ASSERTION_TIMEOUT);
    }

    public Timeouts(long defaultTimeout, long navigationTimeout, long assertionTimeout) {
        if (defaultTimeout <= 0) {
            throw new IllegalArgumentException("Default timeout must be greater than 0");
        }
        if (navigationTimeout <= 0) {
            throw new IllegalArgumentException("Navigation timeout must be greater than 0");
        }
        if (assertionTimeout <= 0) {
            throw new IllegalArgumentException("Assertion timeout must be greater than 0");
        }
        this.defaultTimeout = defaultTimeout;
        this.navigationTimeout = navigationTimeout;
        this.assertionTimeout = assertionTimeout;
    }

    public long getDefaultTimeout() {
        return defaultTimeout;
    }

    public long getNavigationTimeout() {
        return navigationTimeout;
    }

    public long getAssertionTimeout() {
        return assertionTimeout;
    }

    @Override
    public String toString() {
        return "Timeouts{" +
                "defaultTimeout=" + defaultTimeout +
                ", navigationTimeout=" + navigationTimeout +
                ", assertionTimeout=" + assertionTimeout +
                '}';
    }
}
