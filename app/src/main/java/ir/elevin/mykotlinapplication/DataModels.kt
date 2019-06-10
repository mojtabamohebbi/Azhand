package ir.elevin.mykotlinapplication

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

