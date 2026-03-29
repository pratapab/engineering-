package config;

public class RetryConfig {
    private final int maxRetries;
    private final boolean screenshotOnFailure;
    private final boolean videoRecordingOnFirstRetry;

    private static final int DEFAULT_MAX_RETRIES = 2;
    private static final boolean DEFAULT_SCREENSHOT = true;
    private static final boolean DEFAULT_VIDEO_RECORDING = true;

    public RetryConfig() {
        this(DEFAULT_MAX_RETRIES, DEFAULT_SCREENSHOT, DEFAULT_VIDEO_RECORDING);
    }

    public RetryConfig(int maxRetries, boolean screenshotOnFailure, boolean videoRecordingOnFirstRetry) {
        if (maxRetries < 0) {
            throw new IllegalArgumentException("Max retries cannot be negative");
        }
        this.maxRetries = maxRetries;
        this.screenshotOnFailure = screenshotOnFailure;
        this.videoRecordingOnFirstRetry = videoRecordingOnFirstRetry;
    }

    public int getMaxRetries() {
        return maxRetries;
    }

    public boolean isScreenshotOnFailure() {
        return screenshotOnFailure;
    }

    public boolean isVideoRecordingOnFirstRetry() {
        return videoRecordingOnFirstRetry;
    }

    @Override
    public String toString() {
        return "RetryConfig{" +
                "maxRetries=" + maxRetries +
                ", screenshotOnFailure=" + screenshotOnFailure +
                ", videoRecordingOnFirstRetry=" + videoRecordingOnFirstRetry +
                '}';
    }
}
