package nz.school.mrgs.lostandfound

import android.app.DatePickerDialog
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
import nz.school.mrgs.lostandfound.databinding.ActivityReportLostItemBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

// This is the Kotlin file for my "Report a LOST Item" form page.
class ReportLostItemActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReportLostItemBinding
    private val db = Firebase.firestore
    private val auth = Firebase.auth
    private var selectedDate: Calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReportLostItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // This makes the back button in my toolbar work.
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        // This sets up the click listeners for my two main buttons.
        setupClickListeners()

        // This new function sets up the logic for my dynamic dropdowns.
        setupItemTypeSpinnerListener()
    }

    // This is the new function. It listens for changes in the "Item Type" dropdown.
    private fun setupItemTypeSpinnerListener() {
        binding.spinnerItemType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                // I get the item that the user selected, for example, "Shoes".
                val selectedType = parent.getItemAtPosition(position).toString()

                // I figure out which list of sizes to show based on the selected item type.
                val sizeArrayResourceId = when (selectedType) {
                    "Shoes", "Boots" -> R.array.shoe_sizes_array
                    "Uniform", "PE Gear", "Sports Clothing", "Jacket / Hoodie" -> R.array.clothing_sizes_array
                    else -> R.array.general_sizes_array
                }

                // I create a new adapter for the "Size" spinner using the correct list of sizes.
                ArrayAdapter.createFromResource(
                    this@ReportLostItemActivity,
                    sizeArrayResourceId,
                    android.R.layout.simple_spinner_item
                ).also { adapter ->
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    // Finally, I tell the "Size" spinner to use this new adapter.
                    binding.spinnerSize.adapter = adapter
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // I don't need to do anything here.
            }
        }
    }

    private fun setupClickListeners() {
        // This is for the "Date Lost" button. When clicked, it will run my showDatePicker function.
        binding.btnDateLost.setOnClickListener {
            showDatePicker()
        }

        // This is for the "Submit" button. When clicked, it will run my submitReport function.
        binding.btnSubmit.setOnClickListener {
            submitReport()
        }
    }

    // This function's job is to show the calendar pop-up.
    private fun showDatePicker() {
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                // When the user picks a date, I save it in my 'selectedDate' variable.
                selectedDate.set(Calendar.YEAR, year)
                selectedDate.set(Calendar.MONTH, month)
                selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                // I then format the date nicely to show it on the button.
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val formattedDate = dateFormat.format(selectedDate.time)

                // I've removed the debugging Toast message from here.
                // Toast.makeText(this, "Date selected: $formattedDate", Toast.LENGTH_SHORT).show()

                binding.btnDateLost.text = formattedDate
            },
            selectedDate.get(Calendar.YEAR),
            selectedDate.get(Calendar.MONTH),
            selectedDate.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    // This is the main function that runs when the user hits "Submit".
    private fun submitReport() {
        val currentUser = auth.currentUser
        // First, I check to make sure someone is actually logged in.
        if (currentUser == null) {
            // Toast.makeText(this, "You must be logged in to report an item.", Toast.LENGTH_SHORT).show()
            return
        }

        // I get the text from the "Item Title" input box.
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
            location = binding.spinnerLocation.selectedItem.toString(),
            size = binding.spinnerSize.selectedItem.toString(),
            period = binding.spinnerPeriod.selectedItem.toString(),
            color = binding.spinnerColor.selectedItem.toString(),
            notes = binding.etNotes.text.toString().trim(),
            dateLost = selectedDate.time
        )

        // Finally, I save this 'newItem' object to my Firestore database.
        // I'm creating a new collection called "lostItems" to store all the reports.
        db.collection("lostItems")
            .add(newItem)
            .addOnSuccessListener {
                // If it saves successfully, I show a message and close the form.
                // Toast.makeText(this, "Item reported successfully!", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                // If there's an error, I show a message with the error details.
                // Toast.makeText(this, "Error reporting item: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
}
