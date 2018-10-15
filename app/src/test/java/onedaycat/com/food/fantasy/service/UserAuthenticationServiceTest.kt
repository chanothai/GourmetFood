package onedaycat.com.food.fantasy.service

import onedaycat.com.food.fantasy.api.cognito.CognitoUserRepo
import onedaycat.com.food.fantasy.service.cognito.UserAuthenticationService
import onedaycat.com.foodfantasyservicelib.entity.User
import onedaycat.com.foodfantasyservicelib.entity.UserAuth
import onedaycat.com.foodfantasyservicelib.entity.UserCognito
import onedaycat.com.foodfantasyservicelib.error.BadRequestException
import onedaycat.com.foodfantasyservicelib.error.Errors
import onedaycat.com.foodfantasyservicelib.error.InternalError
import onedaycat.com.foodfantasyservicelib.input.CreateUserInput
import onedaycat.com.foodfantasyservicelib.input.GetConfirmPasswordInput
import onedaycat.com.foodfantasyservicelib.input.GetUserAuthenInput
import onedaycat.com.foodfantasyservicelib.input.GetUsernameInput
import onedaycat.com.foodfantasyservicelib.util.clock.Clock
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import java.time.LocalDate
import java.time.ZoneId
import java.util.*

class UserAuthenticationServiceTest {

    @Mock private lateinit var cognitoUserRepo: CognitoUserRepo

    private lateinit var userAuthenticationService: UserAuthenticationService

    private lateinit var userExp: User

    @Before
    fun setup() {
        cognitoUserRepo = mock(CognitoUserRepo::class.java)
        userAuthenticationService = UserAuthenticationService(cognitoUserRepo)

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
    fun signUpSuccess() {
        val input = CreateUserInput(
                userExp.email,
                userExp.name,
                userExp.password!!
        ).apply {
            this.gender = userExp.gender
        }

        doNothing().`when`(cognitoUserRepo).create(userExp)

        val result = userAuthenticationService.signUp(input)

        Assert.assertEquals(userExp, result)

        verify(cognitoUserRepo).create(userExp)
    }

    @Test(expected = InternalError::class)
    fun signUpFailed() {
        val input = CreateUserInput(
                userExp.email,
                userExp.name,
                userExp.password!!
        ).apply {
            this.gender = userExp.gender
        }

        `when`(cognitoUserRepo.create(userExp)).thenThrow(Errors.UnableCreateUser)

        userAuthenticationService.signUp(input)
    }

    @Test
    fun signInSuccess() {
        val input = GetUserAuthenInput(
                userExp.email,
                userExp.password!!
        )

        val time = Clock.NowUTC()

        val user = User(
                "u1",
                input.username,
                "",
                input.password,
                time,
                time
        )

        val userCognitoExp = UserCognito(
                userExp.email,
                "token1234",
                Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant())
        )

        `when`(cognitoUserRepo.authenticate(user)).thenReturn(userCognitoExp)

        val result = userAuthenticationService.signIn(input)

        Assert.assertEquals(userCognitoExp, result)

        verify(cognitoUserRepo).authenticate(user)
    }

    @Test(expected = BadRequestException::class)
    fun signInFailed() {
        val input = GetUserAuthenInput(
                userExp.email,
                userExp.password!!
        )

        val time = Clock.NowUTC()

        val user = User(
                "u1",
                input.username,
                "",
                input.password,
                time,
                time
        )

        `when`(cognitoUserRepo.authenticate(user)).thenThrow(BadRequestException::class.java)

        userAuthenticationService.signIn(input)
    }

    @Test
    fun checkUserName_For_ForgotPassword_Success() {
        val input = GetUsernameInput(
                userExp.email
        )
        doNothing().`when`(cognitoUserRepo).change(input.username)

        val result = userAuthenticationService.forgotPassword(input)

        Assert.assertEquals(result, input.username)

        verify(cognitoUserRepo).change(input.username)
    }

    @Test(expected = BadRequestException::class)
    fun checkUserName_For_ForgotPassword_Failed() {
        val input = GetUsernameInput(
                userExp.email
        )

        `when`(cognitoUserRepo.change(input.username)).thenThrow(BadRequestException::class.java)

        userAuthenticationService.forgotPassword(input)
    }

    @Test
    fun confirm_ChangePassword_Success() {
        val input = GetConfirmPasswordInput(
                userExp.email,
                "password22",
                userExp.password!!,
                "1234"
        )

        val userAuthenExp = UserAuth(
                input.username,
                input.oldPassword
        ).apply {
            this.newPassword = input.newPassword
            this.code = input.code
        }

        doNothing().`when`(cognitoUserRepo).confirm(userAuthenExp)

        val result = userAuthenticationService.changePassword(input)

        Assert.assertEquals(userAuthenExp, result)

        verify(cognitoUserRepo).confirm(userAuthenExp)
    }

    @Test(expected = BadRequestException::class)
    fun confirm_ChangePassword_Failed() {
        val input = GetConfirmPasswordInput(
                userExp.email,
                "password22",
                userExp.password!!,
                "1234"
        )

        val userAuthenExp = UserAuth(
                input.username,
                input.oldPassword
        ).apply {
            this.newPassword = input.newPassword
            this.code = input.code
        }

        `when`(cognitoUserRepo.confirm(userAuthenExp)).thenThrow(BadRequestException::class.java)

        userAuthenticationService.changePassword(input)
    }
}