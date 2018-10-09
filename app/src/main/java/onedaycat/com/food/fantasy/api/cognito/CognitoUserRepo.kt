package onedaycat.com.food.fantasy.api.cognito

import com.amazonaws.mobileconnectors.cognitoidentityprovider.*
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.*
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.ForgotPasswordHandler
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.SignUpHandler
import onedaycat.com.food.fantasy.util.CognitoUserHelper
import onedaycat.com.foodfantasyservicelib.entity.User
import onedaycat.com.foodfantasyservicelib.entity.UserAuth
import onedaycat.com.foodfantasyservicelib.entity.UserCognito
import onedaycat.com.foodfantasyservicelib.error.Errors

class CognitoUserRepo: CognitoAdapter {
    override fun create(user: User) {
        val signUpHandler = object : SignUpHandler {
            override fun onSuccess(cognitoUser: CognitoUser, userConfirmed: Boolean, cognitoUserCodeDeliveryDetails: CognitoUserCodeDeliveryDetails) {

            }

            override fun onFailure(exception: Exception) {
                throw exception
            }
        }

        val userAttributes = CognitoUserAttributes().apply {
            this.addAttribute("email", user.email)
            this.addAttribute("gender", user.gender)
            this.addAttribute("name", user.name)
        }

        CognitoUserHelper.cognitoUser().signUp(
                user.email,
                user.password,
                userAttributes,
                null,
                signUpHandler)

    }

    override fun authenticate(user: User): UserCognito {
        var userCognito: UserCognito? = null
        val authentication = object : AuthenticationHandler {
            override fun onSuccess(userSession: CognitoUserSession?, newDevice: CognitoDevice?) {
                userSession?.let {
                    val rs = it.refreshToken.token
                    userCognito = UserCognito(
                            it.username,
                            it.accessToken.jwtToken,
                            it.accessToken.expiration
                    )
                }
            }

            override fun onFailure(exception: java.lang.Exception?) {
                exception?.let {ex->
                    throw ex
                }
            }

            override fun getAuthenticationDetails(authenticationContinuation: AuthenticationContinuation?, userId: String?) {
                val authDetail = AuthenticationDetails(user.email, user.password, null)

                authenticationContinuation?.setAuthenticationDetails(authDetail)
                authenticationContinuation?.continueTask()
            }

            override fun authenticationChallenge(continuation: ChallengeContinuation?) {

            }

            override fun getMFACode(continuation: MultiFactorAuthenticationContinuation?) {

            }
        }

        CognitoUserHelper.cognitoUser().user.getSession(authentication)

        userCognito?.let {
            return it
        }

        throw Errors.UnableGetUser
    }

    override fun change(username: String) {
        CognitoUserHelper.cognitoUser().getUser(username).forgotPassword(object: ForgotPasswordHandler {
            override fun onSuccess() {

            }

            override fun onFailure(exception: java.lang.Exception?) {
                exception?.let {ex->
                    throw ex
                }
            }

            override fun getResetCode(continuation: ForgotPasswordContinuation?) {

            }
        })
    }

    override fun confirm(userAuth: UserAuth) {
        val cognitoUser = CognitoUserHelper.cognitoUser().getUser(userAuth.username)

        val forgotPasswordHandler = object : ForgotPasswordHandler {
            override fun onSuccess() {

            }

            override fun onFailure(exception: java.lang.Exception?) {
                exception?.let {ex->
                    throw ex
                }
            }

            override fun getResetCode(continuation: ForgotPasswordContinuation?) {

            }
        }

        cognitoUser.confirmPassword(
                        userAuth.code,
                        userAuth.newPassword,
                        forgotPasswordHandler)

    }
}