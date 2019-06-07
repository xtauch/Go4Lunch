package tauch.xavier.go4lunch.models.details


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class DetailsResponse {

    @SerializedName("html_attributions")
    @Expose
    var htmlAttributions: List<Any>? = null
    @SerializedName("result")
    @Expose
    lateinit var detailsResult: DetailsResult
    @SerializedName("status")
    @Expose
    var status: String? = null

}