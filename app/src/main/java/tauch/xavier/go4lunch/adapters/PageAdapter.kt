package tauch.xavier.go4lunch.adapters

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import tauch.xavier.go4lunch.fragments.ListViewFragment
import tauch.xavier.go4lunch.fragments.MapFragment
import tauch.xavier.go4lunch.fragments.WorkmatesFragment


class PageAdapter//Default Constructor
    (mgr: FragmentManager, private val mContext: Context) : FragmentPagerAdapter(mgr) {

    override fun getCount(): Int {
        return 3
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 //Page number 1
            -> MapFragment.newInstance()
            1 //Page number 2
            -> ListViewFragment.newInstance()
            else //Page number 3
            -> WorkmatesFragment.newInstance()
        }
    }
}