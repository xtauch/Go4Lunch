package tauch.xavier.go4lunch.models.details

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Period {

    @SerializedName("close")
    @Expose
    var close: Close? = null
    @SerializedName("open")
    @Expose
    var open: Open? = null

}