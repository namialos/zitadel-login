package org.example.zitadellogin.presentation.keyclocklogin.utils

import org.example.zitadellogin.core.config.AuthConfig.IAM_BASE_URL

val verifier = Pkce.createVerifier()
val challenge = Pkce.createChallenge(verifier)

val authUrl =
    buildString {
        append("$IAM_BASE_URL/realms/platform-x/protocol/openid-connect/auth?")
        append("client_id=platform-x-angular")
        append("&redirect_uri=http://localhost:4202")
        append("&response_type=code")
        append("&scope=openid profile email")
        append("&code_challenge=$challenge")
        append("&code_challenge_method=S256")
    }