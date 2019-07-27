package ir.elevin.azhand

import android.os.Parcel
import android.os.Parcelable
import androidx.fragment.app.Fragment
import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.Reader

data class TabModel(val title: String, val fragment: Fragment)

data class MakeUser(val customer: customer)

data class GetUser(val customer: Account) {
    class Deserializer : ResponseDeserializable<GetUser> {
        override fun deserialize(reader: Reader) = Gson().fromJson(reader, GetUser::class.java)!!
    }
}

data class FinalOrder(val order: String = "")

data class LoginData(val token: String,
                     val user_id: Int,
                     val user_email: String,
                     val user_nicename: String,
                     val user_display_name: String) : Parcelable {

    companion object CREATOR : Parcelable.Creator<LoginData> {
        override fun createFromParcel(parcel: Parcel): LoginData {
            return LoginData(parcel)
        }

        override fun newArray(size: Int): Array<LoginData?> {
            return arrayOfNulls(size)
        }
    }

    constructor(parcel: Parcel) : this(
            parcel.readString()!!,
            parcel.readInt(),
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(token)
        parcel.writeInt(user_id)
        parcel.writeString(user_email)
        parcel.writeString(user_nicename)
        parcel.writeString(user_display_name)
    }

    class Deserializer : ResponseDeserializable<LoginData> {
        override fun deserialize(reader: Reader) = Gson().fromJson(reader, LoginData::class.java)!!
    }

    class ListDeserializer : ResponseDeserializable<List<LoginData>> {

        override fun deserialize(reader: Reader): List<LoginData> {
            val type = object : TypeToken<List<LoginData>>() {}.type
            return Gson().fromJson(reader, type)
        }
    }

    override fun describeContents(): Int {
        return 0
    }
}


