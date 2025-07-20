package nz.school.mrgs.lostandfound

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import nz.school.mrgs.lostandfound.data.Item
import nz.school.mrgs.lostandfound.databinding.ItemCardWithImageLayoutBinding
import java.text.SimpleDateFormat
import java.util.*

class FoundItemsAdapter(
    private var items: List<Item>,
    private val onItemClicked: (Item) -> Unit
) : RecyclerView.Adapter<FoundItemsAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = ItemCardWithImageLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val currentItem = items[position]
        holder.bind(currentItem)
    }

    override fun getItemCount(): Int = items.size

    fun updateItems(newItems: List<Item>) {
        items = newItems
        notifyDataSetChanged()
    }

    inner class ItemViewHolder(private val binding: ItemCardWithImageLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.infoButton.setOnClickListener {
                onItemClicked(items[adapterPosition])
            }
        }

        fun bind(item: Item) {
            binding.itemTitle.text = item.title

            // Format and set the found date (reusing dateLost for now since Item doesn't have dateFound)
            if (item.dateLost != null) {
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                binding.itemDate.text = dateFormat.format(item.dateLost)
            } else {
                binding.itemDate.text = "Date not set"
            }

            // Load image using Glide
            if (!item.imageUrl.isNullOrEmpty()) {
                Glide.with(binding.itemImage.context)
                    .load(item.imageUrl)
                    .centerCrop()
                    .into(binding.itemImage)
            } else {
                // Replace this with a real drawable you have in /res/drawable
                binding.itemImage.setImageResource(R.drawable.placeholder_image)
            }
        }
    }
}
