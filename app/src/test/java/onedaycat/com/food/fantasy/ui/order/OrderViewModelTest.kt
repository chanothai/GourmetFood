package onedaycat.com.food.fantasy.fantasy.ui.order

import android.arch.core.executor.testing.InstantTaskExecutorRule
import kotlinx.coroutines.experimental.runBlocking
import onedaycat.com.foodfantasyservicelib.entity.Order
import onedaycat.com.foodfantasyservicelib.entity.ProductQTY
import onedaycat.com.foodfantasyservicelib.entity.State
import onedaycat.com.foodfantasyservicelib.error.Errors
import onedaycat.com.foodfantasyservicelib.input.GetOrderInput
import onedaycat.com.food.fantasy.service.EcomService
import onedaycat.com.foodfantasyservicelib.util.clock.Clock
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.mockito.Mockito.*

class OrderViewModelTest {
    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    private lateinit var eComService: EcomService

    private lateinit var orderService: OrderService

    private lateinit var orderViewModel: OrderViewModel
    @Before
    fun setup() {
        eComService = mock(EcomService::class.java)
        orderService = mock(OrderService::class.java)

        `when`(eComService.orderService).thenReturn(orderService)

        orderViewModel = OrderViewModel(eComService)
    }

    @Test
    fun loadOrdersSuccess() {
        val input = GetOrderInput(
                "u1"
        )

        val orders = arrayListOf(
                Order(
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
                        State.OrderStatus.PAID))

        val exp = OrdersModel(
                arrayListOf(OrderModel(
                        orders[0].id,
                        orders[0].totalPrice,
                        OrderProductsModel(
                                arrayListOf(OrderProductModel(
                                        "Pork Potato",
                                        5000
                                ))
                        )
                ))
        )

        `when`(eComService.orderService.getOrders(input)).thenReturn(orders)

        runBlocking {
            orderViewModel.loadOrderHistory(input)
        }

        val result = orderViewModel.orders.value

        Assert.assertEquals(exp, result)

        verify(eComService.orderService).getOrders(input)
    }

    @Test
    fun loadOrdersFailed() {
        val input = GetOrderInput(
                "u1"
        )

        `when`(eComService.orderService.getOrders(input)).thenThrow(Errors.UnableGetOrder)

        runBlocking {
            orderViewModel.loadOrderHistory(input)
        }

        val exp = Errors.UnableGetOrder.message
        val result = orderViewModel.msgError.value

        Assert.assertEquals(exp, result)
    }
}