package nz.school.mrgs.lostandfound

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import nz.school.mrgs.lostandfound.databinding.ActivityReportitemBinding

// This is the class for my "Report Item" screen.
class ReportItemActivity : AppCompatActivity() {

    // 'binding' connects this code to my XML layout.
    private lateinit var binding: ActivityReportitemBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // This sets up the connection to the 'activity_reportitem.xml' layout.
        binding = ActivityReportitemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // This function sets up what happens when each button is clicked.
        setupClickListeners()
    }

    private fun setupClickListeners() {
        // This is the listener for the "Report Lost Item" button.
        binding.btnReportLost.setOnClickListener {
            // When clicked, it will open the ReportLostItemActivity page.
            val intent = Intent(this, ReportLostItemActivity::class.java)
            startActivity(intent)
        }

        // This is the listener for the "Report Found Item" button.
        binding.btnReportFound.setOnClickListener {
            // When clicked, it will open the ReportFoundItemActivity page.
            val intent = Intent(this, ReportFoundItemActivity::class.java)
            startActivity(intent)
        }

        // This is the listener for my Dashboard button in the nav bar.
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
