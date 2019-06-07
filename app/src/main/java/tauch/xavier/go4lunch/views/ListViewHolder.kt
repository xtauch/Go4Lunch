package tauch.xavier.go4lunch.views

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import kotlinx.android.synthetic.main.restaurant_list_fragment_item.view.*
import tauch.xavier.go4lunch.R
import tauch.xavier.go4lunch.models.details.DetailsResult
import android.location.Location


class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {



    fun updateWithRestaurants(mDetailsResult: DetailsResult, glide: RequestManager) {
        // Restaurant Name
        itemView.fragment_item_restaurant_name.text = mDetailsResult.name
        // Restaurant Address
        itemView.fragment_item_address.text = mDetailsResult.formattedAddress
        // Restaurant Rating
        itemView.fragment_item_rating.rating = mDetailsResult.rating!!.toFloat()



        val restaurantLocation = Location("")
        restaurantLocation.latitude = mDetailsResult.geometry?.location!!.lat
        restaurantLocation.longitude = mDetailsResult.geometry.location!!.lng


        // Restaurant Distance
        itemView.fragment_item_distance.text = distance.toString()


        // Restaurant Photo
        if (mDetailsResult.photos!!.isNotEmpty()) {
            glide.load(mDetailsResult.photos[0].htmlAttributions)
                .into(itemView.fragment_item_restaurant_image)
        } else {
            itemView.fragment_item_restaurant_image!!.setImageResource(R.drawable.ic_home_black_24dp)
        }



    }
}