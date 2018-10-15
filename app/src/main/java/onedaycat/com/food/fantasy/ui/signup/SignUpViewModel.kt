package onedaycat.com.food.fantasy.ui.signup

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import onedaycat.com.food.fantasy.service.cognito.CognitoService
import onedaycat.com.foodfantasyservicelib.entity.User
import onedaycat.com.foodfantasyservicelib.input.CreateUserInput

class SignUpViewModel(
        private val cogNiToService: CognitoService
): ViewModel() {

    private val mMsgError = MutableLiveData<String>()
    val msgErrorLiveData:LiveData<String>
    get() = mMsgError

    private val mUserLiveData = MutableLiveData<User>()
    val userLiveData: LiveData<User>
    get() = mUserLiveData

    private fun <T> asyncTask(function: () -> T): Deferred<T> {
        return async(CommonPool) { function() }
    }

    suspend fun signUp(input: CreateUserInput) {
        try {
            var user: User? = null
            asyncTask {
                user = cogNiToService.userAuthService.signUp(input)
            }.await()

            user?.let {
                mUserLiveData.postValue(user)
            }
        }catch (e: Exception) {
            mMsgError.postValue(e.message)
        }
    }
}