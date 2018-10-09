package onedaycat.com.food.fantasy.ui.order.fragment


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.layout_empty_state.*
import kotlinx.android.synthetic.main.recyclerview_and_swipe_refresh_layout.swipe_container as swipeRefreshFood
import kotlinx.android.synthetic.main.recyclerview_and_swipe_refresh_layout.rv_with_refresh as orderRecyclerView
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch

import onedaycat.com.food.fantasy.R
import onedaycat.com.food.fantasy.mainfood.activity.MainActivity
import onedaycat.com.food.fantasy.store.CartStore
import onedaycat.com.food.fantasy.ui.order.*
import onedaycat.com.food.fantasy.util.ViewModelUtil
import onedaycat.com.foodfantasyservicelib.input.GetOrderInput
import onedaycat.com.food.fantasy.service.EcomService

class OrderFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener, OrderItemClickListener {
    private lateinit var mainActivity: MainActivity
    private lateinit var orderViewModel: OrderViewModel

    private var orderAdapter: OrderAdapter? = null

    var viewModelFactory: ViewModelProvider.Factory? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_order, container, false)
    }


    companion object {
        @JvmStatic
        fun newInstance() = OrderFragment()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mainActivity = (activity as MainActivity)

        prepareSwipeRefreshLayout()
        initViewMode()

        if (savedInstanceState == null) {
            showOrderHistory()
        }
    }

    private fun prepareSwipeRefreshLayout() {
        swipeRefreshFood.setColorSchemeColors(Color.BLUE, Color.GREEN, Color.YELLOW, Color.RED)
        swipeRefreshFood.setOnRefreshListener(this)
    }

    private fun initLayout() {
        layout_empty_state.visibility = View.VISIBLE
        image_empty_state.setImageResource(R.drawable.ic_history)
        msg_empty_state.text = getString(R.string.msg_order_empty_state)

        orderRecyclerView.visibility = View.GONE
    }

    private fun showOrderHistory() {
        CartStore.foodCart?.let {
            val input = GetOrderInput(
                    CartStore.foodCart?.userId!!
            )

            input
        }?.also {input->
            launch(UI) {
                orderViewModel.loadOrderHistory(input)
            }

        }
    }

    private fun initViewMode() {
        viewModelFactory?.let {
            orderViewModel = ViewModelProviders.of(this, it)
                    .get(OrderViewModel::class.java)

            orderObserver()

            return
        }


        orderViewModel = ViewModelProviders.of(this,
                ViewModelUtil.createViewModelFor(OrderViewModel(EcomService()))).get(OrderViewModel::class.java)

        orderObserver()
    }

    private fun orderObserver() {
        orderViewModel.orders.observe(this, Observer { order ->
            order?.orderModels?.let {orders->
                swipeRefreshFood.isRefreshing = false

                if (orders.size > 0) {
                    orderAdapter?.let {adapter->
                        adapter.notifyDataSetChanged()
                        return@Observer
                    }

                    orderAdapter = OrderAdapter(this.context!!, orders, this)

                    orderRecyclerView.layoutManager = LinearLayoutManager(this.context)
                    orderRecyclerView.adapter = orderAdapter
                    return@Observer
                }

                initLayout()
            }
        })
    }

    override fun onRefresh() {
        showOrderHistory()
    }

    override fun onClicked(orderModel: OrderModel) {
        startActivity(activity?.OrderDetailActivity(orderModel))
    }
}
