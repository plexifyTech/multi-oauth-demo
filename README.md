# oauth-demo

Sample application for integrating multiple OAuth accounts of a user at once.

## Getting Started

First, register an OAuth application with your GitHub and Microsoft account. 

Follow the instructions provided in
https://github.com/settings/applications/new for GitHub
https://learn.microsoft.com/en-us/entra/identity-platform/quickstart-register-app for Microsoft.
Set the redirect uri/authorization callback URL to
http://localhost:8080/login/oauth2/code/aad for Microsoft and 
http://localhost:8080/login/oauth2/code/github for GitHub.

Configure required credentials for each service in the application.yaml.
Setting sensitive data as environment-variables and not storing them in version control is recommended.

## How to use

For quick testing purposes, see and follow instructions in src/main/resources/App.tsx.
The sample implements the following:

Use http://localhost:8080/auth/check/aad to check if a user is currently logged into their Microsoft-Account.
Use http://localhost:8080/auth/check/github to check if a user is currently logged into their GitHub-Account.

If the endpoints return 401, present one login-button for each service.
On click, set your browsers window location to
http://localhost:8080/planner/oauth2/authorization/aad or http://localhost:8080/planner/oauth2/authorization/github.
The browser will then present the login screen of the requested provider.

After successful authentication, just query any protected endpoint to receive requested data.
Get the csrf-token and set it as an X-XSRF-TOKEN request header if required.
