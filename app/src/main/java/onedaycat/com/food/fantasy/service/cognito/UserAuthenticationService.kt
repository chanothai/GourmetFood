package onedaycat.com.food.fantasy.service.cognito

import onedaycat.com.food.fantasy.api.cognito.CognitoUserRepo
import onedaycat.com.foodfantasyservicelib.entity.User
import onedaycat.com.foodfantasyservicelib.entity.UserAuth
import onedaycat.com.foodfantasyservicelib.entity.UserCognito
import onedaycat.com.foodfantasyservicelib.input.CreateUserInput
import onedaycat.com.foodfantasyservicelib.input.GetConfirmPasswordInput
import onedaycat.com.foodfantasyservicelib.input.GetUserAuthenInput
import onedaycat.com.foodfantasyservicelib.input.GetUsernameInput
import onedaycat.com.foodfantasyservicelib.util.clock.Clock

class UserAuthenticationService(
        private val cognitoUserRepo: CognitoUserRepo
) {

    fun signUp(input: CreateUserInput): User {
        val time = Clock.NowUTC()
        val user = User(
                "u1",
                input.email,
                input.name,
                input.password,
                input.gender,
                time,
                time
        )

        cognitoUserRepo.create(user)

        return user
    }

    fun signIn(input: GetUserAuthenInput): UserCognito {
        val time = Clock.NowUTC()
        val user = User(
                "u1",
                input.username,
                "",
                input.password,
                time,
                time
        )

        return cognitoUserRepo.authenticate(user)
    }

    fun forgotPassword(input: GetUsernameInput): String {

        cognitoUserRepo.change(input.username)

        return input.username
    }

    fun changePassword(input: GetConfirmPasswordInput): UserAuth {
        val userAuth = UserAuth(
                input.username,
                input.oldPassword
        ).apply {
            newPassword = input.newPassword
            code = input.code
        }

        cognitoUserRepo.confirm(userAuth)

        return userAuth
    }
}