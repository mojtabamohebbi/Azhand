package ir.elevin.azhand

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.fuel.livedata.liveDataObject
import com.github.kittinunf.result.failure
import com.github.kittinunf.result.success
import ir.elevin.azhand.adapters.CartAdapter
import kotlinx.android.synthetic.main.activity_cart.*
import kotlinx.android.synthetic.main.recycler_fragment.progressBar
import kotlinx.android.synthetic.main.recycler_fragment.recyclerview
import kotlinx.android.synthetic.main.recycler_fragment.swipeRefreshLayout
import libs.mjn.prettydialog.PrettyDialog
import java.util.*
import kotlin.collections.ArrayList

var totalPRICE = 0

class CartActivity : CustomActivity(), CartAdapter.ChangeTotalPrice {

    private var array = ArrayList<Cart>()
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

        adapter = CartAdapter(this, array)
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
            startActivity(intent)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun setTotalPrice(){
        totalPriceTv.text = "جمع: "+decimalFormatCommafy("$totalPRICE")
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
                            array + it.toCollection(array)
                            adapter?.notifyDataSetChanged()
                            recyclerview.scheduleLayoutAnimation()
                            for (i in array){
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
