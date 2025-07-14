package nz.school.mrgs.lostandfound

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

// This is the class for my new Settings screen.
// It's pretty simple for now.
class LostItemsActivity  : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // This line tells the activity to display the layout from the 'activity_settings.xml' file.
        // The app will crash if this file doesn't exist in the res/layout folder.
        setContentView(R.layout.activity_lostitemsactivity)
    }
}
