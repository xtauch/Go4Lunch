package tauch.xavier.go4lunch.utils

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import tauch.xavier.go4lunch.models.details.DetailsResponse
import tauch.xavier.go4lunch.models.search.SearchResponse
import java.util.concurrent.TimeUnit

object GooglePlacesStream {


    private val apiService by lazy {
        GooglePlacesApiService.create()
    }

    fun fetchNearbyRestaurants(
        location: String,
        type: String,
        rankby: String,
        apiKey: String
    ): Observable<SearchResponse> {
        return apiService.getNearbyPlaces(location, type, rankby, apiKey)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .timeout(10, TimeUnit.SECONDS)
    }

    fun fetchDetails(
        placeId: String,
        apiKey: String
    ): Observable<DetailsResponse>{
        return apiService.getDetails(placeId, apiKey)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .timeout(10, TimeUnit.SECONDS)
    }

}