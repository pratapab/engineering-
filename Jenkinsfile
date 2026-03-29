pipeline {
    agent any

    options {
        buildDiscarder(logRotator(numToKeepStr: '10', artifactNumToKeepStr: '5'))
        timestamps()
        timeout(time: 1, unit: 'HOURS')
    }

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

    environment {
        JAVA_HOME = 'C:\\Program Files\\Java\\jdk-11'
        MAVEN_HOME = 'C:\\maven'
        PATH = "${MAVEN_HOME}\\bin;${JAVA_HOME}\\bin;${PATH}"
        PROJECT_NAME = 'SalesforceAutomation'
        TEST_RESULTS_DIR = "${WORKSPACE}\\target\\surefire-reports"
        JIRA_SITE = credentials('jira-site-url')
        JIRA_USER = credentials('jira-username')
        JIRA_TOKEN = credentials('jira-api-token')
    }

    stages {
        stage('Checkout') {
            steps {
                script {
                    echo "=== Stage: Checkout Code ==="
                    checkout scm
                    echo "Repository checked out successfully"
                }
            }
        }

        stage('Build') {
            steps {
                script {
                    echo "=== Stage: Build Project ==="
                    try {
                        bat 'mvn clean compile -DskipTests=true'
                        echo "✓ Build successful"
                    } catch (Exception e) {
                        echo "✗ Build failed: ${e.message}"
                        currentBuild.result = 'FAILURE'
                        error("Build stage failed")
                    }
                }
            }
        }

        stage('Unit Tests') {
            steps {
                script {
                    echo "=== Stage: Unit Tests ==="
                    try {
                        bat 'mvn test -Denv=${ENVIRONMENT}'
                        echo "✓ Unit tests passed"
                    } catch (Exception e) {
                        echo "⚠ Unit tests failed: ${e.message}"
                        currentBuild.result = 'UNSTABLE'
                    }
                }
            }
        }

        stage('Test Results') {
            steps {
                script {
                    echo "=== Stage: Parse Test Results ==="
                    junit testResults: 'target/surefire-reports/TEST-*.xml', allowEmptyResults: true
                    
                    // Archive test reports
                    archiveArtifacts artifacts: 'target/surefire-reports/**/*.xml', allowEmptyArchive: true
                    
                    echo "Test results archived"
                }
            }
        }

        stage('Report to Jira') {
            when {
                expression {
                    return params.REPORT_TO_JIRA && currentBuild.result != 'SUCCESS'
                }
            }
            steps {
                script {
                    echo "=== Stage: Report Failures to Jira ==="
                    try {
                        def testResults = ''
                        def testReportFile = "${TEST_RESULTS_DIR}\\TEST-tests.LoginTestInvalid.xml"
                        
                        if (fileExists(testReportFile)) {
                            def xml = readFile(testReportFile)
                            testResults = parseTestResults(xml)
                        }

                        if (testResults) {
                            reportToJira(testResults)
                            echo "✓ Test failures reported to Jira"
                        } else {
                            echo "ℹ No test failures to report"
                        }
                    } catch (Exception e) {
                        echo "⚠ Jira reporting failed: ${e.message}"
                        // Don't fail the build if Jira reporting fails
                    }
                }
            }
        }

        stage('Notifications') {
            steps {
                script {
                    echo "=== Stage: Send Notifications ==="
                    def status = currentBuild.result ?: 'SUCCESS'
                    def message = """
                    Pipeline: ${env.JOB_NAME}
                    Build: #${env.BUILD_NUMBER}
                    Status: ${status}
                    Environment: ${params.ENVIRONMENT}
                    Duration: ${currentBuild.durationString}
                    """.stripIndent()
                    
                    // Slack notification (optional)
                    // slackSend(color: getStatusColor(status), message: message)
                    
                    echo message
                }
            }
        }
    }

    post {
        always {
            script {
                echo "=== Post Actions ==="
                // Clean workspace if needed
                // cleanWs()
            }
        }
        success {
            echo "✓ Pipeline completed successfully"
        }
        unstable {
            echo "⚠ Pipeline completed with test failures"
        }
        failure {
            echo "✗ Pipeline failed"
        }
    }
}

// ================================================================
// HELPER FUNCTIONS
// ================================================================

def parseTestResults(String xmlContent) {
    """
    Parses TestNG XML results and returns formatted failure summary.
    Used by Jira reporting stage.
    """
    def failures = [:]
    try {
        def testSuite = new XmlSlurper().parseText(xmlContent)
        testSuite.test.method.each { method ->
            if (method.exception) {
                failures[method.@name] = method.exception.text()
            }
        }
    } catch (Exception e) {
        echo "Could not parse test results: ${e.message}"
    }
    return failures
}

def reportToJira(Map failures) {
    """
    Reports test failures to Jira using REST API.
    Requirements:
    - JIRA_SITE: Jira instance URL
    - JIRA_USER: Jira username
    - JIRA_TOKEN: Jira API token
    """
    failures.each { testName, errorMessage ->
        try {
            def issueJson = """
            {
                "fields": {
                    "project": {"key": "SFDC"},
                    "summary": "Test Failed: ${testName}",
                    "description": "Test failure in ${env.JOB_NAME} build #${env.BUILD_NUMBER}\\n\\nError: ${errorMessage}",
                    "issuetype": {"name": "Bug"},
                    "labels": ["automation", "test-failure", "${env.BUILD_NUMBER}"]
                }
            }
            """.stripIndent()

            def response = sh(
                script: """
                curl -X POST "${JIRA_SITE}/rest/api/3/issue" \\
                    -H "Authorization: Basic \$(echo -n '${JIRA_USER}:${JIRA_TOKEN}' | base64)" \\
                    -H "Content-Type: application/json" \\
                    -d '${issueJson}'
                """,
                returnStdout: true
            ).trim()

            echo "Created Jira issue: ${response}"
        } catch (Exception e) {
            echo "Failed to create Jira issue for ${testName}: ${e.message}"
        }
    }
}

def getStatusColor(String status) {
    """
    Returns Slack message color based on build status.
    """
    switch(status) {
        case 'SUCCESS':
            return 'good'
        case 'UNSTABLE':
            return 'warning'
        case 'FAILURE':
            return 'danger'
        default:
            return '#808080'
    }
}
