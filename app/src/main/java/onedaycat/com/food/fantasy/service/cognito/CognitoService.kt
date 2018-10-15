package onedaycat.com.food.fantasy.service.cognito

import onedaycat.com.food.fantasy.api.cognito.CognitoUserRepo

class CognitoService {
    val userAuthService = UserAuthenticationService(CognitoUserRepo())
}