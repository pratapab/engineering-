# Jenkins Setup Guide for SalesforceAutomation

## Prerequisites
- Jenkins server installed and running
- Git repository hosting the SalesforceAutomation project
- Maven installed on Jenkins agents
- Java 11+ installed on Jenkins agents

---

## Step 1: Install Required Jenkins Plugins

1. **Manage Jenkins** → **Manage Plugins** → **Available**
2. Search for and install these plugins:
   - **Pipeline** (Declarative Pipeline)
   - **Git** (Git plugin)
   - **Maven Integration** (Maven Integration plugin)
   - **TestNG Results Plugin** (TestNG plugin)
   - **JUnit Plugin** (for test reporting)
   - **Jira plugin** (JIRA plugin)
   - **HTTP Request Plugin** (for Jira REST API calls)
   - **Email Extension Plugin** (notifications)
   - **Blue Ocean** (optional, for better UI)

---

## Step 2: Configure Jenkins Credentials

### 2.1 Git Credentials
1. **Manage Jenkins** → **Manage Credentials** → **System** → **Global credentials**
2. Click **Add Credentials**
3. Select **SSH Key** or **Username with password**
4. Add credentials for your Git repository
5. Note the credential ID (e.g., `github-ssh`)

### 2.2 Jira Credentials
1. Go to **Manage Jenkins** → **Manage Credentials**
2. Click **Add Credentials**
3. Create two credentials:

**Jira Site URL:**
```
Kind: Secret text
ID: jira-site-url
Secret: https://your-jira-instance.atlassian.net
```

**Jira Username:**
```
Kind: Secret text  
ID: jira-username
Secret: your-jira-email@example.com
```

**Jira API Token:**
```
Kind: Secret text
ID: jira-api-token
Secret: Your_Jira_API_Token (from Jira account settings)
```

### 2.3 Jenkins System Credentials
```
Kind: Secret text
ID: jenkins-api-token
Secret: Your_Jenkins_API_Token
```

---

## Step 3: Create the Pipeline Job

### 3.1 Create New Job
1. **New Item**
2. Name: `SalesforceAutomation`
3. Select **Pipeline**
4. Click **OK**

### 3.2 Configure Pipeline

**General Tab:**
```
Description: Automated test suite for Salesforce using Selenium and TestNG
GitHub project: https://github.com/your-org/SalesforceAutomation
```

**Build Triggers:**
```
☑ GitHub hook trigger for GITScm polling
  (after setting up GitHub webhook)

OR

☑ Poll SCM
  Schedule: H H * * * (daily)
```

**Pipeline Tab:**
```
Definition: Pipeline script from SCM
SCM: Git
Repository URL: https://github.com/your-org/SalesforceAutomation.git
Credentials: [Select your git credentials]
Branch Specifier: */main
Script Path: Jenkinsfile
```

---

## Step 4: Configure Build Parameters

In the **Pipeline** → **Script** section or in the Jenkinsfile, parameters are defined:

```groovy
parameters {
    choice(
        name: 'ENVIRONMENT',
        choices: ['dev', 'staging', 'production'],
        description: 'Target environment for testing'
    )
    booleanParam(
        name: 'REPORT_TO_JIRA',
        defaultValue: true,
        description: 'Report test failures to Jira'
    )
}
```

---

## Step 5: Configure Environment Variables

In **Jenkins** → **Manage Jenkins** → **Configure System** → **Global properties**:

```
Environment variables:
JAVA_HOME = C:\Program Files\Java\jdk-11
MAVEN_HOME = C:\maven
PROJECT_NAME = SalesforceAutomation
```

Or in the Jenkinsfile itself using the `environment` block.

---

## Step 6: Set Up GitHub Webhook (Optional but recommended)

### 6.1 Get Jenkins Hook URL
```
http://your-jenkins-server:8080/github-webhook/
```

### 6.2 Configure GitHub Webhook
1. Go to GitHub repository settings
2. **Webhooks** → **Add webhook**
3. Payload URL: `http://your-jenkins-server:8080/github-webhook/`
4. Content type: `application/json`
5. Events: `Just the push event`
6. Active: ☑

Now Jenkins will automatically trigger builds on git push!

---

## Step 7: Configure Email Notifications

### 7.1 System Email Settings
**Manage Jenkins** → **Configure System** → **Email Notification**:
```
SMTP server: smtp.gmail.com (or your email provider)
Default user e-mail suffix: @gmail.com
```

### 7.2 Extended Email Notification
**Manage Jenkins** → **Configure System** → **Extended E-mail Notification**:
```
SMTP server: smtp.gmail.com
Default recipients: your-email@example.com
```

### 7.3 Update Jenkinsfile
In the Jenkinsfile, add email notifications:
```groovy
post {
    always {
        emailext(
            subject: "${env.JOB_NAME} - Build #${env.BUILD_NUMBER} - ${currentBuild.result}",
            body: """
                Build: ${env.JOB_NAME} #${env.BUILD_NUMBER}
                Status: ${currentBuild.result}
                Log: ${env.BUILD_URL}console
            """,
            to: "${change.author.email}",
            mimeType: 'text/html'
        )
    }
}
```

---

## Step 8: Configure Jira Integration

### 8.1 Jira Plugin Configuration
**Manage Jenkins** → **Configure System** → **Jira**:
```
Jira URL: https://your-jira-instance.atlassian.net
User name: your-jira-email@example.com
API Token: [from Jira account settings]
Test Connection: Click "Test Connection"
```

