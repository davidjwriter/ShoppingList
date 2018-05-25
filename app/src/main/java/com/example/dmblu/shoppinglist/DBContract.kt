package com.example.dmblu.shoppinglist

import android.provider.BaseColumns

object DBContract {

    /* Inner class that defines the table contents */
    class RecipeEntry : BaseColumns {
        companion object {
            val TABLE_NAME = "recipes"
            val COLUMN_RECIPE_ID = "recipeid"
            val COLUMN_NAME = "name"
            val COLUMN_IMAGE = "image"
            val COLUMN_INGREDIENTS = "ingredients"
            val COLUMN_RECIPE = "recipe"
        }
    }

    class IngredientEntry : BaseColumns {
        companion object {
            val TABLE_NAME = "ingredients"
            val COLUMN_INGREDIENT_ID = "ingredientid"
            val COLUMN_NAME = "name"
            val COLUMN_AMOUNT = "amount"
            val COLUMN_TYPE = "type"
        }
    }
}