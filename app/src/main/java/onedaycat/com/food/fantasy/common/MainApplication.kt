package onedaycat.com.food.fantasy.common

import android.app.Application
import android.content.Context
import onedaycat.com.food.fantasy.util.CognitoUserHelper
import onedaycat.com.foodfantasyservicelib.util.cognito.CognitoFoodFantasyServiceLib

class MainApplication: Application() {
    init {
        instance = this
    }

    companion object {
        private var instance: MainApplication? = null

        fun applicationContext(): Context {
            return instance!!.applicationContext
        }
    }

    override fun onCreate() {
        super.onCreate()

        val context = MainApplication.applicationContext()

        CognitoUserHelper.cognitoUser.let {
            CognitoFoodFantasyServiceLib.cognitoUserPool = it()
        }
    }
}