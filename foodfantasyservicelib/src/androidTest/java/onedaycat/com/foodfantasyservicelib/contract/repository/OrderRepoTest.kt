package onedaycat.com.foodfantasyservicelib.contract.repository

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool
import com.amazonaws.regions.Regions
import onedaycat.com.food.fantasy.oauth.OauthCognito
import onedaycat.com.foodfantasyservicelib.entity.Order
import onedaycat.com.foodfantasyservicelib.entity.State
import onedaycat.com.foodfantasyservicelib.entity.newProductQTY
import onedaycat.com.foodfantasyservicelib.error.BadRequestException
import onedaycat.com.foodfantasyservicelib.error.InternalError
import onedaycat.com.foodfantasyservicelib.error.NotFoundException
import onedaycat.com.foodfantasyservicelib.util.clock.Clock
import onedaycat.com.foodfantasyservicelib.util.cognito.CognitoFoodFantasyServiceLib
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class OrderRepoTest {
    private lateinit var orderRepo: OrderRepo
    private lateinit var expOrder: Order

    @Before
    fun setup(){
        CognitoFoodFantasyServiceLib.cognitoUserPool = CognitoUserPool(
                InstrumentationRegistry.getContext(),
                "ap-southeast-1_M7jwwdfSy",
                "6qualkk2qcjqokgsujkgm5c2sq",
                null,
                Regions.AP_SOUTHEAST_1)

        orderRepo = OrderFireStore(OauthCognito())

        val now = Clock.NowUTC()
        expOrder = Order(
                "fc944bc0-8501-4b02-aeb5-331028108f8f",
                "u1",
                mutableListOf(
                        newProductQTY("111", "Apple",100, 10),
                        newProductQTY("222", "Apple",200, 20)
                ),
                300,
                now,
                State.OrderStatus.PAID
        )
    }

    @Test
    fun upsertOrder() {
        orderRepo.upsert(expOrder)
    }

    @Test
    fun getOrder() {
        val order = orderRepo.get(expOrder.userId!!)

        Assert.assertEquals(expOrder.userId, order.userId)
    }

    @Test(expected = InternalError::class)
    fun getOrderFailed() {
        val id = "33333"
        orderRepo.get(id)
    }
}