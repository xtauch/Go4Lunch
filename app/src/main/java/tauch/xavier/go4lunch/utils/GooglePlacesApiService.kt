package tauch.xavier.go4lunch.utils

import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import tauch.xavier.go4lunch.models.details.DetailsResponse
import tauch.xavier.go4lunch.models.search.SearchResponse


interface GooglePlacesApiService {


    // Place Search API call
    @GET("nearbysearch/json")
    fun getNearbyPlaces(
        @Query("location") location: String,
        @Query("type") type: String,
        @Query("rankby") rankby: String,
        @Query("key") apiKey: String
    ): Observable<SearchResponse>

    // Place Details API call
    @GET("details/json")
    fun getDetails(
        @Query("placeid") placeId: String,
        @Query("key") apiKey: String
    ): Observable<DetailsResponse>


    companion object {
        fun create(): GooglePlacesApiService {
            val retrofit = Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/maps/api/place/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()

            return retrofit.create(GooglePlacesApiService::class.java)
        }
    }
}