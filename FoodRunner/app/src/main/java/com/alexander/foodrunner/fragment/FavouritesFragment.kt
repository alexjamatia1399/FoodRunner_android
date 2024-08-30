package com.alexander.foodrunner.fragment

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.alexander.foodrunner.R
import com.alexander.foodrunner.adapter.FavouritesRecyclerAdapter
import com.alexander.foodrunner.database.RestaurantDatabase
import com.alexander.foodrunner.database.RestaurantEntity


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class FavouritesFragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null

    lateinit var recyclerFav: RecyclerView
    lateinit var recyclerAdapter: FavouritesRecyclerAdapter
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var loadingLayout : RelativeLayout
    lateinit var loading : ProgressBar
    var favList = listOf<RestaurantEntity>()
    var favArrayList = arrayListOf<RestaurantEntity>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View?
    {
        val favView = inflater.inflate(R.layout.fragment_favourites, container, false)

        recyclerFav = favView.findViewById(R.id.recyclerFav)
        loadingLayout = favView.findViewById(R.id.loadingLayoutFav)
        loading = favView.findViewById(R.id.progressBarFav)

        layoutManager = GridLayoutManager(activity as Context, 2)

        val database = Room.databaseBuilder (activity as Context, RestaurantDatabase::class.java,
                "restaurants-db").build()

        favList = BackendTaskAllFav(database).execute().get()
        favArrayList.addAll(favList)

        if( activity != null)
        {
            loadingLayout.visibility = View.GONE
            recyclerAdapter = FavouritesRecyclerAdapter( activity as Context, favArrayList )
            recyclerFav.adapter = recyclerAdapter
            recyclerFav.layoutManager = layoutManager
        }

        return favView
    }

    private class BackendTaskAllFav ( val db : RestaurantDatabase )
        : AsyncTask<Void,Void,List<RestaurantEntity>>()
    {
        override fun doInBackground(vararg params: Void?): List<RestaurantEntity>
        {
            return db.resDao().getAllRes()
        }

    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FavouritesFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}