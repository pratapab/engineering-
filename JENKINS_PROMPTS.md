# GitHub Copilot + Jenkins Pipeline Prompts Guide
## SalesforceAutomation Project

This guide provides example prompts for interacting with Jenkins pipelines through GitHub Copilot with MCP integration.

---

## 1. PIPELINE TRIGGERING PROMPTS

### Basic Pipeline Trigger
```
"@github Trigger the SalesforceAutomation-Dev Jenkins pipeline"
```

### Trigger with Specific Environment
```
"@github Run the SalesforceAutomation pipeline for staging environment"
```

### Trigger with Parameters
```
"@github Connect to Jenkins MCP and trigger the SalesforceAutomation 
job with ENVIRONMENT=production and REPORT_TO_JIRA=true"
```

### Parameterized Trigger
```
"Trigger Jenkins job SalesforceAutomation with parameters: 
environment=dev, reportToJira=true"
```

---

## 2. PIPELINE STATUS & MONITORING PROMPTS

### Check Pipeline Status
```
"@github What's the status of the SalesforceAutomation-Dev pipeline?"
```

### View Build History
```
"@github Show me the last 5 builds of the SalesforceAutomation job"
```

### Check Test Results
```
"@github Give me the test results from the latest SalesforceAutomation build"
```

### View Pipeline Logs
```
"@github Show the console output from Jenkins build #42 of SalesforceAutomation"
```

### Monitor Running Build
```
"@github Track the progress of the currently running SalesforceAutomation build"
```

---

## 3. JIRA INTEGRATION PROMPTS

### Create Issue for Test Failure
```
"@github Create a Jira issue (SFDC project) for the test failure: 
LoginTestInvalid - NullPointerException in page initialization"
```

### Link Build to Jira
```
"@github Create a Jira issue for test failures from SalesforceAutomation build #42 
and link them to the pipeline"
```

### Update Jira with Results
```
"Connect to Jira MCP and report test failures from Jenkins build #42 
to project SFDC with labels: automation, test-failure"
```

### Find Related Issues
```
"@github Find all Jira issues in SFDC project with label 'test-failure' 
from the last 7 days"
```

### Create Bulk Issues
```
"@github Create Jira issues for each failing test in SalesforceAutomation 
build #42 and tag them with build number and environment"
```

---

## 4. COMBINED JENKINS + JIRA PROMPTS

### Full Pipeline Results Flow
```
"@github Run SalesforceAutomation-Dev, wait for results, and if there are 
test failures, create Jira issues in SFDC project with test details"
```

### Report and Track
```
"@github Execute SalesforceAutomation pipeline for staging environment, 
report any failures to Jira, and notify me of the results"
```

### Automated Issue Management
```
"Connect to Jenkins and Jira MCP: trigger SalesforceAutomation, 
capture test results, create issues for failures, and add labels 
with build number and timestamp"
```

---

## 5. CONFIGURATION & SETUP PROMPTS

### Check MCP Configuration
```
"@github Verify that Jenkins MCP is configured and can connect to 
http://localhost:8080"
```

### Validate Jira Integration
```
"@github Test the connection to Jira MCP with credentials from environment 
variables and verify SFDC project access"
```

### Display Current Settings
```
"@github Show me the current Jenkins and Jira MCP configuration settings"
```

### Update Pipeline Parameters
```
"@github Modify the SalesforceAutomation Jenkins job to add a new parameter 
for Jira sprint assignment"
```

---

## 6. DEBUGGING & TROUBLESHOOTING PROMPTS

### Debug Build Failure
```
"@github Connect to Jenkins MCP and analyze why SalesforceAutomation 
build #42 failed in the test stage"
```

### Check Credentials
```
"@github Verify that all required Jenkins credentials are properly 
configured for the SalesforceAutomation pipeline"
```

### View Stage Logs
```
"@github Show me the logs from the 'Report to Jira' stage of the 
SalesforceAutomation build #42"
```

### Diagnose Jira Connection Issues
```
"@github Test connectivity to Jira API and verify the authentication 
token is valid"
```

---

## 7. ADVANCED AUTOMATION PROMPTS

### Conditional Pipeline Execution
```
"@github Create a workflow that automatically triggers SalesforceAutomation 
pipeline on git push to main branch and reports results to Jira"
```

### Scheduled Testing
```
"@github Set up a scheduled Jenkins job to run SalesforceAutomation 
tests daily at 2 AM on staging environment"
```

