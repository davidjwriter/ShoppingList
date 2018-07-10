package com.example.dmblu.shoppinglist

import android.os.Bundle
import android.support.v7.app.AppCompatActivity

class CreateList: AppCompatActivity() {
    lateinit var dbHelper:RecipeDBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dbHelper = RecipeDBHelper(this)
    }
}