### 8.2 HTTP Request for Custom Jira Integration
If using HTTP Request plugin instead:

```groovy
def reportToJira(Map failures) {
    failures.each { testName, errorMessage ->
        def payload = """
        {
            "fields": {
                "project": {"key": "SFDC"},
                "summary": "Test Failed: ${testName}",
                "description": "Failed in ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                "issuetype": {"name": "Bug"}
            }
        }
        """
        
        httpRequest(
            url: "${JIRA_SITE}/rest/api/3/issue",
            httpMode: 'POST',
            customHeaders: [
                [maskValue: true, name: 'Authorization', value: "Bearer ${JIRA_TOKEN}"]
            ],
            requestBody: payload,
            validResponseCodes: '200,201'
        )
    }
}
```

---

## Step 9: Test the Pipeline

### 9.1 Manual Trigger
1. Go to the `SalesforceAutomation` job
2. Click **Build with Parameters**
3. Select ENVIRONMENT: `dev`
4. Check REPORT_TO_JIRA: `true`
5. Click **Build**

### 9.2 Monitor Build
- Click the build number
- Click **Console Output** to watch logs in real-time
- Check test results in **Test Result** section

### 9.3 Verify Jira Integration
1. Go to your Jira instance
2. Check SFDC project for new issues with label `test-failure`

---

## Step 10: Customize for Your Environment

### 10.1 Update Jenkinsfile Parameters

Edit [Jenkinsfile](./Jenkinsfile):
```groovy
environment {
    JAVA_HOME = 'C:\\Program Files\\Java\\jdk-11'  // Your Java path
    MAVEN_HOME = 'C:\\maven'                         // Your Maven path
    JIRA_SITE = credentials('jira-site-url')
}
```

### 10.2 Update Credentials in Jenkinsfile
Replace credential references with your Jenkins credential IDs:
```groovy
JIRA_SITE = credentials('jira-site-url')           // Match Jenkins credential ID
JIRA_USER = credentials('jira-username')           // Match Jenkins credential ID
JIRA_TOKEN = credentials('jira-api-token')         // Match Jenkins credential ID
```

### 10.3 Configure Project-Specific Variables
In the Jenkinsfile, update:
```groovy
def jiraProject = 'SFDC'           // Your actual Jira project key
def jiraIssueType = 'Bug'          // Your issue type
def testsuitePath = 'src/test/resources/testng.xml'  // Your test suite path
```

---

## Step 11: Set Up MCP Integration (Advanced)

### 11.1 Install Jenkins MCP Server
```powershell
npm install -g @modelcontextprotocol/server-jenkins
```

### 11.2 Configure MCP in VS Code
Update `.vscode/settings.json`:
```json
{
  "mcp": {
    "servers": [
      {
        "name": "jenkins-mcp",
        "command": "npx",
        "args": ["@modelcontextprotocol/server-jenkins"],
        "env": {
          "JENKINS_URL": "http://your-jenkins-server:8080",
          "JENKINS_USERNAME": "your-username",
          "JENKINS_TOKEN": "your-api-token"
        }
      }
    ]
  }
}
```

### 11.3 Test MCP Connection
In VS Code Copilot chat:
```
"@github Connect to Jenkins MCP and list all SalesforceAutomation jobs"
```

---

## Troubleshooting

### Build Won't Start
```
Check:
1. Git credentials are correct
2. Repository URL is accessible
3. Jenkinsfile exists in repository root
4. Script path is correct: Jenkinsfile
```

### Tests Not Running
```
Check:
1. Maven and Java are in PATH
2. pom.xml is present
3. testng.xml is present at src/test/resources/testng.xml
4. Check build logs for compilation errors
```

### Jira Integration Not Working
```
Check:
1. Jira credentials are correct
2. API token is valid (regenerate if needed)
3. User has permission to create issues
4. SFDC project exists
5. Check CURL command in logs
```

### Test Results Not Parsing
```
Check:
1. Test results are in target/surefire-reports/TEST-*.xml
2. XML format is correct
3. junit plugin is installed
```

---

## Next Steps

1. ✅ Install required plugins
2. ✅ Configure credentials
3. ✅ Create pipeline job
4. ✅ Test with manual build
5. ✅ Set up GitHub webhook
6. ✅ Configure email notifications
7. ✅ Set up Jira integration
8. ✅ Use Copilot with MCP to manage pipeline

---

## Useful Jenkins CLI Commands

```powershell
# Get job info
java -jar jenkins-cli.jar -s http://localhost:8080 get-job SalesforceAutomation

# Trigger build
java -jar jenkins-cli.jar -s http://localhost:8080 build SalesforceAutomation -p ENVIRONMENT=dev

# Get console output
java -jar jenkins-cli.jar -s http://localhost:8080 console SalesforceAutomation 42

# List all jobs
java -jar jenkins-cli.jar -s http://localhost:8080 list-jobs
```

---

## Support & Resources

- [Jenkins Documentation](https://www.jenkins.io/doc/)
- [Jenkins Pipeline Syntax](https://www.jenkins.io/doc/book/pipeline/syntax/)
- [Jira Plugin Documentation](https://plugins.jenkins.io/jira/)
- [TestNG Plugin](https://plugins.jenkins.io/testng-plugin/)
- [GitHub Webhook Documentation](https://docs.github.com/en/developers/webhooks-and-events/webhooks)
