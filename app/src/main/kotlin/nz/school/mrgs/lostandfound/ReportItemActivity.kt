package nz.school.mrgs.lostandfound

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

// This is the class for my "Report An Item" screen.
// It needs to extend AppCompatActivity to be a proper screen.
class ReportItemActivity : AppCompatActivity() {

    // The onCreate function is the first thing that runs when this screen opens.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // This line tells the activity to display the layout from the 'activity_report_item.xml' file.
        // The app will crash if this file doesn't exist in the res/layout folder.
        setContentView(R.layout.activity_reportitem)
    }
}
