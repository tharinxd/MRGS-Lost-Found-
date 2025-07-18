package nz.school.mrgs.lostandfound.data

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

// So this is my data class. It's basically a template for a lost item.
// It defines all the pieces of information that make up a single item report.
// I'll use this to organize the data before I save it to the Firestore database.
data class Item(
    val userId: String = "",
    val title: String = "",
    val location: String = "",
    val size: String = "",
    val period: String = "",
    val color: String = "",
    val notes: String = "",
    // I'm using @ServerTimestamp so that Firestore automatically adds the date and time
    // when the data is saved. This is super useful.
    @ServerTimestamp val dateLost: Date? = null,
    @ServerTimestamp val reportTimestamp: Date? = null
)
