package com.example.mykotlinapp

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EspressoTest {

    // Define your database handler instance
    private lateinit var dbHelper: DatabaseHandler

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)


    @Before
    fun setUp() {
        // Initialize your database handler in the setup
        val context = ApplicationProvider.getApplicationContext<Context>()
        dbHelper = DatabaseHandler(context)
    }

    @After
    fun tearDown() {
        // Clean up any database operations after each test
        dbHelper.close()
    }


    fun getLogMessage(): String? {
        // Retrieve the logcat output
        val command = "logcat -d"
        val process = Runtime.getRuntime().exec(command)

        // Read the logcat output
        val reader = process.inputStream.bufferedReader()
        val logOutput = StringBuilder()
        var line: String?
        while (reader.readLine().also { line = it } != null) {
            logOutput.append(line).append("\n")
        }
        reader.close()

        return logOutput.toString()
    }




    @Test
    fun testAddRecordToDatabase() {
        // Define test data
        val id = 1
        val username = "John Doe"
        val email = "john@example.com"

        // Perform UI actions to add a record
        onView(withId(R.id.u_id)).perform(typeText(id.toString()), closeSoftKeyboard())
        onView(withId(R.id.u_name)).perform(typeText(username), closeSoftKeyboard())
        onView(withId(R.id.u_email)).perform(typeText(email), closeSoftKeyboard())
        onView(withId(R.id.save_btn)).perform(click())

        // Wait for a specific condition (e.g., log message appears in logcat)
        val startTime = System.currentTimeMillis()
        var logMessage = getLogMessage() // Initialize logMessage as nullable
        while (System.currentTimeMillis() - startTime < TIMEOUT_MS && !(logMessage?.contains("Record saved") ?: false)) {
            // Keep checking for the log message until it appears or timeout occurs
            Thread.sleep(100) // Introduce a delay between checks
            logMessage = getLogMessage() // Refresh the log message
        }

        // Check if the log message is present
        assertTrue(logMessage?.contains("Record saved") ?: false)

        // Check if the record is added to the database
        val record = dbHelper.getRecordById(id)
        assertThat(record, notNullValue())
        assertThat(record?.userName, `is`(username))
        assertThat(record?.userEmail, `is`(email))
    }

    companion object {
        const val TIMEOUT_MS = 5000 // Timeout in milliseconds
    }

