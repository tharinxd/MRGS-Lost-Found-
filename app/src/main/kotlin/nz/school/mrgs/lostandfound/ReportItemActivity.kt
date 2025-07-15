package nz.school.mrgs.lostandfound

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import nz.school.mrgs.lostandfound.databinding.ActivityReportitemBinding

// This is the class for my "Report Item" screen.
class ReportItemActivity : AppCompatActivity() {

    // 'binding' connects this code to my new XML layout.
    private lateinit var binding: ActivityReportitemBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // This function sets up what happens when each button is clicked.
        setupClickListeners()

        binding = ActivityReportitemBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun setupClickListeners() {

        // This is the listener for my new Dashboard button in the nav bar.
        binding.btnDashboard.setOnClickListener {
            // It creates an intent to go back to the DashboardActivity.
            val intent = Intent(this, DashboardActivity::class.java)
            // These flags clear the other pages from memory so it feels like starting fresh from the dashboard.
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish() // I call finish() so the user can't go back to this page from the dashboard.
        }
    }
}
