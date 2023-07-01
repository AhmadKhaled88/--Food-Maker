package com.example.FoodMaker.ui.activites


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.example.FoodMaker.R
import com.example.FoodMaker.adapters.MealRecyclerAdapter
import com.example.FoodMaker.adapters.SetOnMealClickListener
import com.example.FoodMaker.data.pojo.Meal
import com.example.FoodMaker.databinding.ActivityCategoriesBinding
import com.example.FoodMaker.mvvm.MealActivityMVVM
import com.example.FoodMaker.ui.fragments.HomeFragment


class MealActivity  : AppCompatActivity() {
    private lateinit var mealActivityMvvm: MealActivityMVVM
    private lateinit var binding: ActivityCategoriesBinding
    private lateinit var myAdapter: MealRecyclerAdapter
    private lateinit var categoryName : String




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoriesBinding.inflate(layoutInflater)
        setContentView(binding.root)


        mealActivityMvvm = ViewModelProviders.of(this)[MealActivityMVVM::class.java]
        mealActivityMvvm.getMealsByCategory(intent.getStringExtra(HomeFragment.CATEGORY_NAME)!!)


        getCategory()
        startLoading()
        prepareRecyclerView()


        mealActivityMvvm.observeMeal().observe(this, object : Observer<List<Meal>> {

            override fun onChanged(t: List<Meal>?) {
                if(t==null){
                    hideLoading()
                    Toast.makeText(applicationContext, "No meals in this category", Toast.LENGTH_SHORT).show()
                    onBackPressed()
                }else {
                    myAdapter.setCategoryList(t)
                    binding.tvCategoryCount.text =categoryName + " : " + t.size.toString()
                    hideLoading()
                }
            }
        })




        myAdapter.setOnMealClickListener(object : SetOnMealClickListener {
            override fun setOnClickListener(meal: Meal) {
                val intent = Intent(applicationContext, MealDetailesActivity::class.java)
                intent.putExtra(HomeFragment.MEAL_ID, meal.idMeal)
                intent.putExtra(HomeFragment.MEAL_NAME, meal.strMeal)
                intent.putExtra(HomeFragment.MEAL_THUMB, meal.strMealThumb)
                startActivity(intent)
            }
        })
    }

    private fun hideLoading() {
        binding.apply {
            loadingGifMeals.visibility = View.INVISIBLE
           mealRoot.setBackgroundColor(ContextCompat.getColor(applicationContext,R.color.white))
        }    }

    private fun startLoading() {
        binding.apply {
            loadingGifMeals.visibility = View.VISIBLE
            mealRoot.setBackgroundColor(ContextCompat.getColor(applicationContext,R.color.black))
        }
    }

    private fun getCategory(){

        categoryName=intent.getStringExtra(HomeFragment.CATEGORY_NAME)!!

    }

    private fun prepareRecyclerView() {
        myAdapter = MealRecyclerAdapter()
        binding.mealRecyclerview.apply {
            layoutManager = GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)
            adapter = myAdapter
        }
    }
}