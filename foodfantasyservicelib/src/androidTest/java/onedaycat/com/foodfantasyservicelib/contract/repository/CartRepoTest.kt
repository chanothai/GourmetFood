package onedaycat.com.foodfantasyservicelib.contract.repository

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool
import com.amazonaws.regions.Regions
import onedaycat.com.food.fantasy.oauth.OauthCognito
import onedaycat.com.foodfantasyservicelib.entity.Cart
import onedaycat.com.foodfantasyservicelib.entity.newProductQTY
import onedaycat.com.foodfantasyservicelib.error.NotFoundException
import onedaycat.com.foodfantasyservicelib.util.cognito.CognitoFoodFantasyServiceLib
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CartRepoTest {

    private lateinit var cartRepo: CartRepo
    private lateinit var expCart: Cart

    @Before
    fun setup(){
        CognitoFoodFantasyServiceLib.cognitoUserPool = CognitoUserPool(
                InstrumentationRegistry.getContext(),
                "ap-southeast-1_M7jwwdfSy",
                "6qualkk2qcjqokgsujkgm5c2sq",
                null,
                Regions.AP_SOUTHEAST_1)

        cartRepo = CartFireStore(OauthCognito())
        expCart = Cart(
                "u1",
                mutableListOf(
                        newProductQTY("111", "Apple",100, 2),
                        newProductQTY("112", "Apple",200, 1)
                )
        )
    }

    @Test
    fun addCart() {
        cartRepo.upsert(expCart)
    }

    @Test
    fun getCartSuccess() {
        val cart = cartRepo.getByUserID(expCart.userId!!)
        Assert.assertEquals(expCart, cart)
    }

    @Test(expected = NotFoundException::class)
    fun getCartFailed() {
        val id = "434332"
        cartRepo.getByUserID(id)
    }
}