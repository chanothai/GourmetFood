package onedaycat.com.food.fantasy

import android.arch.lifecycle.MutableLiveData
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.*
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.contrib.RecyclerViewActions
import android.support.test.espresso.matcher.ViewMatchers
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.filters.LargeTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.support.v7.widget.RecyclerView
import onedaycat.com.food.fantasy.ui.mainfood.FoodListModel
import onedaycat.com.food.fantasy.ui.mainfood.FoodModel
import onedaycat.com.food.fantasy.store.CartStore
import onedaycat.com.food.fantasy.store.FoodCartStore
import onedaycat.com.food.fantasy.ui.mainfood.FoodViewModel
import onedaycat.com.food.fantasy.ui.mainfood.activity.MainActivity
import onedaycat.com.food.fantasy.ui.order.*
import onedaycat.com.food.fantasy.ui.order.fragment.OrderFragment
import onedaycat.com.food.fantasy.util.ViewModelUtil
import onedaycat.com.foodfantasyservicelib.entity.Cart
import onedaycat.com.foodfantasyservicelib.entity.Order
import org.hamcrest.Matchers.not
import org.junit.*
import org.junit.runner.RunWith
import org.mockito.Mockito.*

@RunWith(AndroidJUnit4::class)
@LargeTest
class FoodUITest {
    @get:Rule
    var mActivityRule: ActivityTestRule<MainActivity> = ActivityTestRule(MainActivity::class.java, false, false)

    private val itemElementText = "Pork Tenderloin with Mushrooms and Onions"

    private lateinit var foodViewModel: FoodViewModel
    private lateinit var orderViewModel: OrderViewModel

    private val foodData = MutableLiveData<FoodListModel>()
    private val cartStore = MutableLiveData<CartStore>()
    private val cartLiveData = MutableLiveData<Cart>()
    private val orderObserver = MutableLiveData<OrdersModel>()
    private val foodTotalPrice = MutableLiveData<Int>()
    private val foodPay = MutableLiveData<Order>()
    private val msgError = MutableLiveData<String>()

    @Before
    fun registerIdlingResource() {
        foodViewModel = mock(FoodViewModel::class.java)
        orderViewModel = mock(OrderViewModel::class.java)

        ViewModelUtil.mockViewModel = ViewModelUtil.createViewModelFor(foodViewModel)

        `when`(foodViewModel.foodData).thenReturn(foodData)
        `when`(foodViewModel.cartStore).thenReturn(cartStore)
        `when`(foodViewModel.totalPrice).thenReturn(foodTotalPrice)
        `when`(foodViewModel.pay).thenReturn(foodPay)
        `when`(foodViewModel.msgError).thenReturn(msgError)
        `when`(foodViewModel.cartLiveData).thenReturn(cartLiveData)

        `when`(orderViewModel.orders).thenReturn(orderObserver)

        mActivityRule.launchActivity(null)
    }

    @Test
    fun scrollToItemFood_checkItemTextName_MainActivity() {
        val foodListModel = FoodListModel(
                arrayListOf(FoodModel(
                        "1111",
                        itemElementText,
                        "Delicious",
                        350,
                        "IMG1",
                        false
                ))
        )

        foodData.postValue(foodListModel)

        onView(ViewMatchers.withId(R.id.rv_with_refresh))
                .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(0))

        onView(withText(itemElementText)).check(matches(isDisplayed()))
    }

    @Test
    fun clickedItemFood_checkName_foodDetailActivity() {
        val foodListModel = FoodListModel(
                arrayListOf(FoodModel(
                        "1111",
                        itemElementText,
                        "Delicious",
                        350,
                        "IMG1",
                        false
                ))
        )

        foodData.postValue(foodListModel)

        onView(ViewMatchers.withId(R.id.rv_with_refresh))
                .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))

        onView(withId(R.id.food_detail_name)).check(matches(withText(itemElementText)))
    }

    @Test
    fun cartFragment_checkEmptyState() {
        CartStore.foodCart = FoodCartStore("u1").also {
            it.cartList = arrayListOf()
        }

        CartStore.counter = 0

        cartStore.postValue(CartStore)

        mActivityRule.activity.replacedFragment(CartFragment(), "CartFragment")

        onView(withId(R.id.image_empty_state)).check(matches(isDisplayed()))
        onView(ViewMatchers.withId(R.id.recyclerView)).check(matches(not(isDisplayed())))
    }

    @Test
    fun cartFragment_checkItemInCart() {
        CartStore.foodCart = FoodCartStore("u1").also {
            it.cartList = arrayListOf(
                    onedaycat.com.food.fantasy.ui.cart.CartModel(
                            "1111",
                            "",
                            200,
                            1000,
                            2,
                            true)
            )
        }

        CartStore.counter = 1

        cartStore.postValue(CartStore)

        mActivityRule.activity.replacedFragment(CartFragment(), "CartFragment")

        onView(withId(R.id.image_empty_state)).check(matches(not(isDisplayed())))
        onView(ViewMatchers.withId(R.id.recyclerView)).check(matches(isDisplayed()))
    }

    @Test
    fun orderFragment_check_orderItem() {
        val orderFragment = OrderFragment()
        orderFragment.viewModelFactory = ViewModelUtil.createViewModelFor(orderViewModel)
        mActivityRule.activity.replacedFragment(orderFragment, "OrderFragment")


        OrdersModel().apply {
            this.orderModels = arrayListOf(
                    OrderModel().apply {
                        this.orderId = "r1"
                        this.totalPrice = 2000
                        this.orderProducts = OrderProductsModel().apply {
                            this.products = arrayListOf(
                                    OrderProductModel().apply {
                                        this.productName = itemElementText
                                        this.totalPriceProduct = 700
                                    }
                            )
                        }
                    }
            )
        }.also {
            orderObserver.postValue(it)
        }

        onView(ViewMatchers.withId(R.id.rv_with_refresh)).check(matches(isDisplayed()))
        onView(withId(R.id.image_empty_state)).check(matches(not(isDisplayed())))
    }

    @Test
    fun orderFragment_check_emptyState() {
        val orderFragment = OrderFragment()
        orderFragment.viewModelFactory = ViewModelUtil.createViewModelFor(orderViewModel)
        mActivityRule.activity.replacedFragment(orderFragment, "OrderFragment")

        OrdersModel().apply {
            this.orderModels = arrayListOf()
        }.also {
            orderObserver.postValue(it)
        }

        onView(ViewMatchers.withId(R.id.rv_with_refresh)).check(matches(not(isDisplayed())))
        onView(withId(R.id.image_empty_state)).check(matches(isDisplayed()))
    }

    @Test
    fun orderFragment_clickItem_orderDetailAction() {
        val orderFragment = OrderFragment()
        orderFragment.viewModelFactory = ViewModelUtil.createViewModelFor(orderViewModel)
        mActivityRule.activity.replacedFragment(orderFragment, "OrderFragment")


        OrdersModel().apply {
            this.orderModels = arrayListOf(
                    OrderModel().apply {
                        this.orderId = "r1"
                        this.totalPrice = 2000
                        this.orderProducts = OrderProductsModel().apply {
                            this.products = arrayListOf(
                                    OrderProductModel().apply {
                                        this.productName = itemElementText
                                        this.totalPriceProduct = 700
                                    }
                            )
                        }
                    }
            )
        }.also {
            orderObserver.postValue(it)
        }

        onView(ViewMatchers.withId(R.id.rv_with_refresh))
                .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))

        onView(ViewMatchers.withId(R.id.recyclerView)).check(matches(isDisplayed()))
    }

}