package tauch.xavier.go4lunch.fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableObserver
import tauch.xavier.go4lunch.BuildConfig
import tauch.xavier.go4lunch.R
import tauch.xavier.go4lunch.activities.RestaurantActivity
import tauch.xavier.go4lunch.adapters.ListViewAdapter
import tauch.xavier.go4lunch.fragments.MapFragment.Companion.LOCATION_LAT
import tauch.xavier.go4lunch.fragments.MapFragment.Companion.LOCATION_LNG
import tauch.xavier.go4lunch.models.details.DetailsResponse
import tauch.xavier.go4lunch.models.details.DetailsResult
import tauch.xavier.go4lunch.models.search.Result
import tauch.xavier.go4lunch.models.search.SearchResponse
import tauch.xavier.go4lunch.utils.ItemClickSupport
import tauch.xavier.go4lunch.utils.GooglePlacesStream
import java.util.*

/**
 * Created by Xavier TAUCH on 25/03/2019.
 */
class ListViewFragment : Fragment(){


    private lateinit var mRecyclerView: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var mProgressBar: ProgressBar

    private lateinit var userLocation: Location
    private lateinit var restaurantLocation: Location

    //FOR DATA
    var mDetailsResults: MutableList<DetailsResult> = ArrayList()
    var mDistanceResults: MutableList<String> = ArrayList()
    lateinit var mSearchResponse: MutableList<Result>

    private lateinit var mListViewAdapter: ListViewAdapter
    private var mDisposable: Disposable? = null

