package onedaycat.com.food.fantasy.ui.forgotpassword

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import onedaycat.com.food.fantasy.service.cognito.CognitoService
import onedaycat.com.foodfantasyservicelib.entity.UserAuth
import onedaycat.com.foodfantasyservicelib.input.GetConfirmPasswordInput
import onedaycat.com.foodfantasyservicelib.input.GetUsernameInput
import java.lang.Exception

class ForgotPasswordViewModel(
        private val cogNiToService: CognitoService
): ViewModel() {

    private val mUsernameLiveData = MutableLiveData<String>()
    val usernameLiveData: LiveData<String>
    get() = mUsernameLiveData

    private val mMsgErrorLiveData = MutableLiveData<String>()
    val msgErrorLiveData: LiveData<String>
    get() = mMsgErrorLiveData

    private val mUserAuthLiveData = MutableLiveData<UserAuth>()
    val userAuthLiveData: LiveData<UserAuth>
    get() = mUserAuthLiveData

    private fun <T> asyncTask(function: () -> T): Deferred<T> {
        return async(CommonPool) { function() }
    }

    suspend fun forgotPassword(input: GetUsernameInput) {
        try {
            var username: String? = null
            asyncTask {
                username = cogNiToService.userAuthService.forgotPassword(input)
            }.await()

            mUsernameLiveData.postValue(username)
        }catch (e: Exception) {
            mMsgErrorLiveData.postValue(e.message)
        }
    }

    suspend fun confirmPassword(input: GetConfirmPasswordInput) {
        try {
            var userAuth: UserAuth? = null
            asyncTask {
                userAuth = cogNiToService.userAuthService.changePassword(input)
            }.await()

            mUserAuthLiveData.postValue(userAuth)

        }catch (e: Exception) {
            mMsgErrorLiveData.postValue(e.message)
        }
    }
}