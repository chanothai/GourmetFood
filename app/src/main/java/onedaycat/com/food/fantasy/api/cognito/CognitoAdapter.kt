package onedaycat.com.food.fantasy.api.cognito

import onedaycat.com.foodfantasyservicelib.entity.User
import onedaycat.com.foodfantasyservicelib.entity.UserAuth
import onedaycat.com.foodfantasyservicelib.entity.UserCognito

interface CognitoAdapter {
    fun create(user: User)
    fun authenticate(user: User): UserCognito
    fun change(username: String)
    fun confirm(userAuth: UserAuth)
}