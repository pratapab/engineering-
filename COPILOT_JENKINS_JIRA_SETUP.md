# Jenkins + Jira + Copilot Integration Setup

Complete guide for automated testing with Jenkins pipeline, Jira issue reporting, and GitHub Copilot MCP integration.

---

## 📋 Overview

This setup enables:
- ✅ **Automated Testing**: Maven + TestNG test suite runs on Jenkins
- ✅ **Jira Reporting**: Test failures automatically create Jira issues
- ✅ **Copilot Integration**: Control Jenkins & Jira through VS Code chat using MCP
- ✅ **Multi-Environment**: Run tests in dev, staging, or production
- ✅ **Email Notifications**: Get alerts on test failures

---

## 🚀 Quick Start (10 minutes)

### 1. Setup Jenkins Server
```powershell
# Ensure Jenkins is running on port 8080
# Install required plugins (see JENKINS_SETUP.md Step 1)
```

### 2. Add Credentials to Jenkins
```
⚙️ Manage Jenkins → Manage Credentials → Add:
  - jira-site-url (Secret text)
  - jira-username (Secret text)
  - jira-api-token (Secret text)
  - github-ssh (SSH Key)
```

### 3. Create Pipeline Job
```
New Item → SalesforceAutomation → Pipeline
Pipeline → Definition → Pipeline script from SCM
SCM: Git
Repository URL: https://github.com/your-org/SalesforceAutomation.git
Script Path: Jenkinsfile
```

### 4. Test the Pipeline
```
Build with Parameters → ENVIRONMENT: dev → Build
Check Console Output and Test Results
```

### 5. Verify Jira Integration
```
Check SFDC project for new issues with label: test-failure
```

---

## 📁 Files Created

| File | Purpose |
|------|---------|
| [Jenkinsfile](./Jenkinsfile) | Jenkins pipeline definition with stages and Jira reporting |
| [.vscode/settings.json](./.vscode/settings.json) | MCP configuration for Jenkins & Jira in VS Code |
| [JENKINS_PROMPTS.md](./JENKINS_PROMPTS.md) | Example Copilot prompts for Jenkins/Jira operations |
| [JENKINS_SETUP.md](./JENKINS_SETUP.md) | Detailed Jenkins installation & configuration guide |
| [JIRA_SETUP.md](./JIRA_SETUP.md) | Detailed Jira project setup & integration guide |

---

## 🔧 Configuration Checklist

### Jenkins Configuration
- [ ] Jenkins plugins installed (see JENKINS_SETUP.md)
- [ ] Git credentials added
- [ ] Jira credentials added (3 secrets)
- [ ] Pipeline job created
- [ ] Run manual test build
- [ ] Test results appear in UI
- [ ] Email notifications configured

### Jira Configuration
- [ ] SFDC project created
- [ ] API token generated
- [ ] Jira credentials added to Jenkins
- [ ] Jira plugin installed in Jenkins
- [ ] Connection test successful
- [ ] Test issue creation manually

### VS Code/Copilot Configuration
- [ ] .vscode/settings.json configured
- [ ] JENKINS_URL updated
- [ ] MCP server installed (`npm install -g @modelcontextprotocol/server-jenkins`)
- [ ] Environment variables set
- [ ] Test MCP connection with Copilot chat

---

## 🎯 Usage Examples

### Run Tests via Jenkins UI
```
1. Go to SalesforceAutomation job
2. "Build with Parameters"
3. Select ENVIRONMENT: staging
4. Check REPORT_TO_JIRA: true
5. Click "Build"
```

### Run Tests via Copilot
```
"@github Run SalesforceAutomation-Dev pipeline and report failures to Jira"
```

### Check Test Results
```
Copilot: "@github What are the test results from build #42?"
```

### Create Jira Issue
```
Copilot: "@github Create a Jira issue for test failure 'LoginTestInvalid'"
```

### Monitor Pipeline
```
Copilot: "@github Is SalesforceAutomation-Prod healthy? Show me last 5 builds"
```

