const { config } = require('./config.base');

exports.config = {
  ...config,

  // ============
  // Capabilities
  // ============
  capabilities: [
    {
      browserName: 'chrome',
      'goog:chromeOptions': {
        args: [
          '--headless',
          '--disable-web-security',
          '--disable-features=VizDisplayCompositor',
          '--no-sandbox',
          '--disable-dev-shm-usage',
          '--disable-gpu'
        ]
      },
      maxInstances: 4
    },
    {
      browserName: 'firefox',
      'moz:firefoxOptions': {
        args: ['--headless']
      },
      maxInstances: 2
    }
  ],

  // ===================
  // Test Configurations
  // ===================
  logLevel: 'warn',
  bail: 0,
  waitforTimeout: 15000,
  connectionRetryTimeout: 120000,
  connectionRetryCount: 3,

  // ============
  // Services
  // ============
  services: [
    ['selenium-standalone', {
      logPath: './logs',
      installArgs: {
        drivers: {
          chrome: { version: 'latest' },
          firefox: { version: 'latest' }
        }
      },
      args: {
        drivers: {
          chrome: { version: 'latest' },
          firefox: { version: 'latest' }
        }
      }
    }]
  ],

  // =====
  // Hooks
  // =====
  before: function (capabilities, specs) {
    browser.setWindowSize(1920, 1080);
  }
};
