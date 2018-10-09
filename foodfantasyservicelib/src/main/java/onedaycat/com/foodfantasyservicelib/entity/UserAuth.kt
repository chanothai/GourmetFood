package onedaycat.com.foodfantasyservicelib.entity

data class UserAuth(
        var username: String,
        var password: String
) {
    var newPassword: String = ""
    var code: String = ""
}