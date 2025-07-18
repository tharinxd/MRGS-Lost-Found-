package nz.school.mrgs.lostandfound

import android.os.Bundle
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // I'm connecting the binding class that was auto-generated from my 'activity_lostitemsactivity.xml' file.
        binding = ActivityLostitemsactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // This makes the back button in my toolbar work.
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        // THE FIX IS HERE: I'm now passing the second required parameter to the adapter.
        // This block of code { selectedItem -> ... } is the function that will run
        // whenever a card in the list is clicked.
        adapter = LostItemsAdapter(listOf()) { selectedItem ->
            // For now, I'll just show a message with the item's title.
            // Later, I can change this to open a new page with the full details.
            Toast.makeText(this, "Clicked on: ${selectedItem.title}", Toast.LENGTH_SHORT).show()
        }
        binding.recyclerViewLostItems.adapter = adapter

        // This function will get the data from my Firestore database.
        fetchLostItems()
    }

    // This is the function that connects to Firestore and gets all the lost item reports.
    private fun fetchLostItems() {
        db.collection("lostItems")
            .get()
            .addOnSuccessListener { result ->
                // If it successfully gets the data, it converts the documents into my 'Item' data class.
                val items = result.toObjects(Item::class.java)
                // Then, I update my adapter with this new list of items, which makes them appear on the screen.
                adapter.updateItems(items)
            }
            .addOnFailureListener { exception ->
                // If there's an error, I just show a message.
                Toast.makeText(this, "Error getting items: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
