import android.os.Parcel
import android.os.Parcelable
import com.example.touristadminapp.model.Hotel
import com.example.touristadminapp.model.Place
import com.example.touristadminapp.model.Restaurant


data class City(
    val documentId: String? = null,
    val name: String?,
    val description: String?,
    val imageUrl: String?,
    val whereToStay: List<Hotel>? = emptyList(),
    val whatToDo: List<Place>? = emptyList(),
    val whatToEat: List<Restaurant>? = emptyList()
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.createTypedArrayList(Hotel.CREATOR),
        parcel.createTypedArrayList(Place.CREATOR),
        parcel.createTypedArrayList(Restaurant.CREATOR)
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(documentId)
        parcel.writeString(name)
        parcel.writeString(description)
        parcel.writeString(imageUrl)
        parcel.writeTypedList(whereToStay)
        parcel.writeTypedList(whatToDo)
        parcel.writeTypedList(whatToEat)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<City> {
        override fun createFromParcel(parcel: Parcel): City = City(parcel)
        override fun newArray(size: Int): Array<City?> = arrayOfNulls(size)
    }
}

data class Hotel(
    val imageUrl: String,
    val name: String,
    val description: String,
    val contactNumber: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(imageUrl)
        parcel.writeString(name)
        parcel.writeString(description)
        parcel.writeString(contactNumber)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Hotel> {
        override fun createFromParcel(parcel: Parcel): Hotel = Hotel(parcel)
        override fun newArray(size: Int): Array<Hotel?> = arrayOfNulls(size)
    }
}

data class Place(
    val imageUrl: String,
    val name: String,
    val description: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(imageUrl)
        parcel.writeString(name)
        parcel.writeString(description)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Place> {
        override fun createFromParcel(parcel: Parcel): Place = Place(parcel)
        override fun newArray(size: Int): Array<Place?> = arrayOfNulls(size)
    }
}

data class Restaurant(
    val imageUrl: String,
    val address: String,
    val name: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(imageUrl)
        parcel.writeString(address)
        parcel.writeString(name)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Restaurant> {
        override fun createFromParcel(parcel: Parcel): Restaurant = Restaurant(parcel)
        override fun newArray(size: Int): Array<Restaurant?> = arrayOfNulls(size)
    }
}