//    @Test
//    fun AddUserAndUpdateUser() {
//        val id = 1
//        val username = "Anju"
//        val mail = "anju@gmail.com"
//
//        // Add user
//        onView(withId(R.id.u_id)).perform(typeText(id.toString()))
//        onView(withId(R.id.u_name)).perform(typeText(username))
//        onView(withId(R.id.u_email)).perform(typeText(mail), closeSoftKeyboard())
//        onView(withId(R.id.save_btn)).perform(click())
//
//        val updatedUsername = "Ram"
//        val updatedEmail = "ram.email@example.com"
//
//        // Update user
//        onView(withId(R.id.update_btn)).perform(click())
//        onView(withId(R.id.updateId)).perform(typeText(id.toString()))
//        onView(withId(R.id.updateName)).perform(typeText(updatedUsername))
//        onView(withId(R.id.updateEmail)).perform(typeText(updatedEmail), closeSoftKeyboard())
//        onView(withText("Update")).perform(click())
//
////         Verify that the user's information is updated correctly in the database
//        val updatedRecord = dbHelper.getRecordById(id)
//        Assert.assertNotNull(updatedRecord)
//        Assert.assertEquals(
//            "Updated username should match",
//            updatedUsername,
//            updatedRecord?.userName
//        )
//        Assert.assertEquals("Updated email should match", updatedEmail, updatedRecord?.userEmail)
//
//        // Wait for a specific condition (e.g., log message appears in logcat)
//        val startTime = System.currentTimeMillis()
//        var logMessage = getLogMessage() // Initialize logMessage as nullable
//        while (System.currentTimeMillis() - startTime < TIMEOUT_MS && !(logMessage?.contains("Record updated") ?: false)) {
//            // Keep checking for the log message until it appears or timeout occurs
//            Thread.sleep(100) // Introduce a delay between checks
//            logMessage = getLogMessage() // Refresh the log message
//        }
//
//        // Check if the log message is present
//        assertTrue(logMessage?.contains("Record updated") ?: false)
//    }



    @Test
    fun testAddRecordToDatabaseWithInvalidInput() {
        // Define test data
        val id = 1
        val username = ""
        val email = "john@example.com"

        // Perform UI actions to add a record
        onView(withId(R.id.u_id)).perform(typeText(id.toString()), closeSoftKeyboard())
        onView(withId(R.id.u_name)).perform(typeText(username), closeSoftKeyboard())
        onView(withId(R.id.u_email)).perform(typeText(email), closeSoftKeyboard())
        onView(withId(R.id.save_btn)).perform(click())

        // Wait for a specific condition (e.g., log message appears in logcat)
        val startTime = System.currentTimeMillis()
        var logMessage = getLogMessage() // Initialize logMessage as nullable
        while (System.currentTimeMillis() - startTime < TIMEOUT_MS && !(logMessage?.contains("ID, name, or email cannot be blank") ?: false)) {
            // Keep checking for the log message until it appears or timeout occurs
            Thread.sleep(100) // Introduce a delay between checks
            logMessage = getLogMessage() // Refresh the log message
        }

        // Check if the log message is present
        assertTrue(logMessage?.contains("ID, name, or email cannot be blank") ?: false)
    }


    @Test
    fun testAddRecordToDatabaseWithDuplicateID() {
        // Define test data
        val id = 1
        val username = "john"
        val email = "john@example.com"

        // Perform UI actions to add a record
        onView(withId(R.id.u_id)).perform(typeText(id.toString()), closeSoftKeyboard())
        onView(withId(R.id.u_name)).perform(typeText(username), closeSoftKeyboard())
        onView(withId(R.id.u_email)).perform(typeText(email), closeSoftKeyboard())
        onView(withId(R.id.save_btn)).perform(click())

        val new_id = 1
        val new_username = "john"
        val new_email = "john@example.com"

        // Perform UI actions to add a record
        onView(withId(R.id.u_id)).perform(typeText(new_id.toString()), closeSoftKeyboard())
        onView(withId(R.id.u_name)).perform(typeText(new_username), closeSoftKeyboard())
        onView(withId(R.id.u_email)).perform(typeText(new_email), closeSoftKeyboard())
        onView(withId(R.id.save_btn)).perform(click())

        // Wait for a specific condition (e.g., log message appears in logcat)
        val startTime = System.currentTimeMillis()
        var logMessage = getLogMessage() // Initialize logMessage as nullable
        while (System.currentTimeMillis() - startTime < TIMEOUT_MS && !(logMessage?.contains("Record with this ID already exists") ?: false)) {
            // Keep checking for the log message until it appears or timeout occurs
            Thread.sleep(100) // Introduce a delay between checks
            logMessage = getLogMessage() // Refresh the log message
        }

        // Check if the log message is present
        assertTrue(logMessage?.contains("Record with this ID already exists") ?: false)
    }


    @Test
    fun testUpdateRecordWhichDoesNotExists() {
        // Define test data
        val id = 1
        val username = "john"
        val email = "john@example.com"

        // Perform UI actions to add a record
        onView(withId(R.id.u_id)).perform(typeText(id.toString()), closeSoftKeyboard())
        onView(withId(R.id.u_name)).perform(typeText(username), closeSoftKeyboard())
        onView(withId(R.id.u_email)).perform(typeText(email), closeSoftKeyboard())
        onView(withId(R.id.save_btn)).perform(click())

        onView(withId(R.id.update_btn)).perform(click())
        val new_id = 2
        val new_username = "Ram"
        val new_email = "ram@example.com"

        // Perform UI actions to add a record
        onView(withId(R.id.updateId)).perform(typeText(new_id.toString()), closeSoftKeyboard())
        onView(withId(R.id.updateName)).perform(typeText(new_username), closeSoftKeyboard())
        onView(withId(R.id.updateEmail)).perform(typeText(new_email), closeSoftKeyboard())
        onView(withText("Update Record")).inRoot(isDialog()).check(matches(isDisplayed()))

    // Click on the positive button with text "Update" inside the dialog
        onView(withText("Update")).inRoot(isDialog()).check(matches(isDisplayed())).perform(click())


        // Wait for a specific condition (e.g., log message appears in logcat)
        val startTime = System.currentTimeMillis()
        var logMessage = getLogMessage() // Initialize logMessage as nullable
        while (System.currentTimeMillis() - startTime < TIMEOUT_MS && !(logMessage?.contains("Record with this ID does not exist") ?: false)) {
            // Keep checking for the log message until it appears or timeout occurs
            Thread.sleep(100) // Introduce a delay between checks
            logMessage = getLogMessage() // Refresh the log message
        }

        // Check if the log message is present
        assertTrue(logMessage?.contains("Record with this ID does not exist") ?: false)
    }



    @Test
    fun addUserAndViewUser() {
        val id = 1
        val username = "Anju"
        val mail = "anju@gmail.com"

        // Add user
        onView(withId(R.id.u_id)).perform(typeText(id.toString()))
        onView(withId(R.id.u_name)).perform(typeText(username))
        onView(withId(R.id.u_email)).perform(typeText(mail), closeSoftKeyboard())
        onView(withId(R.id.save_btn)).perform(click())

        // View user
        onView(withId(R.id.view_btn)).perform(click())

        // Verify if the ListView is displayed
        onView(withId(R.id.listView)).check(matches(isDisplayed()))
    }



    @Test
    fun AddUserAndDeleteUser() {

        val id = 1
        val username = "Anju"
        val mail = "anju@gmail.com"

        // Add user
        onView(withId(R.id.u_id)).perform(typeText(id.toString()))
        onView(withId(R.id.u_name)).perform(typeText(username))
        onView(withId(R.id.u_email)).perform(typeText(mail), closeSoftKeyboard())
        onView(withId(R.id.save_btn)).perform(click())

        // Delete user
        onView(withId(R.id.delete_btn)).perform(click())
        onView(withId(R.id.deleteId)).perform(typeText(id.toString()), closeSoftKeyboard())
        onView(withText("Delete")).perform(click())

        // Wait for a specific condition (e.g., log message appears in logcat)
        val startTime = System.currentTimeMillis()
        var logMessage = getLogMessage() // Initialize logMessage as nullable
        while (System.currentTimeMillis() - startTime < TIMEOUT_MS && !(logMessage?.contains("Record deleted") ?: false)) {
            // Keep checking for the log message until it appears or timeout occurs
            Thread.sleep(100) // Introduce a delay between checks
            logMessage = getLogMessage() // Refresh the log message
        }

        // Check if the log message is present
        assertTrue(logMessage?.contains("Record deleted") ?: false)

    }

    @Test
    fun DeleteUserWhichDoesNotExists(){
        val id = 1
        // Delete user
        onView(withId(R.id.delete_btn)).perform(click())
        onView(withId(R.id.deleteId)).perform(typeText(id.toString()), closeSoftKeyboard())
        onView(withText("Delete")).perform(click())


        // Wait for a specific condition (e.g., log message appears in logcat)
        val startTime = System.currentTimeMillis()
        var logMessage = getLogMessage() // Initialize logMessage as nullable
        while (System.currentTimeMillis() - startTime < TIMEOUT_MS && !(logMessage?.contains("Record with this ID does not exist") ?: false)) {
            // Keep checking for the log message until it appears or timeout occurs
            Thread.sleep(100) // Introduce a delay between checks
            logMessage = getLogMessage() // Refresh the log message
        }

        // Check if the log message is present
        assertTrue(logMessage?.contains("Record with this ID does not exist") ?: false)
    }


    @Test
    fun DeleteUserWithInvalidID(){
        val id = ""
        // Delete user
        onView(withId(R.id.delete_btn)).perform(click())
        onView(withId(R.id.deleteId)).perform(typeText(id), closeSoftKeyboard())
        onView(withText("Delete")).perform(click())


        // Wait for a specific condition (e.g., log message appears in logcat)
        val startTime = System.currentTimeMillis()
        var logMessage = getLogMessage() // Initialize logMessage as nullable
        while (System.currentTimeMillis() - startTime < TIMEOUT_MS && !(logMessage?.contains("ID cannot be blank") ?: false)) {
            // Keep checking for the log message until it appears or timeout occurs
            Thread.sleep(100) // Introduce a delay between checks
            logMessage = getLogMessage() // Refresh the log message
        }

        // Check if the log message is present
        assertTrue(logMessage?.contains("ID cannot be blank") ?: false)
    }
}