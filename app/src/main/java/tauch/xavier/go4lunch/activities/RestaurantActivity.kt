package tauch.xavier.go4lunch.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_restaurant.*
import tauch.xavier.go4lunch.R

class RestaurantActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant)
    }



    fun onClickFab(){
        floatingActionButton.setOnClickListener{

        }


    }
}
