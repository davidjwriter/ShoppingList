package com.example.dmblu.shoppinglist

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper

import java.util.ArrayList

class IngredientDBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
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
    fun insertINGREDIENT(INGREDIENT: IngredientModel): Boolean {
        // Gets the data repository in write mode
        val db = writableDatabase

        // Create a new map of values, where column names are the keys
        val values = ContentValues()
        values.put(DBContract.IngredientEntry.COLUMN_INGREDIENT_ID, INGREDIENT.ingredientid)
        values.put(DBContract.IngredientEntry.COLUMN_NAME, INGREDIENT.name)
        values.put(DBContract.IngredientEntry.COLUMN_AMOUNT, INGREDIENT.amount)
        values.put(DBContract.IngredientEntry.COLUMN_TYPE, INGREDIENT.type)

        // Insert the new row, returning the primary key value of the new row
        val newRowId = db.insert(DBContract.IngredientEntry.TABLE_NAME, null, values)

        return true
    }

    @Throws(SQLiteConstraintException::class)
    fun deleteUser(userid: String): Boolean {
        // Gets the data repository in write mode
        val db = writableDatabase
        // Define 'where' part of query.
        val selection = DBContract.IngredientEntry.COLUMN_INGREDIENT_ID + " LIKE ?"
        // Specify arguments in placeholder order.
        val selectionArgs = arrayOf(userid)
        // Issue SQL statement.
        db.delete(DBContract.IngredientEntry.TABLE_NAME, selection, selectionArgs)

        return true
    }

    fun readUser(userid: String): ArrayList<IngredientModel> {
        val users = ArrayList<IngredientModel>()
        val db = writableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery("select * from " + DBContract.IngredientEntry.TABLE_NAME + " WHERE " + DBContract.IngredientEntry.COLUMN_INGREDIENT_ID + "='" + userid + "'", null)
        } catch (e: SQLiteException) {
            // if table not yet present, create it
            db.execSQL(SQL_CREATE_ENTRIES)
            return ArrayList()
        }

        var name: String
        var age: String
        if (cursor!!.moveToFirst()) {
            while (cursor.isAfterLast == false) {
                name = cursor.getString(cursor.getColumnIndex(DBContract.IngredientEntry.COLUMN_NAME))
                //age = cursor.getString(cursor.getColumnIndex(DBContract.IngredientModel.COLUMN_AGE))

                //users.add(IngredientModel(userid, name, age))
                cursor.moveToNext()
            }
        }
        return users
    }

    fun readAllUsers(): ArrayList<IngredientModel> {
        val users = ArrayList<IngredientModel>()
        val db = writableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery("select * from " + DBContract.IngredientEntry.TABLE_NAME, null)
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

                //users.add(IngredientModel(userid, name, age))
                cursor.moveToNext()
            }
        }
        return users
    }

    companion object {
        // If you change the database schema, you must increment the database version.
        val DATABASE_VERSION = 1
        val DATABASE_NAME = "INGREDIENTApp.db"

        private val SQL_CREATE_ENTRIES =
                "CREATE TABLE " + DBContract.IngredientEntry.TABLE_NAME + " (" +
                        DBContract.IngredientEntry.COLUMN_INGREDIENT_ID + " TEXT PRIMARY KEY," +
                        DBContract.IngredientEntry.COLUMN_NAME + " TEXT," +
                        DBContract.IngredientEntry.COLUMN_AMOUNT + " REAL," +
                        DBContract.IngredientEntry.COLUMN_TYPE + "TEXT)"

        private val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + DBContract.IngredientEntry.TABLE_NAME
    }

}