package com.example.dmblu.shoppinglist

import java.sql.Blob

class RecipeEntry(val recipeid:String, val name: String, val image:Blob, val list_ingredients:String, val recipe:String) {
}