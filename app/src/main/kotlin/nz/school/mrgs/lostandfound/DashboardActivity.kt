package nz.school.mrgs.lostandfound

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

// This is the class for my new Dashboard screen.
// For now, its only job is to show the 'activity_dashboard.xml' layout.
class DashboardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
    }
}
