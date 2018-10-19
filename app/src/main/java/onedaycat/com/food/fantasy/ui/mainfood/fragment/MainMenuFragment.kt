package onedaycat.com.food.fantasy.ui.mainfood.fragment


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kotlinx.android.synthetic.main.recyclerview_and_swipe_refresh_layout.rv_with_refresh as foodRecyclerView
import kotlinx.android.synthetic.main.recyclerview_and_swipe_refresh_layout.swipe_container as swipeRefreshFood
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch

import onedaycat.com.food.fantasy.R
import onedaycat.com.food.fantasy.mainfood.ItemClickedCallback
import onedaycat.com.food.fantasy.ui.mainfood.FoodAdapter
import onedaycat.com.food.fantasy.ui.mainfood.FoodModel
import onedaycat.com.food.fantasy.ui.mainfood.activity.foodDetailActivity
import onedaycat.com.food.fantasy.store.CartStore
import onedaycat.com.food.fantasy.ui.mainfood.FoodViewModel
import onedaycat.com.food.fantasy.ui.mainfood.activity.MainActivity
import onedaycat.com.foodfantasyservicelib.input.GetCartInput
import onedaycat.com.foodfantasyservicelib.input.GetProductsInput

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MainMenuFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class MainMenuFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener, ItemClickedCallback {
    //View
    private lateinit var foodViewModel: FoodViewModel

    private lateinit var mActivity: MainActivity
    private var foodAdapter: FoodAdapter? = null
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    //fixed data
    private val limit = 10
    private var badgeCart = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
                MainMenuFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main_menu, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mActivity = (activity as MainActivity)
        prepareSwipeRefreshLayout()
        initViewModel()

        if (savedInstanceState == null) {
            fetchDataFood(limit)
        }
    }

    private fun prepareSwipeRefreshLayout() {
        swipeRefreshFood.setColorSchemeColors(Color.BLUE, Color.GREEN, Color.YELLOW, Color.RED)
        swipeRefreshFood.setOnRefreshListener(this)
    }

    private fun initViewModel() {
        foodViewModel = ViewModelProviders.of(mActivity).get(FoodViewModel::class.java)
        foodViewModel.let {
            foodDataObserver()
            cartObserver()
        }
    }

    private fun msgErrorObserver() {
        foodViewModel.msgError.observe(this, Observer {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        })
    }

    private fun cartObserver() {
        foodViewModel.cartStore.observe(this, Observer {
            it?.let { cartStore ->
                badgeCart = cartStore.counter

                cartStore
            }?.also { cartStore ->
                CartStore.foodCart = cartStore.foodCart
                CartStore.counter = cartStore.counter

                mActivity.createBadgeCart(cartStore.counter)

            }
        })
    }

    private fun foodDataObserver() {
        foodViewModel.foodData.observe(this, Observer { data ->
            swipeRefreshFood.isRefreshing = false

            data?.let {
                foodAdapter?.let { adapter ->
                    adapter.notifyDataSetChanged()
                    adapter
                }

                foodAdapter = FoodAdapter(it, this.context!!, this)

                foodRecyclerView.layoutManager = LinearLayoutManager(context)
                foodRecyclerView.hasFixedSize()
                foodRecyclerView.adapter = foodAdapter
            }

            if (CartStore.foodCart?.cartList?.size == 0) {
                launch(UI) {
//                    foodViewModel.loadCart()
                }
            }
        })
    }

    override fun onRefresh() {
        swipeRefreshFood.isRefreshing = false
        fetchDataFood(limit)
    }

    private fun fetchDataFood(limit: Int) {
        val input = GetProductsInput(limit)

        launch(UI) {
            foodViewModel.loadProducts(input)
        }
    }

    override fun onClicked(foodModel: FoodModel) {
        startActivity(activity?.foodDetailActivity(foodModel))
    }
}
