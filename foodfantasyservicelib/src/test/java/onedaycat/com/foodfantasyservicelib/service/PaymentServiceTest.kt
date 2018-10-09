package onedaycat.com.foodfantasyservicelib.service

import onedaycat.com.foodfantasyservicelib.contract.creditcard_payment.CreditCard
import onedaycat.com.foodfantasyservicelib.contract.creditcard_payment.CreditCardPayment
import onedaycat.com.foodfantasyservicelib.contract.repository.CartRepo
import onedaycat.com.foodfantasyservicelib.contract.repository.OrderRepo
import onedaycat.com.foodfantasyservicelib.contract.repository.PaymentRepo
import onedaycat.com.foodfantasyservicelib.contract.repository.StockRepo
import onedaycat.com.foodfantasyservicelib.entity.*
import onedaycat.com.foodfantasyservicelib.error.BadRequestException
import onedaycat.com.foodfantasyservicelib.error.Errors
import onedaycat.com.foodfantasyservicelib.error.InternalError
import onedaycat.com.foodfantasyservicelib.error.InvalidInputException
import onedaycat.com.foodfantasyservicelib.error.NotFoundException
import onedaycat.com.foodfantasyservicelib.input.ChargeInput
import onedaycat.com.foodfantasyservicelib.input.CreditCardType
import onedaycat.com.foodfantasyservicelib.input.RefundInput
import onedaycat.com.foodfantasyservicelib.util.clock.Clock
import onedaycat.com.foodfantasyservicelib.util.idgen.IdGen
import onedaycat.com.foodfantasyservicelib.validate.PaymentValidate
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import java.lang.Exception

class PaymentServiceTest {
    private lateinit var paymentRepo: PaymentRepo
    private lateinit var orderRepo: OrderRepo
    private lateinit var ccPayment: CreditCardPayment
    private lateinit var pstockRepo: StockRepo
    private lateinit var cartRepo: CartRepo
    private lateinit var paymentService: PaymentService
    private lateinit var paymentValidate: PaymentValidate

    private lateinit var input: ChargeInput
    private lateinit var inputRefund: RefundInput
    private lateinit var tx: Transaction
    private lateinit var txRefund: Transaction
    private lateinit var orderForCharge: Order
    private lateinit var orderForRefund: Order
    private lateinit var expOrder: Order
    private lateinit var expOrderRefund: Order
    private lateinit var expCart: Cart

    private var cart: Cart = Cart()
    private var stock: ProductStock = ProductStock()
    private var pstocks: MutableList<ProductStock?> = mutableListOf()

    @Before
    fun setup() {
        orderRepo = mock(OrderRepo::class.java)
        paymentRepo = mock(PaymentRepo::class.java)
        ccPayment = mock(CreditCardPayment::class.java)
        pstockRepo = mock(StockRepo::class.java)
        cartRepo = mock(CartRepo::class.java)
        paymentValidate = mock(PaymentValidate::class.java)

        paymentService = PaymentService(
                orderRepo,
                ccPayment,
                pstockRepo,
                cartRepo,
                paymentRepo,
                paymentValidate)

        val id = IdGen.NewId()
        val now = Clock.NowUTC()

        cart = Cart().apply {
            this.products = mutableListOf(
                    ProductQTY("111", "Apple",100,1),
                    ProductQTY("222", "Apple",200,1))

            this.userId = "u1"
        }

        input = ChargeInput(
                "u1",
                cart,
                CreditCard(
                        CreditCardType.CreditCardMasterCard,
                        "chanothai",
                        "0000000000000",
                        "123",
                        "01/21"
                )
        )

        inputRefund = RefundInput(
                "u1",
                id
        )

        expCart = cart.newCart(input.userID)
        expCart.addPQTY(newProductQTY("111", "Apple",100, 1),
                stock.newProductStock("111", "Apple",50)!!)
        expCart.addPQTY(newProductQTY("222", "Apple",200, 1),
                stock.newProductStock("222", "Apple",50)!!)

        pstocks = mutableListOf(
                ProductStock("111", "Apple",50),
                ProductStock("222", "Apple",50)
        )

        orderForCharge = Order(
                id,
                input.userID,
                input.cart.products,
                300,
                now,
                State.OrderStatus.PENDING
        )

        orderForRefund = Order(
                id,
                input.userID,
                mutableListOf(
                        ProductQTY("111", "Apple",100,1),
                        ProductQTY("222", "Apple",200,1)),
                300,
                now,
                State.OrderStatus.PAID)


        expOrder = Order(
                id,
                input.userID,
                input.cart.products,
                300,
                now,
                State.OrderStatus.PAID
        )

        expOrderRefund = Order(
                id,
                input.userID,
                mutableListOf(
                        ProductQTY("111", "Apple",100,1),
                        ProductQTY("222", "Apple",200,1)),
                300,
                now,
                State.OrderStatus.REFUNDED)

        tx = Transaction(
                "tx1",
                id,
                TransactionState.CHARGE,
                300,
                now
        )

        txRefund = Transaction(
                "tx1",
                id,
                TransactionState.REFUNDED,
                300,
                now)

        IdGen.setFreezeID(id)
        Clock.setFreezeTimes(now)
    }

