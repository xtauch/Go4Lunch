package tauch.xavier.go4lunch.models.search

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class AltId {

    @SerializedName("place_id")
    @Expose
    var placeId: String? = null
    @SerializedName("scope")
    @Expose
    var scope: String? = null

}