    var mPreferences: SharedPreferences? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.list_view, container, false)

        mRecyclerView = view.findViewById(R.id.RecycleRestaurantList) as RecyclerView
        swipeRefreshLayout = view.findViewById(R.id.fragment_main_swipe_container) as SwipeRefreshLayout
        mProgressBar = view.findViewById(R.id.loadingPanel) as ProgressBar


        mPreferences = activity!!.applicationContext.getSharedPreferences(
            MapFragment.PREFS_MAPS,
            Context.MODE_PRIVATE
        )



        this.configureRecyclerView()
        this.executeHttpNearbyRestaurantsRequest()
        this.configureSwipeRefreshLayout()
        this.configureOnClickRecyclerView()
        return view
    }

    override fun onDestroy() {
        Log.e("onDestroy", "onDestroy TopStoriesFragment")
        this.disposeWhenDestroy()
        super.onDestroy()
    }

    // -----------------
    // ACTION
    // -----------------

    // Configure click on RecyclerView's item
    private fun configureOnClickRecyclerView() {
        ItemClickSupport.addTo(mRecyclerView, R.layout.restaurant_list_fragment_item)
            .setOnItemClickListener(object : ItemClickSupport.OnItemClickListener {
                override fun onItemClicked(recyclerView: RecyclerView, position: Int, v: View) {
                    val intent = Intent(context, RestaurantActivity::class.java)
                    intent.putExtra("getRestaurantName", mDetailsResults[position].name)
                    intent.putExtra("getRestaurantAddress", mDetailsResults[position].formattedAddress)
                    intent.putExtra("getRestaurantUrl", mDetailsResults[position].url)
                    intent.putExtra("getRestaurantRating", mDetailsResults[position].rating)
                    startActivity(intent)
                }
            })
    }


    // -----------------
    // CONFIGURATION
    // -----------------

    // 3 - Configure RecyclerView, Adapter, LayoutManager & glue it together
    private fun configureRecyclerView() {
        // 3.1 - Reset list
        this.mDetailsResults = ArrayList()
        // 3.2 - Create mListViewAdapter passing the top stories
        this.mListViewAdapter = ListViewAdapter(this.mDetailsResults, Glide.with(this))
        // 3.3 - Attach the mListViewAdapter to the RecyclerView to populate items
        this.mRecyclerView.adapter = this.mListViewAdapter
        // 3.4 - Set layout manager to position the items
        val mLayoutManager = LinearLayoutManager(context)
        this.mRecyclerView.layoutManager = mLayoutManager

        mRecyclerView.addItemDecoration(
            DividerItemDecoration(
                mRecyclerView.context,
                DividerItemDecoration.VERTICAL
            )
        )


    }

    // Configure the SwipeRefreshLayout
    private fun configureSwipeRefreshLayout() {
        swipeRefreshLayout.setOnRefreshListener { executeHttpNearbyRestaurantsRequest() }
    }

    // ------------------------------
    //  HTTP REQUEST (RxJAVA)
    // ------------------------------


    // Execute our GooglePlacesStream
    private fun executeHttpNearbyRestaurantsRequest() {
        // Execute the stream subscribing to Observable defined inside GooglePlacesStream
        this.mDisposable =
            GooglePlacesStream.fetchNearbyRestaurants("","restaurant","distance", BuildConfig.API_KEY_GOOGLE_PLACES).subscribeWith(object : DisposableObserver<SearchResponse>() {

                override fun onNext(searchResponse: SearchResponse) {
                    Log.e("ListViewF - onNext", "On Next ListViewFragment")
                    // Update RecyclerView after getting results from API
                    makeList(searchResponse.results)

                    for (se in mSearchResponse){
                        executeHttpDetailsRequest(se.placeId)
                        }

                    }


                override fun onError(e: Throwable) {
                    Log.e("ListViewF - onError", Log.getStackTraceString(e))
                    Toast.makeText(context, "Une erreur est survenue ", Toast.LENGTH_LONG).show()
                }

                override fun onComplete() {
                    Log.e("ListViewF - onComplete", "On Complete ListViewFragment")

                }
            })
    }


    private fun executeHttpDetailsRequest(placeId: String) {
        // Execute the stream subscribing to Observable defined inside GooglePlacesStream
        this.mDisposable =
            GooglePlacesStream.fetchDetails(placeId,BuildConfig.API_KEY_GOOGLE_PLACES).subscribeWith(object : DisposableObserver<DetailsResponse>() {

                override fun onNext(detailsResponse: DetailsResponse) {
                    Log.e("ListViewF - onNext", "On Next ListViewFragment")
                    // Update RecyclerView after getting results from API
                    updateUI(detailsResponse.detailsResult)
                    detailsResponse.detailsResult.distance = calculateDistance((detailsResponse.detailsResult.geometry!!.location),mDistanceResults)

                }
                override fun onError(e: Throwable) {
                    Log.e("ListViewF - onError", Log.getStackTraceString(e))
                    Toast.makeText(context, "Une erreur est survenue ", Toast.LENGTH_LONG).show()
                }

                override fun onComplete() {
                    Log.e("ListViewF - onComplete", "On Complete ListViewFragment")

                }
            })
    }




    private fun disposeWhenDestroy() {
        if (this.mDisposable != null && !this.mDisposable!!.isDisposed) this.mDisposable!!.dispose()
    }




    fun calculateDistance(mLocation: tauch.xavier.go4lunch.models.details.Location, mList : MutableList<String>): List<String>{

        userLocation.latitude = mPreferences?.getString(LOCATION_LAT, "48.864716")!!.toDouble()
        userLocation.longitude = mPreferences?.getString(LOCATION_LNG, "2.349014")!!.toDouble()

        restaurantLocation.latitude = mLocation.lat
        restaurantLocation.longitude = mLocation.lng

        mList.add(userLocation.distanceTo(restaurantLocation).toString())

        return mList
    }



    // ------------------
    //  UPDATE UI
    // ------------------

    fun makeList(searchResponse: List<Result>){
        mSearchResponse.addAll(searchResponse)
    }

    fun updateUI(results: DetailsResult) {
        mProgressBar.visibility = View.GONE
        swipeRefreshLayout.isRefreshing = false
        mDetailsResults.clear()
        mDetailsResults.add(results)
        mListViewAdapter.notifyDataSetChanged()
    }



    companion object {

        fun newInstance(): ListViewFragment {
            return ListViewFragment()
        }
    }
}