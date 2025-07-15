package nz.school.mrgs.lostandfound

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

// So this is the Kotlin file for my "Report a LOST Item" page.
// It has to extend AppCompatActivity to work as a screen in my app.
class ReportLostItemActivity : AppCompatActivity() {

    // The onCreate function is the first thing that runs when this screen opens up.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // This line is super important. It tells the code to show the layout I designed
        // in the 'activity_report_lost_item.xml' file.
        // If I didn't have this line, the screen would just be blank.
        setContentView(R.layout.activity_report_lost_item)
    }
}
