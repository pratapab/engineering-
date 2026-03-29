Feature: Salesforce Login Functionality

  @smoke @critical
  Scenario: Valid admin login
    Given I am on the Salesforce login page
    When I enter valid admin credentials
    And I click the login button
    Then I should be redirected to the home page
    And I should see the user menu

  @smoke @critical
  Scenario: Display user information after login
    Given I am logged in as admin
    When I click on the user profile icon
    Then I should see the user name displayed
    And I should see the user role information

  @regression @sanity
  Scenario: Invalid login credentials
    Given I am on the Salesforce login page
    When I enter invalid username "invalid@example.com"
    And I enter invalid password "wrongpassword"
    And I click the login button
    Then I should see an error message "Invalid username or password"

  @regression @sanity
  Scenario: Session expiry handling
    Given I am not logged in
    When I navigate directly to the home page
    Then I should be redirected to the login page

  @regression
  Scenario: Session persistence across page refreshes
    Given I am logged in as admin
    When I refresh the current page
    Then I should remain logged in
    And I should stay on the same page

  @regression
  Scenario: Network timeout handling
    Given I am on the Salesforce login page
    When the network connection is slow
    Then the login should handle timeouts gracefully

  @regression
  Scenario: Password field validation
    Given I am on the Salesforce login page
    When I enter a valid username
    And I leave the password field empty
    And I click the login button
    Then I should see an error message "Password is required"
