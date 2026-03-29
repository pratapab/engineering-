# Salesforce Automation Test Suite

## Quick Start

### 1. Install Dependencies
```bash
npm install
npx playwright install
```

### 2. Configure Environment
```bash
# Copy and edit configuration
cp config.dev.json config.local.json
# Edit config.local.json with your credentials
```

### 3. Run Tests

#### Run All Tests
```bash
npm test
```

#### Run Specific Test Categories
```bash
npm run test:smoke      # @smoke tagged tests only
npm run test:regression # @regression tagged tests
npm run test:sanity     # @sanity tagged tests
npm run test:critical   # @critical tagged tests
```

#### Run with Parallel Execution
```bash
npm run test:parallel   # Uses 4 workers
```

#### Run in CI Mode (Headless, JUnit output)
```bash
npm run test:ci
```

#### Debug Tests
```bash
npm run test:debug      # Debug mode
npm run test:ui         # Visual test runner
npm run test:headed     # Run with browser visible
```

## Configuration

### Test Categories (Tags)
- `@smoke` - Critical functionality, run first
- `@regression` - Full test suite
- `@sanity` - Basic functionality checks
- `@critical` - Mission-critical features

### Parallel Execution
- **Default**: 4 workers (configurable in playwright.config.ts)
- **CI**: 4 workers with retries
- **Local**: 1 worker for debugging

### Reporters
- **Local**: HTML report with screenshots/videos + line output
- **CI**: JUnit XML for CI integration + line output

### Media Capture
- **Screenshots**: Only on failure
- **Videos**: Retained on failure only
- **Traces**: On first retry

### Global Setup/Teardown
- **Setup**: Authenticate once, store session globally
- **Teardown**: Cleanup test data and sessions

## Project Structure

```
├── tests/
│   ├── global-setup.ts      # Global fixtures and setup
│   ├── login.spec.ts        # Login test suite
│   └── ...                  # Other test files
├── config.dev.json          # Development configuration
├── config.staging.json      # Staging configuration
├── config.production.json   # Production configuration
├── playwright.config.ts     # Main Playwright configuration
├── playwright.ci.config.ts  # CI-specific configuration
└── package.json             # Dependencies and scripts
```

## Environment Variables

```bash
# Set environment
ENVIRONMENT=dev  # dev, staging, production

# Override base URL
BASE_URL=https://login.salesforce.com

# CI mode
CI=true  # Enables CI-specific settings
```

## Browser Support

- **Chromium** (Chrome/Edge)
- **Firefox**
- **WebKit** (Safari)
- **Mobile** (iOS Safari, Android Chrome)

## Test Results

### Local Development
- HTML report: `npm run report`
- Screenshots: `test-results/screenshots/`
- Videos: `test-results/videos/`
- Traces: `test-results/traces/`

### CI Integration
- JUnit XML: `test-results/junit.xml`
- Compatible with Jenkins, GitHub Actions, etc.

## Best Practices

### Writing Tests
```typescript
import { test, expect } from './global-setup';

test.describe('@smoke @critical', () => {
  test('should login successfully', async ({ authenticatedPage, logger }) => {
    logger.info('Testing login functionality');

    // Test logic here
    await expect(authenticatedPage.locator('.user-menu')).toBeVisible();
  });
});
```

### Using Fixtures
- `authenticatedPage` - Pre-authenticated browser page
- `testData` - Global test data
- `logger` - Winston logger instance

### Test Organization
- Use descriptive test names
- Group related tests with `describe`
- Tag tests appropriately
- Use page objects for reusable components

## Troubleshooting

### Common Issues

1. **Browser not found**
   ```bash
   npx playwright install
   ```

2. **Authentication fails**
   - Check credentials in config files
   - Verify Salesforce instance URLs
   - Check network connectivity

3. **Tests timeout**
   - Increase timeouts in config
   - Check network speed
   - Debug with `npm run test:debug`

4. **Parallel execution issues**
   - Reduce workers in config
   - Check system resources
   - Use `npm test` for sequential

## CI/CD Integration

### Jenkins Pipeline
```groovy
stage('Test') {
    steps {
        sh 'npm ci'
        sh 'npm run test:ci'
    }
    post {
        always {
            junit 'test-results/junit.xml'
            publishHTML([
                allowMissing: false,
                alwaysLinkToLastBuild: true,
                keepAll: true,
                reportDir: 'playwright-report',
                reportFiles: 'index.html',
                reportName: 'Playwright Test Report'
            ])
        }
    }
}
```

### GitHub Actions
```yaml
- name: Run tests
  run: npm run test:ci
- name: Upload test results
  uses: actions/upload-artifact@v3
  if: always()
  with:
    name: test-results
    path: test-results/
```

## Performance Tips

1. **Use global authentication** - Login once per test session
2. **Parallel execution** - Run tests across multiple workers
3. **Selective test runs** - Use tags to run only needed tests
4. **CI optimizations** - Headless mode, no videos in CI
5. **Resource cleanup** - Proper teardown and session management

## Contributing

1. Follow the existing code structure
2. Use TypeScript for type safety
3. Add appropriate test tags
4. Include error handling and logging
5. Update this README for new features
