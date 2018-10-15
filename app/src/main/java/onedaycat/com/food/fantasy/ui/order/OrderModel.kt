package onedaycat.com.food.fantasy.ui.order

import android.os.Parcel
import android.os.Parcelable

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
data class OrderModel(
        var orderId: String? = null,
        var totalPrice: Int = 0,
        var orderProducts: OrderProductsModel = OrderProductsModel()
) : Parcelable {
    constructor(source: Parcel) : this(
            source.readString(),
            source.readInt(),
            source.readParcelable<OrderProductsModel>(OrderProductsModel::class.java.classLoader)
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(orderId)
        writeInt(totalPrice)
        writeParcelable(orderProducts, 0)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<OrderModel> = object : Parcelable.Creator<OrderModel> {
            override fun createFromParcel(source: Parcel): OrderModel = OrderModel(source)
            override fun newArray(size: Int): Array<OrderModel?> = arrayOfNulls(size)
        }
    }
}

data class OrderProductModel(
        var productName: String? = null,
        var totalPriceProduct: Int = 0
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readInt())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(productName)
        parcel.writeInt(totalPriceProduct)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<OrderProductModel> {
        override fun createFromParcel(parcel: Parcel): OrderProductModel {
            return OrderProductModel(parcel)
        }

        override fun newArray(size: Int): Array<OrderProductModel?> {
            return arrayOfNulls(size)
        }
    }
}

data class OrderProductsModel(
        var products: ArrayList<OrderProductModel> = arrayListOf()
) : Parcelable {
    constructor(source: Parcel) : this(
            source.createTypedArrayList(OrderProductModel.CREATOR)
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeTypedList(products)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<OrderProductsModel> = object : Parcelable.Creator<OrderProductsModel> {
            override fun createFromParcel(source: Parcel): OrderProductsModel = OrderProductsModel(source)
            override fun newArray(size: Int): Array<OrderProductsModel?> = arrayOfNulls(size)
        }
    }
}

data class OrdersModel(
        var orderModels: ArrayList<OrderModel> = arrayListOf()
)