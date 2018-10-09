package onedaycat.com.foodfantasyservicelib.service

import onedaycat.com.foodfantasyservicelib.contract.creditcard_payment.CreditCardPayment
import onedaycat.com.foodfantasyservicelib.entity.Order
import onedaycat.com.foodfantasyservicelib.contract.repository.CartRepo
import onedaycat.com.foodfantasyservicelib.contract.repository.OrderRepo
import onedaycat.com.foodfantasyservicelib.contract.repository.PaymentRepo
import onedaycat.com.foodfantasyservicelib.contract.repository.StockRepo
import onedaycat.com.foodfantasyservicelib.entity.State
import onedaycat.com.foodfantasyservicelib.error.Errors
import onedaycat.com.foodfantasyservicelib.input.ChargeInput
import onedaycat.com.foodfantasyservicelib.input.RefundInput
import onedaycat.com.foodfantasyservicelib.util.clock.Clock
import onedaycat.com.foodfantasyservicelib.util.idgen.IdGen
import onedaycat.com.foodfantasyservicelib.validate.PaymentValidate

class PaymentService(private val orderRepo: OrderRepo,
                     private val ccPayment: CreditCardPayment,
                     private val stockRepo: StockRepo,
                     private val cartRepo: CartRepo,
                     private val paymentRepo: PaymentRepo,
                     private val paymentValidate: PaymentValidate) {

    fun charge(input: ChargeInput): Order? {
        paymentValidate.inputCharge(input)

        //get all product stock
        val pstocks = stockRepo.getByIDs(input.cart.productIDs())

        pstocks.let {ps->
            //withdraw product stock into stock
            for (pstock in ps) {
                //get product qty every product in cart
                val pQTY = input.cart.getPQTY(pstock?.productID!!)

                if (pQTY != null) {
                    pstock.withDraw(pQTY.qty)
                    stockRepo.upsert(pstock)
                }
            }
        }

        //create pending order with product from cart
        val order = Order(
                id = IdGen.NewId(),
                userId = input.userID,
                products = input.cart.toProductQTYList(),
                totalPrice = input.cart.totalPrice(),
                createDate = Clock.NowUTC(),
                status = State.OrderStatus.PENDING
        )

        //charge credit card
        val tx = ccPayment.charge(order, input.creditCard)

        //update order status to paid
        tx?.let {
            order.paid(tx)
            orderRepo.upsert(order)
            it
        }?.also {
            //create order into repository
            paymentRepo.savePayment(order, tx, pstocks)
        }?.run {
            //delete cart
            cartRepo.delete(input.userID)
        }

        return order
    }

    fun refund(input: RefundInput): Order? {
        paymentValidate.inputRefund(input)

        val order = orderRepo.get(input.orderID)

        if (order.userId != input.userID) {
            throw Errors.NotOrderOwner
        }

        val pstocks = stockRepo.getByIDs(order.productIDs())

        //deposit product stock
        for ( pstock in pstocks) {
            pstock!!.deposit(order.getProductQTY(pstock.productID!!)!!.qty)
        }

        //refund no payment
        val tx = ccPayment.refund(order)

        //make order status to be refunded
        order.refund(tx!!)

        //update order
        paymentRepo.savePayment(order, tx, pstocks)

        return order
    }
}