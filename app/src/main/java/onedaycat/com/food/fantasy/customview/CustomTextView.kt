package onedaycat.com.food.fantasy.customview

import android.content.Context
import android.graphics.Typeface
import android.support.v7.widget.AppCompatButton
import android.support.v7.widget.AppCompatTextView
import android.util.AttributeSet

class CustomTextView @JvmOverloads constructor(
        context:Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = android.R.attr.textViewStyle): AppCompatTextView(context, attrs, defStyleAttr)
{
    init {
        typeface = Typeface.createFromAsset(context.assets, "fonts/SukhumvitSet-Text.ttf")
    }
}

class CustomButtonView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = android.R.attr.buttonStyle): AppCompatButton(context, attrs, defStyleAttr)
{
    init {
        typeface = Typeface.createFromAsset(context.assets, "fonts/SukhumvitSet-Text.ttf")
    }
}

