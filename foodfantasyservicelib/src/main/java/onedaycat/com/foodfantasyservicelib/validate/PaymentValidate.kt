package onedaycat.com.foodfantasyservicelib.validate

import onedaycat.com.foodfantasyservicelib.error.Errors
import onedaycat.com.foodfantasyservicelib.input.ChargeInput
import onedaycat.com.foodfantasyservicelib.input.RefundInput

interface PaymentValidate {
    fun inputCharge(input: ChargeInput)
    fun inputRefund(input: RefundInput)
}

class PaymentMemoValidate: PaymentValidate {
    override fun inputCharge(input: ChargeInput) {
        if (input.userID.isBlank() || input.userID.isEmpty()) {
            throw Errors.InvalidInput
        }

        if ((input.creditCard.name.isEmpty() || input.creditCard.name.isBlank())
                || (input.creditCard.cvv.isEmpty() || input.creditCard.cvv.isBlank())
                || (input.creditCard.expiredDate.isEmpty() || input.creditCard.expiredDate.isBlank())) {

            throw Errors.InvalidInput
        }
    }

    override fun inputRefund(input: RefundInput) {
        if (input.userID.isEmpty()
                || input.userID.isBlank()
                || input.orderID.isEmpty()
                || input.orderID.isBlank()) {

            throw Errors.InvalidInput
        }
    }
}