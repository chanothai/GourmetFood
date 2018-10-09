package onedaycat.com.foodfantasyservicelib.entity

data class User(
        var id: String = "",
        var email:String = "",
        var name:String = "",
        var password: String? = "",
        var gender: String = "",
        var createDate: String = "",
        var updateDate: String = "")
