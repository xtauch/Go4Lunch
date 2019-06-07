package tauch.xavier.go4lunch.models.search

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Result {

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

    @SerializedName("place_id")
    @Expose
    lateinit var  placeId: String

    @SerializedName("reference")
    @Expose
    var reference: String? = null

    @SerializedName("vicinity")
    @Expose
    val vicinity: String? = null

}