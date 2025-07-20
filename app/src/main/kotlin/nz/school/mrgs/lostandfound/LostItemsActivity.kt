package nz.school.mrgs.lostandfound

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import nz.school.mrgs.lostandfound.data.Item
import nz.school.mrgs.lostandfound.databinding.ActivityLostitemsactivityBinding

// This is the Kotlin file for my "Lost Items" page.
class LostItemsActivity : AppCompatActivity() {

    // So I'm setting up my variables here.
    // 'binding' connects to my XML layout, 'adapter' is what builds the list,
    // and 'db' is my connection to the Firestore database.
    private lateinit var binding: ActivityLostitemsactivityBinding
    private lateinit var adapter: LostItemsAdapter
    private val db = Firebase.firestore

    // So, I need a place to store all the items I get from the database,
    // so I can filter them without having to download them again.
    private var allItemsList: List<Item> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // I'm connecting the binding class that was auto-generated from my 'activity_lostitemsactivity.xml' file.
        binding = ActivityLostitemsactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // This makes the back button in my toolbar work.
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        // I'm setting up my adapter here. I pass it an empty list to start with,
        // and I also give it the code that should run when an item is clicked.
        adapter = LostItemsAdapter(listOf()) { selectedItem ->
            // For now, I'll just show a message with the item's title.
            // Later, I can change this to open a new page with the full details.
            Toast.makeText(this, "Clicked on: ${selectedItem.title}", Toast.LENGTH_SHORT).show()
        }
        binding.recyclerViewLostItems.adapter = adapter

        // This function will get the data from my Firestore database.
        fetchLostItems()

        // This new function sets up the listeners for my search and filter controls.
        setupFilterControls()
    }

    // This is the new function that makes the search and filters work.
    private fun setupFilterControls() {
        // This is the listener for the search bar.
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            // This runs every time the user types a letter.
            override fun onQueryTextChange(newText: String?): Boolean {
                filterList()
                return true
            }
            // This would run if the user hit the 'enter' key, but I don't need it for now.
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }
        })

        // This is the listener for the category dropdown.
        binding.spinnerCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                filterList() // When the user picks a new category, I re-filter the list.
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // This is the listener for the color dropdown.
        binding.spinnerColor.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                filterList() // When the user picks a new color, I re-filter the list.
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    // This is my main filtering function. It gets called whenever the user searches or changes a filter.
    private fun filterList() {
        // First, I get the current text from the search bar.
        val query = binding.searchView.query.toString().lowercase()
        // Then I get the selected category and color from the dropdowns.
        val selectedCategory = binding.spinnerCategory.selectedItem.toString()
        val selectedColor = binding.spinnerColor.selectedItem.toString()

        // I start with my full list of items.
        var filteredList = allItemsList

        // If the user has typed something in the search bar, I filter the list.
        // I'm only keeping items whose title contains the search text.
        if (query.isNotEmpty()) {
            filteredList = filteredList.filter { item ->
                item.title.lowercase().contains(query)
            }
        }

        // Now I filter by category. I'll need to add a 'type' field to my Item data class for this to work perfectly.
        // For now, I'll assume the 'item_types_array' has a default "Select Type" or similar at the start.
        if (binding.spinnerCategory.selectedItemPosition > 0) { // This checks if a real category is selected
            filteredList = filteredList.filter { item ->
                item.type == selectedCategory
            }
        }

        // I do the same thing for the color filter.
        if (binding.spinnerColor.selectedItemPosition > 0) { // This checks if a real color is selected
            filteredList = filteredList.filter { item ->
                item.color == selectedColor
            }
        }

        // Finally, I update the adapter with the new, fully filtered list.
        adapter.updateItems(filteredList)
    }

    // This is the function that connects to Firestore and gets all the lost item reports.
    private fun fetchLostItems() {
        db.collection("lostItems")
            .get()
            .addOnSuccessListener { result ->
                // If it successfully gets the data, it converts the documents into my 'Item' data class.
                allItemsList = result.toObjects(Item::class.java)
                // Now that I have the full list, I call filterList() to display it for the first time.
                filterList()
            }
            .addOnFailureListener { exception ->
                // If there's an error, I just show a message.
                Toast.makeText(this, "Error getting items: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
