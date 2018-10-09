package onedaycat.com.foodfantasyservicelib.validate

import onedaycat.com.foodfantasyservicelib.error.Errors
import onedaycat.com.foodfantasyservicelib.input.CreateUserInput

interface UserValidate {
    fun inputUser(input: CreateUserInput)
    fun inputId(id: String)
}

class UserMemoryValidate: UserValidate {
    override fun inputUser(input: CreateUserInput){
        if ((input.name.isEmpty() || input.name.isBlank())
                || (input.email.isEmpty() || input.email.isBlank())
                || (input.password.isEmpty() || input.password.isBlank()))
        {
            throw Errors.InvalidInput
        }
    }

    override fun inputId(id: String) {
        if (id.isEmpty() || id.isBlank()) {
            throw Errors.InvalidInput
        }
    }
}