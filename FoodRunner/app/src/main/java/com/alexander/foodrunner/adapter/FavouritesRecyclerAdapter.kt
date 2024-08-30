package com.alexander.foodrunner.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.alexander.foodrunner.R
import com.alexander.foodrunner.activity.RestaurantDetailsActivity
import com.alexander.foodrunner.database.RestaurantDatabase
import com.alexander.foodrunner.database.RestaurantEntity
import com.alexander.foodrunner.model.Restaurant
import com.squareup.picasso.Picasso

class FavouritesRecyclerAdapter (val context: Context, val itemList: ArrayList<RestaurantEntity> )
    : RecyclerView.Adapter<FavouritesRecyclerAdapter.FavouritesViewHolder> ()
{
    class FavouritesViewHolder (view: View) : RecyclerView.ViewHolder(view)
    {
        val resName : TextView = view.findViewById(R.id.txtResNameFav)
        val resPrice : TextView = view.findViewById(R.id.txtResPriceFav)
        val resRating : TextView = view.findViewById(R.id.txtResRatingFav)
        val resImage : ImageView = view.findViewById(R.id.imgRestaurantFav)
        val resItem : RelativeLayout = view.findViewById(R.id.recyclerItemFav)
        val btnRemove : android.widget.Button = view.findViewById(R.id.btnRemove)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouritesViewHolder
    {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.viewholder_favourites, parent, false)

        return FavouritesViewHolder(view)
    }

    override fun getItemCount(): Int
    {
        return itemList.size
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: FavouritesViewHolder, position: Int)
    {
        val restaurant : RestaurantEntity = itemList[position]
        holder.resName.text = restaurant.name
        holder.resPrice.text = restaurant.price
        holder.resRating.text = restaurant.rating
        Picasso.get().load(restaurant.image).error(R.mipmap.ic_icon_launcher).into(holder.resImage)


        holder.resItem.setOnClickListener(){
            val intent = Intent(context, RestaurantDetailsActivity::class.java)
            intent.putExtra("ResId",restaurant.res_id.toString())
            intent.putExtra("ResName",restaurant.name)
            context.startActivity(intent)
        }


        holder.btnRemove.setOnClickListener{

           val database = Room.databaseBuilder(context, RestaurantDatabase::class.java,
                   "restaurants-db").build()

            val task = BackendDeleteFav (database, restaurant).execute()
            val result = task.get()
            if(result)
            {
                Toast.makeText(context,"${restaurant.name} removed from Favourites",
                        Toast.LENGTH_SHORT).show()
                itemList.removeAt(position)
                notifyItemRemoved(position)
                notifyItemRangeChanged(position,itemList.size)
                holder.itemView.visibility = View.GONE
            }
            else
                Toast.makeText(context,"Some Error occurred while removing from Favourites",
                        Toast.LENGTH_SHORT).show()
        }

    }


    private class BackendDeleteFav ( val db: RestaurantDatabase, val resEntity: RestaurantEntity )
        : AsyncTask<Void, Void, Boolean>()
    {
        override fun doInBackground(vararg params: Void?): Boolean
        {
            db.resDao().delete(resEntity)
            db.close()
            return true
        }

    }
}


















