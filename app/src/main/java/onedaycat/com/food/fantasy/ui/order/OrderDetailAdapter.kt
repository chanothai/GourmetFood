package onedaycat.com.food.fantasy.ui.order

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import onedaycat.com.food.fantasy.R
import onedaycat.com.food.fantasy.ui.order.viewHolder.*

object LayoutOrderType {
    var idType = 0
    var emptyType = 1
    var creditCardType = 2
    var productType = 3
    var totalPriceType = 4

}

class OrderDetailAdapter(
        private val context: Context,
        private val orderModel: OrderModel
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val itemPSize = orderModel.orderProducts.products.size + 4
    private val allItemSize = itemPSize + 2

    override fun getItemViewType(position: Int): Int {
        return when {
            position == 0 -> LayoutOrderType.idType
            position == 1 || position == 3 || position == (itemPSize) -> LayoutOrderType.emptyType
            position == 2 -> LayoutOrderType.creditCardType
            position < itemPSize -> LayoutOrderType.productType
            position > itemPSize -> LayoutOrderType.totalPriceType
            else -> -1
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View?

        return when(viewType) {
            LayoutOrderType.idType -> {
                view = LayoutInflater.from(context).inflate(R.layout.layout_item_order_id_type, parent, false)
                HolderOrderIdType(view)
            }

            LayoutOrderType.emptyType -> {
                view = LayoutInflater.from(context).inflate(R.layout.layout_item_order_empty_type, parent, false)
                HolderEmptyType(view)
            }

            LayoutOrderType.productType -> {
                view = LayoutInflater.from(context).inflate(R.layout.layout_item_order_products_type, parent, false)
                HolderProductType(view)
            }

            LayoutOrderType.totalPriceType -> {
                view = LayoutInflater.from(context).inflate(R.layout.layout_item_order_total_price_type, parent, false)
                HolderTotalPriceType(view)
            }

            LayoutOrderType.creditCardType -> {
                view = LayoutInflater.from(context).inflate(R.layout.layout_item_order_credit_card_type, parent, false)
                HolderCreditCardType(view)
            }

            else -> {
                throw IllegalArgumentException("$viewType don't matched any view")
            }
        }
    }

    override fun getItemCount(): Int {
        return allItemSize
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder) {
            is HolderOrderIdType -> {
                val resultId = "ORDER NO. ${orderModel.orderId}"
                holder.orderId.text = resultId
            }

            is HolderProductType -> {
                val index = position - 4
                holder.pName.text = orderModel.orderProducts.products[index].productName

                val resultPrice = "${context.getString(R.string.currency_dollar)}${orderModel.orderProducts.products[index].totalPriceProduct}"
                holder.pPrice.text = resultPrice
            }

            is  HolderTotalPriceType -> {
                val resultTotal = "${context.getString(R.string.currency_dollar)}${orderModel.totalPrice}"
                holder.orderTotalPrice.text = resultTotal
            }
        }
    }
}