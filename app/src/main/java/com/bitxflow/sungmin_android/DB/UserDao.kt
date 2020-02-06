package com.bitxflow.sungmin_android.DB

import androidx.room.*

@Dao
interface UserDao{

    @Query("SELECT * from user")
    fun getUsers() : List<User>

    @Query("SELECT * from user WHERE user_id = :userId")
    fun getUser(userId : String) : User

    @Query("SELECT * from user WHERE multy_login = :multy")
    fun getMultyLoginUser(multy : Boolean) : User

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(user : User)

    @Update
    fun update(newUser : User)

    @Query("DELETE from user")
    fun deleteAll()

    @Delete
    fun deleteUser(user: User)
}