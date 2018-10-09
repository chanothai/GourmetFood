package onedaycat.com.food.fantasy.fantasy.ui.signup

import android.arch.core.executor.testing.InstantTaskExecutorRule
import kotlinx.coroutines.experimental.runBlocking
import onedaycat.com.food.fantasy.service.CognitoService
import onedaycat.com.food.fantasy.service.UserAuthenticationService
import onedaycat.com.foodfantasyservicelib.entity.User
import onedaycat.com.foodfantasyservicelib.error.Errors
import onedaycat.com.foodfantasyservicelib.input.CreateUserInput
import onedaycat.com.foodfantasyservicelib.util.clock.Clock
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.mockito.Mockito.*

class SignUpViewModelTest {
    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    private lateinit var cognitoService: CognitoService
    private lateinit var userAuthenticationService: UserAuthenticationService

    private lateinit var signupViewModel: SignUpViewModel

    private lateinit var userExp: User

    @Before
    fun setup() {
        cognitoService = mock(CognitoService::class.java)
        userAuthenticationService = mock(UserAuthenticationService::class.java)

        `when`(cognitoService.userAuthService).thenReturn(userAuthenticationService)

        signupViewModel = SignUpViewModel(cognitoService)

        val time = Clock.NowUTC()
        userExp = User(
                "u1",
                "chanothai@onedaycat.com",
                "chanothai duangrahwa",
                "password22",
                "Male",
                time,
                time
        )
    }

    @Test
    fun signUp_Success() {
        val input = CreateUserInput(
                "chanothai@onedaycat.com",
                "chanothai",
                "password"
        )

        `when`(cognitoService.userAuthService.signUp(input)).thenReturn(userExp)

        runBlocking {
            signupViewModel.signUp(input)
        }

        val result = signupViewModel.userLiveData.value
        Assert.assertEquals(userExp, result)

        verify(cognitoService.userAuthService).signUp(input)
    }

    @Test
    fun signUp_Failed() {
        val input = CreateUserInput(
                "chanothai@onedaycat.com",
                "chanothai",
                "password"
        )

        `when`(cognitoService.userAuthService.signUp(input)).thenThrow(Errors.UnableCreateUser)

        runBlocking {
            signupViewModel.signUp(input)
        }

        val msgErrorExp = Errors.UnableCreateUser.message
        val result = signupViewModel.msgErrorLiveData.value

        Assert.assertEquals(msgErrorExp, result)
    }
}