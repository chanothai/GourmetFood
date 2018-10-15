package onedaycat.com.food.fantasy.ui.mainfood

import android.arch.core.executor.testing.InstantTaskExecutorRule
import kotlinx.coroutines.experimental.runBlocking
import onedaycat.com.food.fantasy.store.CartStore
import onedaycat.com.food.fantasy.store.FoodCartLiveStores
import onedaycat.com.food.fantasy.store.FoodCartStore
import onedaycat.com.foodfantasyservicelib.contract.creditcard_payment.CreditCard
import onedaycat.com.foodfantasyservicelib.contract.repository.ProductPaging
import onedaycat.com.foodfantasyservicelib.entity.Cart
import onedaycat.com.foodfantasyservicelib.entity.Order
import onedaycat.com.foodfantasyservicelib.entity.Product
import onedaycat.com.foodfantasyservicelib.entity.ProductQTY
import onedaycat.com.foodfantasyservicelib.entity.State
import onedaycat.com.foodfantasyservicelib.error.Errors
import onedaycat.com.foodfantasyservicelib.input.*
import onedaycat.com.food.fantasy.service.EcomService
import onedaycat.com.food.fantasy.ui.cart.CartModel
import onedaycat.com.foodfantasyservicelib.service.CartService
import onedaycat.com.foodfantasyservicelib.service.PaymentService
import onedaycat.com.foodfantasyservicelib.service.ProductService
import onedaycat.com.foodfantasyservicelib.util.clock.Clock
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.mockito.Mockito.*

class FoodViewModelTest {
    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    private lateinit var foodCartLiveStore: FoodCartLiveStores
    private lateinit var eComService: EcomService

    private lateinit var cartService: CartService
    private lateinit var productService: ProductService
    private lateinit var paymentService: PaymentService

    private lateinit var foodViewModel: FoodViewModel
    private lateinit var productPaging: ProductPaging

    private lateinit var cartStore: CartStore
    private lateinit var cart: Cart
    private lateinit var cartModel: onedaycat.com.food.fantasy.ui.cart.CartModel

    @Before
    fun setup() {
        cart = Cart(
                "u1",
                arrayListOf(ProductQTY(
                        "p1",
                        "Pork Potato",
                        500,
                        10
                ))
        )

        cartModel = CartModel(
                "1111",
                "Pork Potato",
                250,
                2500,
                10,
                true
        )

        with(CartStore) {
            this.foodCart = FoodCartStore(
                    "u1",
                    arrayListOf(
                            cartModel
                    )
            )

            this.counter = 0

            cartStore = this
            foodCartLiveStore = FoodCartLiveStores
        }

        eComService = mock(EcomService::class.java)
        productService = mock(ProductService::class.java)
        cartService = mock(CartService::class.java)
        paymentService = mock(PaymentService::class.java)

        `when`(eComService.productService).thenReturn(productService)
        `when`(eComService.cartService).thenReturn(cartService)
        `when`(eComService.paymentService).thenReturn(paymentService)

        foodViewModel = FoodViewModel(eComService)
    }

    @Test
    fun loadProductSuccess() {
        val input = GetProductsInput().apply {
            this.limit = 1
        }

        val product = Product().apply {
            this.id = "1111"
            this.name = "p1"
            this.price = 300
            this.desc = "Test describe"
            this.image = "img1"
        }.also {
            productPaging = ProductPaging(
                    arrayListOf(it)
            )
        }

        FoodListModel(
                arrayListOf(FoodModel().apply {
                    this.foodId = product.id!!
                    this.foodName = product.name!!
                    this.foodPrice = product.price!!
                    this.foodDesc = product.desc!!
                    this.foodIMG = product.image!!
                })
        ).also { expected->

            `when`(eComService.productService.getProducts(input)).thenReturn(productPaging)

            runBlocking {
                foodViewModel.loadProducts(input)
            }

            val result = foodViewModel.foodData.value

            Assert.assertEquals(expected, result)

            verify(eComService.productService).getProducts(input)
        }
    }

    @Test
    fun loadProductFailed() {
        val input = GetProductsInput().apply {
            this.limit = 1
        }

        val msgExpect = Errors.ProductNotFound.message

        `when`(eComService.productService.getProducts(input)).thenThrow(Errors.ProductNotFound)

        runBlocking {
            foodViewModel.loadProducts(input)
        }

        val result = foodViewModel.msgError.value

        Assert.assertEquals(msgExpect, result)
    }

