package com.alexander.foodrunner.adapter

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import java.util.*
import android.view.LayoutInflater
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.room.Room
import com.alexander.foodrunner.R
import com.alexander.foodrunner.activity.RestaurantDetailsActivity
import com.alexander.foodrunner.database.RestaurantDatabase
import com.alexander.foodrunner.database.RestaurantEntity
import com.alexander.foodrunner.model.Restaurant
import com.alexander.foodrunner.model.RestaurantHolder
import com.squareup.picasso.Picasso


class HomeRecyclerAdapter (val context:Context, val itemList: ArrayList<RestaurantHolder>)
    : RecyclerView.Adapter<HomeRecyclerAdapter.HomeViewHolder> ()
{

    class HomeViewHolder (view:View) : RecyclerView.ViewHolder(view)
    {
        val resName : TextView = view.findViewById(R.id.txtResName)
        val resPrice : TextView = view.findViewById(R.id.txtResPrice)
        val resRating : TextView = view.findViewById(R.id.txtResRating)
        val resImage : ImageView = view.findViewById(R.id.imgRestaurant)
        val resItem : RelativeLayout = view.findViewById(R.id.recyclerItemHome)
        val btnFav : ImageButton = view.findViewById(R.id.btnFav)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder
    {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.viewholder_home, parent, false)

        return HomeViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int)
    {
        val restaurant = itemList[position]
        holder.resName.text = restaurant.res.name
        holder.resPrice.text = restaurant.res.perCost
        holder.resRating.text = restaurant.res.rating
        Picasso.get().load(restaurant.res.imageUrl).error(R.mipmap.ic_icon_launcher).into(holder.resImage)
        holder.btnFav.setImageResource(restaurant.btnColor)


        val entity = RestaurantEntity ( restaurant.res.id.toInt(),
                                        restaurant.res.name,
                                        restaurant.res.rating,
                                        restaurant.res.perCost,
                                        restaurant.res.imageUrl  )




        holder.btnFav.setOnClickListener{

            val database = Room.databaseBuilder (context, RestaurantDatabase::class.java,
                    "restaurants-db").build()

            if( ! BackendTasksHome(database, entity, 'c').execute().get() )
            {
                val task = BackendTasksHome(database, entity, 'i').execute()
                val success = task.get()
                if(success)
                {
                    Toast.makeText(context,"${restaurant.res.name} added to Favourites",
                            Toast.LENGTH_SHORT).show()
                    holder.btnFav.setImageResource(R.drawable.added_fav)
                    itemList[position].btnColor = R.drawable.added_fav
                }
                else
                    Toast.makeText(context,"Some Error occurred while adding to Favourites",
                            Toast.LENGTH_SHORT).show()
            }
            else
            {
                val task = BackendTasksHome(database, entity, 'd').execute()
                val success = task.get()
                if(success)
                {
                    holder.btnFav.setImageResource(R.drawable.not_added_fav)
                    itemList[position].btnColor = R.drawable.not_added_fav
                }
                else
                    Toast.makeText(context,"Some Error occurred while removing from Favourites",
                            Toast.LENGTH_SHORT).show()
            }

        }



        holder.resItem.setOnClickListener {
            val intent = Intent(context,RestaurantDetailsActivity::class.java)
            intent.putExtra("ResId",restaurant.res.id)
            intent.putExtra("ResName",restaurant.res.name)
            context.startActivity(intent)
        }

    }


    private class BackendTasksHome ( val db: RestaurantDatabase, val resEntity: RestaurantEntity, val task:Char )
        : AsyncTask <Void, Void, Boolean>()
    {

        override fun doInBackground(vararg params: Void?): Boolean
        {
            when(task)
            {
                'c' -> {
                    val res : RestaurantEntity? = db.resDao().getResById(resEntity.res_id.toString())
                    db.close()
                    return res != null
                }
                'i' -> {
                    db.resDao().insert(resEntity)
                    db.close()
                    return true
                }
                'd' -> {
                    db.resDao().delete(resEntity)
                    db.close()
                    return true
                }
                else -> return false
            }

        }

    }
}

















