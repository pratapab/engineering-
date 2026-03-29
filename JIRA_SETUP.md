# Jira Integration Setup Guide

## Prerequisites
- Jira Cloud or Server instance
- Admin or project admin access
- API token (for Cloud instances)

---

## Step 1: Generate Jira API Token

### 1.1 For Jira Cloud
1. Go to: https://id.atlassian.com/manage-profile/security/api-tokens
2. Click **Create API token**
3. Enter label: `SalesforceAutomation-Jenkins`
4. Copy the token (you won't see it again!)
5. Store securely in Jenkins credentials

### 1.2 For Jira Server/Data Center
1. Go to **User Profile** (top right) → **Settings**
2. Click **Personal API tokens** (if available)
3. OR use username + password for older versions

---

## Step 2: Create Jira Project

### 2.1 Create SFDC Project
1. **Projects** → **Create project**
2. Select **Team-managed** or **Company-managed**
3. Project name: `Salesforce Automation`
4. Key: `SFDC` (auto-generated)
5. Create project

### 2.2 Create Issue Types
1. **Project Settings** → **Issue types**
2. Common types for automation:
   - ✅ Bug (default)
   - ✅ Test Failure
   - ✅ Flaky Test

### 2.3 Create Custom Fields (Optional)
1. **Project Settings** → **Custom fields**
2. Create fields for automation tracking:
   - `Build Number` (Text)
   - `Test Suite` (Text)
   - `Failure Message` (Long text)
   - `Environment` (Select list)

### 2.4 Create Labels
1. **Project Settings** → **Labels**
2. Create these labels:
   - `automation`
   - `test-failure`
   - `jenkins-build`
   - `flaky`
   - `blocker`

---

## Step 3: Configure Jenkins Jira Integration

### 3.1 Add Jira Credentials to Jenkins
1. **Manage Jenkins** → **Manage Credentials** → **System** → **Global credentials**
2. Click **Add Credentials**

Create three secrets:

**Credential 1: Jira URL**
```
Kind: Secret text
ID: jira-site-url
Secret: https://your-instance.atlassian.net
```

**Credential 2: Jira Email**
```
Kind: Secret text
ID: jira-username
Secret: your-email@example.com
```

**Credential 3: Jira API Token**
```
Kind: Secret text
ID: jira-api-token
Secret: [Your API Token from Step 1.1]
```

### 3.2 Configure Jira Plugin in Jenkins
1. **Manage Jenkins** → **Configure System**
2. Find **JIRA** section
3. Enter:
   ```
   Jira URL: https://your-instance.atlassian.net
   Jira Username: your-email@example.com
   Jira API Token: [Your API Token]
   ```
4. Click **Test Connection**

---

## Step 4: Update Jenkinsfile for Jira Reporting

The provided [Jenkinsfile](./Jenkinsfile) includes Jira integration with the `reportToJira()` function.

### 4.1 Key Configuration Variables
Edit these in Jenkinsfile:
```groovy
environment {
    JIRA_SITE = credentials('jira-site-url')
    JIRA_USER = credentials('jira-username')
    JIRA_TOKEN = credentials('jira-api-token')
}
```

### 4.2 Customize Issue Creation
Edit the `reportToJira()` function to match your setup:
```groovy
def reportToJira(Map failures) {
    failures.each { testName, errorMessage ->
        def issueJson = """
        {
            "fields": {
                "project": {"key": "SFDC"},        // Your Jira project key
                "summary": "Test Failed: ${testName}",
                "description": "Automation test failure details...",
                "issuetype": {"name": "Bug"},       // Your issue type
                "labels": ["automation", "test-failure", "${env.BUILD_NUMBER}"]
            }
        }
        """
        // ... issue creation code
    }
}
```

---

## Step 5: Test Jira Integration

### 5.1 Manual Issue Creation
Run Jenkins build and check:
1. Go to Jenkins job → **Console Output**
2. Look for "Report to Jira" stage
3. Check for successful curl requests

### 5.2 Verify in Jira
1. Go to SFDC project
2. Filter issues by label: `test-failure`
3. Verify issue was created with test details

### 5.3 Example Issue Format
```
Title: Test Failed: LoginTestInvalid
Description:
  Test failure in SalesforceAutomation build #42
  Environment: dev
  Error: NullPointerException in LoginPage.initialize()
  Details: ...
Labels: automation, test-failure, 42
```

---

## Step 6: Advanced Jira Features

### 6.1 Link Build to Jira Issue
Add to Jenkinsfile:
```groovy
environment {
    JIRA_RELEASE = "${env.BUILD_NUMBER}"
}

post {
    failure {
        script {
            sh '''
            curl -X PUT "${JIRA_SITE}/rest/api/3/issue/SFDC-123/changelog" \
                -H "Authorization: Basic $(echo -n '${JIRA_USER}:${JIRA_TOKEN}' | base64)" \
                -H "Content-Type: application/json" \
                -d '{"changes": [...]}'
            '''
        }
    }
}
```

### 6.2 Auto-Close Issues When Tests Pass
```groovy
post {
    success {
        script {
            // Query Jira for open test-failure issues from this build
            // Close them with comment "Fixed in build #X"
        }
    }
}
```

### 6.3 Create Epic for Test Suite
```groovy
stage('Create Epic') {
    steps {
        script {
            def epicJson = """
            {
                "fields": {
                    "project": {"key": "SFDC"},
                    "summary": "SalesforceAutomation Testing - Build #${env.BUILD_NUMBER}",
                    "issuetype": {"name": "Epic"},
                    "labels": ["automation", "jenkins"],
                    "customfield_10000": "${env.BUILD_NUMBER}"
                }
            }
            """
            // Create epic and link test failure issues to it
        }
    }
}
```

---

## Step 7: Jira Webhooks (Optional - for reverse integration)

### 7.1 Create Webhook
1. **Project Settings** → **Webhooks**
2. Click **Create a webhook**
3. Enter:
   ```
   Name: Jenkins Build Trigger
   URL: http://your-jenkins-server:8080/jira-issue-updated/
   Events: Issue created, Issue updated
   ```

### 7.2 Webhook Action
When issues are created in Jira:
- Can trigger Jenkins build
- Can update issue with build results
- Can transition issue workflow

---

## Step 8: Dashboard & Reporting

### 8.1 Create Jira Dashboard
1. **Dashboards** → **Create dashboard**
2. Name: `Automation Testing`
3. Add gadgets:
   - **Issue Navigator**: Filter by label `test-failure` and `automation`
   - **Created vs Resolved**: Track bug lifecycle
   - **Search Results**: Latest test failures

Sample JQL queries:
```jql
# All test failures from automation
project = SFDC AND labels = "test-failure" AND labels = "automation"

# Open test failures
project = SFDC AND labels = "test-failure" AND status != Done

# This week's failures
project = SFDC AND labels = "test-failure" AND created >= -7d

# By environment
project = SFDC AND labels = "test-failure" AND assignee = currentUser()
```

### 8.2 Build Reports
Create recurring report:
1. Go to SFDC project
2. **Reports** → **Create report**
3. Type: **Issues by Status**
4. Filter: Label `test-failure`
5. Schedule email report weekly

---

## Step 9: Configure GitHub Copilot Integration

### 9.1 Update VS Code Settings
```json
{
  "jira": {
    "url": "https://your-jira-instance.atlassian.net",
    "username": "${JIRA_USER}",
    "apiToken": "${JIRA_TOKEN}",
    "projectKey": "SFDC"
  },
  "copilot.chat": {
    "prompts": {
      "jira.createIssue": "Create a Jira issue for test failure: {testName}",
      "jira.findIssues": "Find Jira issues with label 'test-failure' in project SFDC"
    }
  }
}
```

### 9.2 Use Copilot Prompts
```
"@github Create a Jira issue in SFDC for the test failure from build #42"

"@github Find all open test failures in SFDC project from the last 7 days"

"@github Create issues for all failing tests and link them to epic SFDC-100"
```

---

## Step 10: Troubleshooting

### Issue: Can't Create Jira Issues from Jenkins
```
Check:
1. API token is valid and not expired
2. User has permission to create issues in SFDC project
3. JIRA_SITE URL doesn't have trailing slash
4. Curl command in logs shows actual error
5. Firewall allows Jenkins → Jira communication
```

### Issue: Authentication Failed
```
Check:
1. Credentials in Jenkins match Jira account
2. If using Cloud: API token is correct (not password)
3. If using Server: Password is correct
4. Credentials are base64 encoded correctly
```

### Issue: Issue Created but Missing Data
```
Check:
1. Custom fields exist in Jira
2. Field IDs are correct (customfield_XXXXX)
3. Values match field type (text, dropdown, etc.)
4. Required fields are populated
```

### Issue: Jira Connection Timeout
```
Check:
1. JIRA_SITE URL is correct and accessible
2. Firewall rules allow Jenkins server access
3. Jira instance is running and healthy
4. No VPN/proxy issues
5. Increase timeout in curl command
```

---

## Best Practices

1. **Label Consistency**: Always use `automation`, `jenkins`, and `test-failure` labels
2. **Epic Tracking**: Link test failures to build epics
3. **Auto-Transition**: Use workflow rules to auto-transition issues when tests pass
4. **Notifications**: Enable email notifications for test failures
5. **Regular Cleanup**: Archive old test failure issues monthly
6. **Metrics Tracking**: Create dashboards to track failure trends

---

## Environment-Specific Configuration

### Development Environment
```
Project Key: SFDC-DEV
Labels: automation, test-failure, dev, non-blocking
Auto-close: After 3 days if fixed
```

### Staging Environment
```
Project Key: SFDC-STAGING
Labels: automation, test-failure, staging, warning
Auto-close: After 7 days or manual review
```

### Production Environment
```
Project Key: SFDC
Labels: automation, test-failure, production, blocker
Auto-close: Manual review required
Priority: Set based on test criticality
```

---

## Useful Jira REST API Endpoints

```bash
# Create issue
POST /rest/api/3/issue

# Get issue
GET /rest/api/3/issue/{key}

# Update issue
PUT /rest/api/3/issue/{key}

# Transition issue
POST /rest/api/3/issue/{key}/transitions

# Search issues
GET /rest/api/3/search?jql=project=SFDC

# Get project
GET /rest/api/3/project/{key}
```

---

## Next Steps

1. ✅ Generate API token
2. ✅ Create SFDC project
3. ✅ Add credentials to Jenkins
4. ✅ Configure Jira plugin
5. ✅ Test integration with manual build
6. ✅ Create dashboard
7. ✅ Set up automated issue workflows

---

## Support & Resources

- [Jira REST API Documentation](https://developer.atlassian.com/cloud/jira/rest/v3/)
- [Jenkins Jira Plugin](https://plugins.jenkins.io/jira/)
- [How to Create API Token](https://support.atlassian.com/atlassian-account/docs/manage-api-tokens-for-your-atlassian-account/)
- [Jira Automation Rules](https://support.atlassian.com/cloud-automation/docs/)
