package onedaycat.com.food.fantasy.ui.forgotpassword

import android.arch.core.executor.testing.InstantTaskExecutorRule
import kotlinx.coroutines.experimental.runBlocking
import onedaycat.com.food.fantasy.service.CognitoService
import onedaycat.com.food.fantasy.service.UserAuthenticationService
import onedaycat.com.foodfantasyservicelib.entity.UserAuth
import onedaycat.com.foodfantasyservicelib.error.Errors
import onedaycat.com.foodfantasyservicelib.input.GetConfirmPasswordInput
import onedaycat.com.foodfantasyservicelib.input.GetUsernameInput
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify

class ForgotPasswordViewModelTest {
    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    private lateinit var cognitoService: CognitoService
    private lateinit var userAuthenticationService: UserAuthenticationService

    private lateinit var forgotPasswordViewModel: ForgotPasswordViewModel

    @Before
    fun setup() {
        cognitoService = Mockito.mock(CognitoService::class.java)
        userAuthenticationService = Mockito.mock(UserAuthenticationService::class.java)

        `when`(cognitoService.userAuthService).thenReturn(userAuthenticationService)

        forgotPasswordViewModel = ForgotPasswordViewModel(cognitoService)
    }

    @Test
    fun checkUserName_ForgotPassword_Success() {
        val input = GetUsernameInput(
                "u1"
        )

        `when`(cognitoService.userAuthService.forgotPassword(input)).thenReturn(input.username)

        runBlocking {
            forgotPasswordViewModel.forgotPassword(input)
        }

        val result = forgotPasswordViewModel.usernameLiveData.value

        Assert.assertEquals(input.username, result)

        verify(cognitoService.userAuthService).forgotPassword(input)
    }

    @Test
    fun checkUserName_ForgotPassword_Failed() {
        val input = GetUsernameInput(
                "u1"
        )

        `when`(cognitoService.userAuthService.forgotPassword(input)).thenThrow(Errors.UnableGetUser)

        runBlocking {
            forgotPasswordViewModel.forgotPassword(input)
        }

        val msgErrorExp = Errors.UnableGetUser.message
        val result = forgotPasswordViewModel.msgErrorLiveData.value

        Assert.assertEquals(msgErrorExp, result)
    }

    @Test
    fun confirmForgotPassword_Success() {
        val input = GetConfirmPasswordInput(
                "u1",
                "np",
                "op",
                "123"
        )

        val userAuthenExp = UserAuth(
                input.username,
                input.oldPassword
        ).apply {
            this.newPassword = input.newPassword
            this.code = input.code
        }

        `when`(cognitoService.userAuthService.changePassword(input)).thenReturn(userAuthenExp)

        runBlocking {
            forgotPasswordViewModel.confirmPassword(input)
        }

        val result = forgotPasswordViewModel.userAuthLiveData.value

        Assert.assertEquals(userAuthenExp, result)

        verify(cognitoService.userAuthService).changePassword(input)
    }

    @Test
    fun confirmForgotPassword_Failed() {
        val input = GetConfirmPasswordInput(
                "u1",
                "np",
                "op",
                "123"
        )

        `when`(cognitoService.userAuthService.changePassword(input)).thenThrow(Errors.UnableCreateUser)

        runBlocking {
            forgotPasswordViewModel.confirmPassword(input)
        }

        val msgErrorExp = Errors.UnableCreateUser.message
        val result = forgotPasswordViewModel.msgErrorLiveData.value

        Assert.assertEquals(msgErrorExp, result)
    }
}