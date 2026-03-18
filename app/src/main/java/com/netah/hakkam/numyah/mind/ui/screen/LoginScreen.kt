package com.netah.hakkam.numyah.mind.ui.screen

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.netah.hakkam.numyah.mind.viewmodel.AuthViewModel

@SuppressWarnings("unused")
@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    navigateToMain: (() -> Unit)? = null
) {
    LoginView(authViewModel, navigateToMain)
}

@Composable
fun LoginView(authViewModel: AuthViewModel, navigateToMain: (() -> Unit)?) {


    Button(onClick = {
        authViewModel.signInWithGoogle()
    }) {
        Text("Sign in with Google")
    }
}


/*
@SuppressWarnings("unused")
@Composable
fun LoginScreen(
    credentialManager: CredentialManager,
    navigateToMain: (() -> Unit)? = null
) {


    LoginView(credentialManager, navigateToMain)
}

@Composable
fun LoginView(credentialManager: CredentialManager? = null, navigateToMain: (() -> Unit)?) {
    val context = LocalContext.current
    val clientId = stringResource(R.string.default_web_client_id)
    val scope = rememberCoroutineScope()

    Button(onClick = {
        signIn(credentialManager, context, clientId, scope, navigateToMain)
    }) {
        Text("Sign in with Google")
    }
}


private fun signIn(
    credentialManager: CredentialManager? = null,
    context: Context,
    clientId: String,
    scope: CoroutineScope,
    navigateToMain: (() -> Unit)?
) {
    val googleIdOption = GetGoogleIdOption.Builder()
        .setServerClientId(clientId)
        .setFilterByAuthorizedAccounts(false)
        .setAutoSelectEnabled(false)
        .build()

    val request = GetCredentialRequest.Builder()
        .addCredentialOption(googleIdOption)
        .build()
    scope.launch {
        try {
            val result = credentialManager?.getCredential(
                context = context,
                request = request
            )
            handleSignIn(result, navigateToMain)
        } catch (e: GetCredentialException) {
            e.printStackTrace()
        }
    }
}

fun handleSignIn(result: GetCredentialResponse?, navigateToMain: (() -> Unit)?) {
    result?.credential.let { credential ->
        if (credential is CustomCredential &&
            credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) {
            val googleIdTokenCredential = GoogleIdTokenCredential
                .createFrom(credential.data)

            val idToken = googleIdTokenCredential.idToken
            val displayName = googleIdTokenCredential.displayName
            val givenName = googleIdTokenCredential.givenName
            val familyName = googleIdTokenCredential.familyName
            val profilePictureUri = googleIdTokenCredential.profilePictureUri


            Timber.tag("TEST-I").i("idToken: $idToken")
            Timber.tag("TEST-I").i("displayName: $displayName")
            Timber.tag("TEST-I").i("givenName: $givenName")
            Timber.tag("TEST-I").i("familyName: $familyName")
            Timber.tag("TEST-I").i("profilePictureUri: $profilePictureUri")
            navigateToMain?.invoke()
        } else {
            Timber.tag("TEST-I").i("credential: $credential")
            Timber.tag("TEST-I").i("Credential type not supported")
        }
    }.otherwise {
        Timber.tag("TEST-I").i("credential is null")
    }
}

 */