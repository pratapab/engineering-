const path = require('path');
const fs = require('fs');

// Load environment configuration
const ENV = process.env.ENVIRONMENT || 'dev';
const configPath = path.join(__dirname, `config.${ENV}.json`);

if (!fs.existsSync(configPath)) {
  throw new Error(`Configuration file not found: ${configPath}`);
}

const envConfig = JSON.parse(fs.readFileSync(configPath, 'utf8'));

// Global test data and session management
let globalSession = null;
let authenticated = false;

// Custom logger
class TestLogger {
  constructor() {
    this.logFile = path.join(process.cwd(), 'logs', `test-${Date.now()}.log`);
    // Ensure logs directory exists
    const logDir = path.dirname(this.logFile);
    if (!fs.existsSync(logDir)) {
      fs.mkdirSync(logDir, { recursive: true });
    }
  }

  log(level, message, data = {}) {
    const timestamp = new Date().toISOString();
    const logEntry = {
      timestamp,
      level,
      message,
      data,
      test: global.currentTest || 'unknown'
    };

    console.log(`[${level.toUpperCase()}] ${message}`);
    fs.appendFileSync(this.logFile, JSON.stringify(logEntry) + '\n');
  }

  info(message, data) { this.log('info', message, data); }
  error(message, data) { this.log('error', message, data); }
  warn(message, data) { this.log('warn', message, data); }
}

const logger = new TestLogger();

exports.config = {
  // ====================
  // Runner Configuration
  // ====================
  runner: 'local',

  // ============
  // Specs
  // ============
  specs: [
    './tests/specs/**/*.feature'
  ],

  // ============
  // Capabilities
  // ============
  capabilities: [
    {
      browserName: 'chrome',
      'goog:chromeOptions': {
        args: [
          '--disable-web-security',
          '--disable-features=VizDisplayCompositor',
          '--no-sandbox',
          '--disable-dev-shm-usage'
        ]
      },
      maxInstances: 4
    },
    {
      browserName: 'firefox',
      maxInstances: 4
    },
    {
      browserName: 'safari',
      maxInstances: 2
    }
  ],

  // ===================
  // Test Configurations
  // ===================
  logLevel: 'info',
  bail: 0,
  waitforTimeout: 10000,
  connectionRetryTimeout: 120000,
  connectionRetryCount: 3,

  // ============
  // Frameworks
  // ============
  framework: 'cucumber',
  cucumberOpts: {
    require: ['./tests/step-definitions/**/*.js'],
    backtrace: false,
    requireModule: [],
    dryRun: false,
    failFast: false,
    format: ['pretty'],
    colors: true,
    snippets: true,
    source: true,
    profile: [],
    strict: false,
    tagExpression: '',
    timeout: 60000,
    ignoreUndefinedDefinitions: false,
    retry: 2,
    retryTagFilter: '@retry'
  },

  // ============
  // Reporters
  // ============
  reporters: [
    'spec',
    ['junit', {
      outputDir: './test-results/junit',
      outputFileFormat: function(opts) {
        return `results-${opts.cid}.${opts.capabilities.browserName}.xml`;
      }
    }],
    ['allure', {
      outputDir: './allure-results',
      disableWebdriverStepsReporting: false,
      disableWebdriverScreenshotsReporting: false,
      useCucumberStepReporter: true
    }]
  ],

  // ============
  // Services
  // ============
  services: [
    ['selenium-standalone', {
      logPath: './logs',
      installArgs: {
        drivers: {
          chrome: { version: 'latest' },
          firefox: { version: 'latest' },
          safari: { version: 'latest' }
        }
      },
      args: {
        drivers: {
          chrome: { version: 'latest' },
          firefox: { version: 'latest' },
          safari: { version: 'latest' }
        }
      }
    }]
  ],

  // =====
  // Hooks
  // =====
  before: function (capabilities, specs) {
    browser.setWindowSize(1920, 1080);
    global.logger = logger;
    global.testData = envConfig;
  },

  beforeScenario: function (world, context) {
    global.currentTest = world.pickle.name;
    logger.info(`Starting scenario: ${world.pickle.name}`);
  },

  beforeStep: function (step, scenario, context) {
    logger.info(`Executing step: ${step.text}`);
  },

  afterStep: function (step, scenario, result, context) {
    if (result.passed) {
      logger.info(`Step passed: ${step.text}`);
    } else {
      logger.error(`Step failed: ${step.text}`, { error: result.error });
    }
  },

  afterScenario: function (world, result, context) {
    logger.info(`Scenario completed: ${world.pickle.name}`, {
      status: result.passed ? 'PASSED' : 'FAILED',
      duration: result.duration
    });

    // Take screenshot on failure
    if (!result.passed) {
      const screenshotPath = `./test-results/screenshots/${world.pickle.name.replace(/\s+/g, '_')}.png`;
      browser.saveScreenshot(screenshotPath);
      logger.info(`Screenshot saved: ${screenshotPath}`);
    }
  },

  after: function (result, capabilities, specs) {
    logger.info('Test session completed', { result });
  },

  onError: function (error) {
    logger.error('Test error occurred', { error: error.message });
  }
};
