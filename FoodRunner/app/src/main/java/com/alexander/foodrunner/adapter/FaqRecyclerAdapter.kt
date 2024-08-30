package com.alexander.foodrunner.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.alexander.foodrunner.R
import com.alexander.foodrunner.database.RestaurantEntity
import com.squareup.picasso.Picasso

class FaqRecyclerAdapter (val context: Context, val questions: List<String>, val answers: List<String>)
    : RecyclerView.Adapter<FaqRecyclerAdapter.FaqViewHolder> ()
{
    class FaqViewHolder (view: View) : RecyclerView.ViewHolder(view)
    {
        val txtQuestion : TextView = view.findViewById(R.id.txtQuestionFaq)
        val txtAnswer : TextView = view.findViewById(R.id.txtAnswerFaq)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FaqViewHolder
    {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.viewholder_faq, parent, false)

        return FaqViewHolder(view)
    }

    override fun getItemCount(): Int
    {
        return answers.size
    }

    override fun onBindViewHolder (holder: FaqViewHolder, position: Int)
    {
        val ques = questions[position]
        val ans = answers[position]

        holder.txtQuestion.text = ques
        holder.txtAnswer.text = ans
    }
}
















