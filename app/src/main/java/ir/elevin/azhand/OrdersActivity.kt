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
import ir.elevin.azhand.adapters.OrdersAdapter
import kotlinx.android.synthetic.main.activity_orders.*
import libs.mjn.prettydialog.PrettyDialog

class OrdersActivity : CustomActivity() {

    var page = 1
    private var array = ArrayList<Order>()
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

        val params: List<Pair<String, Any?>> = listOf("func" to "get_orders", "page" to page, "uid" to account.id)
        webserviceUrl
                .httpPost(params)
                .liveDataObject(Order.ListDeserializer())
                .observeForever { it ->
                    Log.d("wegweb", it.toString())
                    it?.success {
                        swipeRefreshLayout.isRefreshing = false
                        progressBar.visibility = View.INVISIBLE
                        if (it.isNotEmpty()){
                            page += 1
                            it.toCollection(array)
                            Log.d("gwegweg", ""+array.size)

                            Log.d("afvavwvbw", ""+array[0].products)
//                            Log.d("afvavwvbw", "kjhkjh"+Cart.ListDeserializer().deserialize(""+array[0].products))

                            adapter?.notifyDataSetChanged()
                            mIsLoading = false
                            if (isFirst){
                                recyclerview.scheduleLayoutAnimation()
                            }
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
