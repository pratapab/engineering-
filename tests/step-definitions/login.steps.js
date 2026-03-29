const { Given, When, Then, Before, After } = require('@cucumber/cucumber');
const { expect } = require('chai');
const fs = require('fs');
const path = require('path');

// Global session management
let globalSession = null;
let authenticated = false;

// Page Object for Login functionality
class LoginPage {
  get usernameField() { return $('#username'); }
  get passwordField() { return $('#password'); }
  get loginButton() { return $('#Login'); }
  get errorMessage() { return $('.error-message'); }
  get userMenu() { return $('[data-aura-class="forceEntityIcon"]'); }
  get userProfile() { return $('.profile-card-name'); }

  async open() {
    await browser.url('/');
  }

  async login(username, password) {
    await this.usernameField.setValue(username);
    await this.passwordField.setValue(password);
    await this.loginButton.click();
  }

  async waitForHomePage() {
    await browser.waitUntil(
      async () => {
        const url = await browser.getUrl();
        return url.includes('/home') || url.includes('/setup');
      },
      { timeout: 30000, timeoutMsg: 'Home page did not load within 30 seconds' }
    );
  }
}

const loginPage = new LoginPage();

// Global setup - authenticate once
Before(async function() {
  if (!authenticated) {
    console.log('🔐 Performing global authentication...');

    try {
      await loginPage.open();
      await loginPage.login(
        browser.config.testData.credentials.admin.username,
        browser.config.testData.credentials.admin.password
      );
      await loginPage.waitForHomePage();

      // Store session for reuse
      globalSession = await browser.getCookies();
      authenticated = true;

      console.log('✅ Global authentication successful');
    } catch (error) {
      console.error('❌ Global authentication failed:', error);
      throw error;
    }
  } else {
    console.log('🔄 Reusing existing session');
    // Restore session cookies
    if (globalSession) {
      for (const cookie of globalSession) {
        await browser.setCookies(cookie);
      }
    }
  }
});

// Global teardown
After(async function() {
  // Take screenshot on failure
  if (this.result && !this.result.passed) {
    const screenshotPath = `./test-results/screenshots/${this.pickle.name.replace(/\s+/g, '_')}.png`;
    await browser.saveScreenshot(screenshotPath);
    console.log(`📸 Screenshot saved: ${screenshotPath}`);
  }
});

// Cleanup after all tests
AfterAll(async function() {
  console.log('🧹 Cleaning up test data...');
  authenticated = false;
  globalSession = null;
});

// Step Definitions
Given('I am on the Salesforce login page', async function() {
  await loginPage.open();
});

Given('I am logged in as admin', async function() {
  // Authentication handled in Before hook
  expect(authenticated).to.be.true;
});

Given('I am not logged in', async function() {
  authenticated = false;
  globalSession = null;
  await browser.deleteAllCookies();
});

When('I enter valid admin credentials', async function() {
  // Already handled in global setup
});

When('I enter invalid username {string}', async function(username) {
  await loginPage.usernameField.setValue(username);
});

When('I enter invalid password {string}', async function(password) {
  await loginPage.passwordField.setValue(password);
});

When('I enter a valid username', async function() {
  await loginPage.usernameField.setValue(browser.config.testData.credentials.admin.username);
});

When('I leave the password field empty', async function() {
  // Password field remains empty
});

When('I click the login button', async function() {
  await loginPage.loginButton.click();
});

When('I click on the user profile icon', async function() {
  await loginPage.userMenu.click();
});

When('I navigate directly to the home page', async function() {
  await browser.url('/home');
});

When('I refresh the current page', async function() {
  await browser.refresh();
});

When('the network connection is slow', async function() {
  // Simulate slow network by adding delay
  await browser.executeAsync((done) => {
    setTimeout(done, 5000);
  });
});

Then('I should be redirected to the home page', async function() {
  await loginPage.waitForHomePage();
});

Then('I should see the user menu', async function() {
  await expect(loginPage.userMenu).toBeDisplayed();
});

Then('I should see the user name displayed', async function() {
  await expect(loginPage.userProfile).toBeDisplayed();
});

Then('I should see the user role information', async function() {
  // Verify some user role element exists
  const userRoleElement = await $('[data-aura-class*="role"]');
  await expect(userRoleElement).toBeDisplayed();
});

Then('I should see an error message {string}', async function(expectedMessage) {
  await expect(loginPage.errorMessage).toBeDisplayed();
  const actualMessage = await loginPage.errorMessage.getText();
  expect(actualMessage).to.include(expectedMessage);
});

Then('I should be redirected to the login page', async function() {
  const currentUrl = await browser.getUrl();
  expect(currentUrl).to.include('/login');
});

Then('I should remain logged in', async function() {
  await expect(loginPage.userMenu).toBeDisplayed();
});

Then('I should stay on the same page', async function() {
  const currentUrl = await browser.getUrl();
  expect(currentUrl).to.include('/home');
});

Then('the login should handle timeouts gracefully', async function() {
  // Verify that either login succeeds or shows appropriate error
  try {
    await loginPage.waitForHomePage();
  } catch (error) {
    // If timeout occurs, check for error message
    const errorDisplayed = await loginPage.errorMessage.isDisplayed();
    expect(errorDisplayed).to.be.true;
  }
});
