package com.example.lop.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.lop.data.models.LopProfile

@Dao
interface LopProfileDao {
    @Insert
    suspend fun insertProfile(profile: LopProfile)

    @Query("SELECT * FROM lop_profiles")
    suspend fun getAllProfiles(): List<LopProfile>
}
