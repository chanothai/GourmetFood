package onedaycat.com.foodfantasyservicelib.entity

enum class TransactionState {
    CHARGE, REFUNDED
}

data class Transaction(
        var id: String,
        var orderID: String,
        var status: TransactionState,
        var amount: Int,
        var createAt: String
)