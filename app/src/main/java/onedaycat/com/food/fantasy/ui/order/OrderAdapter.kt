package onedaycat.com.food.fantasy.ui.order

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.order_detail_item.view.*
import onedaycat.com.food.fantasy.R
import onedaycat.com.foodfantasyservicelib.entity.Order

class OrderAdapter(
        val context:Context,
        private val orders: ArrayList<OrderModel>,
        private val orderItemClick: OrderItemClickListener): RecyclerView.Adapter<OrderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.order_detail_item, parent, false)
        return OrderViewHolder(view)
    }

    override fun getItemCount(): Int {
        return orders.size
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val orderIdStr = "Order#${orders[position].orderId}"
        holder.orderId.text = orderIdStr

        val totalPriceStr = "${context.getString(R.string.currency_dollar)}${orders[position].totalPrice}"
        holder.orderTotalPrice.text = totalPriceStr

        holder.setItemViewClicked(orderItemClick, orders[position])
    }
}

class OrderViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    var orderId = itemView.order_item_id!!
    var orderTotalPrice = itemView.order_item_total!!

    fun setItemViewClicked(orderItemClick: OrderItemClickListener, orderModel: OrderModel) {
        itemView.setOnClickListener {
            orderItemClick.onClicked(orderModel)
        }
    }
}