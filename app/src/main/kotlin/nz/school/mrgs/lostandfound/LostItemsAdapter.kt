package nz.school.mrgs.lostandfound

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import nz.school.mrgs.lostandfound.data.Item
import nz.school.mrgs.lostandfound.databinding.ItemCardLayoutBinding
import java.text.SimpleDateFormat
import java.util.Locale

// So this is my adapter. Its job is to take my list of lost items
// and prepare them to be displayed on the screen in the RecyclerView.
// The 'onItemClicked' function will be called when a user taps on the info icon.
class LostItemsAdapter(
    private var items: List<Item>,
    private val onItemClicked: (Item) -> Unit
) : RecyclerView.Adapter<LostItemsAdapter.ItemViewHolder>() {

    // This function is called for each item in the list. It creates a new 'ViewHolder'
    // which is basically one of my 'item_card_layout.xml' cards.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = ItemCardLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    // This function takes the data for one item and puts it into the card's TextViews.
    // It also sets up the click listener for the info button.
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val currentItem = items[position]
        holder.bind(currentItem)
    }

    // This just tells the RecyclerView how many items are in my list.
    override fun getItemCount(): Int = items.size

    // This is a helper function so I can update the list of items later when I get them from Firestore.
    fun updateItems(newItems: List<Item>) {
        items = newItems
        notifyDataSetChanged()
    }

    // This inner class represents one single card in my list.
    // I've moved the click listener logic inside here.
    inner class ItemViewHolder(private val binding: ItemCardLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            // This sets up the click listener on the info button itself.
            binding.infoButton.setOnClickListener {
                // When the button is clicked, I get the specific item for this card
                // and call the 'onItemClicked' function that was passed in from my activity.
                onItemClicked(items[adapterPosition])
            }
        }

        fun bind(item: Item) {
            // Here I'm setting the text for the title.
            binding.itemTitle.text = item.title

            // For the date, I need to format it nicely first before displaying it.
            if (item.dateLost != null) {
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                binding.itemDate.text = dateFormat.format(item.dateLost)
            } else {
                binding.itemDate.text = "Date not set"
            }
        }
    }
}
