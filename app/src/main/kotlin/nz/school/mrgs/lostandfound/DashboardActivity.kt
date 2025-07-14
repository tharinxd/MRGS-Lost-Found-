package nz.school.mrgs.lostandfound

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import nz.school.mrgs.lostandfound.databinding.ActivityDashboardBinding

class DashboardActivity : AppCompatActivity() {

    // So I'm setting up my variables here.
    // 'binding' connects to my XML layout, and 'auth' is for Firebase.
    private lateinit var binding: ActivityDashboardBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Auth so I can use it for signing out.
        auth = Firebase.auth

        // This function sets up what happens when each button is clicked.
        setupClickListeners()
    }

    private fun setupClickListeners() {
        // I'm assuming the names for the new pages I'll create.
        // This code will have errors until I create the actual Activity files.

        binding.cardLostItems.setOnClickListener {
            val intent = Intent(this, LostItemsActivity::class.java)
            startActivity(intent)
        }

        binding.cardFoundItems.setOnClickListener {
            val intent = Intent(this, FoundItemsActivity::class.java)
            startActivity(intent)
        }

        binding.cardMyLostItems.setOnClickListener {
            val intent = Intent(this, MyLostItemsActivity::class.java)
            startActivity(intent)
        }

        binding.cardReportItem.setOnClickListener {
            val intent = Intent(this, ReportItemActivity::class.java)
            startActivity(intent)
        }

        binding.cardSettings.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        // This is the click listener for the logout button.
        binding.cardLogout.setOnClickListener {
            // This line signs the user out of their Firebase account.
            auth.signOut()

            // This creates the instruction to go back to the LoginActivity.
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)

            // I call finish() here to close the dashboard screen completely.
            // This is important so the user can't press the 'back' button to get
            // back into the app after they've logged out.
            finish()
        }
    }
}
