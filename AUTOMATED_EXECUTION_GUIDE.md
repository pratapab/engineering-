# Automated Test Execution Guide
## Run Tests Automatically Without Manual Intervention

---

## 🚀 Overview

This guide explains how to set up **automatic test execution** in Jenkins so tests run without manual builds.

**Three automation options:**
1. ✅ **GitHub Webhook** - Runs tests on every git push
2. ✅ **Poll SCM** - Checks repository hourly for changes
3. ✅ **Scheduled Builds** - Runs tests daily at specific times

---

## Option 1: GitHub Webhook (Recommended)

### What It Does
- Tests run **automatically whenever you push code** to GitHub
- No manual action needed
- Real-time feedback on test failures

### Setup Steps

#### Step 1: Get Webhook URL
```
http://your-jenkins-server:8080/github-webhook/
```

#### Step 2: Add to GitHub
1. Go to GitHub repository → **Settings** → **Webhooks** → **Add webhook**
2. Enter:
   ```
   Payload URL: http://your-jenkins-server:8080/github-webhook/
   Content type: application/json
   Events: Just the push event
   Active: ☑ (checked)
   ```
3. Click **Add webhook**

#### Step 3: Enable in Jenkins Job
1. Go to Jenkins → **SalesforceAutomation** job → **Configure**
2. Under **Build Triggers**, check: ☑ **GitHub hook trigger for GITScm polling**
3. Click **Save**

#### Step 4: Test
```powershell
# Make a small change to a file and push
git add .
git commit -m "Test webhook"
git push

# Jenkins will automatically trigger a build!
# Monitor at: http://localhost:8080/job/SalesforceAutomation/
```

---

## Option 2: Poll SCM (Hourly Check)

### What It Does
- Jenkins checks GitHub **every hour** for new commits
- If changes found, tests run automatically
- Good backup if webhook fails

### Setup Steps

#### Step 1: Enable in Jenkins
1. Go to Jenkins → **SalesforceAutomation** → **Configure**
2. Under **Build Triggers**, check: ☑ **Poll SCM**
3. Enter Schedule:
   ```
   H H * * *
   ```
   (Runs once per hour)
4. Click **Save**

#### Step 2: Custom Schedules

**Multiple times per day:**
```
H H/4 * * *    # Every 4 hours
H H/2 * * *    # Every 2 hours
H * * * *      # Every hour
```

**Specific times:**
```
H 2 * * *      # Daily at 2 AM
H 18 * * 1-5   # Weekdays at 6 PM
0 8,16 * * *   # 8 AM and 4 PM
```

**Every 15 minutes:**
```
H/15 * * * *
```

#### Step 3: Test
```powershell
# Push a change
git add .
git commit -m "Test poll SCM"
git push

# Wait up to 1 hour, or Jenkins will run at next scheduled time
```

---

## Option 3: Scheduled Daily Build

### What It Does
- Tests run at a **scheduled time every day**
- Great for nightly regression testing
- Independent of code changes

### Setup Steps

#### Step 1: Enable in Jenkins
1. Go to Jenkins → **SalesforceAutomation** → **Configure**
2. Under **Build Triggers**, check: ☑ **Build periodically**
3. Enter Schedule:
   ```
   H 2 * * *
   ```
   (Runs daily at 2 AM)
4. Click **Save**

#### Step 2: Schedule Examples

**Daily:**
```
H 2 * * *      # Daily at random hour with 2 base hour
0 2 * * *      # Daily at exactly 2:00 AM
0 3 * * *      # Daily at exactly 3:00 AM
```

**Weekdays only:**
```
0 8 * * 1-5    # Weekdays at 8 AM
0 16 * * 1-5   # Weekdays at 4 PM
```

**Multiple times:**
```
0 8,14,20 * * * # At 8 AM, 2 PM, 8 PM
H H/6 * * *     # Every 6 hours
```

**Weekly:**
```
0 2 * * 0      # Every Sunday at 2 AM
0 2 * * 1      # Every Monday at 2 AM
```

---

## Combined Setup (Recommended)

Use **all three methods** for maximum reliability and coverage:

```
1. GitHub Webhook
   └─ Triggers on every push
   └─ Instant feedback

2. Poll SCM (H H * * *)
   └─ Hourly check as backup
   └─ Catches webhook failures

3. Scheduled Build (H 2 * * *)
   └─ Daily full regression test
   └─ Scheduled maintenance
```

