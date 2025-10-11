package com.example.lop.data.models

data class LopProfile(
    val name: String,
    val phoneNumber: String,
    val email: String?,
    val instagram: String?,
    val snapchat: String?,
    val businessName: String?,
    val profileType: String // e.g. "Personal", "Business"
)