package nz.school.mrgs.lostandfound.data

import android.os.Parcelable
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class Item(
    val userId: String = "",
    val title: String = "",
    val type: String = "",
    val location: String = "",
    val size: String = "",
    val period: String = "",
    val color: String = "",
    val notes: String = "",
    val imageUrl: String? = null, // Optional image URL

    @ServerTimestamp val dateLost: Date? = null,   // For lost items
    @ServerTimestamp val dateFound: Date? = null,  //  For found items

    @ServerTimestamp val reportTimestamp: Date? = null
) : Parcelable
