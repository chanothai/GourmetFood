package onedaycat.com.foodfantasyservicelib.entity

import java.util.*

data class UserCognito(
        val username: String,
        val accessToken: String,
        val expired: Date
)