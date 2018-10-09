package onedaycat.com.foodfantasyservicelib.error

open class Error(override val message: String): RuntimeException(message)

class NotFoundException(val code: Int, override val message: String): Error(message)
class InvalidInputException(val code: Int, override val message: String): Error(message)
class BadRequestException(val code: Int, override val message: String): Error(message)
class InternalError(val code: Int, override val message: String): Error(message)
class UnKnownError(val code: Int, override val message: String): Error(message)



