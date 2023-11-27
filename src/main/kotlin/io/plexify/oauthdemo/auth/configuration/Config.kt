package io.plexify.oauthdemo.auth.configuration

object Config {
    const val GITHUB = "github"
    const val AZURE_ACTIVE_DIRECTORY = "aad"
    val CLIENT_REGISTRATION_ID_SET = setOf(GITHUB, AZURE_ACTIVE_DIRECTORY)
    const val LOGOUT_URL = "/logout"
}