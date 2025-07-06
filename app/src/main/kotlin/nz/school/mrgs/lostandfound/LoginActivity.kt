package nz.school.mrgs.lostandfound

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import nz.school.mrgs.lostandfound.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    // So here, I'm setting up the main variables I'll need for this screen.
    // 'binding' is how I connect to my XML layout to use the buttons and stuff.
    // 'auth' is for all the Firebase login features.
    // 'googleSignInClient' is what handles the Google Sign-In pop-up.
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // This is the standard code to link this Kotlin file to my 'activity_login.xml' layout.
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Here, I'm initializing the Firebase authentication library so I can use it.
        auth = Firebase.auth

        // This part sets up the Google Sign-In pop-up.
        // The '.requestIdToken()' part is important. It asks Google for a special, secure token.
        // I need this token to prove to Firebase that the user is who they say they are.
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            // TODO: Teacher, I've replaced this placeholder with my actual Web Client ID from Google Cloud.
            .requestIdToken("419808596245-tmibfe8npckvb4dcbu0l5bn8o2ico2b3.apps.googleusercontent.com")
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // This is a check to see if someone is already logged in with a valid school account.
        // If they are, we don't need to show them the login screen again, so I just
        // send them straight to the main dashboard.
        if (auth.currentUser != null) {
            navigateToDashboard()
        }

        // This sets up the listener for my "Continue with Google" button.
        // When it's clicked, it will run the 'signInWithGoogle()' function.
        binding.btnGoogleSignIn.setOnClickListener {
            signInWithGoogle()
        }
    }

    // This 'launcher' waits for the result from the Google Sign-In pop-up.
    // When the user picks an account, Google sends the result back here.
    private val googleSignInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                // If the sign-in with Google was successful, I get the account's 'idToken'.
                val account = task.getResult(ApiException::class.java)!!
                // Then I pass that token to my other function to finish the sign-in with Firebase.
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // This 'catch' block handles errors, like if the user closes the pop-up.
                Log.w("LoginActivity", "Google sign in failed", e)
                // Toast.makeText(this, "Google Sign-In Failed.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // This function is simple. It just gets the sign-in 'intent' (the pre-built Google screen)
    // and launches it, which makes the account selection pop-up appear.
    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        googleSignInLauncher.launch(signInIntent)
    }

    // This is the updated function. It's the final, most important part of the login.
    // It takes the token from Google and sends it to Firebase to get an official credential.
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Okay, so the Firebase login was successful. NOW I do my check for the school email.
                    val user = auth.currentUser
                    val schoolDomain = "students.mrgs.school.nz" // This is the domain I want to allow.

                    if (user != null && user.email != null && user.email!!.endsWith(schoolDomain)) {
                        // The email matches the school domain! So I let them in.
                        // Toast.makeText(this, "School account verified.", Toast.LENGTH_SHORT).show()
                        navigateToDashboard()
                    } else {
                        // This runs if they logged in with a non-school email, like a personal gmail.
                        // I show them an error message telling them they need to use a school account.
                        // Toast.makeText(this, "Only users from the $schoolDomain domain are allowed.", Toast.LENGTH_LONG).show()

                        // Then I immediately sign them out of both Firebase and Google.
                        // Signing out of the Google client is important because it lets them
                        // choose a different account next time.
                        auth.signOut()
                        googleSignInClient.signOut()
                    }
                } else {
                    // This happens if the sign-in with Firebase fails for other reasons.
                    // Toast.makeText(this, "Authentication Failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    // This function will be used to move to the next screen after a successful login.
    // I'll build the DashboardActivity later. For now, it just shows a message.
    private fun navigateToDashboard() {
        // Toast.makeText(this, "Logged in! Navigating to Dashboard...", Toast.LENGTH_LONG).show()
        // val intent = Intent(this, DashboardActivity::class.java)
        // startActivity(intent)
        // finish() // I'll use finish() later so the user can't press 'back' to get to the login screen.
    }
}
