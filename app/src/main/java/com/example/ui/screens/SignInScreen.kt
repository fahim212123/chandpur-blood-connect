package com.example.ui.screens

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import com.example.R
import com.example.localization.AppLanguage
import com.example.localization.AppStrings
import com.example.ui.theme.BloodRed800
import com.example.ui.theme.BloodRed900
import com.example.ui.theme.BloodRedContainer
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SurfaceOffWhite
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.Firebase
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun SignInScreen(
  currentLanguage: AppLanguage,
  onToggleLanguage: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val scope = rememberCoroutineScope()
  val isBengali = currentLanguage == AppLanguage.BN

  var isLoading by remember { mutableStateOf(false) }
  var errorMessage by remember { mutableStateOf<String?>(null) }

  fun startGoogleSignIn() {
    val activity = context as? Activity
    if (activity == null) {
      errorMessage = "Activity context is required for Google Sign-In"
      return
    }

    isLoading = true
    errorMessage = null

    scope.launch {
      try {
        val credentialManager = CredentialManager.create(activity)
        val serverClientId = context.getString(R.string.default_web_client_id)

        val googleIdOption = GetSignInWithGoogleOption.Builder(serverClientId)
          .build()

        val request = GetCredentialRequest.Builder()
          .addCredentialOption(googleIdOption)
          .build()

        val result = credentialManager.getCredential(
          request = request,
          context = activity
        )

        val credential = result.credential
        if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
          val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
          val authCredential = GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)
          Firebase.auth.signInWithCredential(authCredential).await()
        } else {
          errorMessage = if (isBengali) "প্রমাণীকরণ সম্পন্ন হয়নি" else "Unsupported credential received"
        }
      } catch (e: GetCredentialCancellationException) {
        // User cancelled picker
        errorMessage = null
      } catch (e: GetCredentialException) {
        errorMessage = if (isBengali) "সাইন-ইন ব্যর্থ হয়েছে: ${e.message}" else "Sign-in failed: ${e.message}"
      } catch (e: Exception) {
        errorMessage = e.localizedMessage ?: "Authentication error"
      } finally {
        isLoading = false
      }
    }
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(PureWhite)
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .statusBarsPadding()
        .padding(24.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      // Top row with language toggle
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
      ) {
        Surface(
          shape = RoundedCornerShape(20.dp),
          color = BloodRedContainer,
          modifier = Modifier
            .testTag("signin_language_toggle")
            .clip(RoundedCornerShape(20.dp))
        ) {
          Row(
            modifier = Modifier
              .clickable { onToggleLanguage() }
              .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.Language,
              contentDescription = "Switch Language",
              tint = BloodRed800,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = if (isBengali) "English" else "বাংলা",
              color = BloodRed800,
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }

      // Middle branding section
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
      ) {
        Box(
          modifier = Modifier
            .size(96.dp)
            .clip(CircleShape)
            .background(BloodRed800),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.WaterDrop,
            contentDescription = "LifeLink CP Logo",
            tint = PureWhite,
            modifier = Modifier.size(56.dp)
          )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
          text = AppStrings.appName(currentLanguage),
          color = BloodRed800,
          fontSize = 32.sp,
          fontWeight = FontWeight.ExtraBold,
          letterSpacing = 0.5.sp
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
          text = AppStrings.appTagline(currentLanguage),
          color = TextDark,
          fontSize = 15.sp,
          fontWeight = FontWeight.SemiBold,
          textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(10.dp))

        Surface(
          shape = RoundedCornerShape(12.dp),
          color = SurfaceOffWhite
        ) {
          Text(
            text = if (isBengali)
              "চাঁদপুর সদর ও ফরিদগঞ্জের জীবন রক্ষাকারী রক্তদান নেটওয়ার্ক"
            else
              "Life-saving blood donation network for Chandpur Sadar & Faridganj",
            color = TextMuted,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
          )
        }

        if (errorMessage != null) {
          Spacer(modifier = Modifier.height(16.dp))
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = BloodRedContainer,
            modifier = Modifier.fillMaxWidth()
          ) {
            Text(
              text = errorMessage ?: "",
              color = BloodRed800,
              fontSize = 12.sp,
              modifier = Modifier.padding(12.dp),
              textAlign = TextAlign.Center
            )
          }
        }
      }

      // Bottom Sign-In Section
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.Center,
          modifier = Modifier.padding(bottom = 14.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Security,
            contentDescription = null,
            tint = BloodRed800,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = if (isBengali) "নিরাপদ ক্লাউড ডেটাবেস সাইন-ইন" else "Secure Cloud Database Authentication",
            fontSize = 12.sp,
            color = TextMuted,
            fontWeight = FontWeight.Medium
          )
        }

        Button(
          onClick = { startGoogleSignIn() },
          enabled = !isLoading,
          modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
            .testTag("sign_in_with_google_button"),
          shape = RoundedCornerShape(14.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = BloodRed800,
            contentColor = PureWhite
          ),
          elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
        ) {
          if (isLoading) {
            CircularProgressIndicator(
              color = PureWhite,
              modifier = Modifier.size(24.dp),
              strokeWidth = 2.dp
            )
          } else {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.WaterDrop,
                contentDescription = null,
                tint = PureWhite,
                modifier = Modifier.size(20.dp)
              )
              Spacer(modifier = Modifier.width(10.dp))
              Text(
                text = if (isBengali) "Google দিয়ে সাইন-ইন করুন" else "Sign in with Google",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
          text = if (isBengali)
            "গোপনীয়তা রক্ষা: সাধারণ সার্চে ব্যক্তিগত ফোন নম্বর প্রদর্শিত হয় না"
          else
            "Privacy protected: Private contact numbers are never exposed in public search",
          fontSize = 11.sp,
          color = TextMuted,
          textAlign = TextAlign.Center,
          lineHeight = 15.sp,
          modifier = Modifier.padding(horizontal = 8.dp)
        )
      }
    }
  }
}
