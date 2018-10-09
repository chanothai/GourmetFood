package onedaycat.com.food.fantasy.ui.cart

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import onedaycat.com.food.fantasy.R
import onedaycat.com.food.fantasy.ui.cart.viewHolder.CartTypeViewHolder
import onedaycat.com.food.fantasy.ui.cart.viewHolder.PaymentTypeViewHolder
import onedaycat.com.food.fantasy.ui.cart.viewHolder.TitleTypeViewHolder

object Type {
    var titleType = 0
    var cartType = 1
    var payType = 2
}

class CartAdapter(
        private val items: ArrayList<onedaycat.com.food.fantasy.ui.cart.CartModel>,
        private val context: Context,
        private val onActionCartListener: onedaycat.com.food.fantasy.ui.cart.OnActionCartListener): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var sizeItem:Int = 0
    private var cartSize = 0

    private  val topicsPay = arrayListOf(
            context.resources.getString(R.string.topic_card_number),
            context.resources.getString(R.string.topic_card_name),
            context.resources.getString(R.string.topic_card_expired),
            context.resources.getString(R.string.topic_card_cvv)
    )

    private val hintsPay = arrayListOf(
            context.resources.getString(R.string.hint_card_number),
            context.resources.getString(R.string.hint_card_name),
            context.resources.getString(R.string.hint_card_expired),
            context.resources.getString(R.string.hint_card_cvv)
    )

    override fun getItemViewType(position: Int): Int {
        return when {
            position == 0 -> Type.titleType
            position < (cartSize) -> Type.cartType
            position == (cartSize) -> Type.titleType
            position > (cartSize) -> Type.payType
            else -> -1
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View?

        when(viewType) {
            Type.titleType -> {
                view = LayoutInflater.from(context).inflate(R.layout.layout_title_type, parent, false)
                return TitleTypeViewHolder(view)
            }

            Type.cartType -> {
                view = LayoutInflater.from(context).inflate(R.layout.layout_cart_type, parent, false)
                return CartTypeViewHolder(view)
            }

            Type.payType -> {
                view = LayoutInflater.from(context).inflate(R.layout.layout_payment_type, parent, false)
                return PaymentTypeViewHolder(view)
            }

            else -> {
                view = LayoutInflater.from(context).inflate(R.layout.layout_title_type, parent, false)
                return TitleTypeViewHolder(view)
            }
        }
    }


    override fun getItemCount(): Int {
        cartSize = items.size + 1
        sizeItem = cartSize + topicsPay.size + 1
        return sizeItem
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder) {
            is TitleTypeViewHolder -> {
                if (cartSize == 0) {
                    holder.titleName.text = context.getString(R.string.title_list_cart)
                }else{
                    holder.titleName.text = context.getString(R.string.title_payment_type)
                }

                return
            }

            is CartTypeViewHolder-> {
                val index = position - 1
                holder.cartName.text = items[index].cartName
                holder.cartQTY.text = items[index].cartQTY.toString()
                holder.setOnItemClicked(items[index], onActionCartListener)
                return
            }

            is PaymentTypeViewHolder -> {
                val index = (position - 2) - items.size
                holder.payTopicName.text = topicsPay[index]
                holder.payEditData.hint = hintsPay[index]

                when(index) {
                    0 -> holder.payEditData.maxEms = 13
                    1 -> {
                        holder.payEditData.inputType = InputType.TYPE_CLASS_TEXT
                        holder.mIsInTheMiddle = true
                    }
                    2 -> holder.payEditData.inputType = InputType.TYPE_DATETIME_VARIATION_DATE
                    else -> holder.mIsInTheMiddle = false
                }

                holder.setEditTextChange(onActionCartListener, index)
                return
            }
        }
    }

}

