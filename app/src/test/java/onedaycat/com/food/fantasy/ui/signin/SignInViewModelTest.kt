package onedaycat.com.food.fantasy.fantasy.ui.signin

import android.arch.core.executor.testing.InstantTaskExecutorRule
import kotlinx.coroutines.experimental.runBlocking
import onedaycat.com.food.fantasy.service.CognitoService
import onedaycat.com.food.fantasy.service.UserAuthenticationService
import onedaycat.com.foodfantasyservicelib.entity.User
import onedaycat.com.foodfantasyservicelib.entity.UserCognito
import onedaycat.com.foodfantasyservicelib.error.Errors
import onedaycat.com.foodfantasyservicelib.input.GetUserAuthenInput
import onedaycat.com.foodfantasyservicelib.util.clock.Clock
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import java.time.LocalDate
import java.time.ZoneId
import java.util.*

class SignInViewModelTest {
    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    private lateinit var cognitoService: CognitoService
    private lateinit var userAuthenticationService: UserAuthenticationService

    private lateinit var signInViewModel: SignInViewModel

    private lateinit var userExp: User

    @Before
    fun setup() {
        cognitoService = Mockito.mock(CognitoService::class.java)
        userAuthenticationService = Mockito.mock(UserAuthenticationService::class.java)

        `when`(cognitoService.userAuthService).thenReturn(userAuthenticationService)

        signInViewModel = SignInViewModel(cognitoService)

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
    fun signin_Success() {
        val input = GetUserAuthenInput(
                userExp.email,
                userExp.password!!
        )

        val userCognitoExp = UserCognito(
                userExp.email,
                "token1234",
                Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant())
        )

        `when`(cognitoService.userAuthService.signIn(input)).thenReturn(userCognitoExp)

        runBlocking {
            signInViewModel.signInUser(input)
        }

        val result = signInViewModel.userCognitoLiveData.value

        Assert.assertEquals(userCognitoExp, result)

        verify(cognitoService.userAuthService).signIn(input)
    }

    @Test
    fun signIn_Failed() {
        val input = GetUserAuthenInput(
                userExp.email,
                userExp.password!!
        )

        val userCognitoExp = UserCognito(
                userExp.email,
                "token1234",
                Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant())
        )

        `when`(cognitoService.userAuthService.signIn(input)).thenThrow(Errors.UnableGetUser)

        runBlocking {
            signInViewModel.signInUser(input)
        }

        val msgErrorExp = Errors.UnableGetUser.message
        val result = signInViewModel.msgErrorLiveData.value

        Assert.assertEquals(msgErrorExp, result)
    }
}