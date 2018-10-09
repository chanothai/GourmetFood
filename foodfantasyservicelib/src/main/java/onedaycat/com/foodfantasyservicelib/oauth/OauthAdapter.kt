package onedaycat.com.food.fantasy.oauth

import onedaycat.com.foodfantasyservicelib.entity.Token

interface OauthAdapter {
    fun validateToken(): Token
}