    @Test
    fun `payment success`() {
        doNothing().`when`(paymentValidate).inputCharge(input)
        doNothing().`when`(cartRepo).delete(input.userID)
        `when`(pstockRepo.getByIDs(expCart.productIDs())).thenReturn(pstocks)
        `when`(ccPayment.charge(orderForCharge, input.creditCard)).thenReturn(tx)
        doNothing().`when`(paymentRepo).savePayment(expOrder, tx, pstocks)

        val order = paymentService.charge(input)

        Assert.assertEquals(expOrder, order)

        verify(paymentValidate).inputCharge(input)
        verify(cartRepo).delete(input.userID)
        verify(pstockRepo).getByIDs(expCart.productIDs())
        verify(ccPayment).charge(expOrder, input.creditCard)
        verify(paymentRepo).savePayment(expOrder, tx, pstocks)
    }

    @Test(expected = InvalidInputException::class)
    fun `payment then validate failed`() {
        `when`(paymentValidate.inputCharge(input)).thenThrow(Errors.InvalidInput)

        paymentService.charge(input)
    }

    @Test(expected = Exception::class)
    fun `payment get product stock but out of stock`() {
        val pstocks:MutableList<ProductStock?> = mutableListOf(
                ProductStock("111", "Apple",0),
                ProductStock("222", "Apple",50)
        )

        doNothing().`when`(paymentValidate).inputCharge(input)
        `when`(cartRepo.getByUserID(input.userID)).thenReturn(expCart)
        `when`(pstockRepo.getByIDs(expCart.productIDs())).thenReturn(pstocks)

        paymentService.charge(input)
    }

    @Test(expected = Exception::class)
    fun `payment but credit card failed`() {
        doNothing().`when`(paymentValidate).inputCharge(input)
        `when`(cartRepo.getByUserID(input.userID)).thenReturn(expCart)
        `when`(pstockRepo.getByIDs(expCart.productIDs())).thenReturn(pstocks)
        `when`(ccPayment.charge(orderForCharge, input.creditCard)).thenThrow(Errors.UnableChargeCreditCard)

        paymentService.charge(input)
    }

    @Test(expected = Exception::class)
    fun `payment but save failed`(){
        doNothing().`when`(paymentValidate).inputCharge(input)
        `when`(cartRepo.getByUserID(input.userID)).thenReturn(expCart)
        `when`(pstockRepo.getByIDs(expCart.productIDs())).thenReturn(pstocks)
        `when`(ccPayment.charge(orderForCharge, input.creditCard)).thenReturn(tx)
        `when`(paymentRepo.savePayment(expOrder, tx, pstocks)).thenThrow(Errors.UnableSavePayment)

        paymentService.charge(input)
    }

    @Test
    fun `refund success`() {
        doNothing().`when`(paymentValidate).inputRefund(inputRefund)
        `when`(orderRepo.get(inputRefund.orderID)).thenReturn(expOrder)
        `when`(pstockRepo.getByIDs(orderForRefund.productIDs())).thenReturn(pstocks)
        `when`(ccPayment.refund(expOrder)).thenReturn(txRefund)
        doNothing().`when`(paymentRepo).savePayment(expOrderRefund, txRefund, pstocks)

        val order = paymentService.refund(inputRefund)

        Assert.assertEquals(expOrderRefund, order)

        verify(paymentValidate).inputRefund(inputRefund)
        verify(orderRepo).get(inputRefund.orderID)
        verify(pstockRepo).getByIDs(orderForRefund.productIDs())
        verify(ccPayment).refund(expOrderRefund)
        verify(paymentRepo).savePayment(expOrderRefund, txRefund, pstocks)
    }

    @Test(expected = InvalidInputException::class)
    fun `refund validate failed`() {
        `when`(paymentValidate.inputRefund(inputRefund)).thenThrow(Errors.InvalidInput)

        paymentService.refund(inputRefund)
    }

    @Test(expected = InternalError::class)
    fun `refund get order failed`() {
        doNothing().`when`(paymentValidate).inputRefund(inputRefund)
        `when`(orderRepo.get(inputRefund.orderID)).thenThrow(Errors.UnableGetOrder)

        paymentService.refund(inputRefund)
    }

    @Test(expected = BadRequestException::class)
    fun `order owner mismatch`() {
        val now = Clock.NowUTC()
        val input = RefundInput(
                "u1",
                "xxxx"
        )

        val orderForRefund = Order(
                "xxxx",
                "u2",
                mutableListOf(
                        ProductQTY("111", "Apple",100,1),
                        ProductQTY("222", "Apple",200,2)),
                300,
                now,
                State.OrderStatus.PAID)

        doNothing().`when`(paymentValidate).inputRefund(input)
        `when`(orderRepo.get(inputRefund.orderID)).thenReturn(orderForRefund)

        paymentService.refund(inputRefund)
    }
}