data class billing_address(val first_name: String = "",
                           val last_name: String = "",
                           val company: String = "",
                           var address_1: String = "",
                           var address_2: String = "",
                           val city: String = "",
                           val state: String = "",
                           val postcode: String = "",
                           val country: String = "",
                           val email: String = "",
                           val phone: String = "") : Parcelable {

    companion object CREATOR : Parcelable.Creator<billing_address> {
        override fun createFromParcel(parcel: Parcel): billing_address {
            return billing_address(parcel)
        }

        override fun newArray(size: Int): Array<billing_address?> {
            return arrayOfNulls(size)
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(first_name)
        parcel.writeString(last_name)
        parcel.writeString(company)
        parcel.writeString(address_1)
        parcel.writeString(address_2)
        parcel.writeString(city)
        parcel.writeString(state)
        parcel.writeString(postcode)
        parcel.writeString(country)
        parcel.writeString(email)
        parcel.writeString(phone)
    }

    constructor(parcel: Parcel) : this(
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!
    )

    class Deserializer : ResponseDeserializable<billing_address> {
        override fun deserialize(reader: Reader) = Gson().fromJson(reader, billing_address::class.java)!!
    }

    class ListDeserializer : ResponseDeserializable<List<billing_address>> {

        override fun deserialize(reader: Reader): List<billing_address> {
            val type = object : TypeToken<List<billing_address>>() {}.type
            return Gson().fromJson(reader, type)
        }
    }

    override fun describeContents(): Int {
        return 0
    }
}

data class shipping_address(val first_name: String = "",
                            val last_name: String = "",
                            val company: String = "",
                            var address_1: String = "",
                            var address_2: String = "",
                            val city: String = "",
                            val state: String = "",
                            val postcode: String = "",
                            val country: String = "") : Parcelable {

    companion object CREATOR : Parcelable.Creator<shipping_address> {
        override fun createFromParcel(parcel: Parcel): shipping_address {
            return shipping_address(parcel)
        }

        override fun newArray(size: Int): Array<shipping_address?> {
            return arrayOfNulls(size)
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(first_name)
        parcel.writeString(last_name)
        parcel.writeString(company)
        parcel.writeString(address_1)
        parcel.writeString(address_2)
        parcel.writeString(city)
        parcel.writeString(state)
        parcel.writeString(postcode)
        parcel.writeString(country)
    }

    constructor(parcel: Parcel) : this(
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!
    )

    class Deserializer : ResponseDeserializable<shipping_address> {
        override fun deserialize(reader: Reader) = Gson().fromJson(reader, shipping_address::class.java)!!
    }

    class ListDeserializer : ResponseDeserializable<List<shipping_address>> {

        override fun deserialize(reader: Reader): List<shipping_address> {
            val type = object : TypeToken<List<shipping_address>>() {}.type
            return Gson().fromJson(reader, type)
        }
    }

    override fun describeContents(): Int {
        return 0
    }
}


data class customer(val id: Int = 0,
                    val email: String = "",
                    val first_name: String = "",
                    val last_name: String = "",
                    val username: String = "",
                    val password: String = "",
                    val billing_address: billing_address = billing_address(),
                    val shipping_address: shipping_address = shipping_address()) {

    class Deserializer : ResponseDeserializable<customer> {
        override fun deserialize(reader: Reader) = Gson().fromJson(reader, customer::class.java)!!
    }
}

data class Account(val id: Int = 0,
                    val email: String = "",
                    val first_name: String = "",
                    val last_name: String = "",
                    val username: String = "",
                    var billing_address: billing_address = billing_address(),
                    var shipping_address: shipping_address = shipping_address()) {

    class Deserializer : ResponseDeserializable<Account> {
        override fun deserialize(reader: Reader) = Gson().fromJson(reader, Account::class.java)!!
    }
}

data class Image(val src: String) : Parcelable {

    companion object CREATOR : Parcelable.Creator<Image> {
        override fun createFromParcel(parcel: Parcel): Image {
            return Image(parcel)
        }

        override fun newArray(size: Int): Array<Image?> {
            return arrayOfNulls(size)
        }
    }

    constructor(parcel: Parcel) : this(
            parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(src)
    }

    override fun describeContents(): Int {
        return 0
    }

    class Deserializer : ResponseDeserializable<Image> {
        override fun deserialize(reader: Reader) = Gson().fromJson(reader, Image::class.java)!!
    }

    class ListDeserializer : ResponseDeserializable<List<Image>> {

        override fun deserialize(reader: Reader): List<Image> {
            val type = object : TypeToken<List<Image>>() {}.type
            return Gson().fromJson(reader, type)
        }
    }

}

data class Product(
        val id: Int = 0,
        val name: String = "",
        val price: String = "",
        val sku: String = "",
        val description: String = "",
        val images: ArrayList<Image>
) : Parcelable {

    companion object CREATOR : Parcelable.Creator<Product> {
        override fun createFromParcel(parcel: Parcel): Product {
            return Product(parcel)
        }

        override fun newArray(size: Int): Array<Product?> {
            return arrayOfNulls(size)
        }
    }

    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!,
            arrayListOf<Image>().apply {
                parcel.readArrayList(Image::class.java.classLoader)
            }
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeString(price)
        parcel.writeString(description)
        parcel.writeArray(arrayOf(images))
    }

    override fun describeContents(): Int {
        return 0
    }

    class Deserializer : ResponseDeserializable<Product> {
        override fun deserialize(reader: Reader) = Gson().fromJson(reader, Product::class.java)!!
    }

    class ListDeserializer : ResponseDeserializable<List<Product>> {

        override fun deserialize(reader: Reader): List<Product> {
            val type = object : TypeToken<List<Product>>() {}.type
            return Gson().fromJson(reader, type)
        }
    }

}

//data class Account(val id: Int = 0
//                   , val phone: String = ""
//) {
//    class Deserializer : ResponseDeserializable<Account> {
//        override fun deserialize(reader: Reader) = Gson().fromJson(reader, Account::class.java)!!
//    }
//}

data class Version(val versionName: String = ""
                   , val isForce: Int = 0,
                   val changes: String = "",
                   val updateUrl: String = ""
) {
    class Deserializer : ResponseDeserializable<Version> {
        override fun deserialize(reader: Reader) = Gson().fromJson(reader, Version::class.java)!!
    }
}

data class Comment(
        val reviewer: String = "",
        val review: String = "",
        val date_created: String = "",
        val rating: Float = 5.0f
) : Parcelable {

    class Deserializer : ResponseDeserializable<Comment> {
        override fun deserialize(reader: Reader) = Gson().fromJson(reader, Comment::class.java)!!
    }

    class ListDeserializer : ResponseDeserializable<List<Comment>> {

        override fun deserialize(reader: Reader): List<Comment> {
            val type = object : TypeToken<List<Comment>>() {}.type
            return Gson().fromJson(reader, type)
        }
    }

    constructor(parcel: Parcel) : this(
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readFloat())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(reviewer)
        parcel.writeString(review)
        parcel.writeString(date_created)
        parcel.writeFloat(rating)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Comment> {
        override fun createFromParcel(parcel: Parcel): Comment {
            return Comment(parcel)
        }

        override fun newArray(size: Int): Array<Comment?> {
            return arrayOfNulls(size)
        }
    }
}

data class MakeCarts(val carts: ArrayList<Cart>) : Parcelable {

    class Deserializer : ResponseDeserializable<MakeCarts> {
        override fun deserialize(reader: Reader) = Gson().fromJson(reader, MakeCarts::class.java)!!
    }

    class ListDeserializer : ResponseDeserializable<List<MakeCarts>> {

        override fun deserialize(reader: Reader): List<MakeCarts> {
            val type = object : TypeToken<List<MakeCarts>>() {}.type
            return Gson().fromJson(reader, type)
        }
    }

    constructor(parcel: Parcel) : this(
            arrayListOf<Cart>().apply {
                parcel.readArrayList(Cart::class.java.classLoader)
            })

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeArray(arrayOf(carts))
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MakeCarts> {
        override fun createFromParcel(parcel: Parcel): MakeCarts {
            return MakeCarts(parcel)
        }

        override fun newArray(size: Int): Array<MakeCarts?> {
            return arrayOfNulls(size)
        }
    }
}

data class Cart(
        val id: Int = 0,
        val product_id: Int = 0,
        val product_name: String = "",
        val product_image: String = "",
        val product_price: Int = 0,
        var quantity: Int = 0
) : Parcelable {

    class Deserializer : ResponseDeserializable<Cart> {
        override fun deserialize(reader: Reader) = Gson().fromJson(reader, Cart::class.java)!!
    }

    class ListDeserializer : ResponseDeserializable<List<Cart>> {

        override fun deserialize(reader: Reader): List<Cart> {
            val type = object : TypeToken<List<Cart>>() {}.type
            return Gson().fromJson(reader, type)
        }
    }

    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readInt(),
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readInt(),
            parcel.readInt())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeInt(product_id)
        parcel.writeString(product_name)
        parcel.writeString(product_image)
        parcel.writeInt(product_price)
        parcel.writeInt(quantity)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Cart> {
        override fun createFromParcel(parcel: Parcel): Cart {
            return Cart(parcel)
        }

        override fun newArray(size: Int): Array<Cart?> {
            return arrayOfNulls(size)
        }
    }
}


