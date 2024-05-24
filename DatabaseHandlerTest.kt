package com.example.mykotlinapp

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DatabaseHandlerTest {

    private lateinit var dbHandler: DatabaseHandler

    @Before
    fun setUp() {
        val appContext=ApplicationProvider.getApplicationContext<Context>()
        dbHandler = DatabaseHandler(appContext)
        dbHandler.writableDatabase.execSQL("DELETE FROM " + DatabaseHandler.TABLE_CONTACTS)
    }

    @After
    fun tearDown() {
        dbHandler.close()
    }

    @Test
    fun testCreateDb() {
        val db = dbHandler.readableDatabase
        assertThat(db.isOpen).isTrue()
    }

    @Test
    fun testAddEmployee() {
        val emp = EmpModelClass(1, "John Doe", "johndoe@example.com")
        val success = dbHandler.addEmployee(emp)
        assertThat(success).isGreaterThan(0L)

        val employees = dbHandler.viewEmployee()
        assertThat(employees).hasSize(1)
        assertThat(employees[0].userId).isEqualTo(emp.userId)
        assertThat(employees[0].userName).isEqualTo(emp.userName)
        assertThat(employees[0].userEmail).isEqualTo(emp.userEmail)
    }

    @Test
    fun testUpdateEmployee() {
        val emp = EmpModelClass(1, "John Doe", "johndoe@example.com")
        dbHandler.addEmployee(emp)

        val updatedEmp = EmpModelClass(1, "Jane Doe", "janedoe@example.com")
        val updateSuccess = dbHandler.updateEmployee(updatedEmp)
        assertThat(updateSuccess).isEqualTo(1)

        val employees = dbHandler.viewEmployee()
        assertThat(employees).hasSize(1)

        assertThat(employees).hasSize(1)
        assertThat(employees[0].userName).isEqualTo(updatedEmp.userName) // Compare updated name
    }

    @Test
    fun testDeleteEmployee() {
        val emp = EmpModelClass(1, "John Doe", "johndoe@example.com")
        dbHandler.addEmployee(emp)

        val deleteSuccess = dbHandler.deleteEmployee(emp)
        assertThat(deleteSuccess).isEqualTo(1)

        val employees = dbHandler.viewEmployee()
        assertThat(employees).isEmpty()
    }

    @Test
    fun testAddMultipleEmployees() {
        val emp1 = EmpModelClass(1, "John Doe", "johndoe@example.com")
        val emp2 = EmpModelClass(2, "Jane Smith", "janesmith@example.com")

        dbHandler.addEmployee(emp1)
        dbHandler.addEmployee(emp2)

        val employees = dbHandler.viewEmployee()
        assertThat(employees).hasSize(2)
        assertThat(employees).contains(emp1)
        assertThat(employees).contains(emp2)
    }

    @Test
    fun testUpdateNonExistentEmployee() {
        val emp = EmpModelClass(1, "John Doe", "johndoe@example.com")

        // Trying to update an employee that doesn't exist
        val updateSuccess = dbHandler.updateEmployee(emp)
        assertThat(updateSuccess).isEqualTo(0)
    }

    @Test
    fun testDeleteNonExistentEmployee() {
        val emp = EmpModelClass(1, "John Doe", "johndoe@example.com")

        // Trying to delete an employee that doesn't exist
        val deleteSuccess = dbHandler.deleteEmployee(emp)
        assertThat(deleteSuccess).isEqualTo(0)
    }

    @Test
    fun testViewEmptyDatabase() {
        val employees = dbHandler.viewEmployee()
        assertThat(employees).isEmpty()
    }

    @Test
    fun testAddEmployeeWithExistingId() {
        // Add an employee with ID 1
        val emp1 = EmpModelClass(1, "John Doe", "johndoe@example.com")
        dbHandler.addEmployee(emp1)

        // Try adding another employee with the same ID
        val emp2 = EmpModelClass(1, "Jane Smith", "janesmith@example.com")
        val success = dbHandler.addEmployee(emp2)
        assertThat(success).isEqualTo(-1) // Assuming -1 indicates failure due to duplicate ID
    }

    @Test
    fun testUpdateEmployeeWithNonExistentId() {
        // Try updating an employee with a non-existent ID
        val emp = EmpModelClass(100, "John Doe", "johndoe@example.com")
        val success = dbHandler.updateEmployee(emp)
        assertThat(success).isEqualTo(0) // Assuming 0 indicates failure due to non-existent ID
    }

    @Test
    fun testDeleteEmployeeWithNonExistentId() {
        // Try deleting an employee with a non-existent ID
        val emp = EmpModelClass(100, "John Doe", "johndoe@example.com")
        val success = dbHandler.deleteEmployee(emp)
        assertThat(success).isEqualTo(0) // Assuming 0 indicates failure due to non-existent ID
    }

    @Test
    fun testViewEmployeesSortedById() {
        // Add some employees with different IDs
        val emp1 = EmpModelClass(2, "Jane Smith", "janesmith@example.com")
        val emp2 = EmpModelClass(3, "Alice Johnson", "alice@example.com")
        val emp3 = EmpModelClass(1, "John Doe", "johndoe@example.com")
        dbHandler.addEmployee(emp1)
        dbHandler.addEmployee(emp2)
        dbHandler.addEmployee(emp3)

        // View the employees and ensure they are sorted by ID
        val employees = dbHandler.viewEmployee()
        assertThat(employees).hasSize(3)
        assertThat(employees[0].userId).isEqualTo(1)
        assertThat(employees[1].userId).isEqualTo(2)
        assertThat(employees[2].userId).isEqualTo(3)
    }

    @Test
    fun testUpdateEmployeeInformation() {
        // Add an employee
        val emp = EmpModelClass(1, "John Doe", "johndoe@example.com")
        dbHandler.addEmployee(emp)

        // Update the employee's information
        val updatedEmp = EmpModelClass(1, "John Smith", "johnsmith@example.com")
        dbHandler.updateEmployee(updatedEmp)

        // Retrieve the updated employee from the database
        val employees = dbHandler.viewEmployee()
        assertThat(employees).hasSize(1)
        assertThat(employees[0].userName).isEqualTo("John Smith")
        assertThat(employees[0].userEmail).isEqualTo("johnsmith@example.com")
    }

    @Test
    fun testDeleteAllEmployees() {
        // Add some employees
        val emp1 = EmpModelClass(1, "John Doe", "johndoe@example.com")
        val emp2 = EmpModelClass(2, "Jane Smith", "janesmith@example.com")
        dbHandler.addEmployee(emp1)
        dbHandler.addEmployee(emp2)

        // Delete each employee individually
        val deleteSuccess1 = dbHandler.deleteEmployee(emp1)
        val deleteSuccess2 = dbHandler.deleteEmployee(emp2)

        // Check if both deletions were successful
        assertThat(deleteSuccess1).isEqualTo(1)
        assertThat(deleteSuccess2).isEqualTo(1)

        // Check if the database is empty
        val employees = dbHandler.viewEmployee()
        assertThat(employees).isEmpty()
    }

    @Test
    fun testAddEmployeeWithEmptyInput() {
        // Try adding an employee with empty input fields
        val emp = EmpModelClass(1, "", "")
        val success = dbHandler.addEmployee(emp)
        assertThat(success).isEqualTo(-1) // Assuming -1 indicates failure due to empty input
    }



    @Test
    fun testLargeDataSet() {
        // Add a large number of employees
        val numEmployees = 1000
        for (i in 1..numEmployees) {
            val emp = EmpModelClass(i, "Employee $i", "employee$i@example.com")
            dbHandler.addEmployee(emp)
        }

        // View all employees and ensure the correct number of records
        val employees = dbHandler.viewEmployee()
        assertThat(employees).hasSize(numEmployees)
    }

    @Test
    fun testAddRecordWithEmptyFields() {
        val emp = EmpModelClass(1, "", "")
        val success = dbHandler.addEmployee(emp)
        assertThat(success).isEqualTo(-1) // Assuming -1 indicates failure due to empty input
    }

    @Test
    fun testUpdateRecordWithEmptyFields() {
        val emp = EmpModelClass(1, "John Doe", "johndoe@example.com")
        dbHandler.addEmployee(emp)

        val updatedEmp = EmpModelClass(1, "", "")
        val updateSuccess = dbHandler.updateEmployee(updatedEmp)
        assertThat(updateSuccess).isEqualTo(0) // Assuming 0 indicates failure due to empty input
    }

    @Test
    fun testAddRecordWithDuplicateId() {
        val emp1 = EmpModelClass(1, "John Doe", "johndoe@example.com")
        dbHandler.addEmployee(emp1)

        val emp2 = EmpModelClass(1, "Jane Smith", "janesmith@example.com")
        val success = dbHandler.addEmployee(emp2)
        assertThat(success).isEqualTo(-1) // Assuming -1 indicates failure due to duplicate ID
    }

    @Test
    fun testUpdateRecordWithNonExistentId() {
        val emp = EmpModelClass(100, "John Doe", "johndoe@example.com")
        val success = dbHandler.updateEmployee(emp)
        assertThat(success).isEqualTo(0) // Assuming 0 indicates failure due to non-existent ID
    }

    @Test
    fun testDeleteRecordWithNonExistentId() {
        val emp = EmpModelClass(100, "John Doe", "johndoe@example.com")
        val success = dbHandler.deleteEmployee(emp)
        assertThat(success).isEqualTo(0) // Assuming 0 indicates failure due to non-existent ID
    }

    @Test
    fun testAddRecordWithInvalidEmail() {
        val emp = EmpModelClass(1, "John Doe", "invalid_email")
        val success = dbHandler.addEmployee(emp)
        assertThat(success).isEqualTo(-1) // Assuming -1 indicates failure due to invalid email format
    }

    @Test
    fun testValidId() {
        val emp = EmpModelClass(123, "John Doe", "johndoe@example.com")
        val success = dbHandler.addEmployee(emp)
        assertThat(success).isGreaterThan(0)
    }

    @Test
    fun testInvalidId() {
        val emp = EmpModelClass(-1, "John Doe", "johndoe@example.com")
        val success = dbHandler.addEmployee(emp)
        assertThat(success).isEqualTo(-1) // Assuming -1 indicates failure due to invalid ID
    }

    @Test
    fun testValidName() {
        val emp = EmpModelClass(1, "John Doe", "johndoe@example.com")
        val success = dbHandler.addEmployee(emp)
        assertThat(success).isGreaterThan(0)
    }

    @Test
    fun testInvalidName() {
        val emp = EmpModelClass(1, "", "johndoe@example.com")
        val success = dbHandler.addEmployee(emp)
        assertThat(success).isEqualTo(-1) // Assuming -1 indicates failure due to empty name
    }

    @Test
    fun testValidEmail() {
        val emp = EmpModelClass(1, "John Doe", "johndoe@example.com")
        val success = dbHandler.addEmployee(emp)
        assertThat(success).isGreaterThan(0)
    }

    @Test
    fun testInvalidEmail() {
        val emp = EmpModelClass(1, "John Doe", "invalid_email")
        val success = dbHandler.addEmployee(emp)
        assertThat(success).isEqualTo(-1) // Assuming -1 indicates failure due to invalid email format
    }
    @Test
    fun testOnUpgrade() {
        // Perform an upgrade by incrementing the database version
        dbHandler.onUpgrade(dbHandler.writableDatabase, dbHandler.readableDatabase.version, dbHandler.readableDatabase.version + 1)

        // Verify if the table is dropped and recreated
        val cursor = dbHandler.readableDatabase.rawQuery("SELECT * FROM ${DatabaseHandler.TABLE_CONTACTS}", null)
        assertThat(cursor).isNotNull()
        assertThat(cursor.count).isEqualTo(0) // Assuming no records should exist after onUpgrade()
        cursor.close()
    }
    @Test
    fun testHashCode() {
        // Create two instances with the same values
        val emp1 = EmpModelClass(1, "John", "john@example.com")
        val emp2 = EmpModelClass(1, "John", "john@example.com")

        // Calculate hash codes
        val hashCode1 = emp1.hashCode()
        val hashCode2 = emp2.hashCode()

        // Assert that hash codes are equal for instances with the same values
        assertEquals(hashCode1, hashCode2)

        // Create two instances with different values
        val emp3 = EmpModelClass(2, "Jane", "jane@example.com")
        val emp4 = EmpModelClass(3, "Alice", "alice@example.com")

        // Calculate hash codes
        val hashCode3 = emp3.hashCode()
        val hashCode4 = emp4.hashCode()

        // Assert that hash codes are not equal for instances with different values
        assert(hashCode3 != hashCode4)
    }



    @Test
    fun testAddEmployeeWithInvalidEmail() {
        // Obtain a valid context using InstrumentationRegistry
        val context = InstrumentationRegistry.getInstrumentation().context

        // Create an instance of DatabaseHandler with the valid context
        val databaseHandler = DatabaseHandler(context)

        // Create an instance of EmpModelClass with an invalid email
        val invalidEmailEmp = EmpModelClass(1, "John", "invalidemail")

        // Call the addEmployee method with the instance having an invalid email
        val result = databaseHandler.addEmployee(invalidEmailEmp)

        // Assert that -1 is returned because of the invalid email
        assertEquals(-1, result)
    }


    @Test
    fun EqualsReturnsTrueForEqualObjects() {
        // Arrange
        val user1 = EmpModelClass(1, "John", "john@example.com")
        val user2 = EmpModelClass(1, "John", "john@example.com")

        // Act
        val result = user1 == user2

        // Assert
        assertTrue(result)
    }

    @Test
    fun EqualsReturnsFalseForObjectsWithDifferentUserId() {
        // Arrange
        val user1 = EmpModelClass(1, "John", "john@example.com")
        val user2 = EmpModelClass(2, "John", "john@example.com")

        // Act
        val result = user1 == user2

        // Assert
        assertFalse(result)
    }

    @Test
    fun EqualsReturnsFalseForObjectsWithDifferentUserName() {
        // Arrange
        val user1 = EmpModelClass(1, "John", "john@example.com")
        val user2 = EmpModelClass(1, "Doe", "john@example.com")

        // Act
        val result = user1 == user2

        // Assert
        assertFalse(result)
    }

    @Test
    fun EqualsReturnsFalseForObjectsWithDifferentUserEmail() {
        // Arrange
        val user1 = EmpModelClass(1, "John", "john@example.com")
        val user2 = EmpModelClass(1, "John", "doe@example.com")

        // Act
        val result = user1 == user2

        // Assert
        assertFalse(result)
    }

    @Test
    fun EqualsReturnsTrueWhenComparingTheSameObject() {
        // Arrange
        val user1 = EmpModelClass(1, "John", "john@example.com")

        // Act
        val result = user1 == user1

        // Assert
        assertTrue(result)
    }

    @Test
    fun EqualsReturnsFalseWhenComparingWithADifferentType() {
        // Arrange
        val user = EmpModelClass(1, "John", "john@example.com")

        // Act
        val result = user.equals("")

        // Assert
        assertFalse(result)
    }


    }
