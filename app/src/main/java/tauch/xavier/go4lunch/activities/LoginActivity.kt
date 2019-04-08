package tauch.xavier.go4lunch.activities


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.constraintlayout.widget.ConstraintLayout

import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_login.*

import tauch.xavier.go4lunch.R
import java.util.*

class LoginActivity : AppCompatActivity() {


    //FOR DATA
    // 1 - Identifier for Sign-In Activity
    private val RC_SIGN_IN = 123

    private val googleProvider = arrayListOf(
        AuthUI.IdpConfig.GoogleBuilder().build() // GOOGLE
    )

    private val facebookProvider = arrayListOf(
        AuthUI.IdpConfig.FacebookBuilder().build() // FACEBOOK
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        if (FirebaseAuth.getInstance().currentUser != null)
            startMainActivity()

        googleLoginButton.setOnClickListener {
            startSignInActivity(googleProvider)
        }
        facebookLoginButton.setOnClickListener {
            startSignInActivity(facebookProvider)
        }


    }


    // --------------------
    // NAVIGATION
    // --------------------


    // 2 - Launch Sign-In Activity
    private fun startSignInActivity(provider: ArrayList<AuthUI.IdpConfig>) {
        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setTheme(R.style.LoginTheme)
                .setAvailableProviders(provider)
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


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        // 4 - Handle SignIn Activity response on activity result
        this.handleResponseAfterSignIn(requestCode, resultCode, data);
    }


    // --------------------
    // UI
    // --------------------

    // 2 - Show Snack Bar with a message
    private fun showSnackBar(constraintLayout: ConstraintLayout, message: String) {
        Snackbar.make(constraintLayout, message, Snackbar.LENGTH_SHORT).show()
    }

    // --------------------
    // UTILS
    // --------------------

    // 3 - Method that handles response after SignIn Activity close
    private fun handleResponseAfterSignIn(requestCode: Int, resultCode: Int, data: Intent) {

        val response: IdpResponse? = IdpResponse.fromResultIntent(data)

        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) { // SUCCESS
                showSnackBar(this.constraintLayout, getString(R.string.connection_succeed))
                startMainActivity()
            } else { // ERRORS
                when {
                    response == null -> showSnackBar(
                        this.constraintLayout,
                        getString(R.string.error_authentication_canceled)
                    )
                    response.error?.errorCode == ErrorCodes.NO_NETWORK -> showSnackBar(
                        this.constraintLayout,
                        getString(R.string.error_no_internet)
                    )
                    response.error?.errorCode == ErrorCodes.UNKNOWN_ERROR -> showSnackBar(
                        this.constraintLayout,
                        getString(R.string.error_unknown_error)
                    )
                }
            }
        }
    }



}