### Configuration in Jenkins

1. Go to **SalesforceAutomation** → **Configure**
2. Under **Build Triggers**, enable all three:
   ```
   ☑ GitHub hook trigger for GITScm polling
   ☑ Poll SCM
      Schedule: H H * * *
   ☑ Build periodically
      Schedule: H 2 * * *
   ```
3. Click **Save**

---

## Running Tests Automatically via Script

### PowerShell: Trigger Build Remotely

```powershell
# Set variables
$JenkinsUrl = "http://localhost:8080"
$JobName = "SalesforceAutomation"
$JenkinsUser = "your-username"
$JenkinsToken = "your-api-token"

# Create auth header
$pair = "$JenkinsUser`:$JenkinsToken"
$bytes = [System.Text.Encoding]::ASCII.GetBytes($pair)
$base64 = [System.Convert]::ToBase64String($bytes)
$authHeader = "Basic $base64"

# Trigger build with parameters
$url = "$JenkinsUrl/job/$JobName/buildWithParameters?ENVIRONMENT=dev&REPORT_TO_JIRA=true"

Invoke-RestMethod -Uri $url `
    -Method POST `
    -Headers @{"Authorization" = $authHeader}
```

### Bash: Trigger Build Remotely

```bash
#!/bin/bash
JENKINS_URL="http://localhost:8080"
JOB_NAME="SalesforceAutomation"
JENKINS_USER="your-username"
JENKINS_TOKEN="your-api-token"

curl -X POST \
  -u "$JENKINS_USER:$JENKINS_TOKEN" \
  "$JENKINS_URL/job/$JOB_NAME/buildWithParameters?ENVIRONMENT=dev&REPORT_TO_JIRA=true"
```

### Automated Trigger Example (Windows Task Scheduler)

```powershell
# Create a scheduled task to trigger builds every morning
$trigger = New-ScheduledTaskTrigger -Daily -At 2:00AM
$action = New-ScheduledTaskAction -ScriptBlock {
    # Trigger Jenkins build
    & "C:\path\to\trigger-jenkins-build.ps1"
}
Register-ScheduledTask -TaskName "SalesforceAutomationDailyTest" `
    -Trigger $trigger -Action $action -RunLevel Highest
```

---

## Automated Setup Script

### Run Auto-Setup Script

```powershell
# Set Jenkins credentials
$env:JENKINS_USER = "your-username"
$env:JENKINS_TOKEN = "your-api-token"

# Run setup script
& ".\setup-jenkins-auto.ps1" `
    -JenkinsUrl "http://localhost:8080" `
    -JenkinsUser $env:JENKINS_USER `
    -JenkinsToken $env:JENKINS_TOKEN `
    -GitRepository "https://github.com/your-org/SalesforceAutomation.git"
```

**The script will:**
- ✅ Verify Jenkins connection
- ✅ Check Git credentials
- ✅ Create pipeline job
- ✅ Display webhook URL
- ✅ Provide setup instructions
- ✅ Trigger first test build

---

## Monitoring Automatic Builds

### View Build History
```
http://localhost:8080/job/SalesforceAutomation/
```

### View Console Output (Real-time)
```
http://localhost:8080/job/SalesforceAutomation/lastBuild/console
```

### Check Build Status
```powershell
# Get latest build info
$authHeader = "Basic $(
    [Convert]::ToBase64String(
        [Text.Encoding]::ASCII.GetBytes("user:token")
    )
)"

Invoke-RestMethod -Uri "http://localhost:8080/job/SalesforceAutomation/lastBuild/api/json" `
    -Headers @{ "Authorization" = $authHeader }
