package tauch.xavier.go4lunch.activities


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import tauch.xavier.go4lunch.R
import java.util.*

class LoginActivity : AppCompatActivity() {

    //FOR DATA
    // 1 - Identifier for Sign-In Activity
    private val RC_SIGN_IN = 123


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (FirebaseAuth.getInstance().currentUser != null)
            startMainActivity()

       setContentView(R.layout.activity_login)
    }



    // --------------------
    // NAVIGATION
    // --------------------


    // 2 - Launch Sign-In Activity
    private fun startSignInActivity() {
        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setTheme(R.style.LoginTheme)
                .setAvailableProviders(
                    Arrays.asList(
                        AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build(), // FACEBOOK
                        AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()) // GOOGLE
                )
                .setIsSmartLockEnabled(false, true)
                .setLogo(R.drawable.ic_launcher_background)
                .build(),
            RC_SIGN_IN
        )
    }


    fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}