    @Test
    fun loadCartSuccess() {
        val input = GetCartInput(
                "u1"
        )

        `when`(eComService.cartService.getCartWithUserID(input)).thenReturn(cart)

        runBlocking {
            foodViewModel.loadCart(input)
        }

        val result = foodViewModel.cartStore.value?.foodCart

        Assert.assertEquals(cartStore.foodCart, result)

        verify(eComService.cartService).getCartWithUserID(input)
    }

    @Test
    fun loadCartFailed() {
        val input = GetCartInput(
                "u1"
        )
        `when`(eComService.cartService.getCartWithUserID(input)).thenThrow(Errors.CartNotFound)

        runBlocking {
            foodViewModel.loadCart(input)
        }

        val exp = Errors.CartNotFound.message
        val resultError = foodViewModel.msgError.value

        Assert.assertEquals(exp, resultError)
    }

    @Test
    fun paymentSuccess() {
        val input = ChargeInput(
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

        val exp = Order(
                "r1",
                "u1",
                arrayListOf(ProductQTY(
                        "p1",
                        "Pork Potato",
                        500,
                        10
                )),
                5000,
                Clock.NowUTC(),
                State.OrderStatus.PAID
        )

        `when`(eComService.paymentService.charge(input)).thenReturn(exp)

        runBlocking {
            foodViewModel.payment(input)
        }

        val result = foodViewModel.pay.value

        Assert.assertEquals(exp, result)

        verify(eComService.paymentService).charge(input)
    }

    @Test
    fun paymentFailed() {
        val input = ChargeInput(
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

        `when`(eComService.paymentService.charge(input)).thenThrow(Errors.UnableSavePayment)

        runBlocking {
            foodViewModel.payment(input)
        }

        val exp = Errors.UnableSavePayment.message
        val result = foodViewModel.msgError.value

        Assert.assertEquals(exp, result)
    }

    @Test
    fun addAllProductCartSuccess() {
        val input = AddCartsToCartInput(
                cart
        )

        `when`(eComService.cartService.addProductCarts(input)).thenReturn(cart)

        runBlocking {
            foodViewModel.addAllProductCart(input)
        }

        val result = foodViewModel.cartLiveData.value

        Assert.assertEquals(cart, result)

        verify(eComService.cartService).addProductCarts(input)
    }

    @Test
    fun addAllProductCartFailed() {
        val input = AddCartsToCartInput(
                cart
        )

        `when`(eComService.cartService.addProductCarts(input)).thenThrow(Errors.UnableSaveCart)

        runBlocking {
            foodViewModel.addAllProductCart(input)
        }

        val exp = Errors.UnableSaveCart.message
        val result = foodViewModel.msgError.value

        Assert.assertEquals(exp, result)
    }

    @Test
    fun addProductToCartSuccess() {
        val input = AddToCartInput(
                "u1",
                "1111",
                "Pork Potato",
                100,
                10
        )

        `when`(eComService.cartService.addProductCart(input)).thenReturn(cart)

        runBlocking { foodViewModel.addProductToCart(input) }

        val result = foodViewModel.cartStore.value?.foodCart
        Assert.assertEquals(CartStore.foodCart, result)

        verify(eComService.cartService).addProductCart(input)
    }

    @Test
    fun addProductToCartFailed() {
        val input = AddToCartInput(
                "u1",
                "1111",
                "Pork Potato",
                100,
                10)

        `when`(eComService.cartService.addProductCart(input)).thenThrow(Errors.UnableGetCart)

        runBlocking { foodViewModel.addProductToCart(input) }

        val exp = Errors.UnableGetCart.message
        val result = foodViewModel.msgError.value

        Assert.assertEquals(exp, result)
    }

    @Test
    fun deleteCartTest() {
        CartStore.foodCart?.cartList = arrayListOf()
        CartStore.counter = 0

        foodViewModel.deleteCart()

        val exp = CartStore.foodCart
        val result = foodViewModel.cartStore.value?.foodCart

        Assert.assertEquals(exp, result)
    }

    @Test
    fun initTotalPriceTest() {
        val foodModel = FoodModel().apply {
            this.foodId = "1111"
            this.foodName = "Pork Potato"
            this.foodPrice = 340
            this.foodDesc = "Delicious"
            this.foodIMG = "IMG1"
            this.isAddToCart = true
        }.also {
            foodViewModel.initTotalPrice(it)
        }

        val exp = FoodSumModel().apply {
            this.qty = 10
            this.price = foodModel.foodPrice
            this.totalPrice = price * qty
        }

        val result = foodViewModel.foodSumModel.value

        Assert.assertEquals(exp, result)
    }

    @Test
    fun foodDetailSumTotalPriceTestWhenFoodInCart() {
        val foodModel = FoodModel().apply {
            this.foodId = "1111"
            this.foodName = "Pork Potato"
            this.foodPrice = 340
            this.foodDesc = "Delicious"
            this.foodIMG = "IMG1"
            this.isAddToCart = true
        }.also {
            foodViewModel.initTotalPrice(it)
        }

        foodViewModel.foodDetailSumTotalPrice(true)

        val exp = FoodSumModel().apply {
            this.qty = 11
            this.price = foodModel.foodPrice
            this.totalPrice = price * qty
        }

        val result = foodViewModel.foodSumModel.value

        Assert.assertEquals(exp, result)

    }

    @Test
    fun foodDetailSumTotalPriceTestWhenNoFoodInCart() {
        val foodModel = FoodModel().apply {
            this.foodId = "1111"
            this.foodName = "Pork Potato"
            this.foodPrice = 340
            this.foodDesc = "Delicious"
            this.foodIMG = "IMG1"
            this.isAddToCart = true
        }.also {
            foodViewModel.initTotalPrice(it)
        }

        foodViewModel.foodDetailSumTotalPrice(false)

        val exp = FoodSumModel().apply {
            this.qty = 9
            this.price = foodModel.foodPrice
            this.totalPrice = price * qty
        }

        val result = foodViewModel.foodSumModel.value

        Assert.assertEquals(exp, result)

    }

    @Test
    fun cartSumTotalPriceTest() {
        foodViewModel.cartSumTotalPrice()

        val exp = CartStore.foodCart?.cartList?.get(0)?.cartTotalPrice
        val result = foodViewModel.totalPrice.value

        Assert.assertEquals(exp, result)

    }

    @Test
    fun updateCartItemTestWhenHasFood() {
        foodViewModel.updateCartItem(cartModel)

        val exp = CartStore.foodCart
        val result = foodViewModel.cartStore.value?.foodCart

        Assert.assertEquals(exp, result)

    }

    @Test
    fun updateCartItemTestWhenNoFood() {
        cartModel.hasFood = false
        foodViewModel.updateCartItem(cartModel)

        val exp = CartStore.foodCart
        val result = foodViewModel.cartStore.value?.foodCart

        Assert.assertEquals(exp, result)
    }

    @Test
    fun remainingLoginTest() {
        foodViewModel.deleteCart()
        val resultPay = foodViewModel.pay.value

        Assert.assertEquals(null, resultPay)

        val expMsgError = "Error Message"
        foodViewModel.createErrorMessage(expMsgError)
        val resultMsgError = foodViewModel.msgError.value

        Assert.assertEquals(expMsgError, resultMsgError)

        val ccNumber = CreditCard(
                CreditCardType.CreditCardMasterCard,
                "","0000000000000", "", ""
        )

        val resultCartNumber = foodViewModel.createCreditCart("0000000000000", 0)
        Assert.assertEquals(ccNumber, resultCartNumber)

        val ccName = CreditCard(
                CreditCardType.CreditCardMasterCard,
                "chanothai","0000000000000", "", ""
        )

        val resultName = foodViewModel.createCreditCart("chanothai", 1)
        Assert.assertEquals(ccName, resultName)

        val ccExpireDate = CreditCard(
                CreditCardType.CreditCardMasterCard,
                "chanothai","0000000000000", "", "01/21"
        )
        val resultCreditCard = foodViewModel.createCreditCart("01/21", 2)
        Assert.assertEquals(ccExpireDate, resultCreditCard)


        val ccCVV = CreditCard(
                CreditCardType.CreditCardMasterCard,
                "chanothai","0000000000000", "123", "01/21"
        )

        val resultCVV = foodViewModel.createCreditCart("123", 3)
        Assert.assertEquals(ccCVV, resultCVV)
    }
}