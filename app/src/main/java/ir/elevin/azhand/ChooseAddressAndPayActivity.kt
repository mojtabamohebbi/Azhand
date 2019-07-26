package ir.elevin.azhand

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.fuel.livedata.liveDataObject
import com.github.kittinunf.fuel.livedata.liveDataResponse
import com.github.kittinunf.result.failure
import com.github.kittinunf.result.success
import com.squareup.picasso.Picasso
import com.zarinpal.ewallets.purchase.OnCallbackRequestPaymentListener
import com.zarinpal.ewallets.purchase.PaymentRequest
import com.zarinpal.ewallets.purchase.ZarinPal
import kotlinx.android.synthetic.main.activity_choose_address.*
import kotlinx.android.synthetic.main.address_row.view.*
import kotlinx.android.synthetic.main.card_row.view.*
import kotlinx.android.synthetic.main.dialog_add_address.*
import libs.mjn.prettydialog.PrettyDialog
import java.util.*
import kotlin.collections.ArrayList

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class ChooseAddressAndPayActivity : CustomActivity(){

    lateinit var products: String
    var cardId: Int = 0
    var amount: Int = 0
    var address = ""

    private var addresses = ArrayList<Address>()
    private var addressAdapter: AddressAdapter = AddressAdapter(this)

    private var cardArray = ArrayList<Card>()

    @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_address)

        products = intent.extras.getString("products")
        amount = intent.extras.getInt("amount")

        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        title = "انتخاب آدرس"

        addressRecyclerView.layoutManager = LinearLayoutManager(this)
        cardRecyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        addressRecyclerView.adapter = addressAdapter

        addAddressButton.setOnClickListener {
            addOrEditAddressDialog(false)
        }

        addFirstAddressButton.setOnClickListener {
            addOrEditAddressDialog(false)
        }

        payButton.setOnClickListener {
            insertOrder()
        }

        if (intent.data != null) {
            ZarinPal.getPurchase(this).verificationPayment(intent.data) {
                isPaymentSuccess, refID, paymentRequest -> Log.i("TAG", "onCallbackResultVerificationPayment: $refID")
            }
        }

        payButton.setOnClickListener {
            val payment: PaymentRequest = ZarinPal.getPaymentRequest()

            payment.merchantID = "43c2d9fa-990a-11e9-aaf8-000c29344814"
            payment.amount = 100
            payment.description = "پرداخت"
            payment.setCallbackURL("app://app")
            payment.mobile = account.billing_address.phone
            payment.email = account.email


            ZarinPal.getPurchase(applicationContext).startPayment(payment, object : OnCallbackRequestPaymentListener {
                override fun onCallbackResultPaymentRequest(status: Int, authority: String, paymentGatewayUri: Uri, intent: Intent) {

                    startActivity(intent)
                }
            })
        }

        getAddresses()
    }

    private fun insertOrder(){
        if (address.isEmpty()){
            Toast.makeText(this, "لطفا آدرس محل تحویل را وارد نمایید", Toast.LENGTH_LONG).show()
        }else{
            webserviceUrl.httpPost(listOf("func" to "insert_order",
                    "uid" to account.id, "products" to products,
                    "amount" to amount, "cardId" to cardId, "address" to address, "transcode" to "3423892")).liveDataResponse().observeForever {
                Log.d("WEGweg", it.first.data.toString(Charsets.UTF_8))
                if (it.first.data.toString(Charsets.UTF_8) == "1"){
                    Toast.makeText(this, "با موفقیت ثبت شد", Toast.LENGTH_LONG).show()
                }else{
                    val dd = PrettyDialog(this)
                    dd.setTitle(this.getString(R.string.error))
                            .setIcon(R.drawable.error)
                            .setMessage(this.getString(R.string.network_error))
                            .addButton(this.getString(R.string.try_again), R.color.colorWhite, R.color.colorRed) {
                                dd.dismiss()
                                insertOrder()
                            }
                            .show()
                }
            }
        }
    }

    private fun getAddresses(){
        webserviceUrl.httpPost(listOf("func" to "get_addresses", "uid" to account.id))
                .liveDataObject(Address.ListDeserializer()).observeForever { it ->
                    Log.d("regwegewg", it.toString())
                    it.success {
                        if (it.isNotEmpty()){
                            it[0].isSelected = true
                            address = it[0].address
                            it.toCollection(addresses)
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
//                        getCards()
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

//    private fun getCards(){
//        webserviceUrl.httpPost(listOf("func" to "get_cards", "uid" to account.id))
//                .liveDataObject(Card.ListDeserializer()).observeForever { it ->
//                    Log.d("regwegewg22", it.toString())
//                    it.success {
//                        if (it.isNotEmpty()){
//                            it[0].isSelected = true
//                            cardId = it[0].id
//                        }
//                        cardArray = it.toCollection(cardArray)
//                        cardRecyclerView.adapter = CardAdapter(this)
//                        cardRecyclerView.visibility = View.VISIBLE
//                        cardsProgressBar.visibility = View.GONE
//                    }
//                    it.failure {
//                        getCards()
//                    }
//                }
//    }

    fun addOrEditAddressDialog(isEdit: Boolean, lastAddress: String = "", addressId: Int = 0, position: Int = 0){
        val d = Dialog(this)
        d.requestWindowFeature(Window.FEATURE_NO_TITLE)
        d.setContentView(R.layout.dialog_add_address)
        d.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        d.headerTitleTv.text = if (isEdit) "ویرایش آدرس" else "ثبت آدرس جدید"
        if (isEdit){
            d.addressEt.setText(lastAddress)
            d.addButton.text = "ویرایش"
        }
        
        d.addButton.setOnClickListener {
            val address = d.addressEt.text.toString()
            if (address.length < 10){
                Toast.makeText(this, "لطفا آدرس کامل را وارد نمایید", Toast.LENGTH_LONG).show()
            }else{
                d.addAddressProgressBar.visibility = View.VISIBLE
                d.addButton.isEnabled = false
                webserviceUrl.httpPost(listOf("func" to if(isEdit) "edit_address" else "add_address", "uid" to account.id, "address" to address, "aid" to addressId)).liveDataResponse().observeForever {
                    Log.d("WEGweg", it.first.data.toString(Charsets.UTF_8))
                    if (it.first.data.toString(Charsets.UTF_8) == "1"){
                        for (i in addresses){
                            i.isSelected = false
                        }
                        if (!isEdit){
                            addresses.add(0, Address(id = 0, address = address, isSelected = true))
                            addresses[0].isSelected = true
                            this.address = address
                        }else{
                            addresses[position].address = address
                            addresses[position].isSelected = true
                            this.address = address
                        }
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
    
    inner class CardAdapter(private val activity: FragmentActivity) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
            return CardHolder(LayoutInflater.from(activity).inflate(R.layout.card_row, p0, false))
        }

        override fun getItemCount(): Int {
            return cardArray.size
        }

        @SuppressLint("SetTextI18n", "RestrictedApi", "CheckResult")
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val data = cardArray[position]

            Picasso.get().load(data.image).into((holder as CardHolder).imageView)

            if (data.isSelected){
                holder.maskView.visibility = View.VISIBLE
                holder.checkedIv.visibility = View.VISIBLE
            }else{
                holder.maskView.visibility = View.GONE
                holder.checkedIv.visibility = View.GONE
            }

            holder.itemView.setOnClickListener {
                for (i in cardArray){
                    i.isSelected = false
                }
                cardId = data.id
                cardArray[position].isSelected = true
                notifyDataSetChanged()
            }
        }
    }

    private class CardHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView = view.iv!!
        val checkedIv = view.checkedIv!!
        val maskView = view.selectMaskView!!
    }

    inner class AddressAdapter(private val activity: FragmentActivity) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
            return AddressHolder(LayoutInflater.from(activity).inflate(R.layout.address_row, p0, false))
        }

        override fun getItemCount(): Int {
            return addresses.size
        }

        @SuppressLint("SetTextI18n", "RestrictedApi", "CheckResult")
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val data = addresses[position]
            (holder as AddressHolder).addressTv.text = data.address

            if (data.isSelected){
                holder.selectedIv.visibility = View.VISIBLE
                holder.addressTv.setTextColor(AppCompatResources.getColorStateList(activity, R.color.colorGreen))
            }else{
                holder.selectedIv.visibility = View.INVISIBLE
                holder.addressTv.setTextColor(AppCompatResources.getColorStateList(activity, R.color.colorDisableText))
            }

            holder.itemView.setOnClickListener {
                for (i in addresses){
                    i.isSelected = false
                }
                address = data.address
                addresses[position].isSelected = true
                notifyDataSetChanged()
            }

            holder.editButton.setOnClickListener {
                addOrEditAddressDialog(true, data.address, data.id, position)
            }
            holder.deleteButton.setOnClickListener {
                deleteAddress(data.id, position)
            }
        }

        private fun deleteAddress(addressId: Int, position: Int){
            val progressBar = progressDialog(this@ChooseAddressAndPayActivity)
            webserviceUrl.httpPost(listOf("func" to "delete_address", "uid" to account.id, "aid" to addressId)).liveDataResponse().observeForever {
                if (it.first.data.toString(Charsets.UTF_8) == "1"){
                    address = ""
                    progressBar.dismiss()
                    addresses.removeAt(position)
                    notifyDataSetChanged()
                    Toast.makeText(this@ChooseAddressAndPayActivity, "با موفقیت حذف شد", Toast.LENGTH_LONG).show()
                }else{
                    progressBar.dismiss()
                    Toast.makeText(this@ChooseAddressAndPayActivity, "عملیات ناموفق لطفا دوباره تلاش کنید", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private class AddressHolder(view: View) : RecyclerView.ViewHolder(view) {
        val addressTv = view.addressTv!!
        val deleteButton = view.deleteButton!!
        val editButton = view.editButton!!
        val selectedIv = view.selectedIv!!
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}