```

---

## Email Notifications on Automatic Builds

### Configure Email Settings

1. **Jenkins** → **Manage Jenkins** → **Configure System**
2. Find **Email Notification** section:
   ```
   SMTP server: smtp.gmail.com
   Default user e-mail suffix: @gmail.com
   ```
3. Update **Post-build Actions** in Jenkinsfile:
   ```groovy
   post {
       always {
           emailext(
               subject: "Build #${BUILD_NUMBER} - ${BUILD_STATUS}",
               body: "Test results: ${BUILD_URL}",
               to: "dev-team@example.com"
           )
       }
   }
   ```

---

## Troubleshooting Automatic Builds

### Problem: Webhook Not Triggering

**Check:**
1. GitHub webhook is active (green checkmark)
2. Jenkins has webhook plugin installed
3. Jenkins URL is accessible from GitHub
4. "GitHub hook trigger" is enabled in job

**Fix:**
```
1. GitHub → Settings → Webhooks → Click webhook
2. Click "Redeliver" to test webhook
3. Check "Recent Deliveries" for errors
4. If error: update Jenkins job configuration
```

### Problem: Poll SCM Not Working

**Check:**
1. Poll SCM schedule is enabled
2. Schedule syntax is correct
3. Git credentials are working
4. Repository URL is correct

**Fix:**
```
1. Jenkins → SalesforceAutomation → Configure
2. Under "Source Code Management" → Git → Validate credentials
3. Check poll log: Jenkins → SalesforceAutomation → Polling Log
```

### Problem: Scheduled Build Not Running

**Check:**
1. Time zone is correct
2. Schedule syntax is valid (use cron format)
3. Job is not disabled
4. Jenkins system time is correct

**Fix:**
```
1. Jenkins → Manage Jenkins → System Information → Check time
2. Validate cron syntax: https://crontab.guru
3. Check Jenkins logs: Jenkins → Manage Jenkins → System Log
```

---

## Best Practices

### 1. Use GitHub Webhook + Poll SCM
```
Primary: GitHub Webhook (immediate feedback)
Backup: Poll SCM (if webhook fails)
```

### 2. Schedule Nightly Builds for Full Regression
```
Daily at 2 AM: Full test suite
On every push: Critical tests only
```

### 3. Set Up Notifications
```groovy
post {
    failure {
        emailext(
            to: "${change.author.email}",
            subject: "Test Failed: Build #${BUILD_NUMBER}",
            body: "Fix: ${BUILD_URL}/changes"
        )
    }
}
```

### 4. Monitor Build Queue
```
Jenkins → Build Queue → No more than 5 builds queued
```

### 5. Archive Test Results
```groovy
junit testResults: 'target/surefire-reports/TEST-*.xml'
archiveArtifacts artifacts: 'target/**/*.html'
```

---

## Quick Reference: Enable All Triggers

### Jenkins UI Steps
1. SalesforceAutomation → Configure
2. Build Triggers:
   ```
   ☑ GitHub hook trigger for GITScm polling
   
   ☑ Poll SCM
     Schedule: H H * * *
   
   ☑ Build periodically
     Schedule: H 2 * * *
   ```
3. Save

### Result
```
Git Push → GitHub Webhook → Immediate build ✓
Hourly → Poll SCM → Automatic build if changes ✓
Daily 2AM → Scheduled → Nightly regression test ✓
```

---

## Next Steps

1. ✅ Choose automation method (webhook, polling, or both)
2. ✅ Run `setup-jenkins-auto.ps1` script
3. ✅ Configure Jenkins job with triggers
4. ✅ Set up GitHub webhook
5. ✅ Configure email notifications
6. ✅ Test automatic build triggered by git push
7. ✅ Monitor builds in Jenkins UI
8. ✅ Check Jira for automatic issue creation

---

## Command Reference

| Task | Command |
|------|---------|
| Trigger build manually | See PowerShell/Bash scripts above |
| View build history | `http://localhost:8080/job/SalesforceAutomation/` |
| View last build output | `http://localhost:8080/job/SalesforceAutomation/lastBuild/console` |
| Get build status | REST API: `/job/SalesforceAutomation/lastBuild/api/json` |
| Disable job | Jenkins UI → Job → Disable |
| Enable job | Jenkins UI → Job → Enable |
| Delete job | Jenkins UI → Job → Delete |
| Clear build history | Jenkins UI → Job → Clear Build History |

---

## Summary

✅ **Automatic Execution Methods:**
- GitHub Webhook: Runs on every push
- Poll SCM: Runs hourly
- Scheduled Build: Runs daily

✅ **Setup Time:**
- Quick: 5 minutes (basic webhook)
- Complete: 15 minutes (all three methods)

✅ **No Manual Action Needed:**
- Tests run automatically
- Jira issues created automatically  
- Notifications sent automatically
- Results tracked in Jenkins and Jira

---

**You're now ready for fully automated testing!** 🎉
