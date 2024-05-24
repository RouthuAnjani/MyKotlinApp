package com.example.mykotlinapp

//creating a Data Model Class
class EmpModelClass (var userId: Int, val userName:String , val userEmail: String){
    // Add these methods to your EmpModelClass

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is EmpModelClass) return false
        if (userId != other.userId) return false
        if (userName != other.userName) return false
        if (userEmail != other.userEmail) return false
        return true
    }

    override fun hashCode(): Int {
        var result = userId
        result = 31 * result + userName.hashCode()
        result = 31 * result + userEmail.hashCode()
        return result
    }

}