package ir.elevin.azhand

import android.os.Parcel
import android.os.Parcelable
import androidx.fragment.app.Fragment
import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.Reader

data class TabModel(val title: String, val fragment: Fragment)

data class Product(
        val id: Int = 0,
        val name: String = "",
        val image: String = "",
        val price: String = "",
        val maintenance: String = "",
        val description: String = "",
        val potImage: String = "",
        val macroImage: String = "",
        val catId: String = ""
) : Parcelable {

    class Deserializer : ResponseDeserializable<Product> {
        override fun deserialize(reader: Reader) = Gson().fromJson(reader, Product::class.java)!!
    }

    class ListDeserializer : ResponseDeserializable<List<Product>> {

        override fun deserialize(reader: Reader): List<Product> {
            val type = object : TypeToken<List<Product>>() {}.type
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
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!)

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeString(image)
        parcel.writeString(price)
        parcel.writeString(maintenance)
        parcel.writeString(description)
        parcel.writeString(potImage)
        parcel.writeString(macroImage)
        parcel.writeString(catId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Product> {
        override fun createFromParcel(parcel: Parcel): Product {
            return Product(parcel)
        }

        override fun newArray(size: Int): Array<Product?> {
            return arrayOfNulls(size)
        }
    }
}


data class Account(val id: Int = 0
                   ,val name: String = ""
                   ,val phone: String = ""
                   ,val address: String = ""
) {
    class Deserializer : ResponseDeserializable<Account> {
        override fun deserialize(reader: Reader) = Gson().fromJson(reader, Account::class.java)!!
    }
}


data class Comment(
        val name: String = "",
        val comment: String = "",
        val dateCreate: String = "",
        val rate: Float = 5.0f
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
        parcel.writeString(name)
        parcel.writeString(comment)
        parcel.writeString(dateCreate)
        parcel.writeFloat(rate)
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

data class Cart(
        val id: Int = 0,
        val pid: Int = 0,
        val name: String = "",
        val image: String = "",
        val price: Int = 0,
        var totalPrice: Int = 0,
        var num: Int = 0
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
            parcel.readInt(),
            parcel.readInt())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeInt(pid)
        parcel.writeString(name)
        parcel.writeString(image)
        parcel.writeInt(price)
        parcel.writeInt(totalPrice)
        parcel.writeInt(num)
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
        val address: String = "",
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
