# Quick Start: Automatic Jenkins Execution (5 Minutes)

## 🎯 Goal
Run tests automatically without manual Jenkins builds

---

## ⚡ Quick Setup (Choose One)

### Option A: GitHub Webhook (Automatic on Push)
```
1. Copy your Jenkins webhook URL:
   http://localhost:8080/github-webhook/

2. Go to GitHub → Repository → Settings → Webhooks → Add webhook
   - Payload URL: [paste webhook URL above]
   - Content type: application/json
   - Events: Just the push event
   - Active: ☑

3. Jenkins job will now run automatically on every push!
```

### Option B: Scheduled Daily Build (Automatic at Set Time)
```
1. Go to Jenkins → SalesforceAutomation → Configure

2. Build Triggers → Check: ☑ Build periodically
   Schedule: H 2 * * * (runs daily at 2 AM)

3. Save

4. Tests will run automatically every night!
```

### Option C: Hourly Check (Automatic if Changes Detected)
```
1. Go to Jenkins → SalesforceAutomation → Configure

2. Build Triggers → Check: ☑ Poll SCM
   Schedule: H H * * * (checks every hour)

3. Save

4. Tests will run automatically if you push changes!
```

---

## 🚀 Quick Trigger (Manual, No UI)

### PowerShell (Windows)
```powershell
$env:JENKINS_USER = "admin"
$env:JENKINS_TOKEN = "your-api-token"

& ".\trigger-build.ps1" -Environment dev
```

### Bash (Linux/Mac)
```bash
export JENKINS_USER="admin"
export JENKINS_TOKEN="your-api-token"

bash trigger-build.sh dev
```

### Direct curl Command
```bash
curl -X POST -u "admin:your-api-token" \
  "http://localhost:8080/job/SalesforceAutomation/buildWithParameters?ENVIRONMENT=dev&REPORT_TO_JIRA=true"
```

---

## 📋 Automatic Execution Checklist

- [ ] Jenkins installed and running: `http://localhost:8080`
- [ ] Git credentials added to Jenkins
- [ ] Jira credentials added to Jenkins
- [ ] Jenkinsfile committed to repository (`/Jenkinsfile`)
- [ ] Choose automation method (webhook/polling/schedule)
- [ ] Test: Push change or manually trigger build
- [ ] Verify: Check Jenkins console output
- [ ] Verify: Check Jira for test failure issues

---

## 🔄 How It Works

```
┌─────────────────────────────────────┐
│  Option A: GitHub Webhook           │
│  (You: git push)                    │
└──────────────┬──────────────────────┘
               ↓
┌─────────────────────────────────────┐
│  GitHub notifies Jenkins            │
└──────────────┬──────────────────────┘
               ↓
┌─────────────────────────────────────┐
│  Jenkins: Trigger Build             │
│  - Checkout code                    │
│  - Compile & Test                   │
│  - Report to Jira                   │
└──────────────┬──────────────────────┘
               ↓
┌─────────────────────────────────────┐
│  Results:                           │
│  - Test Report in Jenkins           │
│  - Jira Issues Created              │
│  - Email Notification Sent          │
└─────────────────────────────────────┘
```

---

## 📊 Recommended Setup

**Use all three for maximum reliability:**

1. **GitHub Webhook** (Primary)
   - Instant feedback on push
   - Tests run within seconds

2. **Poll SCM** (Backup)
   - Check every hour
   - Catches webhook failures

3. **Scheduled Build** (Regression)
   - Full test suite nightly
   - 2 AM for low load

---

## 🐛 Troubleshooting

### "Build not triggering on push"
```
Check:
1. GitHub webhook shows green checkmark
2. Jenkins job: Build Triggers → GitHub hook enabled
3. Jenkins is accessible from GitHub (firewall?)
4. Try: GitHub webhook → Recent Deliveries → Redeliver
```

### "Poll SCM not working"
```
Check:
1. Jenkins job → Configure → Poll SCM is checked
2. Schedule syntax is valid: H H * * *
3. Git credentials are working
4. Run: Jenkins → SalesforceAutomation → Poll Log
```

### "Build runs but tests fail"
```
Check:
1. Console output: Jenkins → job → build # → Console Output
2. Test results: Jenkins → job → Test Result
3. Check Jira for test failure issues
4. Run locally: mvn test -Denv=dev
```

---

## 📁 Files Created

| File | Purpose |
|------|---------|
| `setup-jenkins-auto.ps1` | Auto-configure Jenkins (run once) |
| `trigger-build.ps1` | Manually trigger builds (Windows) |
| `trigger-build.sh` | Manually trigger builds (Linux/Mac) |
| `AUTOMATED_EXECUTION_GUIDE.md` | Detailed automation guide |

---

## 🎯 Test It Now

### Step 1: Ensure Jenkins is Running
```powershell
# Check Jenkins is accessible
curl.exe http://localhost:8080
```

### Step 2: Run Setup Script (One Time)
```powershell
$env:JENKINS_USER = "admin"
$env:JENKINS_TOKEN = "your-api-token"

.\setup-jenkins-auto.ps1
```

### Step 3: Add Credentials to Jenkins
```
Jenkins → Manage Jenkins → Manage Credentials → Add:
- jira-site-url
- jira-username
- jira-api-token
- github-ssh (if not already there)
```

### Step 4: Trigger First Build
```powershell
.\trigger-build.ps1 -Environment dev
```

### Step 5: Monitor
```
Jenkins UI: http://localhost:8080/job/SalesforceAutomation/
Watch for: "Build scheduled..." → "Running..." → "Success/Failure"
```

---

## 📚 Next Steps

1. ✅ **Read**: `AUTOMATED_EXECUTION_GUIDE.md` for detailed options
2. ✅ **Run**: `setup-jenkins-auto.ps1` to configure Jenkins
3. ✅ **Add**: Jira credentials to Jenkins
4. ✅ **Set**: GitHub webhook OR configure Poll SCM
5. ✅ **Test**: Push code or use `trigger-build.ps1`
6. ✅ **Verify**: Tests run automatically
7. ✅ **Monitor**: Check Jenkins and Jira for results

---

## 💡 Pro Tips

### Disable Manual UI Builds
If you want ONLY automatic builds:
```
Jenkins → SalesforceAutomation → Configure
→ Disable "Build when a change is pushed to GitHub"
```

### Rollback to Manual
```powershell
# Always can manually trigger
.\trigger-build.ps1 -Environment staging
```

### Track Build Progress
```powershell
# Quick status check
curl -s -u "admin:token" http://localhost:8080/job/SalesforceAutomation/lastBuild/api/json | 
  Select-Object @{N='Status';E={$_.result}}, @{N='Duration';E={$_.duration}}
```

---

## 🎉 You're Done!

Your tests are now **set up for automatic execution** in Jenkins!

- ✅ Tests run automatically on code push
- ✅ Test failures reported to Jira
- ✅ No manual builds needed
- ✅ Real-time feedback

**Enjoy automated testing!** 🚀
