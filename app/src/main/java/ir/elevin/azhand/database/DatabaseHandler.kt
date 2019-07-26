package ir.elevin.azhand.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import ir.elevin.azhand.Address

import org.jetbrains.anko.db.SqlOrderDirection
import org.jetbrains.anko.db.delete
import org.jetbrains.anko.db.select


class DatabaseHandler(val context: Context){

    private val helper: DatabaseHelper = DatabaseHelper.getInstance(context)
    private var database: SQLiteDatabase = helper.writableDatabase

    fun close(){
        helper.close()
    }

//    fun getAccounts(): ArrayList<AccountShort>{
//        val data = ArrayList<AccountShort>()
//        database.select("accounts", "accountId", "title", "description", "poster").exec {
//            // Doing some stuff with emails
//            this.moveToFirst()
    //if count
//            for (i in 0 until this.count){
//                val account = AccountShort(getInt(0), getString(1), getString(2), getString(3))
//                data.add(account)
//                this.moveToNext()
//            }
//        }
//        return data
//    }

    fun getAddresses(): ArrayList<Address> {
        val addresses = ArrayList<Address>()
        database.select("addresses", "id, address")
                .orderBy("id", SqlOrderDirection.DESC)
                .exec {
                    this.moveToFirst()
                    if (count > 0){
                        addresses.add(Address(getInt(0), getString(1), false))
                    }
        }
        return addresses
    }
//
    fun deleteAddresses(){
        database.delete("addresses")
    }
//
    fun insertAddress(address: String){
        val values = ContentValues()
        values.put("address", address)
        database.insert("addresses", null, values)
    }

    fun deleteAddress(id: Int){
        database.delete("addresses", "id = $id")
    }

    fun editAddress(address: String, id: Int){
        database.execSQL("update addresses set address = '$address' where id = $id")
    }

}