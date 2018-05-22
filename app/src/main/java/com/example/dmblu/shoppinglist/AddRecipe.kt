package com.example.dmblu.shoppinglist

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_add_recipe.ingredientType

class AddRecipe : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    //List of ingredient types for drop down list
    var list_of_items = arrayOf("Pounds", "Ounces", "Tablespoons", "Quanitity")
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        toastSelected("Selected: " + list_of_items[position])
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_recipe)

        ingredientType!!.setOnItemSelectedListener(this)
        val aa = ArrayAdapter(this, android.R.layout.simple_spinner_item, list_of_items)
        ingredientType!!.setAdapter(aa)
    }

    fun toastSelected(sel:String) {
        val myToast = Toast.makeText(this, sel, Toast.LENGTH_SHORT)
        myToast.show()
    }
}
