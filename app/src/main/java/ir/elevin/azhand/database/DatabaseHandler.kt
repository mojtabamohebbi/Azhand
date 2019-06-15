package ir.elevin.azhand.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import ir.elevin.azhand.Account
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

    fun getAccount(): Account {
        var data = Account()
        database.select("account", "id", "name", "phone", "address")
                .orderBy("id", SqlOrderDirection.DESC)
                .limit(1)
                .exec {
                    this.moveToFirst()
                    if (count > 0){
                        data = Account(getInt(0), getString(1), getString(2), getString(3))
                    }
        }
        return data
    }

    fun deleteAccount(){
        database.delete("account")
    }

    fun insertAccounts(id: Int, name: String, phone: String, address: String){
        val values = ContentValues()
        values.put("id", id)
        values.put("name", name)
        values.put("phone", phone)
        values.put("address", address)
        database.insert("account", null, values)
    }

}