package com.netah.hakkam.numyah.mind.auth

import android.content.Context
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.netah.hakkam.numyah.mind.R
import timber.log.Timber

class GoogleSignInManager(
    private val context: Context
) {
    private val credentialManager = CredentialManager.create(context)

    suspend fun signIn(): Result<GoogleUser> {
        return try {
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false) // true = returning users only
                .setServerClientId(context.getString(R.string.default_web_client_id))
                .setAutoSelectEnabled(false)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result = credentialManager.getCredential(
                context = context,
                request = request
            )

            val credential = result.credential
            Timber.tag("TEST-I").i("credential: $credential")

            if (
                credential is CustomCredential &&
                credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
            ) {
                val googleIdTokenCredential = GoogleIdTokenCredential
                    .createFrom(credential.data)

                Result.success(
                    GoogleUser(
                        id = googleIdTokenCredential.id,
                        displayName = googleIdTokenCredential.displayName,
                        email = googleIdTokenCredential.id,
                        profilePictureUrl = googleIdTokenCredential.profilePictureUri?.toString(),
                        idToken = googleIdTokenCredential.idToken
                    )
                )
            } else {
                Result.failure(IllegalStateException("Unexpected credential type"))
            }
        } catch (e: GoogleIdTokenParsingException) {
            e.printStackTrace()
            Result.failure(e)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    suspend fun signOut() {
        credentialManager.clearCredentialState(ClearCredentialStateRequest())
    }
}

data class GoogleUser(
    val id: String,
    val displayName: String?,
    val email: String?,
    val profilePictureUrl: String?,
    val idToken: String
)