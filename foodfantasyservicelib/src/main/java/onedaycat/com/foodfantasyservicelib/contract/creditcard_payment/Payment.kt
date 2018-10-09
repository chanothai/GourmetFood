package onedaycat.com.foodfantasyservicelib.contract.creditcard_payment

import onedaycat.com.foodfantasyservicelib.entity.Order
import onedaycat.com.foodfantasyservicelib.entity.Transaction
import onedaycat.com.foodfantasyservicelib.entity.TransactionState
import onedaycat.com.foodfantasyservicelib.error.Error
import onedaycat.com.foodfantasyservicelib.error.Errors
import onedaycat.com.foodfantasyservicelib.input.CreditCardType
import onedaycat.com.foodfantasyservicelib.util.clock.Clock
import onedaycat.com.foodfantasyservicelib.util.idgen.IdGen


data class CreditCard(
        var type: CreditCardType,
        var name: String,
        var cardNumber: String,
        var cvv: String,
        var expiredDate: String)

interface CreditCardPayment {
    fun charge(order: Order, creditCard: CreditCard): Transaction?
    fun refund(order: Order): Transaction?
}

class CreditCardMemoPayment: CreditCardPayment {

    private val creditCard: CreditCard = CreditCard(
            CreditCardType.CreditCardMasterCard,
            "chanothai",
            "0000000000000",
            "123",
            "01/21"
    )

    override fun charge(order: Order, creditCard: CreditCard): Transaction? {
        if (this.creditCard != creditCard) {
            throw Errors.UnableChargeCreditCard
        }

        return Transaction(
                IdGen.NewId(),
                order.id!!,
                TransactionState.CHARGE,
                order.totalPrice,
                Clock.NowUTC())
    }

    override fun refund(order: Order): Transaction? {
        return Transaction(
                IdGen.NewId(),
                order.id!!,
                TransactionState.REFUNDED,
                order.totalPrice,
                Clock.NowUTC())
    }

}
