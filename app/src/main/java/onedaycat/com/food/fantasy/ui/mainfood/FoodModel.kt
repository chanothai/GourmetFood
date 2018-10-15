package onedaycat.com.food.fantasy.ui.mainfood

import android.os.Parcel
import android.os.Parcelable

data class FoodModel(
        var foodId: String = "",
        var foodName: String = "",
        var foodDesc: String = "",
        var foodPrice: Int = 0,
        var foodIMG: String = "",
        var isAddToCart: Boolean = false
) : Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readInt(),
            parcel.readString()!!,
            parcel.readByte() != 0.toByte())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(foodId)
        parcel.writeString(foodName)
        parcel.writeString(foodDesc)
        parcel.writeInt(foodPrice)
        parcel.writeString(foodIMG)
        parcel.writeByte(if (isAddToCart) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FoodModel> {
        override fun createFromParcel(parcel: Parcel): FoodModel {
            return FoodModel(parcel)
        }

        override fun newArray(size: Int): Array<FoodModel?> {
            return arrayOfNulls(size)
        }
    }
}

data class FoodListModel(
        var foodList: ArrayList<FoodModel>
)

data class FoodSumModel(
        var qty: Int = 0,
        var price: Int = 0,
        var totalPrice: Int = 0
)