---

## 📊 Pipeline Stages

```
1. Checkout        → Clone repository
2. Build          → Maven compile
3. Unit Tests     → Run TestNG suite
4. Test Results   → Parse and archive results
5. Report to Jira → Create issues for failures
6. Notifications  → Send email alerts
```

---

## 🔐 Environment Variables

Set these on your machine or in Jenkins:

```powershell
# Jenkins credentials (for MCP)
$env:JENKINS_USER = "your-jenkins-username"
$env:JENKINS_TOKEN = "your-jenkins-api-token"

# Jira credentials (for MCP & pipeline)
$env:JIRA_USER = "your-jira-email@example.com"
$env:JIRA_TOKEN = "your-jira-api-token"

# Jenkins build environment
$env:JAVA_HOME = "C:\Program Files\Java\jdk-11"
$env:MAVEN_HOME = "C:\maven"
```

---

## 🧪 Testing the Setup

### Test 1: Jenkins Connectivity
```powershell
# In Jenkins server
curl -u username:api-token http://localhost:8080/api/json
```

### Test 2: Git Repository
```powershell
# Verify Jenkinsfile exists
type .\Jenkinsfile
```

### Test 3: Maven Build
```powershell
cd "c:\Users\REDDY\AIVS CODE\java projects\SalesforceAutomation"
mvn clean compile
```

### Test 4: TestNG Tests
```powershell
mvn test
```

### Test 5: Jira Credentials
```powershell
$JIRA_TOKEN = "your-api-token"
$JIRA_USER = "your-email@example.com"
$JIRA_SITE = "https://your-instance.atlassian.net"

# Test Jira connection
$auth = [Convert]::ToBase64String([System.Text.Encoding]::UTF8.GetBytes("${JIRA_USER}:${JIRA_TOKEN}"))
curl -H "Authorization: Basic $auth" "${JIRA_SITE}/rest/api/3/myself"
```

### Test 6: MCP Connection
```
In VS Code Copilot:
"@github Connect to Jenkins MCP and list SalesforceAutomation jobs"
```

---

## 🚨 Troubleshooting

### Problem: Pipeline Won't Build

**Solution Checklist:**
```
1. Check Git credentials in Jenkins
2. Verify Jenkinsfile syntax: apache-groovy-dsl
3. Look at Console Output for errors
4. Ensure Maven can compile project

mvn clean compile -X  # Run with debug
```

### Problem: Tests Not Running

**Solution:**
```
Check:
- testng.xml exists and is valid
- Maven Surefire plugin configured
- Test classes in correct package: tests.LoginTestValid

Quick test:
mvn test -Dtest=LoginTestValid
```

### Problem: Jira Issues Not Created

**Solution:**
```
Check in Jenkins Console Output for curl error:
- Verify API token is valid
- Check Jira user has SFDC project access
- Ensure JIRA_SITE URL is correct
- Try curl command manually:

curl -X POST "${JIRA_SITE}/rest/api/3/issue" \
  -H "Authorization: Basic $(echo -n '${JIRA_USER}:${JIRA_TOKEN}' | base64)" \
  -H "Content-Type: application/json" \
  -d @issue.json
```

### Problem: MCP Not Responding

**Solution:**
```
1. Check MCP server is running:
   netstat -ano | findstr :3000  # or your MCP port

2. Verify .vscode/settings.json:
   - JENKINS_URL is correct
   - Credentials are set
   - MCP server command is correct

3. Check VS Code extension logs:
   View → Output → GitHub Copilot

4. Restart VS Code and MCP server
```

---

## 📚 Detailed Guides

For step-by-step instructions, see:

- **[JENKINS_SETUP.md](./JENKINS_SETUP.md)** - Complete Jenkins configuration
- **[JIRA_SETUP.md](./JIRA_SETUP.md)** - Complete Jira configuration
- **[JENKINS_PROMPTS.md](./JENKINS_PROMPTS.md)** - Copilot prompt examples
- **[Jenkinsfile](./Jenkinsfile)** - Pipeline code with inline comments

