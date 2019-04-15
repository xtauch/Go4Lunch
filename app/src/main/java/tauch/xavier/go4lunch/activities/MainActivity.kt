package tauch.xavier.go4lunch.activities

import android.content.Intent
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
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.ListFragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.common.api.Status
import com.google.android.gms.tasks.OnSuccessListener


import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import tauch.xavier.go4lunch.auth.ProfileActivity

// Add import statements for the new library.
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import java.util.*
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.android.synthetic.main.nav_header_main.*
import kotlinx.android.synthetic.main.nav_header_main.view.*
import tauch.xavier.go4lunch.R
import tauch.xavier.go4lunch.api.UserHelper
import tauch.xavier.go4lunch.fragments.MapFragment
import tauch.xavier.go4lunch.fragments.WorkmatesFragment
import tauch.xavier.go4lunch.models.User


open class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    //FOR DESIGN
    private var toolbar: Toolbar? = null
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var header: View

    private lateinit var mUserImage: ImageView
    private lateinit var mUserEmail: TextView
    private lateinit var mUserName: TextView



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        // Initialize Places.
        Places.initialize(applicationContext, "AIzaSyCdk8PTVsnRCFw9BUeAJ8gK4A8WXI52Uzo")

        // Create a new Places client instance.
        // var placesClient: PlacesClient = Places.createClient(this)

        configureToolbar()
        // Configure Autocomplete
        configureAutocompleteSupportFragment()

        // Configure DrawerLayout
        this.configureDrawerLayout()

        // Configure NavigationView
        this.configureNavigationView()

        this.updateUIWhenCreating()

        bottomNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }



    private fun configureToolbar() {
        // Get the toolbar view inside the activity layout
        this.toolbar = findViewById(R.id.toolbar)
        // Sets the Toolbar
        setSupportActionBar(toolbar)
    }
    // ---------------------
    // AUTOCOMPLETE
    // ---------------------

    private fun configureAutocompleteSupportFragment() {
        // Initialize the AutocompleteSupportFragment.
        val autocompleteFragment: AutocompleteSupportFragment =
            supportFragmentManager.findFragmentById(R.id.autocomplete) as AutocompleteSupportFragment
        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME))

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                // TODO: Get info about the selected place.
                Log.i("onPlaceSelected", "Place: " + place.name + ", " + place.id)
            }

            override fun onError(status: Status) {
                // TODO: Handle the error.
                Log.i("onErrorAutoComplete", "An error occurred: $status")

            }
        })
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
            R.id.action_search -> {}
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
        this.drawerLayout = findViewById(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
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
                    val mUsername = currentUser?.username
                    userName.text = mUsername
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

      //FOR DATA
    // 2 - Identify each Http Request
    private const val SIGN_OUT_TASK = 10
    private const val DELETE_USER_TASK = 20
    private const val UPDATE_USERNAME = 30

    }
}