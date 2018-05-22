package com.example.dmblu.shoppinglist

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    /*
        Function that goes into the form to add a recipe to database
     */
    fun addRecipe(view: View) {
        val randomIntent = Intent(this, AddRecipe::class.java)
        startActivity(randomIntent)
    }
}
