package com.bitxflow.sungmin_android.DB

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = arrayOf(User::class),version = 1)
abstract class MemberDatabase : RoomDatabase()
{
    abstract fun userDao():UserDao

    companion object{
        private var INSTANCE : MemberDatabase? = null

        fun getInstance(context : Context): MemberDatabase? {
            if(INSTANCE == null){
                synchronized(MemberDatabase::class){
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                        MemberDatabase::class.java,
                        "MemberDatabase.db")
                        .fallbackToDestructiveMigration()
                        .build()
                }
            }

            return INSTANCE
        }

        fun destroyInstace(){
            INSTANCE = null
        }
    }


}