---

## 🔄 Workflow Examples

### Daily Automated Testing
```
1. Git push triggers Jenkins webhook
2. Pipeline runs: Build → Test → Report
3. If failures → Jira issues created
4. Email notification sent
5. Dev team sees issue in Jira
```

### Manual Test Run
```
1. Ask Copilot: "Run SalesforceAutomation tests in staging"
2. Copilot triggers Jenkins pipeline
3. You wait for results (5-10 min)
4. Failures automatically reported to Jira
5. You check SFDC project for issues
```

### Production Testing (Cautious)
```
1. Require manual build trigger (not automatic)
2. Always review results before closing issues
3. Use REPORT_TO_JIRA=true flag only for prod
4. Archive all test artifacts for 30 days
```

---

## 🔗 Integration Points

```
GitHub Copilot (VS Code)
        ↓
    MCP Server (Jenkins & Jira)
        ↓
Jenkins ← Pipeline ← Git Webhook
        ↓
  Run Tests (Maven + TestNG)
        ↓
  Parse Results
        ↓
  Report to Jira
        ↓
Jira Issues & Email Notifications
```

---

## 🎓 Learning Resources

- [Jenkins Documentation](https://www.jenkins.io/doc/)
- [Declarative Pipeline Syntax](https://www.jenkins.io/doc/book/pipeline/syntax/)
- [TestNG Documentation](https://testng.org/)
- [Jira REST API](https://developer.atlassian.com/cloud/jira/rest/v3/)
- [GitHub Copilot Documentation](https://docs.github.com/en/copilot)
- [Model Context Protocol (MCP)](https://modelcontextprotocol.io/)

---

## 📞 Support

For issues or questions:

1. **Jenkins Issues**: Check Console Output in Jenkins UI
2. **Jira Issues**: Check Jira logs and API responses
3. **Test Issues**: Run `mvn test` locally to debug
4. **MCP Issues**: Check VS Code output panel

---

## 🎉 Next Steps

After setup is complete:

1. ✅ Commit Jenkinsfile to repository
2. ✅ Set up GitHub webhook to auto-trigger builds
3. ✅ Create Jira dashboard for test failures
4. ✅ Schedule daily/weekly test runs
5. ✅ Train team on Copilot prompts
6. ✅ Monitor test failure trends
7. ✅ Optimize flaky tests

---

## 📋 Maintenance Tasks

### Weekly
- [ ] Review test failure trends in Jira
- [ ] Check for flaky tests
- [ ] Verify all pipelines are healthy

### Monthly
- [ ] Archive old test failure issues
- [ ] Review and update test data
- [ ] Check Jenkins logs for errors
- [ ] Update Jira project information

### Quarterly
- [ ] Review test coverage
- [ ] Optimize slow tests
- [ ] Update Maven/Java dependencies
- [ ] Review security credentials

---

## 📞 Quick Reference

| Task | Command/Action |
|------|----------------|
| Run tests locally | `mvn test` |
| Build project | `mvn clean compile` |
| Trigger Jenkins | Jenkins UI → Build with Parameters |
| Check Jira | Visit SFDC project → Filter by `test-failure` |
| View pipeline | `http://localhost:8080/job/SalesforceAutomation` |
| Use Copilot | "@github [your prompt]" in VS Code chat |
| Check credentials | Jenkins → Manage Credentials |
| View logs | Jenkins → Build → Console Output |

---

## ✅ Done!

You now have:
- ✅ Jenkinsfile with complete pipeline
- ✅ VS Code MCP configuration
- ✅ Jira integration setup
- ✅ Copilot prompt examples
- ✅ Setup guides for Jenkins and Jira
- ✅ Troubleshooting documentation

**Next:** Follow [JENKINS_SETUP.md](./JENKINS_SETUP.md) to configure your Jenkins server!
