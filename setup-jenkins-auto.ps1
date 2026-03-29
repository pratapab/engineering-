# ============================================================
# AUTOMATED JENKINS SETUP SCRIPT
# Purpose: Automatically create and configure the Jenkins job
# ============================================================

param(
    [string]$JenkinsUrl = "http://localhost:8080",
    [string]$JenkinsUser = $env:JENKINS_USER,
    [string]$JenkinsToken = $env:JENKINS_TOKEN,
    [string]$GitRepository = "https://github.com/your-org/SalesforceAutomation.git",
    [string]$GitCredentialsId = "github-ssh"
)

# Function to encode credentials for Basic Auth
function Get-BasicAuthHeader {
    param([string]$User, [string]$Token)
    $pair = "$User`:$Token"
    $bytes = [System.Text.Encoding]::ASCII.GetBytes($pair)
    $base64 = [System.Convert]::ToBase64String($bytes)
    return "Basic $base64"
}

# Function to make Jenkins API calls
function Invoke-JenkinsApi {
    param(
        [string]$Method,
        [string]$Endpoint,
        [string]$Body,
        [string]$ContentType = "application/json"
    )
    
    $authHeader = Get-BasicAuthHeader -User $JenkinsUser -Token $JenkinsToken
    $url = "$JenkinsUrl$Endpoint"
    
    $headers = @{
        "Authorization" = $authHeader
        "Content-Type" = $ContentType
    }
    
    try {
        if ($Method -eq "GET") {
            $response = Invoke-RestMethod -Uri $url -Method $Method -Headers $headers
        } else {
            $response = Invoke-RestMethod -Uri $url -Method $Method -Headers $headers -Body $Body
        }
        return $response
    } catch {
        Write-Host "❌ Error: $_"
        return $null
    }
}

# ============================================================
# STEP 1: Verify Jenkins Connection
# ============================================================
Write-Host "=== Step 1: Verifying Jenkins Connection ===" -ForegroundColor Cyan

try {
    $jenkinsInfo = Invoke-JenkinsApi -Method "GET" -Endpoint "/api/json"
    if ($jenkinsInfo) {
        Write-Host "✓ Jenkins is running at $JenkinsUrl" -ForegroundColor Green
        Write-Host "  Version: $($jenkinsInfo.version)" -ForegroundColor Green
    }
} catch {
    Write-Host "❌ Cannot connect to Jenkins at $JenkinsUrl" -ForegroundColor Red
    Write-Host "   Please ensure Jenkins is running and credentials are correct" -ForegroundColor Red
    exit 1
}

# ============================================================
# STEP 2: Check/Create Git Credentials
# ============================================================
Write-Host "`n=== Step 2: Checking Git Credentials ===" -ForegroundColor Cyan

try {
    $creds = Invoke-JenkinsApi -Method "GET" -Endpoint "/credentials/store/system/domain/_/credential/$GitCredentialsId/api/json"
    if ($creds) {
        Write-Host "✓ Git credentials already exist: $GitCredentialsId" -ForegroundColor Green
    }
} catch {
    Write-Host "⚠ Git credentials not found. You'll need to add them manually:" -ForegroundColor Yellow
    Write-Host "  1. Jenkins → Manage Credentials → System → Global credentials" -ForegroundColor Yellow
    Write-Host "  2. Add Credentials (SSH Key or Username/Password)" -ForegroundColor Yellow
    Write-Host "  3. Use ID: $GitCredentialsId" -ForegroundColor Yellow
}

# ============================================================
# STEP 3: Create Pipeline Job Configuration
# ============================================================
Write-Host "`n=== Step 3: Creating Pipeline Job ===" -ForegroundColor Cyan

