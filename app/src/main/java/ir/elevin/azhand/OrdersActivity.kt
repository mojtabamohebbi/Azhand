package ir.elevin.azhand

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.fuel.livedata.liveDataObject
import com.github.kittinunf.result.failure
import com.github.kittinunf.result.success
import ir.elevin.azhand.adapters.OrdersAdapter
import ir.mjmim.woocommercehelper.enums.RequestMethod
import ir.mjmim.woocommercehelper.helpers.OAuthSigner
import kotlinx.android.synthetic.main.activity_orders.*
import libs.mjn.prettydialog.PrettyDialog

class OrdersActivity : CustomActivity() {

    var page = 1
    private var array = ArrayList<OrderToShow>()
    private var adapter: OrdersAdapter? = null

    private var mIsLoading = true
    private lateinit var layoutManager: LinearLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orders)

        setSupportActionBar(orderToolbar)
        title = "سفارشات"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recyclerview.setHasFixedSize(false)

        adapter = OrdersAdapter(this, array)
        recyclerview.adapter = adapter
        recyclerview.layoutManager = layoutManager

        swipeRefreshLayout.setOnRefreshListener {
            getData(true)
        }

        val mScrollListener = object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (mIsLoading)
                    return
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val pastVisibleItems = layoutManager.findLastVisibleItemPosition()
                if (pastVisibleItems + visibleItemCount >= totalItemCount) {
                    //End of list
                    progressBar.visibility = View.VISIBLE
                    mIsLoading = true
                    getData(false)
                }
            }
        }

        recyclerview.addOnScrollListener(mScrollListener)

        getData(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    fun getData(isFirst: Boolean){

        if(isFirst){
            page = 1
            array.clear()
            swipeRefreshLayout.isRefreshing = true
        }

        val params = HashMap<String, String>().apply {
            put("customer", "${account.id}")
            put("per_page", "10")
            put("page", "$page")
        }

        val resultLink: String? = OAuthSigner(wooBuilder)
                .getSignature(RequestMethod.GET, "/orders", params)
        Log.d("gwegewg", resultLink+"--")

        resultLink!!
                .httpGet()
                .liveDataObject(OrderToShow.ListDeserializer())
                .observeForever { it ->
                    Log.d("wegweb", it.toString())
                    it?.success {
                        swipeRefreshLayout.isRefreshing = false
                        progressBar.visibility = View.INVISIBLE
                        if (it.isNotEmpty()){
                            page += 1
                            it.toCollection(array)
                            Log.d("gwegweg", ""+array.size)
                            Log.d("afvavwvbw", ""+array[0].line_items)
                            Log.d("efgrgre", ""+array[0])
//                            Log.d("afvavwvbw", "kjhkjh"+Cart.ListDeserializer().deserialize(""+array[0].products))

                            adapter?.notifyDataSetChanged()
                            mIsLoading = false
                            if (isFirst){
                                recyclerview.scheduleLayoutAnimation()
                            }
                        }else if(array.size == 0){
                            notFoundTv.visibility = View.VISIBLE
                        }
                    }
                    it?.failure {
                        Log.d("errorFound", it.message)
                        progressBar.visibility = View.INVISIBLE
                        swipeRefreshLayout.isRefreshing = false
                        mIsLoading = false
                        val d = PrettyDialog(this)
                        d.setTitle(getString(R.string.error))
                                .setIcon(R.drawable.error)
                                .setMessage(getString(R.string.network_error))
                                .addButton(getString(R.string.try_again), R.color.colorWhite, R.color.colorRed) {
                                    d.dismiss()
                                    getData(false)
                                }
                                .show()
                    }
                }
    }
}
