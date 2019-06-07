package tauch.xavier.go4lunch.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView

import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.ListFragment

import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions


import com.google.android.gms.tasks.OnSuccessListener


import com.google.android.libraries.places.api.Places
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng


import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener


import kotlinx.android.synthetic.main.nav_header_main.view.*
import kotlinx.android.synthetic.main.activity_main.*
import tauch.xavier.go4lunch.BuildConfig

import tauch.xavier.go4lunch.R
import tauch.xavier.go4lunch.api.UserHelper
import tauch.xavier.go4lunch.fragments.MapFragment
import tauch.xavier.go4lunch.fragments.WorkmatesFragment
import tauch.xavier.go4lunch.models.User
import tauch.xavier.go4lunch.auth.ProfileActivity
import java.util.*

open class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    //FOR DESIGN
    private lateinit var toolbar: Toolbar
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var header: View

    private lateinit var mUserImage: ImageView
    private lateinit var mUserEmail: TextView
    private lateinit var mUserName: TextView
    private lateinit var autocompleteFragment : AutocompleteSupportFragment

    private lateinit var placesClient : PlacesClient




    override fun onStart() {
        super.onStart()
        // Create a new Places client instance
        placesClient = Places.createClient(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        this.configureToolbar()
        this.configureDrawerLayout()
        this.configureNavigationView()
        this.updateUIWhenCreating()


        if (!Places.isInitialized()) {
            // Initialize Places
            Places.initialize(applicationContext, BuildConfig.API_KEY_GOOGLE_PLACES)
        }
        // Initialize the AutocompleteSupportFragment.
        autocompleteFragment = supportFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME))

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener{

            override fun onPlaceSelected(place: Place) {
                // TODO: Get info about the selected place.
                cardView.visibility = View.INVISIBLE
                toolbar.visibility = View.VISIBLE
                Log.i("AutocompleteSelected", "Place: " + place.name + ", " + place.id)
            }

            override fun onError(status: Status) {
                // TODO: Handle the error.
                Log.i("AutocompleteError", "An error occurred: $status")
            }
        })

        bottomNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }



    private fun configureToolbar() {
        // Get the toolbar view inside the activity layout
        this.toolbar = findViewById(R.id.toolbar)
        // Sets the Toolbar
        setSupportActionBar(toolbar)

    }




    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu and add it to the Toolbar
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_maps -> {
                supportFragmentManager.beginTransaction()
                    .replace(
                        R.id.fragment_container,
                        MapFragment()
                    ).commit()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_list -> {
                supportFragmentManager.beginTransaction()
                    .replace(
                        R.id.fragment_container,
                        ListFragment()
                    ).commit()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_workmates -> {
                supportFragmentManager.beginTransaction()
                    .replace(
                        R.id.fragment_container,
                        WorkmatesFragment()
                    ).commit()
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onBackPressed() {
        // 5 - Handle back click to close menu
        if (this.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_search -> {
                cardView.visibility = View.VISIBLE
                toolbar.visibility = View.INVISIBLE
            }
            else -> {
            }
        }
        return super.onOptionsItemSelected(item)
    }


    // ---------------------
    // NAVIGATION ITEM CLICK
    // ---------------------

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
           // R.id.activity_main_drawer_yourlunch -> startActivity(Intent(this, SearchActivity::class.java))
            R.id.activity_main_drawer_settings -> startActivity(Intent(this, ProfileActivity::class.java))
            R.id.activity_main_drawer_logout -> signOutUserFromFirebase()
            else -> {}
        }
        this.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    // ---------------------
    // CONFIG
    // ---------------------

    // ---------------------
    // DRAWER LAYOUT
    // ---------------------

    private fun configureDrawerLayout() {
        drawerLayout = drawer_layout
        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close)


        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    }

    // ---------------------
    // NAVIGATION VIEW
    // ---------------------

    private fun configureNavigationView() {
        this.navigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        header = navigationView.getHeaderView(0)
        mUserImage = header.userImage
        mUserEmail = header.userEmail
        mUserName = header.userName


    }


    // --------------------
    // UI
    // --------------------

    // 1 - Update UI when activity is creating
    private fun updateUIWhenCreating(){
        if (this.getCurrentUser() != null){

            //Get picture URL from Firebase
            if (getCurrentUser()?.photoUrl != null) {
                Glide.with(this)
                    .load(getCurrentUser()?.photoUrl)
                    .apply(RequestOptions.circleCropTransform())
                    .into(mUserImage)
            }

            //Get email & username from Firebase
            val email: String? = getCurrentUser()?.email

            UserHelper.getUser(getCurrentUser()!!.uid).addOnSuccessListener { documentSnapshot ->
                    val currentUser: User? = documentSnapshot?.toObject(User::class.java)
                    val dbUsername = currentUser?.username
                    this.mUserName.text = dbUsername
                }


            //Update views with data
            this.mUserEmail.text = email
        }
    }






    // --------------------
    // REST REQUESTS
    // --------------------
    // Create http requests (SignOut & Delete)

     private fun signOutUserFromFirebase(){
        AuthUI.getInstance()
                .signOut(this)
                .addOnSuccessListener(this, this.updateUIAfterRESTRequestsCompleted(SIGN_OUT_TASK))
    }

    private fun deleteUserFromFirebase(){
        if (this.getCurrentUser() != null) {
            AuthUI.getInstance()
                    .delete(this)
                    .addOnSuccessListener(this, this.updateUIAfterRESTRequestsCompleted(DELETE_USER_TASK))
        }
    }




     private fun updateUIAfterRESTRequestsCompleted(id : Int):OnSuccessListener<Void>{
         return OnSuccessListener {
                  when (id){
                      SIGN_OUT_TASK -> finish()
                      DELETE_USER_TASK -> finish()
                      else -> {}
            }
         }
    }

















private fun getCurrentUser(): FirebaseUser? { return FirebaseAuth.getInstance().currentUser }

private fun isCurrentUserLogged(): Boolean { return (this.getCurrentUser() != null) }







companion object {

        // Assign each fragment with a number
        private const val FRAGMENT_MAPS = 0
        private const val FRAGMENT_LISTVIEW = 1
        private const val FRAGMENT_WORKMATES = 2

       // Identify each Http Request
        private const val SIGN_OUT_TASK = 10
        private const val DELETE_USER_TASK = 20
        private const val UPDATE_USERNAME = 30

    }
}