$jobConfig = @"
<?xml version="1.1" encoding="UTF-8"?>
<org.jenkinsci.plugins.workflow.job.WorkflowJob plugin="workflow-job@1272.v47038e79fbf7">
  <actions/>
  <description>Automated test suite for Salesforce using Selenium and TestNG</description>
  <keepDependencies>false</keepDependencies>
  <properties>
    <hudson.model.ParametersDefinitionProperty>
      <parameterDefinitions>
        <hudson.model.ChoiceParameterDefinition>
          <name>ENVIRONMENT</name>
          <description>Target environment for testing</description>
          <choices class="java.util.Arrays\$ArrayList">
            <a>
              <int>3</int>
              <java.lang.String>dev</java.lang.String>
              <java.lang.String>staging</java.lang.String>
              <java.lang.String>production</java.lang.String>
            </a>
          </choices>
          <defaultValue>dev</defaultValue>
        </hudson.model.ChoiceParameterDefinition>
        <hudson.model.BooleanParameterDefinition>
          <name>REPORT_TO_JIRA</name>
          <description>Report test failures to Jira</description>
          <defaultValue>true</defaultValue>
        </hudson.model.BooleanParameterDefinition>
      </parameterDefinitions>
    </hudson.model.ParametersDefinitionProperty>
    <com.cloudbees.plugins.credentials.CredentialsProvider_-StoreAction>
      <stopOnError>false</stopOnError>
    </com.cloudbees.plugins.credentials.CredentialsProvider_-StoreAction>
    <org.jenkinsci.plugins.workflow.job.properties.PipelineTriggersJobProperty>
      <triggers>
        <com.cloudbees.jenkins.plugins.kubernetes.event.GlobalDefaultPodTemplateEventListener>
          <excludedNamespaces/>
        </com.cloudbees.jenkins.plugins.kubernetes.event.GlobalDefaultPodTemplateEventListener>
      </triggers>
    </org.jenkinsci.plugins.workflow.job.properties.PipelineTriggersJobProperty>
  </properties>
  <definition class="org.jenkinsci.plugins.workflow.cps.CpsScm" plugin="workflow-cps@3133.vf9cda1a22c2f">
    <scm class="hudson.plugins.git.GitSCM" plugin="git@4.8.3">
      <configVersion>2</configVersion>
      <userRemoteConfigs>
        <hudson.plugins.git.UserRemoteConfig>
          <url>$GitRepository</url>
          <credentialsId>$GitCredentialsId</credentialsId>
        </hudson.plugins.git.UserRemoteConfig>
      </userRemoteConfigs>
      <branches>
        <hudson.plugins.git.BranchSpec>
          <name>*/main</name>
        </hudson.plugins.git.BranchSpec>
      </branches>
      <doGenerateSubmoduleConfigurations>false</doGenerateSubmoduleConfigurations>
      <submoduleCfg class="list"/>
      <extensions/>
    </scm>
    <scriptPath>Jenkinsfile</scriptPath>
  </definition>
  <triggers/>
  <disabled>false</disabled>
</org.jenkinsci.plugins.workflow.job.WorkflowJob>
"@

try {
    $encodedConfig = [System.Text.Encoding]::UTF8.GetBytes($jobConfig)
    
    $response = Invoke-RestMethod `
        -Uri "$JenkinsUrl/createItem?name=SalesforceAutomation" `
        -Method POST `
        -Headers @{
            "Authorization" = Get-BasicAuthHeader -User $JenkinsUser -Token $JenkinsToken
            "Content-Type" = "application/xml"
        } `
        -Body $jobConfig
    
    Write-Host "✓ Pipeline job 'SalesforceAutomation' created successfully" -ForegroundColor Green
} catch {
    if ($_.Exception.Message -like "*already exists*" -or $_.Exception.Response.StatusCode -eq 400) {
        Write-Host "ℹ Job 'SalesforceAutomation' already exists" -ForegroundColor Yellow
    } else {
        Write-Host "⚠ Could not create job (may need manual configuration): $_" -ForegroundColor Yellow
    }
}

# ============================================================
# STEP 4: Enable GitHub Webhook Trigger
# ============================================================
Write-Host "`n=== Step 4: Setting up GitHub Webhook Trigger ===" -ForegroundColor Cyan
Write-Host "Webhook URL (add to GitHub): $JenkinsUrl/github-webhook/" -ForegroundColor Cyan
Write-Host "`nSteps to enable automatic builds on git push:" -ForegroundColor Cyan
Write-Host "  1. Go to GitHub repository: Settings → Webhooks → Add webhook" -ForegroundColor Cyan
Write-Host "  2. Payload URL: $JenkinsUrl/github-webhook/" -ForegroundColor Cyan
Write-Host "  3. Content type: application/json" -ForegroundColor Cyan
Write-Host "  4. Events: Just the push event" -ForegroundColor Cyan
Write-Host "  5. Active: ☑ (checked)" -ForegroundColor Cyan

# ============================================================
# STEP 5: Enable Build Polling
# ============================================================
Write-Host "`n=== Step 5: Setting up Poll SCM (Backup Trigger) ===" -ForegroundColor Cyan
Write-Host "Will check repository every hour for changes" -ForegroundColor Cyan
Write-Host "Jenkins UI: SalesforceAutomation → Configure → Build Triggers → Poll SCM" -ForegroundColor Cyan
Write-Host "Schedule: H H * * * (hourly)" -ForegroundColor Cyan

# ============================================================
# STEP 6: Create Scheduled Daily Build
# ============================================================
Write-Host "`n=== Step 6: Setting up Scheduled Daily Builds ===" -ForegroundColor Cyan
Write-Host "Configure in Jenkins UI:" -ForegroundColor Cyan
Write-Host "  1. SalesforceAutomation → Configure → Build Triggers → Build periodically" -ForegroundColor Cyan
Write-Host "  2. Schedule: H 2 * * * (Daily at 2 AM)" -ForegroundColor Cyan

