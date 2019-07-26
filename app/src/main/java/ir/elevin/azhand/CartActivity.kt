package ir.elevin.azhand

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.core.util.Pair
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.fuel.livedata.liveDataObject
import com.github.kittinunf.fuel.livedata.liveDataResponseString
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
import com.google.gson.Gson
import ir.mjmim.woocommercehelper.enums.RequestMethod
import ir.mjmim.woocommercehelper.enums.SigningMethod
import ir.mjmim.woocommercehelper.helpers.OAuthSigner
import ir.mjmim.woocommercehelper.main.WooBuilder
import kotlinx.android.synthetic.main.dialog_confirm.*

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
            getData(true)
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
            val intent = Intent(this, ChooseAddressAndPayActivity::class.java)
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
            (holder as CartHolder).nameTv.text = data.product_name
            holder.basePriceTv.text = "قیمت واحد: ${decimalFormatCommafy("${data.product_price}")}"

            holder.totalPriceTv.text = "مبلغ کل: ${decimalFormatCommafy("${data.quantity*data.product_price}")}"

            holder.numProductTv.text = "${data.quantity}"

            Picasso.get()
                    .load(data.product_image)
                    .placeholder(R.drawable.no_image)
                    .error(R.drawable.no_image)
                    .into(holder.productIv)

            holder.productIv.setOnClickListener {
                getProductData(data.product_id, holder.productIv, holder.progressBar)
            }

            holder.deleteButton.setOnClickListener {

                val d = Dialog(activity)
                d.requestWindowFeature(Window.FEATURE_NO_TITLE)
                d.setContentView(R.layout.dialog_confirm)
                d.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                d.messageTv.text = "نسبت به حذف این محصول از سبد خرید خود اطمینان دارید؟"

                d.yesButton.setOnClickListener {
                    deleteFromCart(data.product_id, position)
                    d.dismiss()
                }

                d.noButton.setOnClickListener {
                    d.dismiss()
                }

                d.show()

            }

            holder.upNumButton.setOnClickListener {
                YoYo.with(Techniques.BounceInDown).duration(300).playOn(holder.numProductTv)
                YoYo.with(Techniques.FadeIn).duration(300).playOn(holder.upNumButton)
                data.quantity += 1
                holder.numProductTv.text = "${data.quantity}"
                totalPRICE += data.product_price
                setTotalPrice()
            }

            holder.downNumButton.setOnClickListener {
                YoYo.with(Techniques.BounceInUp).duration(300).playOn(holder.numProductTv)
                YoYo.with(Techniques.FadeIn).duration(300).playOn(holder.downNumButton)
                if (data.quantity > 1){
                    data.quantity -= 1
                    totalPRICE -= data.product_price
                    setTotalPrice()
                }
                holder.numProductTv.text = "${data.quantity}"
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
        val progressBar = view.progressBar!!
    }

    private fun deleteFromCart(pid: Int, position: Int){

        webserviceUrl.httpPost(listOf("func" to "delete_cart",
                "uid" to account.id,
                "pid" to pid)).liveDataResponseString().observeForever {
            if (it.second.component1() == "1"){
                Toast.makeText(this, "با موفقیت حذف شد", Toast.LENGTH_SHORT).show()
                productsArray.removeAt(position)
                adapter?.notifyDataSetChanged()
            }else{
                val d = PrettyDialog(this)
                d.setTitle(this.getString(R.string.error))
                        .setIcon(R.drawable.error)
                        .setMessage(this.getString(R.string.network_error))
                        .addButton(this.getString(R.string.try_again), R.color.colorWhite, R.color.colorRed) {
                            d.dismiss()
                            deleteFromCart(pid, position)
                        }
                        .show()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun getProductData(pid: Int, view: View, progressBar: ProgressBar){
//        val progressDialog = progressDialog(this)
        progressBar.visibility = View.VISIBLE
        Log.d("gwegwe", "${account.id}")

        val params = HashMap<String, String>().apply {}

        val wooBuilder = WooBuilder().apply {
            isHttps = false
            baseUrl = "florals.ir/wp-json/wc/v3"
            signing_method = SigningMethod.HMACSHA1
            wc_key = "ck_b89b3e5cd871e50755f2d021967aa903cf2839cc"
            wc_secret = "cs_3c6fd6cfd5399a358f6ae285848829c7cc2cae88"
        }

        val resultLink: String? = OAuthSigner(wooBuilder)
                .getSignature(RequestMethod.GET, "/products/$pid", params)
        Log.d("gwegewg", resultLink+"--")


        resultLink!!.httpGet().liveDataObject(Product.Deserializer()).observeForever {
            Log.d("response", it.toString())
            it.success {it ->
                progressBar.visibility = View.GONE
                val intent = Intent(this, ProductDetailActivity::class.java)
                intent.putExtra("data", it)
                intent.putExtra("images", it.images)
//                var des = it.description
//                des = des.replace("<p>", "", true)
//                des = des.replace("</p>", "", true)
                intent.putExtra("description", it.description)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                    val pair1 = Pair.create<View, String>(view, view.transitionName)
//                    val pair2 = Pair.create<View, String>(cardView, view.transitionName)
                    val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                            this, pair1)
                    ContextCompat.startActivity(this, intent, options.toBundle())
                } else {
                    ContextCompat.startActivity(this, intent, null)
                }
            }
            it.failure {
                progressBar.visibility = View.GONE
                val d = PrettyDialog(this)
                d.setTitle(this.getString(R.string.error))
                        .setIcon(R.drawable.error)
                        .setMessage(this.getString(R.string.network_error))
                        .addButton(this.getString(R.string.try_again), R.color.colorWhite, R.color.colorRed) {
                            d.dismiss()
                            getProductData(pid, view, progressBar)
                        }
                        .show()
            }
        }
    }

    var isTimerAnimationSet = false
    private fun getData(isFirst: Boolean = false){

        if (isFirst){
            productsArray.clear()
        }

        totalPRICE = 0

        webserviceUrl.httpPost(listOf("func" to "get_cart", "uid" to account.id))
                .liveDataObject(Cart.ListDeserializer())
                .observeForever { it ->
                    Log.d("wegwegweg", it.toString())
                    it?.success {
                        swipeRefreshLayout.isRefreshing = false
                        progressBar.visibility = View.INVISIBLE
                        if (it.isNotEmpty()){
                            productsArray + it.toCollection(productsArray)
                            adapter?.notifyDataSetChanged()
                            recyclerview.scheduleLayoutAnimation()
                            for (i in productsArray){
                                totalPRICE += i.product_price*i.quantity
                            }
                            setTotalPrice()
                            payBar.animate().translationY(0f).duration = 600
                            topColorView.animate().translationY(0f).duration = 900
                            if (!isTimerAnimationSet){
                                isTimerAnimationSet = true
                                val timer = Timer()
                                timer.schedule(object: TimerTask() {
                                    override fun run() {
                                        runOnUiThread {
                                            YoYo.with(Techniques.ZoomInLeft).duration(300).playOn(nextIv)
                                        }
                                    }
                                }, 1000, 500)
                            }
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
