package nz.school.mrgs.lostandfound.data

import android.os.Parcelable
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class Item(
    val userId: String = "",
    val title: String = "",
    val type: String = "", // I added this missing 'type' property.
    val location: String = "",
    val size: String = "",
    val period: String = "",
    val color: String = "",
    val notes: String = "",
    @ServerTimestamp val dateLost: Date? = null,
    @ServerTimestamp val reportTimestamp: Date? = null
) : Parcelable
