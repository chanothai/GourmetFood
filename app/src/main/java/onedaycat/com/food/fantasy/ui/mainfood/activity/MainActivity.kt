package onedaycat.com.food.fantasy.mainfood.activity

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationAdapter
import com.aurelhubert.ahbottomnavigation.notification.AHNotification
import kotlinx.android.synthetic.main.appbar_normal.*
import kotlinx.android.synthetic.main.activity_main.bottom_bar as bottomBar
import onedaycat.com.food.fantasy.R
import onedaycat.com.food.fantasy.common.BaseActivity
import onedaycat.com.food.fantasy.mainfood.FoodViewModel
import onedaycat.com.food.fantasy.store.CartStore
import onedaycat.com.food.fantasy.store.FoodCartStore
import onedaycat.com.food.fantasy.ui.cart.fragment.CartFragment
import onedaycat.com.food.fantasy.ui.mainfood.fragment.MainMenuFragment
import onedaycat.com.food.fantasy.ui.order.fragment.OrderFragment
import onedaycat.com.food.fantasy.ui.signin.SignInActivity
import onedaycat.com.food.fantasy.util.CognitoUserHelper
import onedaycat.com.food.fantasy.util.ViewModelUtil
import onedaycat.com.food.fantasy.service.EcomService

class MainActivity : BaseActivity(), AHBottomNavigation.OnTabSelectedListener {

    override fun getToolbarInstance(): Toolbar? = toolbar
    override fun isDisplayHomeEnable(): Boolean? = false
    override fun title(): String? = null

    var foodViewModel: FoodViewModel? = null
    var viewModelFactory: ViewModelProvider.Factory? = ViewModelUtil.mockViewModel

    private val userID = "u1"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViewModel()
        createBottomBar()
        if (savedInstanceState == null) {
            replacedFragment(
                    MainMenuFragment.newInstance(),
                    "MainMenuFragment")
        }
    }

    private fun initViewModel() {
        with(CartStore) {
            this.foodCart?.let {
                return@with
            }

            this.foodCart = FoodCartStore().apply {
                this.userId = userID
            }
        }

        with(CartStore) {
            viewModelFactory?.let {
                foodViewModel = ViewModelProviders.of(this@MainActivity, viewModelFactory)
                        .get(FoodViewModel::class.java)
                return@with
            }

            foodViewModel = ViewModelProviders.of(this@MainActivity,
                    ViewModelUtil.createViewModelFor(FoodViewModel(EcomService())))
                    .get(FoodViewModel::class.java)
        }

        updateBottomBarObserver()
    }

    private fun updateBottomBarObserver() {
        foodViewModel?.let {vm->
            vm.bottomBarLiveData.observe(this, Observer {page->
                page?.let {
                    createBadgeCart(0)
                    bottomBar.currentItem = page
                }
            })
        }
    }

    private fun createBottomBar() {
        val tabColors = resources.getIntArray(R.array.color_bottom_navigation_selected)
        val adapter = AHBottomNavigationAdapter(this, R.menu.menu_navigation)
        adapter.setupWithBottomNavigation(bottomBar, tabColors)
        bottomBar.accentColor = ContextCompat.getColor(this, R.color.colorPrimary)

        bottomBar.setOnTabSelectedListener(this)
    }

    fun createBadgeCart(count: Int) {
        if (count == 0) {
            bottomBar.setNotification("", 1)
            return
        }

        AHNotification.Builder().let { builder ->
            builder.setText(count.toString())
                    .setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary))
                    .setTextColor(ContextCompat.getColor(this, R.color.color_bg_white))

            builder.build()
        }.also { notification ->

            bottomBar.setNotification(notification, 1)
        }
    }


    fun replacedFragment(fragment: Fragment, tag: String) {
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.container, fragment, tag)
                .commit()
    }

    override fun onTabSelected(position: Int, wasSelected: Boolean): Boolean {
        return when (position) {
            0 -> {
                replacedFragment(
                        MainMenuFragment.newInstance(),
                        "MainMenuFragment")

                updateTitleToolbar(getString(R.string.title_menu_th))
                true
            }

            1 -> {
                replacedFragment(CartFragment.newInstance(), "CartFragment")
                updateTitleToolbar(getString(R.string.title_cart_th))
                true
            }

            2 -> {
                replacedFragment(OrderFragment.newInstance(), "OrderFragment")
                updateTitleToolbar(getString(R.string.title_toolbar_list_order))
                true
            }

            else -> false
        }
    }

    override fun onBackPressed() {

    }

    override fun onResume() {
        super.onResume()
        with(CartStore) {
            createBadgeCart(counter)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.sign_out_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        return when(item?.itemId) {
            R.id.sign_out_menu -> {
                CognitoUserHelper.cognitoUser().currentUser.signOut()

                startActivity(Intent(this@MainActivity, SignInActivity::class.java))
                finish()
                return true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}
