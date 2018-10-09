package onedaycat.com.food.fantasy.mainfood.activity

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.appbar_header_main_food.*
import kotlinx.android.synthetic.main.appbar_main_food.*
import kotlinx.android.synthetic.main.food_detail_information.*
import kotlinx.android.synthetic.main.layout_food_detail_control.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import onedaycat.com.food.fantasy.R
import onedaycat.com.food.fantasy.common.BaseActivity
import onedaycat.com.food.fantasy.mainfood.FoodModel
import onedaycat.com.food.fantasy.mainfood.FoodViewModel
import onedaycat.com.food.fantasy.store.CartStore
import onedaycat.com.food.fantasy.util.ViewModelUtil
import onedaycat.com.foodfantasyservicelib.input.AddToCartInput
import onedaycat.com.food.fantasy.service.EcomService

fun Context.foodDetailActivity(foodModel: FoodModel): Intent {
    return Intent(this, FoodDetailActivity::class.java).apply {
        this.putExtra(FOOD_MODEL, foodModel)
    }
}

private const val FOOD_MODEL = "food_model"

class FoodDetailActivity : BaseActivity() {
    private lateinit var foodModel: FoodModel

    private lateinit var foodViewModel: FoodViewModel
    private lateinit var userId: String
    private var isAddToCart = false

    override fun getToolbarInstance(): Toolbar? = toolbar_collapse

    override fun isDisplayHomeEnable(): Boolean? = true
    override fun title(): String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food_detail)

        bindView()
        bindBundle()
        initViewModel()

        if (savedInstanceState == null) {
            setView()
        }
    }

    private fun initViewModel() {
        foodViewModel = ViewModelProviders.of(this@FoodDetailActivity,
                ViewModelUtil.createViewModelFor(FoodViewModel(EcomService())))
                .get(FoodViewModel::class.java)

        foodSumObserver()
        cartStoreObserver()
    }

    private fun foodSumObserver() {
        foodViewModel.foodSumModel.observe(this, Observer { it ->
            it?.let {
                val priceStr = "${getString(R.string.currency_dollar)}${it.totalPrice}"
                food_detail_total_price.text = priceStr

                food_detail_qty.text = it.qty.toString()
            }
        })
    }

    private fun cartStoreObserver() {
        foodViewModel.cartStore.observe(this, Observer {cartStore ->

            cartStore?.let {
                userId = it.foodCart?.userId!!

                if (!isAddToCart) {
                    with(foodModel) {
                        foodViewModel.initTotalPrice(this)
                    }

                    return@let
                }

                CartStore.foodCart = it.foodCart
                CartStore.counter = it.counter

                finish()
            }

            dismissDialog()
        })
    }

    private fun bindBundle() {
        intent?.let {
            foodModel = it.getParcelableExtra(FOOD_MODEL)
        }
    }

    private fun bindView() {
        btn_add_qty.setOnClickListener{
            foodViewModel.foodDetailSumTotalPrice(true)
        }

        btn_remove_qty.setOnClickListener{
            foodViewModel.foodDetailSumTotalPrice(false)
        }

        btn_add_cart.setOnClickListener{
            val currentQTY = food_detail_qty.text.toString().toInt()

            val input = AddToCartInput(
                    userId,
                    foodModel.foodId,
                    foodModel.foodName,
                    currentQTY
            )

            isAddToCart = true
            showLoadingDialog()

            launch(UI) { foodViewModel.addProductToCart(input) }
        }
    }

    private fun setView() {
        val price = "${resources.getString(R.string.currency_dollar)}${foodModel.foodPrice}"
        food_detail_name.text = foodModel.foodName
        food_detail_price.text = price
        food_detail_desc.text = foodModel.foodDesc
        food_detail_total_price.text = price

        Glide.with(this)
                .load(foodModel.foodIMG)
                .into(app_bar_image)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return if (item?.itemId == android.R.id.home) {
            finish()
            true
        }else {
            super.onOptionsItemSelected(item)
        }
    }
}
