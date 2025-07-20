package nz.school.mrgs.lostandfound

import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import nz.school.mrgs.lostandfound.data.Item
import nz.school.mrgs.lostandfound.databinding.ActivityItemDetailBinding
import java.text.SimpleDateFormat
import java.util.Locale

// This is the class for my new Item Detail screen.
class ItemDetailActivity : AppCompatActivity() {

    // 'binding' connects this code to my 'activity_item_detail.xml' layout.
    private lateinit var binding: ActivityItemDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityItemDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // This makes the back button in the toolbar work. When clicked, it just finishes
        // this activity and goes back to the previous one (the lost items list).
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        // So here, I'm getting the Item object that was passed from the previous screen.
        // I have to check the Android version because the way you get 'Parcelable' objects changed recently.
        val item = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("EXTRA_ITEM", Item::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra<Item>("EXTRA_ITEM")
        }

        // Now I check if the item actually exists.
        if (item != null) {
            // If it does, I call my function to fill in all the details on the screen.
            populateUi(item)
        } else {
            // If something went wrong and no item was passed, I'll show an error and close the page
            // so the user doesn't just see a blank screen.
            Toast.makeText(this, "Error: Item data not found.", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    // This function's job is to take all the data from the item object and put it into the right TextViews.
    private fun populateUi(item: Item) {
        binding.toolbar.title = item.title
        binding.detailSize.text = "Size: ${item.size}"
        binding.detailLocation.text = "Location Lost: ${item.location}"
        binding.detailPeriod.text = "Period Lost: ${item.period}"
        binding.detailNotes.text = item.notes

        // I need to format the date before displaying it so it looks nice.
        if (item.dateLost != null) {
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            binding.detailDate.text = "Date: ${dateFormat.format(item.dateLost)}"
        } else {
            binding.detailDate.text = "Date: Not set"
        }
    }
}
