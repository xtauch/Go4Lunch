package tauch.xavier.go4lunch.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import tauch.xavier.go4lunch.R
import tauch.xavier.go4lunch.models.details.DetailsResult
import tauch.xavier.go4lunch.views.ListViewHolder

class ListViewAdapter // CONSTRUCTOR
    (
    private val mDetailsResults: List<DetailsResult>, private val glide: RequestManager
) : RecyclerView.Adapter<ListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        // CREATE VIEW HOLDER AND INFLATING ITS XML LAYOUT
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.restaurant_list_fragment_item, parent, false)

        return ListViewHolder(view)
    }


    // UPDATE VIEW HOLDER WITH A TOP STORY
    override fun onBindViewHolder(viewHolder: ListViewHolder, position: Int) {
        viewHolder.updateWithRestaurants(this.mDetailsResults[position], this.glide)
    }

    // RETURN THE TOTAL COUNT OF ITEMS IN THE LIST
    override fun getItemCount(): Int {
        return this.mDetailsResults.size
    }

}