package nz.school.mrgs.lostandfound

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

// This is the class for my "My Lost Items" screen.
// It needs to extend AppCompatActivity to be a proper screen.
class MyLostItemsActivity : AppCompatActivity() {

    // The onCreate function is the first thing that runs when this screen opens.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // This line tells the activity to display the layout from the 'activity_my_lost_items.xml' file.
        // The app will crash if this file doesn't exist in the res/layout folder.
        setContentView(R.layout.activity_mylostitems)
    }
}
