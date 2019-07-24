package ir.elevin.azhand.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import org.jetbrains.anko.db.*

class DatabaseHelper private constructor(ctx: Context) : ManagedSQLiteOpenHelper(ctx, "AzhandStoreDatabase", null, 1) {
    init {
        instance = this
    }

    companion object {
        private var instance: DatabaseHelper? = null

        @Synchronized
        fun getInstance(ctx: Context) = instance ?: DatabaseHelper(ctx.applicationContext)
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Here you create tables
        db.createTable("account", true,
                "id" to INTEGER + PRIMARY_KEY + UNIQUE,
                "first_name" to TEXT,
                "last_name" to TEXT,
                "username" to TEXT,
                "billing_address" to TEXT,
                "shipping_address" to TEXT,
                "postcode" to TEXT,
                "token" to TEXT,
                "phone" to TEXT)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Here you can upgrade tables, as usual
//        db.dropTable("Demand", true)
    }
}

// Access property for Context
val Context.database: DatabaseHelper
    get() = DatabaseHelper.getInstance(this)