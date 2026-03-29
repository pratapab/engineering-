# ============================================================
# TRIGGER JENKINS BUILD SCRIPT (Windows PowerShell)
# Usage: .\trigger-build.ps1 [dev|staging|production]
# ============================================================

param(
    [ValidateSet("dev", "staging", "production")]
    [string]$Environment = "dev",
    
    [ValidateSet("true", "false")]
    [string]$ReportToJira = "true",
    
    [string]$JenkinsUrl = $env:JENKINS_URL -or "http://localhost:8080",
    [string]$JenkinsUser = $env:JENKINS_USER -or "admin",
    [string]$JenkinsToken = $env:JENKINS_TOKEN -or "your-api-token"
)

$JobName = "SalesforceAutomation"

# Function to get Basic Auth header
function Get-BasicAuthHeader {
    param([string]$User, [string]$Token)
    $pair = "$User`:$Token"
    $bytes = [System.Text.Encoding]::ASCII.GetBytes($pair)
    $base64 = [System.Convert]::ToBase64String($bytes)
    return "Basic $base64"
}

Write-Host "==========================================" -ForegroundColor Cyan
Write-Host "🚀 TRIGGERING JENKINS BUILD" -ForegroundColor Cyan
Write-Host "==========================================" -ForegroundColor Cyan
Write-Host "Jenkins URL: $JenkinsUrl" -ForegroundColor Green
Write-Host "Job Name: $JobName" -ForegroundColor Green
Write-Host "Environment: $Environment" -ForegroundColor Green
Write-Host "Report to Jira: $ReportToJira" -ForegroundColor Green
Write-Host ""

# Build trigger URL
$TriggerUrl = "$JenkinsUrl/job/$JobName/buildWithParameters"
$TriggerUrl += "?ENVIRONMENT=$Environment&REPORT_TO_JIRA=$ReportToJira"

try {
    # Trigger build
    Write-Host "📡 Sending build request..." -ForegroundColor Yellow
    
    $authHeader = Get-BasicAuthHeader -User $JenkinsUser -Token $JenkinsToken
    
    $response = Invoke-WebRequest -Uri $TriggerUrl `
        -Method POST `
        -Headers @{ "Authorization" = $authHeader } `
        -ErrorAction Stop
    
    if ($response.StatusCode -eq 201 -or $response.StatusCode -eq 200) {
        Write-Host "✅ Build triggered successfully!" -ForegroundColor Green
        Write-Host ""
        Write-Host "Queue URL: $JenkinsUrl/queue" -ForegroundColor Cyan
        Write-Host "Job URL: $JenkinsUrl/job/$JobName" -ForegroundColor Cyan
        Write-Host ""
        Write-Host "🔍 Monitoring build..." -ForegroundColor Yellow
        
        # Wait and get build number
        Start-Sleep -Seconds 3
        
        try {
            $buildJson = Invoke-RestMethod `
                -Uri "$JenkinsUrl/job/$JobName/lastBuild/api/json" `
                -Headers @{ "Authorization" = $authHeader }
            
            $buildNumber = $buildJson.number
            Write-Host "Build #$buildNumber is running..." -ForegroundColor Green
            Write-Host "Console: $JenkinsUrl/job/$JobName/$buildNumber/console" -ForegroundColor Cyan
        } catch {
            Write-Host "⏳ Build queued. Check Jenkins UI for progress." -ForegroundColor Yellow
        }
    }
} catch {
    Write-Host "❌ Failed to trigger build!" -ForegroundColor Red
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "Done! 🎉" -ForegroundColor Green
