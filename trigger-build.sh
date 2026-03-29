#!/bin/bash
# ============================================================
# TRIGGER JENKINS BUILD SCRIPT (Linux/Mac)
# Usage: ./trigger-build.sh [dev|staging|production]
# ============================================================

set -e

# Configuration
JENKINS_URL="${JENKINS_URL:-http://localhost:8080}"
JENKINS_USER="${JENKINS_USER:-admin}"
JENKINS_TOKEN="${JENKINS_TOKEN:-your-api-token}"
JOB_NAME="SalesforceAutomation"

# Parameters
ENVIRONMENT="${1:-dev}"
REPORT_TO_JIRA="${2:-true}"

# Validate environment
if [[ "$ENVIRONMENT" != "dev" && "$ENVIRONMENT" != "staging" && "$ENVIRONMENT" != "production" ]]; then
    echo "❌ Invalid environment: $ENVIRONMENT"
    echo "Usage: $0 [dev|staging|production] [true|false]"
    exit 1
fi

# Trigger URL
TRIGGER_URL="${JENKINS_URL}/job/${JOB_NAME}/buildWithParameters?ENVIRONMENT=${ENVIRONMENT}&REPORT_TO_JIRA=${REPORT_TO_JIRA}"

echo "=========================================="
echo "🚀 TRIGGERING JENKINS BUILD"
echo "=========================================="
echo "Jenkins URL: $JENKINS_URL"
echo "Job Name: $JOB_NAME"
echo "Environment: $ENVIRONMENT"
echo "Report to Jira: $REPORT_TO_JIRA"
echo ""

# Trigger build
echo "📡 Sending build request..."
response=$(curl -s -w "\n%{http_code}" -X POST \
    -u "${JENKINS_USER}:${JENKINS_TOKEN}" \
    "${TRIGGER_URL}")

http_code=$(echo "$response" | tail -n1)
body=$(echo "$response" | head -n-1)

if [[ "$http_code" == "201" ]] || [[ "$http_code" == "200" ]]; then
    echo "✅ Build triggered successfully!"
    echo ""
    echo "Queue URL: ${JENKINS_URL}/queue"
    echo "Job URL: ${JENKINS_URL}/job/${JOB_NAME}"
    echo ""
    echo "🔍 Monitoring build..."
    
    # Get build number
    sleep 3
    BUILD_JSON=$(curl -s -u "${JENKINS_USER}:${JENKINS_TOKEN}" \
        "${JENKINS_URL}/job/${JOB_NAME}/lastBuild/api/json")
    
    BUILD_NUMBER=$(echo "$BUILD_JSON" | grep -o '"number":[0-9]*' | head -1 | cut -d':' -f2)
    
    if [ ! -z "$BUILD_NUMBER" ]; then
        echo "Build #${BUILD_NUMBER} is running..."
        echo "Console: ${JENKINS_URL}/job/${JOB_NAME}/${BUILD_NUMBER}/console"
    fi
else
    echo "❌ Failed to trigger build!"
    echo "HTTP Status: $http_code"
    echo "Response: $body"
    exit 1
fi
