package com.lge.core.db

import androidx.room.Room
import androidx.room.RoomDatabase
import com.lge.core.app.ApplicationProxy

class DatabaseManager {
    private lateinit var mDatabase: RoomDatabase

    companion object {
        private var mInstance: DatabaseManager? = null

        @JvmStatic
        fun getInstance(): DatabaseManager = mInstance?:
        synchronized(this) {
            mInstance?: DatabaseManager().also {
                mInstance = it
            }
        }
    }

    fun init() {
        mDatabase = Room.databaseBuilder(ApplicationProxy.getContext(), AppDatabase::class.java, "AppDB").build()
    }

    fun getDatabase(): AppDatabase = mDatabase as AppDatabase

    fun printStatus() {
        val appDatabase: AppDatabase = getDatabase() as AppDatabase

        // count
        // isOpen
    }
}