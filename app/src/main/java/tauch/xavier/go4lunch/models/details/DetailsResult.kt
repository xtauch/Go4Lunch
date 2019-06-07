package tauch.xavier.go4lunch.models.details

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class DetailsResult {
    @Expose
    val formattedAddress: String? = null

    @SerializedName("formatted_phone_number")
    @Expose
    val formattedPhoneNumber: String? = null

    @SerializedName("geometry")
    @Expose
    val geometry: Geometry? = null

    @SerializedName("icon")
    @Expose
    var icon: String? = null

    @SerializedName("id")
    @Expose
    var id: String? = null

    @SerializedName("name")
    @Expose
    var name: String? = null

    @SerializedName("opening_hours")
    @Expose
    val openingHours: OpeningHours? = null

    @SerializedName("photos")
    @Expose
    val photos: List<Photo>? = null

    @SerializedName("place_id")
    @Expose
    val placeId: String? = null

    @SerializedName("rating")
    @Expose
    val rating: Double? = null

    @SerializedName("reference")
    @Expose
    var reference: String? = null

    @SerializedName("types")
    @Expose
    val types: List<String>? = null
    @SerializedName("url")
    @Expose
    var url: String? = null

    @SerializedName("website")
    @Expose
    val website: String? = null

    lateinit var distance: List<String>
}