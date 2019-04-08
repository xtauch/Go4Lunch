package tauch.xavier.go4lunch.auth

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.activity_profile.*
import tauch.xavier.go4lunch.activities.MainActivity
import tauch.xavier.go4lunch.R


/**
 * Created by Xavier TAUCH on 27/03/2019.
 */
class ProfileActivity : MainActivity() {


    //FOR DESIGN
    lateinit var imageViewProfile: ImageView
    lateinit var textInputEditTextUsername: TextInputEditText

    lateinit var updateButton: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        imageViewProfile = profile_activity_imageview_profile
        textInputEditTextUsername = profile_activity_edit_text_username

        configureOnClickUpdateButton()
    }





private fun configureOnClickUpdateButton() {
        profile_activity_button_update
    }
}