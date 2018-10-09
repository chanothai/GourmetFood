package onedaycat.com.food.fantasy.service

import onedaycat.com.food.fantasy.api.cognito.CognitoUserRepo

class CognitoService {
    val userAuthService = UserAuthenticationService(CognitoUserRepo())
}