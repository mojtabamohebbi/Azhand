package ir.elevin.azhand

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.fuel.livedata.liveDataObject
import com.github.kittinunf.result.failure
import com.github.kittinunf.result.success
import ir.elevin.azhand.adapters.AddressAdapter
import ir.elevin.azhand.adapters.CardAdapter
import kotlinx.android.synthetic.main.activity_choose_address.*

class ChooseAddressActivity : CustomActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_address)

        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        title = "تکمیل خرید"

        addressRecyclerView.layoutManager = LinearLayoutManager(this)
        cardRecyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)

        getAddresses()
    }

    private fun getAddresses(){
        webserviceUrl.httpPost(listOf("func" to "get_addresses", "uid" to account.id))
                .liveDataObject(Address.ListDeserializer()).observeForever { it ->
                    Log.d("regwegewg", it.toString())
                    it.success {
                        if (it.isNotEmpty()){
                            addressRecyclerView.adapter = AddressAdapter(this, it)
                            addressRecyclerView.scheduleLayoutAnimation()
                            addressRecyclerView.visibility = View.VISIBLE
                            addAddressButton.visibility = View.VISIBLE
                            addressProgressBar.visibility = View.GONE
                            addFirstDesTv.visibility = View.GONE
                            addFirstAddressButton.visibility = View.GONE
                        }else{
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

    private fun getCards(){
        webserviceUrl.httpPost(listOf("func" to "get_cards", "uid" to account.id))
                .liveDataObject(Card.ListDeserializer()).observeForever { it ->
                    Log.d("regwegewg22", it.toString())
                    it.success {
                        cardRecyclerView.adapter = CardAdapter(this, it)
                        cardRecyclerView.visibility = View.VISIBLE
                        cardsProgressBar.visibility = View.GONE
                    }
                    it.failure {
                        getCards()
                    }
                }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}
