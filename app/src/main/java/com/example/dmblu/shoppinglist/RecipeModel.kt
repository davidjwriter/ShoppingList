package com.example.dmblu.shoppinglist

import java.sql.Blob

class RecipeModel(val recipeid:String, val name: String, val image:ByteArray, val list_ingredients:String, val recipe:String) {
}