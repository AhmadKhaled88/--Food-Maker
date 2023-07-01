package com.example.FoodMaker.ui.activites

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.example.FoodMaker.R
import com.example.FoodMaker.data.pojo.MealDB
import com.example.FoodMaker.data.pojo.MealDetail
import com.example.FoodMaker.databinding.ActivityMealDetailesBinding
import com.example.FoodMaker.mvvm.DetailsMVVM
import com.example.FoodMaker.ui.fragments.HomeFragment
import com.google.android.material.snackbar.Snackbar

class MealDetailesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMealDetailesBinding
    private lateinit var detailsMVVM: DetailsMVVM
    private var mealId = ""
    private var mealStr = ""
    private var mealThumb = ""
    private var ytUrl = ""
    private lateinit var dtMeal:MealDetail


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        detailsMVVM = ViewModelProviders.of(this)[DetailsMVVM::class.java]
        binding = ActivityMealDetailesBinding.inflate(layoutInflater)
        setContentView(binding.root)



        showLoading()

        getMealInfoFromIntent()

        setUpViewWithMealInformation()
        setFloatingButtonStatues()



        detailsMVVM.getMealById(intent.getStringExtra(HomeFragment.MEAL_ID).toString())

        detailsMVVM.observeMealDetail().observe(this, object : Observer<List<MealDetail>> {
            override fun onChanged(t: List<MealDetail>?) {

                if (t != null && t.isNotEmpty()) {
                    setTextsInViews(t[0])
                    stopLoading()
                } else {
                    // Handle the case when the list is null or empty
                    Toast.makeText(applicationContext,"Erorr Loding",Toast.LENGTH_LONG).show()
                }


//                setTextsInViews(t!![0])
//                stopLoading()


            }

        })

        binding.imgYoutube.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(ytUrl)))
        }


        binding.btnSave.setOnClickListener {
            if(isMealSavedInDatabase()){
                deleteMeal()
                binding.btnSave.setImageResource(R.drawable.ic_baseline_save_24)
                Snackbar.make(
                    findViewById(android.R.id.content),
                    "Meal was deleted",
                Snackbar.LENGTH_SHORT).show()
            }else{
                saveMeal()
                binding.btnSave.setImageResource(R.drawable.ic_saved)
                Snackbar.make(
                    findViewById(android.R.id.content),
                    "Meal saved",
                    Snackbar.LENGTH_SHORT).show()
            }
        }

    }



    private fun deleteMeal() {
        detailsMVVM.deleteMealById(mealId)
    }



    private fun setFloatingButtonStatues() {
        if(isMealSavedInDatabase()){
            binding.btnSave.setImageResource(R.drawable.ic_saved)
        }else{
            binding.btnSave.setImageResource(R.drawable.ic_baseline_save_24)
        }
    }

    private fun isMealSavedInDatabase(): Boolean {
       return detailsMVVM.isMealSavedInDatabase(mealId)
    }

    private fun saveMeal() {
        val meal = MealDB(dtMeal.idMeal.toInt(),
            dtMeal.strMeal,
            dtMeal.strArea,
            dtMeal.strCategory,
            dtMeal.strInstructions,
            dtMeal.strMealThumb,
            dtMeal.strYoutube)

        detailsMVVM.insertMeal(meal)
    }

    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
        binding.btnSave.visibility = View.GONE
        binding.imgYoutube.visibility = View.INVISIBLE
    }


    private fun stopLoading() {
        binding.progressBar.visibility = View.INVISIBLE
        binding.btnSave.visibility = View.VISIBLE

        binding.imgYoutube.visibility = View.VISIBLE

    }

    private fun setTextsInViews(meal: MealDetail) {
        this.dtMeal = meal
        ytUrl = meal.strYoutube
        binding.apply {
            tvInstructions.text = "- Instructions : "
            tvContent.text = meal.strInstructions
            tvAreaInfo.visibility = View.VISIBLE
            tvCategoryInfo.visibility = View.VISIBLE
            tvAreaInfo.text = tvAreaInfo.text.toString() + meal.strArea
            tvCategoryInfo.text = tvCategoryInfo.text.toString() + meal.strCategory
            imgYoutube.visibility = View.VISIBLE
        }
    }


    private fun setUpViewWithMealInformation() {
        binding.apply {
            collapsingToolbar.title = mealStr
            Glide.with(applicationContext)
                .load(mealThumb)
                .into(imgMealDetail)
        }

    }

    private fun getMealInfoFromIntent() {

        intent?.let{
            mealId = intent.getStringExtra(HomeFragment.MEAL_ID).toString()
            mealStr = intent.getStringExtra(HomeFragment.MEAL_NAME).toString()
            mealThumb = intent.getStringExtra(HomeFragment.MEAL_THUMB).toString()
        }



       // mealId =intent.getStringExtra(HomeFragment.MEAL_ID)!!
//        mealStr = intent.getStringExtra(HomeFragment.MEAL_NAME)!!
//        mealThumb = intent.getStringExtra(HomeFragment.MEAL_THUMB)!!

    }

}


