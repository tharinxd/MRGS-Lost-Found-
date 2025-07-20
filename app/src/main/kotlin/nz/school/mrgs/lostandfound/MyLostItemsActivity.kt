package nz.school.mrgs.lostandfound

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import nz.school.mrgs.lostandfound.data.Item
import nz.school.mrgs.lostandfound.databinding.ActivityMylostitemsBinding

// This is the Kotlin file for my "My Lost Items" page.
class MyLostItemsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMylostitemsBinding
    private lateinit var adapter: LostItemsAdapter
    private val db = Firebase.firestore
    private val auth = Firebase.auth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMylostitemsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // This makes the back button in my toolbar work.
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        // I'm setting up the same adapter I used for the other page.
        // When a card is clicked, it will open the same detail page.
        adapter = LostItemsAdapter(listOf()) { selectedItem ->
            val intent = Intent(this, ItemDetailActivity::class.java)
            intent.putExtra("EXTRA_ITEM", selectedItem)
            startActivity(intent)
        }
        binding.recyclerViewMyLostItems.adapter = adapter

        // This function will get the data from my Firestore database.
        fetchMyLostItems()
    }

    // This is the function that connects to Firestore and gets only the current user's items.
    private fun fetchMyLostItems() {
        val currentUser = auth.currentUser
        // First, I check to make sure a user is actually logged in.
        if (currentUser == null) {
            Toast.makeText(this, "You need to be logged in to see your items.", Toast.LENGTH_SHORT).show()
            return
        }

        // This is the most important part.
        // I'm querying the "lostItems" collection, but I'm adding a 'whereEqualTo' clause.
        // This tells Firestore to only give me the documents where the 'userId' field
        // exactly matches the ID of the person who is currently logged in.
        db.collection("lostItems")
            .whereEqualTo("userId", currentUser.uid)
            .get()
            .addOnSuccessListener { result ->
                // The rest of the code is the same as before.
                val items = result.toObjects(Item::class.java)
                adapter.updateItems(items)
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error getting your items: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
