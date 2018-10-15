package onedaycat.com.food.fantasy.ui.mainfood

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.food_detail_item.view.*
import onedaycat.com.food.fantasy.R
import onedaycat.com.food.fantasy.mainfood.ItemClickedCallback

class FoodAdapter(
        private val items: FoodListModel,
        private val context: Context,
        private val itemClickedCallback: ItemClickedCallback): RecyclerView.Adapter<FoodViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.food_list_item, parent, false)

        return FoodViewHolder(view)
    }

    override fun getItemCount(): Int {
        if (items.foodList.size == 0) return 0
        return items.foodList.size
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        holder.foodName.text = items.foodList[position].foodName

        val price = "THB${items.foodList[position].foodPrice}"
        holder.foodPrice.text = price

        Glide.with(context)
                .load(items.foodList[position].foodIMG)
                .apply(holder.configImage(context))
                .into(holder.foodIMG)

        holder.setHolderClicked(itemClickedCallback, items.foodList[position])

    }
}

class FoodViewHolder(itemView:View): RecyclerView.ViewHolder(itemView) {
    var foodName = itemView.food_item_name!!
    var foodIMG = itemView.food_item_img!!
    var foodPrice = itemView.food_item_price!!

    fun setHolderClicked(itemClickedCallback: ItemClickedCallback, foodModel: FoodModel) {
        itemView.setOnClickListener {
            itemClickedCallback.onClicked(foodModel)
        }
    }
    fun configImage(context: Context):RequestOptions {
        val radius = context.resources.getDimensionPixelSize(R.dimen.radius_image)

        return RequestOptions().apply {
            centerCrop()
            transforms(CenterCrop(), RoundedCorners(radius))
            placeholder(R.mipmap.ic_launcher)
        }
    }
}