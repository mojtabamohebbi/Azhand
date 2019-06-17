package ir.elevin.azhand

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.fuel.livedata.liveDataObject
import com.github.kittinunf.fuel.livedata.liveDataResponse
import com.github.kittinunf.result.failure
import com.github.kittinunf.result.success
import ir.elevin.azhand.adapters.AddressAdapter
import ir.elevin.azhand.adapters.CardAdapter
import kotlinx.android.synthetic.main.activity_choose_address.*
import kotlinx.android.synthetic.main.dialog_add_address.*
import libs.mjn.prettydialog.PrettyDialog
import java.util.*
import kotlin.collections.ArrayList

class ChooseAddressActivity : CustomActivity() {

    private val addresses = ArrayList<Address>()
    private var addressAdapter: AddressAdapter = AddressAdapter(this, addresses)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_address)

        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        title = "تکمیل خرید"

        addressRecyclerView.layoutManager = LinearLayoutManager(this)
        cardRecyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        addressRecyclerView.adapter = addressAdapter

        addAddressButton.setOnClickListener {
            addAddressDialog()
        }

        addFirstAddressButton.setOnClickListener {
            addAddressDialog()
        }

        getAddresses()
    }

    private fun getAddresses(){
        webserviceUrl.httpPost(listOf("func" to "get_addresses", "uid" to account.id))
                .liveDataObject(Address.ListDeserializer()).observeForever { it ->
                    Log.d("regwegewg", it.toString())
                    it.success {
                        if (it.isNotEmpty()){
                            it[0].isSelected = true
                            addresses += it
                            addressAdapter.notifyDataSetChanged()
                            addressRecyclerView.scheduleLayoutAnimation()
                            showAddressList()
                        }else{
                            colorLayoutPayButton.setBackgroundColor(AppCompatResources.getColorStateList(this, R.color.colorAccentLight).defaultColor)
                            addressRecyclerView.visibility = View.GONE
                            addAddressButton.visibility = View.GONE
                            addressProgressBar.visibility = View.GONE
                            addFirstDesTv.visibility = View.VISIBLE
                            addFirstAddressButton.visibility = View.VISIBLE
                        }
                        getCards()
                    }
                    it.failure {
                        Log.d("vwevwebeb", it.localizedMessage)
                        getAddresses()
                    }
                }
    }

    private fun showAddressList(){
        val timer = Timer()
        timer.schedule(object: TimerTask(){
            override fun run() {
                runOnUiThread {
                    YoYo.with(Techniques.Swing).duration(700).playOn(payButton)
                }
            }
        }, 1000, 4000)
        colorLayoutPayButton.setBackgroundColor(AppCompatResources.getColorStateList(this, R.color.colorAccent).defaultColor)
        addressRecyclerView.visibility = View.VISIBLE
        addAddressButton.visibility = View.VISIBLE
        addressProgressBar.visibility = View.GONE
        addFirstDesTv.visibility = View.GONE
        addFirstAddressButton.visibility = View.GONE
    }

    private fun getCards(){
        webserviceUrl.httpPost(listOf("func" to "get_cards", "uid" to account.id))
                .liveDataObject(Card.ListDeserializer()).observeForever { it ->
                    Log.d("regwegewg22", it.toString())
                    it.success {
                        if (it.isNotEmpty()){
                            it[0].isSelected = true
                        }
                        cardRecyclerView.adapter = CardAdapter(this, it)
                        cardRecyclerView.visibility = View.VISIBLE
                        cardsProgressBar.visibility = View.GONE
                    }
                    it.failure {
                        getCards()
                    }
                }
    }

    private fun addAddressDialog(){
        val d = Dialog(this)
        d.requestWindowFeature(Window.FEATURE_NO_TITLE)
        d.setContentView(R.layout.dialog_add_address)
        d.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        d.addButton.setOnClickListener {
            val address = d.addressEt.text.toString()
            if (address.length < 10){
                Toast.makeText(this, "لطفا آدرس کامل را وارد نمایید", Toast.LENGTH_LONG).show()
            }else{
                d.addAddressProgressBar.visibility = View.VISIBLE
                d.addButton.isEnabled = false
                webserviceUrl.httpPost(listOf("func" to "add_address", "uid" to account.id, "address" to address)).liveDataResponse().observeForever {
                    Log.d("WEGweg", it.first.data.toString(Charsets.UTF_8))
                    if (it.first.data.toString(Charsets.UTF_8) == "1"){
                        for (i in addresses){
                            i.isSelected = false
                        }
                        addresses.add(0, Address(id = 0, address = address, isSelected = true))
                        addressAdapter.notifyDataSetChanged()
                        showAddressList()
                        d.dismiss()
                        Toast.makeText(this, getString(R.string.success), Toast.LENGTH_LONG).show()
                    }else{
                        d.addAddressProgressBar.visibility = View.INVISIBLE
                        val dd = PrettyDialog(this)
                        dd.setTitle(this.getString(R.string.error))
                                .setIcon(R.drawable.error)
                                .setMessage(this.getString(R.string.network_error))
                                .addButton(this.getString(R.string.try_again), R.color.colorWhite, R.color.colorRed) {
                                    d.addButton.isEnabled = true
                                    d.addButton.performClick()
                                    dd.dismiss()
                                }
                                .show()
                    }
                }
            }
        }

        d.show()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}
