package ir.elevin.azhand

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.fuel.livedata.liveDataObject
import com.github.kittinunf.result.failure
import com.github.kittinunf.result.success
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_cart.*
import kotlinx.android.synthetic.main.cart_row.view.*
import kotlinx.android.synthetic.main.recycler_fragment.progressBar
import kotlinx.android.synthetic.main.recycler_fragment.recyclerview
import kotlinx.android.synthetic.main.recycler_fragment.swipeRefreshLayout
import libs.mjn.prettydialog.PrettyDialog
import java.util.*
import kotlin.collections.ArrayList
import com.google.gson.JsonObject
import com.google.gson.JsonArray
import com.google.gson.Gson

class CartActivity : CustomActivity(){

    private var totalPRICE = 0

    private var productsArray = ArrayList<Cart>()
    private var adapter: CartAdapter? = null

    private lateinit var layoutManager: LinearLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        setSupportActionBar(cartToolbar)
        title = "سبد خرید"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        layoutManager = LinearLayoutManager(this)
        recyclerview.setHasFixedSize(false)

        adapter = CartAdapter(this)
        recyclerview.adapter = adapter
        recyclerview.layoutManager = layoutManager

        swipeRefreshLayout.setOnRefreshListener {
            getData()
        }

        payBar.translationY = 200f
        topColorView.translationY = 400f
        swipeRefreshLayout.isRefreshing = true
        getData()

        topLayout.postDelayed({
            topLayout.visibility = View.VISIBLE
            topColorView.animate().alpha(1f).duration = 700
            YoYo.with(Techniques.SlideInRight).duration(1000).playOn(topLayout)
        }, 500)

        payBar.setOnClickListener {
            val intent = Intent(this, ChooseAddressActivity::class.java)
            val json = Gson().toJson(productsArray)
            intent.putExtra("products", json)
            intent.putExtra("amount", totalPRICE)
            startActivity(intent)
        }
    }
    
    @SuppressLint("SetTextI18n")
    private fun setTotalPrice(){
        totalPriceTv.text = "جمع: "+decimalFormatCommafy("$totalPRICE")
    }

    inner class CartAdapter(private val activity: FragmentActivity) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
        
        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
            return CartHolder(LayoutInflater.from(activity).inflate(R.layout.cart_row, p0, false))
        }

        override fun getItemCount(): Int {
            return productsArray.size
        }

        @SuppressLint("SetTextI18n", "RestrictedApi")
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val data = productsArray[position]
            (holder as CartHolder).nameTv.text = data.name
            holder.basePriceTv.text = "قیمت واحد: ${decimalFormatCommafy("${data.price}")}"

            data.totalPrice = data.price*data.num
            holder.totalPriceTv.text = "مبلغ کل: ${decimalFormatCommafy("${data.totalPrice}")}"

            holder.numProductTv.text = "${data.num}"

            Picasso.get()
                    .load(data.image)
                    .placeholder(R.drawable.no_image)
                    .error(R.drawable.no_image)
                    .into(holder.productIv)

            holder.productIv.setOnClickListener {

            }

            holder.deleteButton.setOnClickListener {

            }

            holder.upNumButton.setOnClickListener {
                YoYo.with(Techniques.BounceInDown).duration(300).playOn(holder.numProductTv)
                YoYo.with(Techniques.FadeIn).duration(300).playOn(holder.upNumButton)
                data.num += 1
                holder.numProductTv.text = "${data.num}"
                totalPRICE += data.price
                setTotalPrice()
            }

            holder.downNumButton.setOnClickListener {
                YoYo.with(Techniques.BounceInUp).duration(300).playOn(holder.numProductTv)
                YoYo.with(Techniques.FadeIn).duration(300).playOn(holder.downNumButton)
                if (data.num > 1){
                    data.num -= 1
                    totalPRICE -= data.price
                    setTotalPrice()
                }
                holder.numProductTv.text = "${data.num}"
            }
        }

    }

    class CartHolder(view: View) : RecyclerView.ViewHolder(view) {
        val productIv = view.productIv!!
        val nameTv = view.nameTv!!
        val basePriceTv = view.basePriceTv!!
        val totalPriceTv = view.totalPriceTv!!
        val upNumButton = view.upNumButton!!
        val downNumButton = view.downNumButton!!
        val numProductTv = view.numProductTv!!
        val deleteButton = view.deleteButton!!
    }

    private fun getData(){
        totalPRICE = 0
        val params: List<Pair<String, Any?>> = listOf("func" to "get_cart", "uid" to account.id)
        webserviceUrl
                .httpPost(params)
                .liveDataObject(Cart.ListDeserializer())
                .observeForever { it ->
                    it?.success {
                        swipeRefreshLayout.isRefreshing = false
                        progressBar.visibility = View.INVISIBLE
                        if (it.isNotEmpty()){
                            productsArray + it.toCollection(productsArray)
                            adapter?.notifyDataSetChanged()
                            recyclerview.scheduleLayoutAnimation()
                            for (i in productsArray){
                                totalPRICE += i.price*i.num
                            }
                            setTotalPrice()
                            payBar.animate().translationY(0f).duration = 600
                            topColorView.animate().translationY(0f).duration = 900
                            val timer = Timer()
                            timer.schedule(object: TimerTask() {
                                override fun run() {
                                    runOnUiThread {
                                        YoYo.with(Techniques.ZoomInLeft).duration(300).playOn(nextIv)
                                    }
                                }
                            }, 1000, 500)
                            notFoundTv.visibility = View.GONE
                        }else{
                            notFoundTv.visibility = View.VISIBLE
                        }
                    }
                    it?.failure {
                        Log.d("errorFound", it.message)
                        progressBar.visibility = View.INVISIBLE
                        swipeRefreshLayout.isRefreshing = false
                        val d = PrettyDialog(this)
                        d.setTitle(getString(R.string.error))
                                .setIcon(R.drawable.error)
                                .setMessage(getString(R.string.network_error))
                                .addButton(getString(R.string.try_again), R.color.colorWhite, R.color.colorRed) {
                                    d.dismiss()
                                    getData()
                                }
                                .show()
                    }
                }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
