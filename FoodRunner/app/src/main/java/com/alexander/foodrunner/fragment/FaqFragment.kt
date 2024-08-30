package com.alexander.foodrunner.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alexander.foodrunner.R
import com.alexander.foodrunner.adapter.FaqRecyclerAdapter
import com.alexander.foodrunner.adapter.FavouritesRecyclerAdapter
import com.alexander.foodrunner.adapter.HomeRecyclerAdapter


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class FaqFragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null

    lateinit var recyclerFaq : RecyclerView
    lateinit var recyclerAdapter : FaqRecyclerAdapter
    lateinit var layoutManager : RecyclerView.LayoutManager

    val question1 = "How can I register to FoodRunner ?"
    val question2 = "Where can I see the list of foods ?"
    val question3 = "Where can I see my order details ?"
    val question4 = "Can I check my previous orders ?"
    val question5 = "Can I check my account profile details ?"
    val question6 = "How many restaurants are available in FoodRunner ?"
    val question7 = "How can I change my password if I have forgotten it ?"
    val question8 = "Can I save my favourite restaurants ?"
    val answer1 = "In the Login page, you can click on the Sign Up text and the Register Yourself page will open where you can fill in your details, and then click on the Register button to register your account."
    val answer2 = "You can click on any restaurant from the All Restaurants page and then on the Restaurant Details page of that restaurant you can see all the foods available in that restaurant's food menu."
    val answer3 = "After you select all the foods that you want to order, you can click on the Proceed to Cart button to open the My Cart page where you can see the details of that particular order."
    val answer4 = "Yes, In the Navigation drawer there is a Order History option, clicking on which you can open the My Previous Orders page and see all your previous orders and their details."
    val answer5 = "Yes, In the Navigation drawer there is a My Profile option, clicking on which you can open the My Profile page and see all your profile details."
    val answer6 = "There are a total of 17 restaurants in FoodRunner and you can see all of them in the All Restaurants page."
    val answer7 = "In the Login page, just below the Login button there is a Forgot Password text, click on it and you will be taken to the Forgot Password page, where you have to enter your registered email and mobile number. Once you have entered, click on the Next button and you will be taken to the Reset Password page, where you have to enter the OTP sent in your email and your new password. After entering click on the Submit button and your password will be changed."
    val answer8 = "Yes, you can save your favourite restaurants by clicking on the heart-shaped button present beside every restaurant name. You can then see the saved restaurants in the Favourites page, which can be opened by clicking on the Favourites option present in the Navigation drawer."

    val questionList = listOf (question1,question2,question8,question3,question4,question5,question6, question7)
    val answerList = listOf (answer1,answer2,answer8,answer3,answer4,answer5,answer6,answer7)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View?
    {
        val faqView = inflater.inflate(R.layout.fragment_faq, container, false)

        recyclerFaq = faqView.findViewById(R.id.recyclerFaq)
        layoutManager = LinearLayoutManager(activity)


        recyclerAdapter = FaqRecyclerAdapter(activity as Context, questionList, answerList)
        recyclerFaq.adapter = recyclerAdapter
        recyclerFaq.layoutManager = layoutManager

        recyclerFaq.addItemDecoration(DividerItemDecoration(recyclerFaq.context,
                (layoutManager as LinearLayoutManager).orientation))

        return faqView
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FaqFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}