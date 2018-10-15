package onedaycat.com.food.fantasy.ui.splashscreen

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import onedaycat.com.food.fantasy.ui.signin.SignInActivity
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserDetails
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GetDetailsHandler
import onedaycat.com.food.fantasy.R
import onedaycat.com.food.fantasy.ui.mainfood.activity.MainActivity
import onedaycat.com.food.fantasy.util.CognitoUserHelper
import java.lang.Exception

class SplashScreen : AppCompatActivity() {
    private val mDelayHandler = Handler()
    private val mRunable = Runnable {
        checkUserDetail()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        delay()
    }

    private fun delay() {
        mDelayHandler.postDelayed(mRunable, 3000)
    }

    private fun checkUserDetail() {
        val callback = object : GetDetailsHandler {
            override fun onSuccess(cognitoUserDetails: CognitoUserDetails?) {
                startActivity(Intent(this@SplashScreen, MainActivity::class.java))
                finish()
            }

            override fun onFailure(exception: Exception?) {
                startActivity(Intent(this@SplashScreen, SignInActivity::class.java))
                finish()
            }
        }

        CognitoUserHelper.cognitoUser().currentUser.getDetailsInBackground(callback)
    }

    override fun onDestroy() {
        mDelayHandler.removeCallbacks(mRunable)

        super.onDestroy()
    }
}
