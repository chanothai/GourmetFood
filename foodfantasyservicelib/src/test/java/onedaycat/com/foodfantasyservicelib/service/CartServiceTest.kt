package onedaycat.com.foodfantasyservicelib.service

import onedaycat.com.foodfantasyservicelib.entity.*
import onedaycat.com.foodfantasyservicelib.contract.repository.CartRepo
import onedaycat.com.foodfantasyservicelib.contract.repository.StockRepo
import onedaycat.com.foodfantasyservicelib.error.*
import onedaycat.com.foodfantasyservicelib.input.AddToCartInput
import onedaycat.com.foodfantasyservicelib.input.GetCartInput
import onedaycat.com.foodfantasyservicelib.input.RemoveFromCartInput
import onedaycat.com.foodfantasyservicelib.validate.CartValidateAdapter
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*

class CartServiceTest {
    private lateinit var cartService: CartService
    private lateinit var cartValidateAdapter: CartValidateAdapter
    private lateinit var cartRepo: CartRepo
    private lateinit var stockRepo: StockRepo

    private lateinit var input: AddToCartInput
    private lateinit var inputRemove: RemoveFromCartInput
    private lateinit var inputCart: GetCartInput

    private lateinit var expCart: Cart
    private lateinit var stock:ProductStock
    private lateinit var stockWithPrice: ProductStockWithPrice

    @Before
    fun setup() {
        cartValidateAdapter = mock(CartValidateAdapter::class.java)
        cartRepo = mock(CartRepo::class.java)
        stockRepo = mock(StockRepo::class.java)
        cartService = CartService(stockRepo, cartRepo, cartValidateAdapter)

        stock = ProductStock().createProductStock("111", "Apple",50)!!
        stockWithPrice = ProductStockWithPrice(
                stock,
                100
        )

        input = AddToCartInput(
                "u1",
                "111",
                "Apple",
                10
        )

        inputRemove = RemoveFromCartInput(
                "u1",
                "111",
                5
        )

        inputCart = GetCartInput(
                "u1"
        )

        expCart = Cart(
                userId = input.userID,
                products = mutableListOf(
                        ProductQTY("111", "Apple",100, 10)
                )
        )
    }

    @Test
    fun `Add to cart success`() {
        val newCart = Cart(input.userID,mutableListOf())
        newCart.addPQTY(newProductQTY(input.productID, "Apple",100,10), stock)

        doNothing().`when`(cartValidateAdapter).inputCart(input)
        `when`(cartRepo.getByUserID(input.userID)).thenReturn(newCart)
        `when`(stockRepo.getWithPrice(input.productID)).thenReturn(stockWithPrice)
        doNothing().`when`(cartRepo).upsert(expCart)

        val actualCart = cartService.addProductCart(input)

        Assert.assertEquals(expCart, actualCart)

        verify(cartValidateAdapter).inputCart(input)
        verify(cartRepo).getByUserID(input.userID)
        verify(stockRepo).getWithPrice(input.productID)
        verify(cartRepo).upsert(expCart)
    }

    @Test(expected = InvalidInputException::class)
    fun `Add cart but validate failed`() {
        `when`(cartValidateAdapter.inputCart(input)).thenThrow(Errors.InvalidInput)

        cartService.addProductCart(input)
    }

    @Test(expected = InternalError::class)
    fun `Add cart but get cart failed`() {
        doNothing().`when`(cartValidateAdapter).inputCart(input)
        `when`(cartRepo.getByUserID(input.userID)).thenThrow(Errors.UnableGetCart)

        cartService.addProductCart(input)
    }

    @Test(expected = InternalError::class)
    fun `get stock failed`() {
        val newCart = Cart(input.userID,mutableListOf())
        newCart.addPQTY(newProductQTY(input.productID, "Apple",100, 5), stock)

        doNothing().`when`(cartValidateAdapter).inputCart(input)
        `when`(cartRepo.getByUserID(input.userID)).thenReturn(newCart)
        `when`(stockRepo.getWithPrice(input.productID)).thenThrow(Errors.UnableGetProductStock)

        cartService.addProductCart(input)
    }

    @Test(expected = InternalError::class)
    fun `Save cart failed`() {
        val newCart = Cart(input.userID,mutableListOf())
        newCart.addPQTY(newProductQTY(input.productID, "Apple",100, 5), stock)

        doNothing().`when`(cartValidateAdapter).inputCart(input)
        `when`(cartRepo.getByUserID(input.userID)).thenReturn(newCart)
        `when`(stockRepo.getWithPrice(input.productID)).thenReturn(stockWithPrice)
        `when`(cartRepo.upsert(expCart)).thenThrow(Errors.UnableSaveCart)

        cartService.addProductCart(input)
    }