# ============================================================
# STEP 7: Verify Jira Credentials
# ============================================================
Write-Host "`n=== Step 7: Verifying Jira Configuration ===" -ForegroundColor Cyan

Write-Host "Required Jira credentials in Jenkins:" -ForegroundColor Cyan
Write-Host "  1. jira-site-url (Secret text)" -ForegroundColor Cyan
Write-Host "  2. jira-username (Secret text)" -ForegroundColor Cyan
Write-Host "  3. jira-api-token (Secret text)" -ForegroundColor Cyan

Write-Host "`nTo add them:" -ForegroundColor Cyan
Write-Host "  Jenkins → Manage Credentials → System → Global credentials → Add Credentials" -ForegroundColor Cyan

# ============================================================
# STEP 8: Test the Pipeline
# ============================================================
Write-Host "`n=== Step 8: Triggering Test Build ===" -ForegroundColor Cyan

$triggerUrl = "$JenkinsUrl/job/SalesforceAutomation/buildWithParameters?ENVIRONMENT=dev&REPORT_TO_JIRA=true"

Write-Host "Choose how to start testing:" -ForegroundColor Cyan
Write-Host "`n1. VIA COMMAND LINE:" -ForegroundColor Green
Write-Host "   `$authHeader = '$(Get-BasicAuthHeader -User $JenkinsUser -Token $JenkinsToken)'" -ForegroundColor Gray
Write-Host "   Invoke-RestMethod -Uri `"$triggerUrl`" -Method POST -Headers @{'Authorization'=`$authHeader}" -ForegroundColor Gray

Write-Host "`n2. VIA WEB UI:" -ForegroundColor Green
Write-Host "   Visit: $JenkinsUrl/job/SalesforceAutomation" -ForegroundColor Gray
Write-Host "   Click: Build with Parameters" -ForegroundColor Gray
Write-Host "   Select: ENVIRONMENT = dev, REPORT_TO_JIRA = true" -ForegroundColor Gray
Write-Host "   Click: Build" -ForegroundColor Gray

Write-Host "`n3. VIA CURL:" -ForegroundColor Green
Write-Host "   curl -X POST -u `"$JenkinsUser`:\$JENKINS_TOKEN`" `"$triggerUrl`"" -ForegroundColor Gray

# ============================================================
# STEP 9: Verify Email Notifications
# ============================================================
Write-Host "`n=== Step 9: Email Notification Setup ===" -ForegroundColor Cyan
Write-Host "Configure email settings in Jenkins:" -ForegroundColor Cyan
Write-Host "  Jenkins → Manage Jenkins → Configure System → Email Notification" -ForegroundColor Cyan
Write-Host "  Set SMTP server and default recipients" -ForegroundColor Cyan

# ============================================================
# COMPLETION SUMMARY
# ============================================================
Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "  ✅ JENKINS AUTOMATION SETUP COMPLETE" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan

Write-Host "`n📋 QUICK CHECKLIST:" -ForegroundColor Green
Write-Host "  ☐ Add Git credentials to Jenkins (if not already done)" -ForegroundColor Yellow
Write-Host "  ☐ Add Jira credentials to Jenkins" -ForegroundColor Yellow
Write-Host "  ☐ Set up GitHub webhook" -ForegroundColor Yellow
Write-Host "  ☐ Configure email notifications" -ForegroundColor Yellow
Write-Host "  ☐ Trigger first test build" -ForegroundColor Yellow
Write-Host "  ☐ Verify test results in Jenkins" -ForegroundColor Yellow
Write-Host "  ☐ Check Jira for test failure issues" -ForegroundColor Yellow

Write-Host "`n🔗 JENKINS JOB URL:" -ForegroundColor Green
Write-Host "  $JenkinsUrl/job/SalesforceAutomation" -ForegroundColor Cyan

Write-Host "`n📚 NEXT STEPS:" -ForegroundColor Green
Write-Host "  1. Read: JENKINS_SETUP.md (Section 2: Configure Credentials)" -ForegroundColor Yellow
Write-Host "  2. Read: JIRA_SETUP.md (Section 3: Add Jira Credentials)" -ForegroundColor Yellow
Write-Host "  3. Trigger a build and verify it works" -ForegroundColor Yellow
Write-Host "  4. Set up GitHub webhook for automatic builds" -ForegroundColor Yellow

Write-Host "`n💡 AUTOMATION OPTIONS:" -ForegroundColor Green
Write-Host "  • GitHub Webhook → Automatic on every git push" -ForegroundColor Cyan
Write-Host "  • Poll SCM → Check repository hourly for changes" -ForegroundColor Cyan
Write-Host "  • Scheduled Build → Run tests daily at 2 AM" -ForegroundColor Cyan
Write-Host "  • Manual Trigger → Build with custom parameters" -ForegroundColor Cyan

Write-Host "`n" -ForegroundColor Green