data class Address(
        val id: Int = 0,
        var address: String = "",
        var isSelected: Boolean = false
) : Parcelable {

    class Deserializer : ResponseDeserializable<Address> {
        override fun deserialize(reader: Reader) = Gson().fromJson(reader, Address::class.java)!!
    }

    class ListDeserializer : ResponseDeserializable<List<Address>> {

        override fun deserialize(reader: Reader): List<Address> {
            val type = object : TypeToken<List<Address>>() {}.type
            return Gson().fromJson(reader, type)
        }
    }

    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readString()!!)

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(address)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Address> {
        override fun createFromParcel(parcel: Parcel): Address {
            return Address(parcel)
        }

        override fun newArray(size: Int): Array<Address?> {
            return arrayOfNulls(size)
        }
    }
}

data class Card(
        val id: Int = 0,
        val image: String = "",
        var isSelected: Boolean = false
) : Parcelable {

    class Deserializer : ResponseDeserializable<Card> {
        override fun deserialize(reader: Reader) = Gson().fromJson(reader, Card::class.java)!!
    }

    class ListDeserializer : ResponseDeserializable<List<Card>> {

        override fun deserialize(reader: Reader): List<Card> {
            val type = object : TypeToken<List<Card>>() {}.type
            return Gson().fromJson(reader, type)
        }
    }

    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readString()!!)

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(image)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Card> {
        override fun createFromParcel(parcel: Parcel): Card {
            return Card(parcel)
        }

        override fun newArray(size: Int): Array<Card?> {
            return arrayOfNulls(size)
        }
    }
}


data class GarbageOrder(
        val id: Int = 0,
        val transCode: String = "",
        val dateCreate: String = "",
        val address: String = "",
        var cardPoster: String = "",
        val products: String = "",
        var amount: Int = 0,
        var status: Int = 0,
        var isSelected: Boolean = false
) : Parcelable {

    class Deserializer : ResponseDeserializable<GarbageOrder> {
        override fun deserialize(reader: Reader) = Gson().fromJson(reader, GarbageOrder::class.java)!!
    }

    class ListDeserializer : ResponseDeserializable<List<GarbageOrder>> {

        override fun deserialize(reader: Reader): List<GarbageOrder> {
            val type = object : TypeToken<List<GarbageOrder>>() {}.type
            return Gson().fromJson(reader, type)
        }
    }

    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readInt(),
            parcel.readInt())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(transCode)
        parcel.writeString(dateCreate)
        parcel.writeString(address)
        parcel.writeString(cardPoster)
        parcel.writeString(products)
        parcel.writeInt(amount)
        parcel.writeInt(status)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<GarbageOrder> {
        override fun createFromParcel(parcel: Parcel): GarbageOrder {
            return GarbageOrder(parcel)
        }

        override fun newArray(size: Int): Array<GarbageOrder?> {
            return arrayOfNulls(size)
        }
    }
}


