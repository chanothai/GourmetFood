package onedaycat.com.food.fantasy.ui.signin

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import onedaycat.com.food.fantasy.service.CognitoService
import onedaycat.com.foodfantasyservicelib.entity.UserCognito
import onedaycat.com.foodfantasyservicelib.input.GetUserAuthenInput
import java.lang.Exception

class SignInViewModel(
        private val cogNiToService: CognitoService
): ViewModel() {

    private val mMsgError = MutableLiveData<String>()
    val msgErrorLiveData: LiveData<String>
    get() = mMsgError

    private val mUserCogNiTo = MutableLiveData<UserCognito>()
    val userCogNiToLiveData: LiveData<UserCognito>
    get() = mUserCogNiTo

    private fun <T> asyncTask(function: () -> T): Deferred<T> {
        return async(CommonPool) { function() }
    }

    suspend fun signInUser(input: GetUserAuthenInput) {
        try {
            var userCognito: UserCognito? = null
            asyncTask {
                userCognito = cogNiToService.userAuthService.signIn(input)
            }.await()

            userCognito?.let {
                mUserCogNiTo.postValue(it)
            }
        }catch (e: Exception) {
            mMsgError.postValue(e.message)
        }
    }
}