package onedaycat.com.food.fantasy.ui.cart.viewHolder

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.Button
import android.widget.TextView
import kotlinx.android.synthetic.main.layout_cart_type.view.*

class CartTypeViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    var cartName = itemView.cart_item_name ?: TextView(itemView.context)
    var cartQTY = itemView.cart_item_qty ?: TextView(itemView.context)
    var btnAddQTY = itemView.btn_add_qty ?: Button(itemView.context)
    var btnRemoveQTY = itemView.btn_remove_qty ?: Button(itemView.context)

    fun setOnItemClicked(cartModel: onedaycat.com.food.fantasy.ui.cart.CartModel, onActionCartListener: onedaycat.com.food.fantasy.ui.cart.OnActionCartListener) {
        btnAddQTY.setOnClickListener {

            cartModel.let {
                it.hasFood = true
                it.cartQTY += 1
                cartQTY.text = "${cartModel.cartQTY}"

                it
            }.also {
                onActionCartListener.onAddCart(it)
            }
        }

        btnRemoveQTY.setOnClickListener { view ->
            cartModel.let {
                it.hasFood = false
                if (it.cartQTY == 1) {
                    return@setOnClickListener
                }

                it.cartQTY -= 1
                cartQTY.text = "${it.cartQTY}"

                it
            }

            with(cartModel) {
                onActionCartListener.onRemoveCart(this)
            }
        }
    }
}