data class Supporter(
        val id: Int = 0,
        var sex: Int = 0,
        val name: String = "",
        val image: String = "",
        val responsibility: String = "",
        val phone: String = "",
        var textColor: String = ""
) : Parcelable {

    class Deserializer : ResponseDeserializable<Supporter> {
        override fun deserialize(reader: Reader) = Gson().fromJson(reader, Supporter::class.java)!!
    }

    class ListDeserializer : ResponseDeserializable<List<Supporter>> {

        override fun deserialize(reader: Reader): List<Supporter> {
            val type = object : TypeToken<List<Supporter>>() {}.type
            return Gson().fromJson(reader, type)
        }
    }

    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readInt(),
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!)

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeInt(sex)
        parcel.writeString(name)
        parcel.writeString(image)
        parcel.writeString(responsibility)
        parcel.writeString(phone)
        parcel.writeString(textColor)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Supporter> {
        override fun createFromParcel(parcel: Parcel): Supporter {
            return Supporter(parcel)
        }

        override fun newArray(size: Int): Array<Supporter?> {
            return arrayOfNulls(size)
        }
    }
}

data class MakeOrder(val order: order)

data class payment_details(val method_id: String = "bacs",
                           val method_title: String = "Direct Bank Transfer",
                           val paid: Boolean = true) {
    class Deserializer : ResponseDeserializable<payment_details> {
        override fun deserialize(reader: Reader) = Gson().fromJson(reader, payment_details::class.java)!!
    }

    class ListDeserializer : ResponseDeserializable<List<payment_details>> {

        override fun deserialize(reader: Reader): List<payment_details> {
            val type = object : TypeToken<List<payment_details>>() {}.type
            return Gson().fromJson(reader, type)
        }
    }
}

data class line_items(val product_id: Int = 0,
                      val quantity: Int = 0,
                      val name: String = "",
                      val total: Int = 0,
                      val price: Int = 0) {
    class Deserializer : ResponseDeserializable<line_items> {
        override fun deserialize(reader: Reader) = Gson().fromJson(reader, line_items::class.java)!!
    }

    class ListDeserializer : ResponseDeserializable<List<line_items>> {

        override fun deserialize(reader: Reader): List<line_items> {
            val type = object : TypeToken<List<line_items>>() {}.type
            return Gson().fromJson(reader, type)
        }
    }
}


data class shipping_lines(val method_id: String = "flat_rate",
                          val method_title: String = "Flat Rate",
                          val total: Int = 10) {
    class Deserializer : ResponseDeserializable<shipping_lines> {
        override fun deserialize(reader: Reader) = Gson().fromJson(reader, shipping_lines::class.java)!!
    }

    class ListDeserializer : ResponseDeserializable<List<shipping_lines>> {

        override fun deserialize(reader: Reader): List<shipping_lines> {
            val type = object : TypeToken<List<shipping_lines>>() {}.type
            return Gson().fromJson(reader, type)
        }
    }
}

data class order(var payment_details: payment_details = payment_details(),
                 var billing_address: billing_address = billing_address(),
                 var shipping_address: shipping_address = shipping_address(),
                 val customer_id: Int = 0,
                 val line_items: ArrayList<line_items> = ArrayList(),
                 val shipping_lines: ArrayList<shipping_lines> = ArrayList()) {

    class Deserializer : ResponseDeserializable<shipping_address> {
        override fun deserialize(reader: Reader) = Gson().fromJson(reader, shipping_address::class.java)!!
    }

    class ListDeserializer : ResponseDeserializable<List<shipping_address>> {

        override fun deserialize(reader: Reader): List<shipping_address> {
            val type = object : TypeToken<List<shipping_address>>() {}.type
            return Gson().fromJson(reader, type)
        }
    }
}

data class shipping(val address_1: String = "")

data class OrderToShow(val id: Int = 0,
                       val status: String = "",
                       val date_created: String = "",
                       val total: Int = 0,
                       val shipping: shipping = shipping(),
                       val line_items: ArrayList<line_items>,
                       var isSelected: Boolean = false) {

    class Deserializer : ResponseDeserializable<OrderToShow> {
        override fun deserialize(reader: Reader) = Gson().fromJson(reader, OrderToShow::class.java)!!
    }

    class ListDeserializer : ResponseDeserializable<List<OrderToShow>> {

        override fun deserialize(reader: Reader): List<OrderToShow> {
            val type = object : TypeToken<List<OrderToShow>>() {}.type
            return Gson().fromJson(reader, type)
        }
    }

}