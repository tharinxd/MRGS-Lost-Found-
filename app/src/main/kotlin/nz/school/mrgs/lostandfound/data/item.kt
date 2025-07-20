package nz.school.mrgs.lostandfound.data

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

// So this is my data class. It's basically a template for a lost item.
// It defines all the pieces of information that make up a single item report.
// I'll use this to organize the data before I save it to the Firestore database.
data class Item(
    val userId: String = "",
    val title: String = "",
    val type: String = "",
    val location: String = "",
    val size: String = "",
    val period: String = "",
    val color: String = "",
    val notes: String = "",
    @ServerTimestamp val dateLost: Date? = null,
    @ServerTimestamp val reportTimestamp: Date? = null
)


//forgot to push the commit, however i have fixed the filtering option in lost items.