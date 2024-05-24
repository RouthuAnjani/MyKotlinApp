package com.example.mykotlinapp


import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteException

//creating the database logic, extending the SQLiteOpenHelper base class
open class DatabaseHandler(context: Context): SQLiteOpenHelper(context,DATABASE_NAME,null,DATABASE_VERSION) {
    companion object {
        private val DATABASE_VERSION = 1
        private val DATABASE_NAME = "EmployeeDatabase"
        val TABLE_CONTACTS = "EmployeeTable"
        val KEY_ID = "id"
        val KEY_NAME = "name"
        val KEY_EMAIL = "email"
    }
    override fun onCreate(db: SQLiteDatabase?) {
        //creating table with fields
        val CREATE_CONTACTS_TABLE = ("CREATE TABLE " + TABLE_CONTACTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
                + KEY_EMAIL + " TEXT" + ")")
        db?.execSQL(CREATE_CONTACTS_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS)
        onCreate(db)
    }
    // Check if the ID already exists in the database
    fun isIdExists(id: Int): Boolean {
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_CONTACTS WHERE $KEY_ID = ?"
        val cursor = db.rawQuery(query, arrayOf(id.toString()))
        val exists = cursor.count > 0
        cursor.close()
        db.close()
        return exists
    }


    //method to insert data
    fun addEmployee(emp: EmpModelClass):Long{
        if (emp.userName.isEmpty() || emp.userEmail.isEmpty() || !isValidEmail(emp.userEmail)) {
            return -1 // Return -1 if any input field is empty
        }
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_ID, emp.userId)
        contentValues.put(KEY_NAME, emp.userName) // EmpModelClass Name
        contentValues.put(KEY_EMAIL,emp.userEmail ) // EmpModelClass Phone
        // Inserting Row
        val success = db.insert(TABLE_CONTACTS, null, contentValues)
        //2nd argument is String containing nullColumnHack
        db.close() // Closing database connection
        return success
    }
    // Check if the email is valid
    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
    //method to read data
    fun viewEmployee():List<EmpModelClass>{
        val empList:ArrayList<EmpModelClass> = ArrayList<EmpModelClass>()
        val selectQuery = "SELECT  * FROM $TABLE_CONTACTS"
        val db = this.readableDatabase
        var cursor: Cursor? = null
        try{
            cursor = db.rawQuery(selectQuery, null)
        }catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            return ArrayList()
        }
        var userId: Int
        var userName: String
        var userEmail: String
        if (cursor.moveToFirst()) {
            do {
                userId = cursor.getInt(cursor.getColumnIndex("id"))
                userName = cursor.getString(cursor.getColumnIndex("name"))
                userEmail = cursor.getString(cursor.getColumnIndex("email"))
                val emp= EmpModelClass(userId = userId, userName = userName, userEmail = userEmail)
                empList.add(emp)
            } while (cursor.moveToNext())
        }
        return empList
    }
    //method to update data
    fun updateEmployee(emp: EmpModelClass):Int{
        if (emp.userName.isEmpty() || emp.userEmail.isEmpty()) {
            return 0 // Return 0 if any input field is empty
        }
        if (!isValidEmail(emp.userEmail)) {
            return -1 // Return -1 if email is invalid
        }
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_ID, emp.userId)
        contentValues.put(KEY_NAME, emp.userName) // EmpModelClass Name
        contentValues.put(KEY_EMAIL,emp.userEmail ) // EmpModelClass Email

        // Updating Row
        val success = db.update(TABLE_CONTACTS, contentValues,"id="+emp.userId,null)
        //2nd argument is String containing nullColumnHack
        db.close() // Closing database connection
        return success
    }
    //method to delete data
    fun deleteEmployee(emp: EmpModelClass):Int{
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_ID, emp.userId) // EmpModelClass UserId
        // Deleting Row
        val success = db.delete(TABLE_CONTACTS,"id="+emp.userId,null)
        //2nd argument is String containing nullColumnHack
        db.close() // Closing database connection
        return success
    }
    // Add this method to your DatabaseHandler class
    fun getRecordById(id: Int): EmpModelClass? {
        val db = readableDatabase
        var record: EmpModelClass? = null
        val cursor = db.query(
            TABLE_CONTACTS,
            arrayOf(KEY_ID, KEY_NAME, KEY_EMAIL),
            "$KEY_ID = ?",
            arrayOf(id.toString()),
            null,
            null,
            null
        )
        if (cursor != null && cursor.moveToFirst()) {
            val recordId = cursor.getInt(cursor.getColumnIndex(KEY_ID))
            val username = cursor.getString(cursor.getColumnIndex(KEY_NAME))
            val email = cursor.getString(cursor.getColumnIndex(KEY_EMAIL))
            record = EmpModelClass(recordId, username, email)
        }
        cursor?.close()
        db.close()
        return record
    }

}