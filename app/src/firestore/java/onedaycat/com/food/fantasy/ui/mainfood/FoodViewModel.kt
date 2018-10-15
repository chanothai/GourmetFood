package onedaycat.com.food.fantasy.ui.mainfood

import android.arch.lifecycle.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import onedaycat.com.food.fantasy.store.CartStore
import onedaycat.com.food.fantasy.store.FoodCartLiveStores
import onedaycat.com.foodfantasyservicelib.contract.creditcard_payment.CreditCard
import onedaycat.com.foodfantasyservicelib.contract.repository.ProductPaging
import onedaycat.com.foodfantasyservicelib.entity.Cart
import onedaycat.com.foodfantasyservicelib.entity.Order
import onedaycat.com.foodfantasyservicelib.error.Error
import onedaycat.com.foodfantasyservicelib.input.*
import onedaycat.com.food.fantasy.service.EcomService
import onedaycat.com.food.fantasy.ui.cart.CartModel

class FoodViewModel(private val eComService: EcomService
) : ViewModel() {
    private var foodList = arrayListOf<FoodModel>()

    private var _totalPrice = MutableLiveData<Int>()
    val totalPrice: LiveData<Int>
        get() = _totalPrice

    val cartStore: LiveData<CartStore> = FoodCartLiveStores.liveData

    private val _foodData = MutableLiveData<FoodListModel>()
    val foodData: LiveData<FoodListModel>
        get() = _foodData

    private val _pay = MutableLiveData<Order>()
    val pay: LiveData<Order>
        get() = _pay

    private val _msgError = MutableLiveData<String>()
    val msgError: LiveData<String>
        get() = _msgError

    private val _cartLiveData = MutableLiveData<Cart>()
    val cartLiveData: LiveData<Cart>
        get() = _cartLiveData

    private val _foodSumModel = MutableLiveData<FoodSumModel>()
    val foodSumModel: LiveData<FoodSumModel>
        get() = _foodSumModel

    private val mBottomBarItem = MutableLiveData<Int>()
    val bottomBarLiveData: LiveData<Int>
    get() = mBottomBarItem


    private fun <T> asyncTask(function: () -> T): Deferred<T> {
        return async(CommonPool) { function() }
    }

    suspend fun loadProducts(input: GetProductsInput) {
        try {
            val productPaging = asyncTask { eComService.productService.getProducts(input) }.await()

            productPaging.let { foodPaging ->
                foodList.let { foods ->
                    when {
                        foods.size == 0 -> {
                            val fs = addFoodModel(foodPaging)
                            _foodData.postValue(fs)
                        }
                    }
                }
            }
        }catch (e: onedaycat.com.foodfantasyservicelib.error.Error) {
            _msgError.postValue(e.message)
        }
    }

    private fun addFoodModel(productPaging: ProductPaging): FoodListModel {
        for (product in productPaging.products) {
            val foodModel = FoodModel(
                    foodId = product.id,
                    foodName = product.name,
                    foodDesc = product.desc,
                    foodPrice = product.price,
                    foodIMG = product.image
            )

            foodList.add(foodModel)
        }

        return FoodListModel(foodList)
    }

    private fun addCartToStore(cart:Cart) {
        cart.let {
            val cartStore = cartStore.value?.let { cartStore ->
                var qty = 0
                for (product in it.products) {
                    qty += product.qty
                }

                cartStore.counter = qty

                var totalPrice = 0
                val cartsModel = arrayListOf<CartModel>()
                for (item in it.products) {
                    CartModel().apply {
                        cartPId = item.productId
                        cartName = item.productName
                        cartQTY = item.qty
                        cartPrice = item.price
                        cartTotalPrice = item.price * item.qty

                        totalPrice += cartTotalPrice
                    }.also {cartModel->
                        cartsModel.add(cartModel)
                    }
                }

                _totalPrice.value = totalPrice
                cartStore.foodCart?.cartList = cartsModel
                cartStore
            }

            FoodCartLiveStores.liveData.postValue(cartStore)
        }
    }

    suspend fun loadCart(input: GetCartInput) {
        try {
            val cart = asyncTask { eComService.cartService.getCartWithUserID(input) }.await()
            cart?.let {
                addCartToStore(it)
            }

        }catch (e:onedaycat.com.foodfantasyservicelib.error.Error) {
            _msgError.value = e.message
        }
    }

    suspend fun addAllProductCart(input: AddCartsToCartInput) {
        try {
            val cart = asyncTask { eComService.cartService.addProductCarts(input) }.await()
            cart.let {
                _cartLiveData.value = cart
            }

        }catch (e:onedaycat.com.foodfantasyservicelib.error.Error) {
            _msgError.value = e.message
        }
    }

    suspend fun addProductToCart(input: AddToCartInput) {
        try {
            val cart = asyncTask { eComService.cartService.addProductCart(input) }.await()

            addCartToStore(cart)
        } catch (e: Error) {
            _msgError.value = e.message
        }
    }

    fun deleteCart() {
        cartStore.value?.foodCart?.cartList = arrayListOf()
        cartStore.value?.counter = 0

        FoodCartLiveStores.liveData.value = cartStore.value
    }

    fun initTotalPrice(foodModel: FoodModel) {
        var qty = 1
        cartStore.value?.foodCart?.cartList?.let { cartsModel->
            val index = cartsModel.indexOfFirst {
                it.cartPId == foodModel.foodId
            }

            if (index != -1) {
                qty = cartsModel[index].cartQTY
            }
        }

        _foodSumModel.value = FoodSumModel().apply {
            this.qty = qty
            this.price = foodModel.foodPrice
            this.totalPrice = price * qty
        }
    }

    fun foodDetailSumTotalPrice(isAddItem: Boolean) {
        val sum = _foodSumModel.value?.let {
            if (isAddItem) {
                it.qty += 1
            }else{
                it.qty = minusItem(it.qty)
            }

            it.totalPrice = it.price * it.qty

            it
        }

        _foodSumModel.value = sum
    }

    private fun minusItem(qty: Int):Int {
        val result = qty - 1
        if (result == 0) {
            return 1
        }

        return result
    }

    fun cartSumTotalPrice() {
        FoodCartLiveStores.liveData.value = cartStore.value?.let {cartStore->
            cartStore.foodCart?.cartList?.let {carts->
                var totalPrice = 0
                for (cart in carts) {
                    cart.cartTotalPrice = cart.cartPrice * cart.cartQTY

                    totalPrice += cart.cartTotalPrice
                }

                _totalPrice.value = totalPrice

                carts
            }

            cartStore
        }
    }

    fun updateCartItem(cart: onedaycat.com.food.fantasy.ui.cart.CartModel) {
        FoodCartLiveStores.liveData.value = cartStore.value?.let { cartStore->
            cartStore.foodCart?.cartList?.let {carts->
                val index = carts.indexOfFirst {
                    it.cartPId == cart.cartPId
                }

                if (index != -1) {
                    if (cart.hasFood) {
                        cartStore.counter += 1
                        carts[index].cartTotalPrice = cart.cartTotalPrice + cart.cartPrice

                        totalPrice.value?.let { it->
                            var totalPrice = it
                            totalPrice += cart.cartPrice
                            totalPrice
                        }?.also {
                            _totalPrice.value = it
                        }

                    }else{
                        cartStore.counter -= 1
                        carts[index].cartTotalPrice = cart.cartTotalPrice - cart.cartPrice

                        totalPrice.value?.let { it->
                            var totalPrice = it
                            totalPrice -= cart.cartPrice
                            totalPrice
                        }?.also {
                            _totalPrice.value = it
                        }
                    }
                }

                carts
            }

            cartStore
        }
    }

    suspend fun payment(input: ChargeInput) {
        try {
            var order: Order? = null

            asyncTask { order = eComService.paymentService.charge(input) }.await()

            _pay.value = order

        }catch (e: onedaycat.com.foodfantasyservicelib.error.Error) {
            _msgError.value = e.message
        }
    }

    fun deleteOrder() {
        _pay.value = null
    }

    fun updateCurrentBottomItem(item:Int) {
        mBottomBarItem.value = item
    }

    private var creditCard = CreditCard(
            CreditCardType.CreditCardMasterCard,
            "",
            "",
            "",
            ""
    )

    fun createErrorMessage(msg: String) {
        _msgError.value = msg
    }

    fun createCreditCart(text: String, position: Int): CreditCard {
        return creditCard.apply {
            when(position){
                0 -> cardNumber = text
                1 -> name = text
                2 -> expiredDate = text
                3 -> cvv = text
            }
        }
    }
}