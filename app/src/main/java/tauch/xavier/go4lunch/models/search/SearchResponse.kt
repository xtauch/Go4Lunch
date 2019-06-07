package tauch.xavier.go4lunch.models.search

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class SearchResponse {

    @SerializedName("html_attributions")
    @Expose
    var htmlAttributions: List<Any>? = null
    @SerializedName("results")
    @Expose
    lateinit var results: List<Result>
    @SerializedName("status")
    @Expose
    var status: String? = null

}