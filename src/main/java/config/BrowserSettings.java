package config;

public class BrowserSettings {
    private final boolean headlessMode;
    private final int viewportWidth;
    private final int viewportHeight;
    private final String browserType;

    private static final int DEFAULT_VIEWPORT_WIDTH = 1920;
    private static final int DEFAULT_VIEWPORT_HEIGHT = 1080;
    private static final boolean DEFAULT_HEADLESS = false;
    private static final String DEFAULT_BROWSER = "chrome";

    public BrowserSettings() {
        this(DEFAULT_HEADLESS, DEFAULT_VIEWPORT_WIDTH, DEFAULT_VIEWPORT_HEIGHT, DEFAULT_BROWSER);
    }

    public BrowserSettings(boolean headlessMode, int viewportWidth, int viewportHeight, String browserType) {
        if (viewportWidth <= 0 || viewportHeight <= 0) {
            throw new IllegalArgumentException("Viewport dimensions must be greater than 0");
        }
        if (browserType == null || browserType.isEmpty()) {
            throw new IllegalArgumentException("Browser type cannot be null or empty");
        }
        this.headlessMode = headlessMode;
        this.viewportWidth = viewportWidth;
        this.viewportHeight = viewportHeight;
        this.browserType = browserType.toLowerCase();
    }

    public boolean isHeadlessMode() {
        return headlessMode;
    }

    public int getViewportWidth() {
        return viewportWidth;
    }

    public int getViewportHeight() {
        return viewportHeight;
    }

    public String getBrowserType() {
        return browserType;
    }

    @Override
    public String toString() {
        return "BrowserSettings{" +
                "headlessMode=" + headlessMode +
                ", viewportWidth=" + viewportWidth +
                ", viewportHeight=" + viewportHeight +
                ", browserType='" + browserType + '\'' +
                '}';
    }
}
