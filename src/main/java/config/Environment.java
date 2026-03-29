package config;

public enum Environment {
    DEV("dev"),
    STAGING("staging"),
    PRODUCTION("production");

    private final String name;

    Environment(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static Environment fromString(String value) {
        try {
            return Environment.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid environment: " + value + ". Valid values: DEV, STAGING, PRODUCTION");
        }
    }
}
