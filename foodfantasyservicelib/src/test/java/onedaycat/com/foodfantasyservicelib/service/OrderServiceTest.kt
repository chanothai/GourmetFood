package onedaycat.com.foodfantasyservicelib.service

import onedaycat.com.foodfantasyservicelib.entity.Order
import onedaycat.com.foodfantasyservicelib.entity.Product
import onedaycat.com.foodfantasyservicelib.entity.newProductQTY
import onedaycat.com.foodfantasyservicelib.error.Errors
import onedaycat.com.foodfantasyservicelib.contract.repository.OrderRepo
import onedaycat.com.foodfantasyservicelib.error.InternalError
import onedaycat.com.foodfantasyservicelib.error.InvalidInputException
import onedaycat.com.foodfantasyservicelib.input.GetOrderInput
import onedaycat.com.foodfantasyservicelib.util.clock.Clock
import onedaycat.com.foodfantasyservicelib.validate.OrderValidate
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*

class OrderServiceTest {
    @Mock
    private lateinit var orderRepo:OrderRepo
    private lateinit var orderValidate: OrderValidate
    private lateinit var orderService: OrderService

    @Mock
    private lateinit var input: GetOrderInput
    private lateinit var products: MutableList<Product>
    private lateinit var expOrder: Order

    @Before
    fun setup() {
        orderRepo = mock(OrderRepo::class.java)
        orderValidate = mock(OrderValidate::class.java)
        orderService = OrderService(orderRepo, orderValidate)

        input = GetOrderInput(
                "o1"
        )

        products = mutableListOf(
                Product(id = "1", price = 1000),
                Product(id = "2", price = 2000)

        )

        expOrder = Order(
                "o1",
                "1111",
                createDate = Clock.NowUTC()
        )
    }

    @Test
    fun `get order success`() {
        expOrder.addProduct(newProductQTY(products[0].id!!, "Apple",100, 1), products[0])
        expOrder.addProduct(newProductQTY(products[1].id!!, "Apple",200, 2), products[1])

        doNothing().`when`(orderValidate).inputGetOrder(input)
        `when`(orderRepo.get(input.id)).thenReturn(expOrder)

        val order = orderService.getOrder(input)

        Assert.assertEquals(expOrder, order)

        verify(orderValidate).inputGetOrder(input)
        verify(orderRepo).get(input.id)
    }

    @Test(expected = InvalidInputException::class)
    fun `validate failed`() {
        `when`(orderValidate.inputGetOrder(input)).thenThrow(Errors.InvalidInput)

        orderService.getOrder(input)
    }

    @Test(expected = InternalError::class)
    fun `get order failed`() {
        doNothing().`when`(orderValidate).inputGetOrder(input)
        `when`(orderRepo.get(input.id)).thenThrow(Errors.UnableGetOrder)

        orderService.getOrder(input)
    }
}