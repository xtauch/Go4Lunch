package tauch.xavier.go4lunch.models.details


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Close {

    @SerializedName("day")
    @Expose
    var day: Int? = null
    @SerializedName("time")
    @Expose
    var time: String? = null

}