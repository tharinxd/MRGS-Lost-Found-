package data.model

import android.os.Parcel
import android.os.Parcelable

data class LostItem() : Parcelable {
    constructor(parcel: Parcel) : this() {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<LostItem> {
        override fun createFromParcel(parcel: Parcel): LostItem {
            return LostItem(parcel)
        }

        override fun newArray(size: Int): Array<LostItem?> {
            return arrayOfNulls(size)
        }
    }
}