    @Test
    fun `remove cart success`() {
        val newCart = Cart(inputRemove.userID,mutableListOf())
        newCart.addPQTY(newProductQTY("111", "Apple",100, 5), stock)
        newCart.addPQTY(
                newProductQTY("222", "Apple",100, 5),
                stock.newProductStock("222", "Apple",50)!!)

        val expCart = Cart(inputRemove.userID,mutableListOf())
        expCart.addPQTY(newProductQTY(
                "222", "Apple",100, 5),
                stock.newProductStock("222", "Apple",50)!!)

        doNothing().`when`(cartValidateAdapter).inputRemoveCart(inputRemove)
        `when`(cartRepo.getByUserID(inputRemove.userID)).thenReturn(newCart)
        `when`(stockRepo.getWithPrice(inputRemove.productID)).thenReturn(stockWithPrice)
        doNothing().`when`(cartRepo).upsert(expCart)

        val actualCart = cartService.removeFromeCart(inputRemove)

        Assert.assertEquals(expCart, actualCart)

        verify(cartValidateAdapter).inputRemoveCart(inputRemove)
        verify(cartRepo).getByUserID(inputRemove.userID)
        verify(stockRepo).getWithPrice(inputRemove.productID)
        verify(cartRepo).upsert(expCart)
    }

    @Test(expected = InvalidInputException::class)
    fun `remove cart but validate failed`() {
        `when`(cartValidateAdapter.inputRemoveCart(inputRemove)).thenThrow(Errors.InvalidInput)

        cartService.removeFromeCart(inputRemove)
    }

    @Test(expected = InternalError::class)
    fun `remove cart but get cart failed`() {
        doNothing().`when`(cartValidateAdapter).inputRemoveCart(inputRemove)
        `when`(cartRepo.getByUserID(inputRemove.userID)).thenThrow(Errors.UnableGetCart)

        cartService.removeFromeCart(inputRemove)
    }

    @Test(expected = InternalError::class)
    fun `remove cart but get stock failed`() {
        doNothing().`when`(cartValidateAdapter).inputRemoveCart(inputRemove)
        `when`(cartRepo.getByUserID(inputRemove.userID)).thenReturn(expCart)
        `when`(stockRepo.getWithPrice(inputRemove.productID)).thenThrow(Errors.UnableGetProductStock)

        cartService.removeFromeCart(inputRemove)
    }

    @Test(expected = NotFoundException::class)
    fun `remove cart failed`() {
        val newCart = Cart(inputRemove.userID,mutableListOf())

        doNothing().`when`(cartValidateAdapter).inputRemoveCart(inputRemove)
        `when`(cartRepo.getByUserID(inputRemove.userID)).thenReturn(newCart)
        `when`(stockRepo.getWithPrice(inputRemove.productID)).thenReturn(stockWithPrice)

        cartService.removeFromeCart(inputRemove)
    }

    @Test(expected = InternalError::class)
    fun `remove cart but save or upadte failed`() {
        val newCart = Cart(inputRemove.userID, mutableListOf())
        newCart.addPQTY(newProductQTY("111", "Apple",100, 5), stock)
        newCart.addPQTY(newProductQTY("222", "Apple",100, 5),
                stock.newProductStock("222", "Apple",50)!!)

        expCart = Cart(inputRemove.userID, mutableListOf())
        expCart.addPQTY(newProductQTY("222", "Apple",100, 5),
                stock.newProductStock("222", "Apple",50)!!)

        doNothing().`when`(cartValidateAdapter).inputRemoveCart(inputRemove)
        `when`(cartRepo.getByUserID(inputRemove.userID)).thenReturn(newCart)
        `when`(stockRepo.getWithPrice(inputRemove.productID)).thenReturn(stockWithPrice)
        `when`(cartRepo.upsert(expCart)).thenThrow(Errors.UnableSaveCart)

        cartService.removeFromeCart(inputRemove)
    }


    @Test
    fun `get cart success`() {
        expCart = Cart(inputRemove.userID, mutableListOf())
        expCart.addPQTY(newProductQTY("111", "Apple",100,5), stock)

        doNothing().`when`(cartValidateAdapter).inputGetCart(inputCart)
        `when`(cartRepo.getByUserID(input.userID)).thenReturn(expCart)

        val actualCart = cartService.getCartWithUserID(inputCart)

        Assert.assertEquals(expCart, actualCart)

        verify(cartValidateAdapter).inputGetCart(inputCart)
        verify(cartRepo).getByUserID(input.userID)
    }

    @Test(expected = InvalidInputException::class)
    fun `get cart but validate failed`() {
        `when`(cartValidateAdapter.inputGetCart(inputCart)).thenThrow(Errors.InvalidInput)

        cartService.getCartWithUserID(inputCart)
    }

    @Test(expected = InternalError::class)
    fun `get cart failed`() {
        doNothing().`when`(cartValidateAdapter).inputGetCart(inputCart)
        `when`(cartRepo.getByUserID(input.userID)).thenThrow(Errors.UnableGetCart)

        cartService.getCartWithUserID(inputCart)
    }
}