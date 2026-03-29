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
          '--disable-web-security',
          '--disable-features=VizDisplayCompositor',
          '--no-sandbox',
          '--disable-dev-shm-usage'
        ]
      },
      maxInstances: 4
    }
  ],

  // ===================
  // Test Configurations
  // ===================
  logLevel: 'info',
  bail: 0,
  waitforTimeout: 10000,
  connectionRetryTimeout: 120000,
  connectionRetryCount: 3

  // Note: Selenium Grid configuration removed - using selenium-standalone service
};
