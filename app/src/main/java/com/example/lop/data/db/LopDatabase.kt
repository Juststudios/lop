package com.example.lop.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.lop.data.models.LopProfile

@Database(entities = [LopProfile::class], version = 1, exportSchema = false)
abstract class LopDatabase : RoomDatabase() {
    abstract fun profileDao(): LopProfileDao
}
