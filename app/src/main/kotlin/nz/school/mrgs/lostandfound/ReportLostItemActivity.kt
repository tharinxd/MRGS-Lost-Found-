package nz.school.mrgs.lostandfound

import android.app.DatePickerDialog
import android.content.Intent
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

class ReportLostItemActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReportLostItemBinding
    private val db = Firebase.firestore
    private val auth = Firebase.auth
    private var selectedDate: Calendar = Calendar.getInstance()
    private var dateSelected = false // Flag to check if date has been selected

    private lateinit var clothingSizeAdapter: ArrayAdapter<CharSequence>
    private lateinit var shoeSizeAdapter: ArrayAdapter<CharSequence>
    private lateinit var generalSizeAdapter: ArrayAdapter<CharSequence>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReportLostItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        clothingSizeAdapter = ArrayAdapter.createFromResource(this, R.array.clothing_sizes_array, android.R.layout.simple_spinner_item)
        shoeSizeAdapter = ArrayAdapter.createFromResource(this, R.array.shoe_sizes_array, android.R.layout.simple_spinner_item)
        generalSizeAdapter = ArrayAdapter.createFromResource(this, R.array.general_sizes_array, android.R.layout.simple_spinner_item)

        clothingSizeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        shoeSizeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        generalSizeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        setupClickListeners()
        setupItemTypeSpinnerListener()
    }

    private fun setupItemTypeSpinnerListener() {
        binding.spinnerItemType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedType = parent.getItemAtPosition(position).toString()
                binding.spinnerSize.adapter = when (selectedType) {
                    "Shoes", "Boots" -> shoeSizeAdapter
                    "Uniform", "PE Gear", "Sports Clothing", "Jacket / Hoodie" -> clothingSizeAdapter
                    else -> generalSizeAdapter
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnDateLost.setOnClickListener {
            showDatePicker()
        }

        binding.btnSubmit.setOnClickListener {
            // Disable button to prevent double-taps
            it.isEnabled = false
            submitReport()
        }

        binding.btnDashboard.setOnClickListener {
            val intent = Intent(this, DashboardActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }
    }

    private fun showDatePicker() {
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                selectedDate.set(Calendar.YEAR, year)
                selectedDate.set(Calendar.MONTH, month)
                selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                binding.btnDateLost.text = dateFormat.format(selectedDate.time)
                dateSelected = true // Set flag to true
            },
            selectedDate.get(Calendar.YEAR),
            selectedDate.get(Calendar.MONTH),
            selectedDate.get(Calendar.DAY_OF_MONTH)
        )

        // Prevent selecting a date in the future.
        datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
        datePickerDialog.show()
    }

    private fun submitReport() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(this, "You must be logged in.", Toast.LENGTH_SHORT).show()
            binding.btnSubmit.isEnabled = true // Re-enable button
            return
        }

        val title = binding.etItemTitle.text.toString().trim()
        if (title.isEmpty()) {
            binding.etItemTitle.error = "Title cannot be empty"
            binding.btnSubmit.isEnabled = true // Re-enable button
            return
        }

        // Validate that title contains at least one letter
        if (!title.any { it.isLetter() }) {
            binding.etItemTitle.error = "Title must contain letters and not just symbols/numbers"
            binding.btnSubmit.isEnabled = true // Re-enable button
            return
        }

        // Validate that a date has been selected
        if (!dateSelected) {
            Toast.makeText(this, "Please select the date the item was lost.", Toast.LENGTH_LONG).show()
            binding.btnSubmit.isEnabled = true // Re-enable button
            return
        }

        val itemType = binding.spinnerItemType.selectedItem.toString()
        if (itemType.startsWith("Select")) {
            Toast.makeText(this, "Please select an item type.", Toast.LENGTH_SHORT).show()
            binding.btnSubmit.isEnabled = true // Re-enable button
            return
        }

        val color = binding.spinnerColor.selectedItem.toString()
        if (color.startsWith("Select")) {
            Toast.makeText(this, "Please select a color.", Toast.LENGTH_SHORT).show()
            binding.btnSubmit.isEnabled = true // Re-enable button
            return
        }

        val newItem = Item(
            userId = currentUser.uid,
            title = title,
            type = itemType,
            location = binding.spinnerLocation.selectedItem.toString(),
            size = binding.spinnerSize.selectedItem?.toString() ?: "N/A", // Handle case where size spinner might be empty
            period = binding.spinnerPeriod.selectedItem.toString(),
            color = color,
            notes = binding.etNotes.text.toString().trim(),
            dateLost = selectedDate.time
        )

        db.collection("lostItems")
            .add(newItem)
            .addOnSuccessListener {
                Toast.makeText(this, "Item reported successfully!", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error reporting item: ${e.message}", Toast.LENGTH_LONG).show()
                binding.btnSubmit.isEnabled = true // Re-enable on failure
            }
    }
}
