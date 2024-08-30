package com.alexander.foodrunner.adapter

import android.content.Context
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.alexander.foodrunner.R
import com.alexander.foodrunner.activity.RestaurantDetailsActivity
import com.alexander.foodrunner.database.MyCartDatabase
import com.alexander.foodrunner.database.FoodEntity
import com.alexander.foodrunner.model.Food
import java.util.ArrayList

class RestaurantRecyclerAdapter (val context:Context, private val foodMenu:ArrayList<Food>)
    : RecyclerView.Adapter<RestaurantRecyclerAdapter.FoodViewHolder> ()
{
    class FoodViewHolder (view:View) : RecyclerView.ViewHolder(view)
    {
        val txtSerial : TextView = view.findViewById(R.id.txtFoodSerial)
        val txtItem : TextView = view.findViewById(R.id.txtFoodItem)
        val txtPrice :TextView = view.findViewById(R.id.txtFoodPrice)
        val btnAdd : TextView = view.findViewById(R.id.btnAddFood)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder
    {
        val foodView = LayoutInflater.from(parent.context)
                .inflate(R.layout.viewholder_restaurant_details,parent,false)
        return FoodViewHolder(foodView)
    }

    override fun getItemCount(): Int
    {
        return foodMenu.size
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int)
    {
        val fooditem = foodMenu[position]
        holder.txtSerial.text = (position + 1).toString()
        holder.txtItem.text = fooditem.name
        holder.txtPrice.text = fooditem.perCost

        val food = FoodEntity (fooditem.id.toInt(),fooditem.name,fooditem.perCost,fooditem.resId)



        holder.btnAdd.setOnClickListener {

            val database = Room.databaseBuilder(context,MyCartDatabase::class.java,"foods-db").build()

            val present = BackendTasksCart(database,food,"check").execute().get()

            if( !present )
            {
                val task = BackendTasksCart(database,food,"insert").execute()
                val inserted = task.get()
                if(inserted)
                {
                    holder.btnAdd.text = "REMOVE"
                    holder.btnAdd.setBackgroundResource(R.drawable.mybutton)
                }
                else
                    Toast.makeText(context,"Some Error occurred while adding to Cart", Toast.LENGTH_SHORT).show()
            }
            else
            {
                val task = BackendTasksCart(database,food,"delete").execute()
                val deleted = task.get()
                if(deleted)
                {
                    holder.btnAdd.text = "ADD"
                    holder.btnAdd.setBackgroundResource(R.drawable.loginbutton)
                }
                else
                    Toast.makeText(context,"Some Error occurred while removing from Cart", Toast.LENGTH_SHORT).show()
            }

            if( !cartEmpty() )
                (context as RestaurantDetailsActivity).activateCartButton()
            else
                (context as RestaurantDetailsActivity).deactivateCartButton()

        }

    }



    private class BackendTasksCart (val db:MyCartDatabase, val food:FoodEntity, val task:String)
        : AsyncTask<Void, Void, Boolean>()
    {
        override fun doInBackground(vararg params: Void?): Boolean
        {
            when(task)
            {
                "check" -> {
                    val item :FoodEntity? = db.foodDao().getFoodById(food.food_id.toString())
                    db.close()
                    return  item!=null
                }
                "insert" -> {
                    db.foodDao().insert(food)
                    db.close()
                    return true
                }
                "delete" -> {
                    db.foodDao().delete(food)
                    db.close()
                    return true
                }
                else -> return false
            }
        }
    }

    private class BackendAllFoodsInCart (val context:Context)
        : AsyncTask< Void, Void, List<FoodEntity> >()
    {
        val db = Room.databaseBuilder(context, MyCartDatabase::class.java,"foods-db").build()

        override fun doInBackground(vararg params: Void?): List<FoodEntity>
        {
            return db.foodDao().getAllFood()
        }
    }

    fun cartEmpty() : Boolean
    {
        val foodDB = BackendAllFoodsInCart(context).execute().get()
        return foodDB.isNullOrEmpty()
    }

}







