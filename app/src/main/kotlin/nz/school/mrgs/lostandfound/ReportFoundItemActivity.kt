package nz.school.mrgs.lostandfound

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import nz.school.mrgs.lostandfound.data.Item
import nz.school.mrgs.lostandfound.databinding.ActivityReportFoundItemBinding
import java.util.Calendar

// So this is the Kotlin file for my "Report a FOUND Item" form page.
class ReportFoundItemActivity : AppCompatActivity() {

    // So I'm setting up my main variables here.
    // 'binding' connects to my XML layout, and 'db' and 'auth' are for my Firebase connection.
    private lateinit var binding: ActivityReportFoundItemBinding
    private val db = Firebase.firestore
    private val auth = Firebase.auth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReportFoundItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // This makes the back button in my toolbar work.
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        // I'm calling my functions here to set up all the button clicks and the smart dropdown menu.
        setupClickListeners()
        setupItemTypeSpinnerListener()
    }

    // This is the function I made to make the dropdowns change. It listens for when the user picks an "Item Type".
    private fun setupItemTypeSpinnerListener() {
        binding.spinnerItemType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                // I get the item that the user selected, for example, "Shoes".
                val selectedType = parent.getItemAtPosition(position).toString()

                // I use a 'when' statement here to figure out which list of sizes to show.
                val sizeArrayResourceId = when (selectedType) {
                    "Shoes", "Boots" -> R.array.shoe_sizes_array
                    "Uniform", "PE Gear", "Sports Clothing", "Jacket / Hoodie" -> R.array.clothing_sizes_array
                    else -> R.array.general_sizes_array
                }

                // Here, I create a new list adapter for the "Size" spinner using the correct list of sizes.
                ArrayAdapter.createFromResource(
                    this@ReportFoundItemActivity,
                    sizeArrayResourceId,
                    android.R.layout.simple_spinner_item
                ).also { adapter ->
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    // Finally, I tell the "Size" spinner to use this new list.
                    binding.spinnerSize.adapter = adapter
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // I don't need to do anything here if the user doesn't pick anything.
            }
        }
    }

    // This function just organizes my button clicks.
    private fun setupClickListeners() {
        // This is for the "Submit" button. When clicked, it will run my submitReport function.
        binding.btnSubmit.setOnClickListener {
            submitReport()
        }

        // This is for the image upload button.
        binding.btnUploadImage.setOnClickListener {
            // TODO: I'll add the code here later to open the phone's gallery to pick an image.
            Toast.makeText(this, "Image upload not implemented yet.", Toast.LENGTH_SHORT).show()
        }
    }

    // This is the main function that runs when the user hits "Submit".
    private fun submitReport() {
        val currentUser = auth.currentUser
        // First, I check to make sure someone is actually logged in.
        if (currentUser == null) {
            Toast.makeText(this, "You must be logged in to report an item.", Toast.LENGTH_SHORT).show()
            return
        }

        // I get the text from the "Item Title" input box and make sure it's not empty.
        val title = binding.etItemTitle.text.toString().trim()
        if (title.isEmpty()) {
            binding.etItemTitle.error = "Title cannot be empty"
            return
        }

        // Here, I create a new 'Item' object using the data class I made.
        // I get all the selected values from the form's dropdowns and text boxes.
        val newItem = Item(
            userId = currentUser.uid,
            title = title,
            type = binding.spinnerItemType.selectedItem.toString(),
            location = binding.spinnerLocation.selectedItem.toString(),
            size = binding.spinnerSize.selectedItem.toString(),
            period = binding.spinnerPeriod.selectedItem.toString(),
            color = binding.spinnerColor.selectedItem.toString(),
            notes = binding.etNotes.text.toString().trim(),
            // For a found item, the date is when it was found. I'm just using the current time for now.
            dateLost = Calendar.getInstance().time
        )

        // Finally, I save this 'newItem' object to my Firestore database.
        // I'm creating a new collection called "foundItems" to store all the reports for found items.
        db.collection("foundItems")
            .add(newItem)
            .addOnSuccessListener {
                // If it saves successfully, I show a message and close the form.
                Toast.makeText(this, "Item reported successfully!", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                // If there's an error, I show a message with the error details.
                Toast.makeText(this, "Error reporting item: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
}
