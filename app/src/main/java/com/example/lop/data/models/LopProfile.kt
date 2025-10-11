//package com.example.lop.data.models
//
//data class LopProfile(
//    val name: String,
//    val phoneNumber: String,
//    val email: String?,
//    val instagram: String?,
//    val snapchat: String?,
//    val businessName: String?,
//    val profileType: String // e.g. "Personal", "Business"
//

package com.example.lop.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lop_profiles")
data class LopProfile(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val phoneNumber: String,
    val email: String? = null,
    val businessName: String? = null,
    val instagram: String? = null,
    val snapchat: String? = null,
    val profileType: String? = null

)
