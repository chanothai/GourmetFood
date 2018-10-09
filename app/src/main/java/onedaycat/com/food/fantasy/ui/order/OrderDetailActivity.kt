package onedaycat.com.food.fantasy.ui.order

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import kotlinx.android.synthetic.main.appbar_collapsing_toolbar.*
import kotlinx.android.synthetic.main.recyclerview_layout.*
import onedaycat.com.food.fantasy.R
import onedaycat.com.food.fantasy.common.BaseActivity

fun Context.OrderDetailActivity(orderModel: OrderModel): Intent{
    return Intent(this, OrderDetailActivity()::class.java).apply {
        putExtra(ORDER_MODEL, orderModel)
    }
}

private const val ORDER_MODEL = "order_model"

class OrderDetailActivity : BaseActivity() {
    private lateinit var orderViewModel: OrderViewModel
    private lateinit var orderModel: OrderModel

    private var orderAdapter:OrderDetailAdapter? = null

    override fun isDisplayHomeEnable(): Boolean? = true
    override fun getToolbarInstance(): Toolbar? = toolbar
    override fun title(): String? = getString(R.string.title_order_detail_th)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_detail)

        if (savedInstanceState == null) {
            showDetailOrder()
        }
    }

    private fun showDetailOrder() {
        intent?.let {
            orderModel = it.getParcelableExtra(ORDER_MODEL)
            orderModel
        }?.also {model->

            orderAdapter?.let { adapter->
                adapter.notifyDataSetChanged()
                return
            }

            orderAdapter = OrderDetailAdapter(this, model)
            recyclerView.layoutManager = LinearLayoutManager(this)
            recyclerView.adapter = orderAdapter
        }
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
