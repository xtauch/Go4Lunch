package tauch.xavier.go4lunch.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import tauch.xavier.go4lunch.R
import java.util.*

/**
 * Created by Xavier TAUCH on 25/03/2019.
 */
class ListViewFragment : Fragment(){




    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.list_view, container, false)



        return view
    }








    companion object {

        fun newInstance(): ListViewFragment {
            return ListViewFragment()
        }
    }
}