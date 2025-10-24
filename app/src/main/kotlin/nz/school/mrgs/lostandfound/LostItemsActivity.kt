package nz.school.mrgs.lostandfound

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import nz.school.mrgs.lostandfound.data.Item
import nz.school.mrgs.lostandfound.databinding.ActivityLostitemsactivityBinding

class LostItemsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLostitemsactivityBinding
    private lateinit var adapter: LostItemsAdapter
    private val db = Firebase.firestore

    // This list will hold the items for the currently displayed category.
    private var currentItemsList: List<Item> = listOf()
    private var currentCategory: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLostitemsactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        adapter = LostItemsAdapter(listOf()) { selectedItem ->
            val intent = Intent(this, ItemDetailActivity::class.java)
            intent.putExtra("EXTRA_ITEM", selectedItem)
            startActivity(intent)
        }
        binding.recyclerViewLostItems.adapter = adapter

        // Get the category from the intent that started this activity.
        currentCategory = intent.getStringExtra("CATEGORY")

        // Set the spinner to the correct category passed from the previous screen.
        currentCategory?.let {
            val categoryAdapter = binding.spinnerCategory.adapter as ArrayAdapter<String>
            val position = categoryAdapter.getPosition(it)
            if (position >= 0) {
                binding.spinnerCategory.setSelection(position)
            }
        }

        // Fetch items for the initially selected category.
        fetchItemsForCategory(currentCategory)

        // Setup listeners for local filtering.
        setupFilterControls()
    }

    private fun setupFilterControls() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String?): Boolean {
                applyLocalFilters()
                return true
            }
            override fun onQueryTextSubmit(query: String?): Boolean { return false }
        })

        // This listener re-fetches from Firestore when the user selects a different category.
        binding.spinnerCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedCategory = parent?.getItemAtPosition(position).toString()
                if (currentCategory != selectedCategory) {
                    currentCategory = selectedCategory
                    fetchItemsForCategory(currentCategory)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // The color spinner just filters the list we already have.
        binding.spinnerColor.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                applyLocalFilters()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    // This function filters the list that is already in memory.
    private fun applyLocalFilters() {
        val searchQuery = binding.searchView.query.toString().lowercase()
        val selectedColor = binding.spinnerColor.selectedItem.toString()

        var filteredList = currentItemsList

        if (searchQuery.isNotEmpty()) {
            filteredList = filteredList.filter { it.title.lowercase().contains(searchQuery) }
        }

        // Correctly filter by color, allowing for "All Colors"
        if (selectedColor != "All Colors") {
            filteredList = filteredList.filter { it.color == selectedColor }
        }

        adapter.updateItems(filteredList)
    }

    // This function fetches from Firestore based on the category.
    private fun fetchItemsForCategory(category: String?) {
        // DEFENSIVE CHECK: If no category is passed, show an empty list. This is key.
        if (category.isNullOrEmpty()) {
            currentItemsList = emptyList()
            applyLocalFilters() // Update the adapter with the empty list.
            Toast.makeText(this, "No category was selected.", Toast.LENGTH_SHORT).show()
            return
        }

        val query: Query = if (category == "All Items") {
            db.collection("lostItems")
        } else {
            db.collection("lostItems").whereEqualTo("type", category)
        }

        query.get()
            .addOnSuccessListener { result ->
                currentItemsList = result.toObjects(Item::class.java)
                // Reset local filters and display the new list.
                binding.searchView.setQuery("", false)
                binding.spinnerColor.setSelection(0)
                applyLocalFilters()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error getting items: ${exception.message}", Toast.LENGTH_SHORT).show()
                currentItemsList = emptyList()
                applyLocalFilters()
            }
    }
}
