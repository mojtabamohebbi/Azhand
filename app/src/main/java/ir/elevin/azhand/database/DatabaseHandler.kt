package ir.elevin.azhand.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import ir.elevin.azhand.Address
import ir.elevin.azhand.FinalOrder

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

    fun getFinalOrder(): String {
        var final = ""
        database.select("finalOrder", "ord")
                .exec {
                    this.moveToFirst()
                    if (count > 0){
                        final = this.getString(0)
                    }
        }
        return final
    }
//
    fun deleteAddresses(){
        database.delete("addresses")
    }
//
    fun insertFinalOrder(orders: String){
    database.execSQL("delete from finalOrder")
        val values = ContentValues()
        values.put("ord", orders)
        database.insert("finalOrder", null, values)
    }

    fun deleteAddress(id: Int){
        database.delete("addresses", "id = $id")
    }

    fun addAddressToFinalOrder(address: String){

        var final = ""
        database.select("finalOrder", "ord")
                .exec {
                    this.moveToFirst()
                    if (count > 0){
                        final = this.getString(0)
                    }
                }

        final = final.replace("\"address_1\":\"\"", "\"address_1\":\""+address+"\"", true)
        Log.d("wegewgewgeggwegwegewgeg", final)
        database.execSQL("update finalOrder set ord = '$final'")
    }

}