### Multi-Environment Orchestration
```
"@github Create a Jenkins workflow that runs SalesforceAutomation in dev, 
staging, and production sequentially, reporting results to Jira at each stage"
```

### Performance Tracking
```
"@github Extract performance metrics from SalesforceAutomation test runs 
and create trending charts in Jira"
```

---

## 8. MCP-SPECIFIC PROMPTS

### Direct MCP Commands
```
"@mcp:jenkins-mcp triggerJob(jobName='SalesforceAutomation', 
parameters={ENVIRONMENT: 'dev', REPORT_TO_JIRA: true})"
```

### Query Jenkins via MCP
```
"@mcp:jenkins-mcp getJobInfo('SalesforceAutomation')"
```

### Get Build Details
```
"@mcp:jenkins-mcp getBuild('SalesforceAutomation', '42')"
```

### List All Pipelines
```
"@mcp:jenkins-mcp listJobs(pattern='SalesforceAutomation*')"
```

---

## SETUP REQUIREMENTS

Before using these prompts, ensure:

### 1. Jenkins Setup
```
- Jenkins instance running (http://localhost:8080)
- SalesforceAutomation job configured
- Jenkins user account created
- API token generated for the user
```

### 2. Jira Setup
```
- Jira instance accessible
- SFDC project created
- API token generated
- User has permission to create/edit issues
```

### 3. Environment Variables
```
JENKINS_USER=your-jenkins-username
JENKINS_TOKEN=your-jenkins-api-token
JIRA_USER=your-jira-email
JIRA_TOKEN=your-jira-api-token
```

### 4. VS Code Configuration
```
- GitHub Copilot extension installed and activated
- MCP integration enabled in settings.json
- Jenkins MCP server configured with credentials
- Jira MCP server configured with credentials
```

### 5. MCP Server Installation
```powershell
# Install Jenkins MCP
npm install -g @modelcontextprotocol/server-jenkins

# Install Jira MCP (if available)
npm install -g @modelcontextprotocol/server-jira

# Or use containerized MCP servers
```

---

## USAGE EXAMPLES

### Example 1: Run Tests and Report Failures
```
User: "@github Run SalesforceAutomation tests in dev and report any failures to Jira"

Copilot will:
1. Connect to Jenkins MCP
2. Trigger SalesforceAutomation-Dev pipeline
3. Wait for completion
4. Parse test results
5. Create Jira issues for failures
6. Return summary
```

### Example 2: Monitor vs Report
```
User: "@github What are the test results from the latest SalesforceAutomation build?"

Copilot will:
1. Query Jenkins for latest build
2. Retrieve test results
3. Format and display results
4. Ask if you want to create Jira issues
```

### Example 3: Quick Status Check
```
User: "@github Is the SalesforceAutomation-Prod pipeline healthy?"

Copilot will:
1. Get last 5 builds
2. Analyze success rate
3. Check for recent failures
4. Provide health assessment
```

---

## TIPS FOR EFFECTIVE PROMPTING

1. **Be Specific**: Include environment and pipeline name
   ```
   Good: "Trigger SalesforceAutomation-Dev with environment=staging"
   Poor: "Run tests"
   ```

2. **Use Context**: Reference build numbers or issues
   ```
   Good: "Report failures from build #42 to Jira"
   Poor: "Create a Jira issue"
   ```

3. **Chain Operations**: Combine multiple actions
   ```
   Good: "Trigger pipeline, wait for results, and report to Jira"
   Poor: Ask three separate questions
   ```

4. **Ask for Confirmation**: For production changes
   ```
   "Should I trigger SalesforceAutomation-Prod now?"
   ```

---

## TROUBLESHOOTING PROMPTS

If Copilot doesn't understand your request:

1. **Be More Explicit**
   ```
   Instead of: "Check the pipeline"
   Try: "Connect to Jenkins MCP and get the status of SalesforceAutomation build #42"
   ```

2. **Use Direct MCP Syntax**
   ```
   "@mcp:jenkins-mcp triggerJob('SalesforceAutomation', {ENVIRONMENT: 'dev'})"
   ```

3. **Break it Down**
   ```
   Instead of: "Set up everything"
   Try: 
   a) "Verify Jenkins MCP connection"
   b) "Verify Jira MCP connection"
   c) "Trigger pipeline"
   ```

---

## NEXT STEPS

1. Configure environment variables with your credentials
2. Update `.vscode/settings.json` with your Jenkins and Jira URLs
3. Test MCP connections with simple prompts
4. Start using advanced automation prompts
5. Create custom prompt templates for your workflow
