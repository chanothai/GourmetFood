package onedaycat.com.food.fantasy.ui.cart.viewHolder

import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.TextView
import onedaycat.com.food.fantasy.R
import onedaycat.com.food.fantasy.ui.cart.OnActionCartListener

class PaymentTypeViewHolder(itemView:View): RecyclerView.ViewHolder(itemView) {
    var payTopicName = itemView.findViewById<TextView>(R.id.pay_item_topic)
    var payEditData = itemView.findViewById<EditText>(R.id.pay_item_edit)

    var mIsInTheMiddle = false
    get() {
        return field.let {
            it
        }
    }
    set(value) {
        value.let {
            field = it
        }
    }

    fun setEditTextChange(actionCartListener: onedaycat.com.food.fantasy.ui.cart.OnActionCartListener, position: Int) {
        payEditData.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
                actionCartListener.onTextChange(text.toString(), position)
            }
        })
    }


}
