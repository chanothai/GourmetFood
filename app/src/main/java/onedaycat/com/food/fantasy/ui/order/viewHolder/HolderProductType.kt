package onedaycat.com.food.fantasy.ui.order.viewHolder

import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.synthetic.main.layout_item_order_products_type.view.*
import kotlinx.android.synthetic.main.layout_item_order_total_price_type.view.*

class HolderProductType(itemnView: View): RecyclerView.ViewHolder(itemnView) {
    var pName = itemView.order_item_name
    var pPrice = itemView.order_item_price
}