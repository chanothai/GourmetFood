package onedaycat.com.food.fantasy.oauth

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationDetails
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler
import onedaycat.com.foodfantasyservicelib.entity.Token
import onedaycat.com.foodfantasyservicelib.error.Error
import onedaycat.com.foodfantasyservicelib.error.Errors
import onedaycat.com.foodfantasyservicelib.util.cognito.CognitoFoodFantasyServiceLib
import java.lang.Exception

class OauthCognito: OauthAdapter {

    override fun validateToken(): Token {
        var token: Token? = null

        val authenticationHandler = object : AuthenticationHandler {
            override fun onSuccess(userSession: CognitoUserSession?, newDevice: CognitoDevice?) {
                userSession?.let {
                    if (!it.isValid) {
                        throw Errors.TokenExpired
                    }

                    token = Token(
                            it.accessToken.jwtToken,
                            it.refreshToken.token
                    )
                }
            }

            override fun onFailure(exception: Exception?) {
                exception?.let {
                    throw it
                }
            }

            override fun getAuthenticationDetails(authenticationContinuation: AuthenticationContinuation?, userId: String?) {
                userId?.let {
                    return
                }

                val authDetail = AuthenticationDetails("chanothai@onedaycat.com", "password", null)

                authenticationContinuation?.setAuthenticationDetails(authDetail)
                authenticationContinuation?.continueTask()
            }

            override fun authenticationChallenge(continuation: ChallengeContinuation?) {

            }

            override fun getMFACode(continuation: MultiFactorAuthenticationContinuation?) {

            }
        }

        CognitoFoodFantasyServiceLib.cognitoUserPool?.currentUser?.getSession(authenticationHandler)

        token?.let {
            return it
        }

        throw Errors.TokenNotFound
    }
}