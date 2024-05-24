    package com.example.mykotlinapp

    import android.app.AlertDialog
    import android.os.Bundle
    import android.util.Log
    import android.view.View
    import android.widget.EditText
    import android.widget.ListView
    import androidx.appcompat.app.AppCompatActivity

    class MainActivity : AppCompatActivity() {
        lateinit var u_id: EditText
        lateinit var u_name: EditText
        lateinit var u_email: EditText
        private lateinit var listView: ListView

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_main)
            u_id = findViewById(R.id.u_id)
            u_name = findViewById(R.id.u_name)
            u_email = findViewById(R.id.u_email)
            listView = findViewById(R.id.listView)
        }

        // Method for saving records in database
        fun saveRecord(view: View) {
            val id = u_id.text.toString()
            val name = u_name.text.toString()
            val email = u_email.text.toString()
            val databaseHandler: DatabaseHandler = DatabaseHandler(this)
            if (id.trim() != "" && name.trim() != "" && email.trim() != "") {
                // Check if the ID already exists in the database
                if (databaseHandler.isIdExists(Integer.parseInt(id))) {
                    Log.d(TAG, "Record with this ID already exists")
                } else {
                    val status = databaseHandler.addEmployee(EmpModelClass(Integer.parseInt(id), name, email))
                    if (status > -1) {
                        Log.d(TAG, "Record saved")
                        u_id.text.clear()
                        u_name.text.clear()
                        u_email.text.clear()
                    }
                }
            } else {
                Log.d(TAG, "ID, name, or email cannot be blank")
            }
        }

        // Method for reading records from database in ListView
        fun viewRecord(view: View) {
            // Creating the instance of DatabaseHandler class
            val databaseHandler: DatabaseHandler = DatabaseHandler(this)
            // Calling the viewEmployee method of DatabaseHandler class to read the records
            val emp: List<EmpModelClass> = databaseHandler.viewEmployee()
            val empArrayId = Array<String>(emp.size) { "0" }
            val empArrayName = Array<String>(emp.size) { "null" }
            val empArrayEmail = Array<String>(emp.size) { "null" }
            var index = 0
            for (e in emp) {
                empArrayId[index] = e.userId.toString()
                empArrayName[index] = e.userName
                empArrayEmail[index] = e.userEmail
                index++
            }
            // Creating custom ArrayAdapter
            val myListAdapter = MyListAdapter(this, empArrayId, empArrayName, empArrayEmail)
            listView.adapter = myListAdapter
        }

        // Method for updating records based on user id
        fun updateRecord(view: View) {
            val dialogBuilder = AlertDialog.Builder(this)
            val inflater = this.layoutInflater
            val dialogView = inflater.inflate(R.layout.update_dialog, null)
            dialogBuilder.setView(dialogView)

            val edtId = dialogView.findViewById(R.id.updateId) as EditText
            val edtName = dialogView.findViewById(R.id.updateName) as EditText
            val edtEmail = dialogView.findViewById(R.id.updateEmail) as EditText

            dialogBuilder.setTitle("Update Record")
            dialogBuilder.setMessage("Enter data below")
            dialogBuilder.setPositiveButton("Update") { _, _ ->
                val updateId = edtId.text.toString()
                val updateName = edtName.text.toString()
                val updateEmail = edtEmail.text.toString()
                // Creating the instance of DatabaseHandler class
                val databaseHandler: DatabaseHandler = DatabaseHandler(this)
                if (updateId.trim() != "" && updateName.trim() != "" && updateEmail.trim() != "") {
                    // Check if the record with the specified ID exists
                    if (databaseHandler.isIdExists(Integer.parseInt(updateId))) {
                        // Calling the updateEmployee method of DatabaseHandler class to update record
                        val status = databaseHandler.updateEmployee(EmpModelClass(Integer.parseInt(updateId), updateName, updateEmail))
                        if (status > -1) {
                            Log.d(TAG, "Record updated")
                        }
                    } else {
                        Log.d(TAG, "Record with this ID does not exist")
                    }
                } else {
                    Log.d(TAG, "ID, name, or email cannot be blank")
                }
            }
            dialogBuilder.setNegativeButton("Cancel", null)
            val b = dialogBuilder.create()
            b.show()
        }

        // Method for deleting records based on id
        fun deleteRecord(view: View) {
            // Creating AlertDialog for taking user id
            val dialogBuilder = AlertDialog.Builder(this)
            val inflater = this.layoutInflater
            val dialogView = inflater.inflate(R.layout.delete_dialog, null)
            dialogBuilder.setView(dialogView)

            val dltId = dialogView.findViewById(R.id.deleteId) as EditText
            dialogBuilder.setTitle("Delete Record")
            dialogBuilder.setMessage("Enter id below")
            dialogBuilder.setPositiveButton("Delete") { _, _ ->
                val deleteId = dltId.text.toString()
                // Creating the instance of DatabaseHandler class
                val databaseHandler: DatabaseHandler = DatabaseHandler(this)
                if (deleteId.trim() != "") {
                    // Check if the record with the specified ID exists
                    if (databaseHandler.isIdExists(Integer.parseInt(deleteId))) {
                        // Calling the deleteEmployee method of DatabaseHandler class to delete record
                        val status = databaseHandler.deleteEmployee(EmpModelClass(Integer.parseInt(deleteId), "", ""))
                        if (status > -1) {
                            Log.d(TAG, "Record deleted")
                        }
                    } else {
                        Log.d(TAG, "Record with this ID does not exist")
                    }
                } else {
                    Log.d(TAG, "ID cannot be blank")
                }
            }
            dialogBuilder.setNegativeButton("Cancel", null)
            val b = dialogBuilder.create()
            b.show()
        }

        companion object {
            const val TAG = "MainActivity"
        }


    }
