package nz.school.mrgs.lostandfound

import android.Manifest
import android.app.DatePickerDialog
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import nz.school.mrgs.lostandfound.data.Item
import nz.school.mrgs.lostandfound.databinding.ActivityReportFoundItemBinding
import java.text.SimpleDateFormat
import java.util.*

class ReportFoundItemActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReportFoundItemBinding
    private val db = Firebase.firestore
    private val auth = Firebase.auth
    private val storage = Firebase.storage
    private var selectedDate: Calendar = Calendar.getInstance()
    private var imageUri: Uri? = null

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            imageUri = it
            binding.itemImagePreview.setImageURI(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReportFoundItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(arrayOf(Manifest.permission.READ_MEDIA_IMAGES), 1001)
        } else {
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1002)
        }

        setupClickListeners()
        setupItemTypeSpinnerListener()
    }

    private fun setupItemTypeSpinnerListener() {
        binding.spinnerItemType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedType = parent.getItemAtPosition(position).toString()
                val sizeArrayResourceId = when (selectedType) {
                    "Shoes", "Boots" -> R.array.shoe_sizes_array
                    "Uniform", "PE Gear", "Sports Clothing", "Jacket / Hoodie" -> R.array.clothing_sizes_array
                    else -> R.array.general_sizes_array
                }
                ArrayAdapter.createFromResource(
                    this@ReportFoundItemActivity,
                    sizeArrayResourceId,
                    android.R.layout.simple_spinner_item
                ).also { adapter ->
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    binding.spinnerSize.adapter = adapter
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun setupClickListeners() {
        binding.btnSubmit.setOnClickListener {
            submitReport()
        }

        binding.btnUploadImage.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        binding.btnDateFound.setOnClickListener {
            showDatePicker()
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
                binding.btnDateFound.text = dateFormat.format(selectedDate.time)
            },
            selectedDate.get(Calendar.YEAR),
            selectedDate.get(Calendar.MONTH),
            selectedDate.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun submitReport() {
        val title = binding.etItemTitle.text.toString().trim()
        val currentUser = auth.currentUser

        if (currentUser == null) {
            Toast.makeText(this, "You must be logged in to report an item.", Toast.LENGTH_SHORT).show()
            return
        }

        if (title.isEmpty()) {
            binding.etItemTitle.error = "Title cannot be empty"
            return
        }

        if (imageUri != null) {
            Log.d("UPLOAD", "Uploading from URI: $imageUri")
            Toast.makeText(this, "Uploading image...", Toast.LENGTH_SHORT).show()

            val filename = UUID.randomUUID().toString()
            val storageRef = storage.reference.child("found_items/$filename")

            storageRef.putFile(imageUri!!)
                .addOnSuccessListener {
                    storageRef.downloadUrl
                        .addOnSuccessListener { uri ->
                            val imageUrl = uri.toString()
                            saveItemToFirestore(imageUrl)
                        }
                        .addOnFailureListener { e ->
                            Log.e("UPLOAD_FAILURE", "Failed to get image URL.", e)
                            Toast.makeText(this, "Proceeding without image URL.", Toast.LENGTH_SHORT).show()
                            saveItemToFirestore(null)
                        }
                }
                .addOnFailureListener { e ->
                    Log.e("UPLOAD_FAILURE", "Image upload failed.", e)
                    Toast.makeText(this, "Image upload failed. Proceeding without image.", Toast.LENGTH_SHORT).show()
                    saveItemToFirestore(null)
                }
        } else {
            // No image selected, continue
            saveItemToFirestore(null)
        }
    }

    private fun saveItemToFirestore(imageUrl: String?) {
        val newItem = Item(
            userId = auth.currentUser!!.uid,
            title = binding.etItemTitle.text.toString().trim(),
            type = binding.spinnerItemType.selectedItem.toString(),
            location = binding.spinnerLocation.selectedItem.toString(),
            size = binding.spinnerSize.selectedItem.toString(),
            period = binding.spinnerPeriod.selectedItem.toString(),
            color = binding.spinnerColor.selectedItem.toString(),
            notes = binding.etNotes.text.toString().trim(),
            imageUrl = imageUrl ?: "", // empty string if no image
            dateLost = selectedDate.time
        )

        db.collection("foundItems")
            .add(newItem)
            .addOnSuccessListener {
                Toast.makeText(this, "Item reported successfully!", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Log.e("FIRESTORE_FAILURE", "Error reporting item.", e)
                Toast.makeText(this, "Error reporting item: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
}
