package com.example.dmblu.shoppinglist

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.dmblu.shoppinglist.R.id.recipe

import java.util.ArrayList

class RecipeDBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_ENTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES)
        onCreate(db)
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }

    @Throws(SQLiteConstraintException::class)
    fun insertRecipe(recipe: RecipeModel, ingredients:ArrayList<IngredientModel>): Boolean {
        // Gets the data repository in write mode
        val db = writableDatabase

        //Get the new id for the new recipe row
        val newid = getMaxIdFromTable(DBContract.RecipeEntry.COLUMN_RECIPE_ID, DBContract.RecipeEntry.TABLE_NAME) + 1

        //Get the id of each of the ingredients
        var ingredientIDs:ArrayList<Int> = ArrayList<Int>()
        for (ingredient in ingredients) {
            var temp:Int = getIngredientID(ingredient)
            if (temp >= 0) {
                ingredientIDs.add(temp)
            } else {
                val newIngredientID = getMaxIdFromTable(DBContract.IngredientEntry.COLUMN_INGREDIENT_ID, DBContract.IngredientEntry.TABLE_NAME) + 1
                insertIngredient(ingredient, newIngredientID)
                ingredientIDs.add(newIngredientID)
            }
        }

        // Create a new map of values, where column names are the keys
        val values = ContentValues().apply {
            put(DBContract.RecipeEntry.COLUMN_RECIPE_ID, newid)
            put(DBContract.RecipeEntry.COLUMN_NAME, recipe.name)
            put(DBContract.RecipeEntry.COLUMN_IMAGE, recipe.image)
            put(DBContract.RecipeEntry.COLUMN_INGREDIENTS, ingredientIDs.toString())
            put(DBContract.RecipeEntry.COLUMN_RECIPE, recipe.recipe)
        }

        // Insert the new row, returning the primary key value of the new row
        val newRowId = db?.insert(DBContract.RecipeEntry.TABLE_NAME, null, values)

        return true
    }

    /*
        Adds a new ingredient into the ingredient table
     */
    @Throws(SQLiteConstraintException::class)
    fun insertIngredient(ingredient:IngredientModel, id:Int):Boolean {

        val db = writableDatabase

        //Create a new map of values, where column names are the keys
        val values = ContentValues().apply {
            put(DBContract.IngredientEntry.COLUMN_INGREDIENT_ID, id)
            put(DBContract.IngredientEntry.COLUMN_NAME, ingredient.name)
            put(DBContract.IngredientEntry.COLUMN_AMOUNT, ingredient.amount.toString())
            put(DBContract.IngredientEntry.COLUMN_TYPE, ingredient.type)
        }
        val newRowID = db?.insert(DBContract.IngredientEntry.TABLE_NAME, null, values)
        return true
    }

    @Throws(SQLiteConstraintException::class)
    fun deleteUser(userid: String): Boolean {
        // Gets the data repository in write mode
        val db = writableDatabase
        // Define 'where' part of query.
        val selection = DBContract.RecipeEntry.COLUMN_RECIPE_ID + " LIKE ?"
        // Specify arguments in placeholder order.
        val selectionArgs = arrayOf(userid)
        // Issue SQL statement.
        db.delete(DBContract.RecipeEntry.TABLE_NAME, selection, selectionArgs)

        return true
    }

    /*
        Method gets the max id in a given table (recipe or ingredient) to be able to add a new
        row in the table
     */
    fun getMaxIdFromTable(column:String, table:String):Int {
        var cursor: Cursor? = null
        val db = writableDatabase
        try {
            cursor = db.rawQuery("select max($column) from $table", null)
        } catch (e: SQLiteException) {
            db.execSQL(SQL_CREATE_ENTRIES)
            return 0
        }
        val temp = 0
        cursor.moveToLast()
        if (cursor.count > 0 && cursor.getString(temp) != null) {
            return cursor.getString(temp).toInt()
        } else {
            return -1
        }
    }

    /*
        Method will go through the ingrediennt table and check if this ingredient combination is
        in there, and if it is then return the ID, else return -1
     */
    fun getIngredientID(ingredient:IngredientModel):Int {
        var cursor: Cursor? = null
        val db = writableDatabase
        try {
            cursor = db.rawQuery(getIngredientIDSQL(
                    DBContract.IngredientEntry.TABLE_NAME,
                    DBContract.IngredientEntry.COLUMN_NAME,
                    ingredient.name,
                    DBContract.IngredientEntry.COLUMN_AMOUNT,
                    ingredient.amount.toString(),
                    DBContract.IngredientEntry.COLUMN_TYPE,
                    ingredient.type
            ), null)
        } catch (e: SQLiteException) {
            Log.d("Ingredient Exception", e.message)
            //db.execSQL(INGREDIENT_CREATE_ENTRIES)
            return -1
        }
        cursor.moveToLast()
        if (cursor.count > 0 && cursor.getString(0) != null) {
            return cursor.getString(0).toInt()
        }
        return -1
    }

    /*
        Helper method to create the sql command in an easier to read format
     */
    fun getIngredientIDSQL(
            table:String,
            nameColumn:String,
            name:String,
            amountColumn:String,
            amount:String,
            typeColumn:String,
            type:String
    ):String {
        return ("select * from $table where $nameColumn = '$name' AND $amountColumn = '$amount' AND $typeColumn = '$type'")
    }

    fun readUser(userid: String): ArrayList<RecipeModel> {
        val users = ArrayList<RecipeModel>()
        val db = writableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery("select * from " + DBContract.RecipeEntry.TABLE_NAME + " WHERE " + DBContract.RecipeEntry.COLUMN_RECIPE_ID + "='" + userid + "'", null)
        } catch (e: SQLiteException) {
            // if table not yet present, create it
            db.execSQL(SQL_CREATE_ENTRIES)
            return ArrayList()
        }

        var name: String
        var age: String
        if (cursor!!.moveToFirst()) {
            while (cursor.isAfterLast == false) {
                name = cursor.getString(cursor.getColumnIndex(DBContract.RecipeEntry.COLUMN_NAME))
                //age = cursor.getString(cursor.getColumnIndex(DBContract.RecipeModel.COLUMN_AGE))

                //users.add(RecipeModel(userid, name, age))
                cursor.moveToNext()
            }
        }
        return users
    }

    fun readAllUsers(): ArrayList<RecipeModel> {
        val users = ArrayList<RecipeModel>()
        val db = writableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery("select * from " + DBContract.RecipeEntry.TABLE_NAME, null)
        } catch (e: SQLiteException) {
            db.execSQL(SQL_CREATE_ENTRIES)
            return ArrayList()
        }

        var userid: String
        var name: String
        var age: String
        if (cursor!!.moveToFirst()) {
            while (cursor.isAfterLast == false) {
                //userid = cursor.getString(cursor.getColumnIndex(DBContract.UserEntry.COLUMN_USER_ID))
                //name = cursor.getString(cursor.getColumnIndex(DBContract.UserEntry.COLUMN_NAME))
                //age = cursor.getString(cursor.getColumnIndex(DBContract.UserEntry.COLUMN_AGE))

                //users.add(RecipeModel(userid, name, age))
                cursor.moveToNext()
            }
        }
        return users
    }

    companion object {
        // If you change the database schema, you must increment the database version.
        val DATABASE_VERSION = 5
        val DATABASE_NAME = "RecipeApp.db"

        //SQL commands for the recipe table
        private val SQL_CREATE_ENTRIES =
                "CREATE TABLE " + DBContract.RecipeEntry.TABLE_NAME + " (" +
                        DBContract.RecipeEntry.COLUMN_RECIPE_ID + " TEXT PRIMARY KEY," +
                        DBContract.RecipeEntry.COLUMN_NAME + " TEXT," +
                        DBContract.RecipeEntry.COLUMN_IMAGE + " BLOB," +
                        DBContract.RecipeEntry.COLUMN_INGREDIENTS + " TEXT," +
                        DBContract.RecipeEntry.COLUMN_RECIPE + " TEXT)"

        private val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + DBContract.RecipeEntry.TABLE_NAME

        //SQL commands for the ingredients table
        private val INGREDIENT_CREATE_ENTRIES =
                "CREATE TABLE " + DBContract.IngredientEntry.TABLE_NAME + " (" +
                        DBContract.IngredientEntry.COLUMN_INGREDIENT_ID + " TEXT PRIMARY KEY," +
                        DBContract.IngredientEntry.COLUMN_NAME + " TEXT," +
                        DBContract.IngredientEntry.COLUMN_AMOUNT + " TEXT," +
                        DBContract.IngredientEntry.COLUMN_TYPE + " TEXT)"

        private val INGREDIENT_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + DBContract.IngredientEntry.TABLE_NAME
    }

}

private operator fun String.minus(recipeid: String): Int {
    return this.toInt() - recipeid.toInt()
}
