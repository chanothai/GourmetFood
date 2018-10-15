package onedaycat.com.foodfantasyservicelib.service

import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import onedaycat.com.foodfantasyservicelib.entity.User
import onedaycat.com.foodfantasyservicelib.error.*
import onedaycat.com.foodfantasyservicelib.contract.repository.UserRepo
import onedaycat.com.foodfantasyservicelib.input.CreateUserInput
import onedaycat.com.foodfantasyservicelib.input.GetUserInput
import onedaycat.com.foodfantasyservicelib.util.clock.Clock
import onedaycat.com.foodfantasyservicelib.util.idgen.IdGen
import onedaycat.com.foodfantasyservicelib.validate.UserValidate

class UserService(
        private val userRepo: UserRepo,
        private val userValidate: UserValidate) {

    private var newUser: User? = null

    fun createUser(input: CreateUserInput): User? {
        try {
            userValidate.inputUser(input)

            val user = userRepo.getByEmail(input.email)

            if (user != null) {
                throw Errors.EmailExist
            }

        }catch (e:NotFoundException) {
            newUser = User(
                    IdGen.NewId(),
                    input.email,
                    input.name,
                    input.password,
                    Clock.NowUTC(),
                    Clock.NowUTC())

            userRepo.create(newUser!!)
        }

        return newUser
    }

    fun getUser(input: GetUserInput): User? {
        userValidate.inputId(input.userId)

        return userRepo.get(input.userId)
    }
}