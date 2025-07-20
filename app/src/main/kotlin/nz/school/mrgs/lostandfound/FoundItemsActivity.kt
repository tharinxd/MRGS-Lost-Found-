package nz.school.mrgs.lostandfound

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import nz.school.mrgs.lostandfound.data.Item
import nz.school.mrgs.lostandfound.databinding.ActivityFounditemsactivityBinding

// This is the Kotlin file for my "Found Items" page.
class FoundItemsActivity : AppCompatActivity() {

    // So I'm setting up my variables here.
    // 'binding' connects to my XML layout, 'adapter' is what builds the list,
    // and 'db' is my connection to the Firestore database.
    private lateinit var binding: ActivityFounditemsactivityBinding
    private lateinit var adapter: FoundItemsAdapter // I'm using my new reusable adapter.
    private val db = Firebase.firestore
    private var allItemsList: List<Item> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // I'm connecting the binding class that was auto-generated from my 'activity_founditemsactivity.xml' file.
        binding = ActivityFounditemsactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // This makes the back button in my toolbar work.
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        // I'm setting up my adapter here. When the info button is clicked, it will open the detail page.
        adapter = FoundItemsAdapter(listOf()) { selectedItem ->
            val intent = Intent(this, ItemDetailActivity::class.java)
            intent.putExtra("EXTRA_ITEM", selectedItem)
            startActivity(intent)
        }
        binding.recyclerViewFoundItems.adapter = adapter

        // This function will get the data from my Firestore database.
        fetchFoundItems()

        // This function sets up the listeners for my search and filter controls.
        setupFilterControls()
    }

    // This function makes the search and filters work.
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
                filterList()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // This is the listener for the color dropdown.
        binding.spinnerColor.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                filterList()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    // This is my main filtering function.
    private fun filterList() {
        val query = binding.searchView.query.toString().lowercase()
        val selectedCategory = binding.spinnerCategory.selectedItem.toString()
        val selectedColor = binding.spinnerColor.selectedItem.toString()

        var filteredList = allItemsList

        if (query.isNotEmpty()) {
            filteredList = filteredList.filter { item ->
                item.title.lowercase().contains(query)
            }
        }

        // I'm assuming the first item in the dropdown is a placeholder like "Select Category".
        if (binding.spinnerCategory.selectedItemPosition > 0) {
            filteredList = filteredList.filter { item ->
                item.type == selectedCategory
            }
        }

        // Doing the same check for the color filter.
        if (binding.spinnerColor.selectedItemPosition > 0) {
            filteredList = filteredList.filter { item ->
                item.color == selectedColor
            }
        }

        adapter.updateItems(filteredList)
    }

    // This function connects to Firestore and gets all the found item reports.
    private fun fetchFoundItems() {
        // The only change from the Lost Items page is that I'm looking in the "foundItems" collection.
        db.collection("foundItems")
            .get()
            .addOnSuccessListener { result ->
                allItemsList = result.toObjects(Item::class.java)
                filterList()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error getting items: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
