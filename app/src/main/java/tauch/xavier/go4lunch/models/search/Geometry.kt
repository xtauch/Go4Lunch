package tauch.xavier.go4lunch.models.search

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Geometry {

    @SerializedName("location")
    @Expose
    lateinit var location: Location

}