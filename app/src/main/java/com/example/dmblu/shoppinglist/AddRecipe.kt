package com.example.dmblu.shoppinglist

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.dmblu.shoppinglist.R.id.*
import kotlinx.android.synthetic.main.activity_add_recipe.*

class AddRecipe : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    //List of ingredients
    var list_of_ingredients = mutableListOf<Ingredient>()
    var ingredientCounter = 0
    lateinit var dbHelper:RecipeDBHelper
    /*
        Function to add a new ingredient to our list of ingredients
     */
    fun addIngredient(view:View) {
        list_of_ingredients.add(Ingredient(ingredient_0.text.toString(), amount_0.text.toString().toDouble(), ingrType))
        val myToast = Toast.makeText(this, "Ingredient Added!", Toast.LENGTH_SHORT)
        myToast.show()
        ingredient_0.text.clear()
        amount_0.text.clear()
    }

    //List of ingredient types for drop down list
    var list_of_items = arrayOf("Pounds", "Ounces", "Tablespoons", "Quanitity")
    var ingrType = ""
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        toastSelected("Selected: " + list_of_items[position])
        ingrType = list_of_items[position]
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_recipe)

        ingredientType_0!!.setOnItemSelectedListener(this)
        val aa = ArrayAdapter(this, android.R.layout.simple_spinner_item, list_of_items)
        ingredientType_0!!.setAdapter(aa)
        dbHelper = RecipeDBHelper(this)
    }

    /*
        TODO: delete this once debugging is done
     */
    fun toastSelected(sel:String) {
        val myToast = Toast.makeText(this, sel, Toast.LENGTH_SHORT)
        myToast.show()
    }




    /*
        Submission form function
     */
    fun submitForm(view:View) {
        Log.d("formTag","Form Submitted")
        Log.d("Name", name.text.toString())
        Log.d("Ingredient", "Ingredents:")
        for (ingr in list_of_ingredients) {
            Log.d("ing", "Ingredient 1")
            Log.d("ing", ingr.ingredient)
            Log.d("ing", ingr.amount.toString())
            Log.d("ing", ingr.type)
        }
        Log.d("RecipeModel", recipe.text.toString())
        var ingrs:ArrayList<IngredientModel> = ArrayList<IngredientModel>()
        for (ingr in list_of_ingredients) {
            ingrs.add(IngredientModel("-1",ingr.ingredient, ingr.amount, ingr.type))
        }
        var newRecipe:RecipeModel = RecipeModel("-1", name.text.toString(), ByteArray(1), ingrs.toString(), recipe.text.toString())
        dbHelper.insertRecipe(newRecipe, ingrs)
        toastSelected("Recipe Added")
    }
}
