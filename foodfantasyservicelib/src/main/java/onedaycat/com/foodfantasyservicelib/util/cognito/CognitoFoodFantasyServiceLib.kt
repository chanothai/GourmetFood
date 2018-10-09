package onedaycat.com.foodfantasyservicelib.util.cognito

import android.content.Context
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool
import com.amazonaws.regions.Regions

class CognitoFoodFantasyServiceLib {

    companion object {
        @JvmStatic
        var cognitoUserPool: CognitoUserPool? = null
        get() {
            return field?.let {
                it
            }
        }

        set(value) {
            value?.let {
                field = it
            }
        }
    }
}