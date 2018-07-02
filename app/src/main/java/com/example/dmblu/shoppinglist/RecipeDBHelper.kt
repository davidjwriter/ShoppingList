package com.example.dmblu.shoppinglist

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.dmblu.shoppinglist.IngredientDBHelper.Companion.DATABASE_NAME
import com.example.dmblu.shoppinglist.IngredientDBHelper.Companion.DATABASE_VERSION
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
    fun insertRecipe(recipe: RecipeModel): Boolean {
        // Gets the data repository in write mode
        val db = writableDatabase

        //Get the highest primary key, or first available to assign
//        val allRecipes = readAllUsers()
//        allRecipes.sortWith(Comparator { o1, o2 -> o1.recipeid - o2.recipeid})
//        val newid = allRecipes.get(allRecipes.lastIndex).recipeid + 1
        val newid = getMaxId() + 1
        // Create a new map of values, where column names are the keys
        val values = ContentValues().apply {
            put(DBContract.RecipeEntry.COLUMN_RECIPE_ID, newid)
            put(DBContract.RecipeEntry.COLUMN_NAME, recipe.name)
            put(DBContract.RecipeEntry.COLUMN_IMAGE, recipe.image)
            put(DBContract.RecipeEntry.COLUMN_INGREDIENTS, recipe.list_ingredients)
            put(DBContract.RecipeEntry.COLUMN_RECIPE, recipe.recipe)
        }

        // Insert the new row, returning the primary key value of the new row
        val newRowId = db?.insert(DBContract.RecipeEntry.TABLE_NAME, null, values)

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

    fun getMaxId():Int {
        var cursor: Cursor? = null
        val db = writableDatabase
        try {
            cursor = db.rawQuery("select max(" + DBContract.RecipeEntry.COLUMN_RECIPE_ID + ") from " + DBContract.RecipeEntry.TABLE_NAME, null)
        } catch (e: SQLiteException) {
            db.execSQL(SQL_CREATE_ENTRIES)
            return 0
        }
        val temp = 0
        Log.d("temp is:", temp.toString())
        Log.d("Number of rows", cursor.getCount().toString())
        cursor.moveToLast()
        return cursor.getString(temp).toInt()
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

        private val SQL_CREATE_ENTRIES =
                "CREATE TABLE " + DBContract.RecipeEntry.TABLE_NAME + " (" +
                        DBContract.RecipeEntry.COLUMN_RECIPE_ID + " TEXT PRIMARY KEY," +
                        DBContract.RecipeEntry.COLUMN_NAME + " TEXT," +
                        DBContract.RecipeEntry.COLUMN_IMAGE + " BLOB," +
                        DBContract.RecipeEntry.COLUMN_INGREDIENTS + " TEXT," +
                        DBContract.RecipeEntry.COLUMN_RECIPE + " TEXT)"

        private val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + DBContract.RecipeEntry.TABLE_NAME
    }

}

private operator fun String.minus(recipeid: String): Int {
    return this.toInt() - recipeid.toInt()
}
