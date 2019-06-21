package ir.elevin.azhand.fragments

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.transition.Explode
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.fuel.livedata.liveDataObject
import com.github.kittinunf.result.failure
import com.github.kittinunf.result.success
import ir.elevin.azhand.*
import ir.elevin.azhand.adapters.ProductAdapter

import kotlinx.android.synthetic.main.recycler_fragment.*
import libs.mjn.prettydialog.PrettyDialog

class GardeningFragment : androidx.fragment.app.Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.recycler_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()

    }

    var page = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            enterTransition = Explode()
            exitTransition = Explode()
        }
    }

    private var array = ArrayList<Product>()
    private var adapter: ProductAdapter? = null
    fun addAccount(){
        val data = Product()
        array.add(0, data)
        adapter?.notifyDataSetChanged()
    }

    private var mIsLoading = true
    private lateinit var layoutManager: StaggeredGridLayoutManager

    private fun setupRecyclerView() {

        layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        recyclerview.setHasFixedSize(false)

        adapter = ProductAdapter(activity!!, array)
        recyclerview.adapter = adapter
        recyclerview.layoutManager = layoutManager

        swipeRefreshLayout.setOnRefreshListener {
            getData(true)
        }

        val mScrollListener = object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (mIsLoading)
                    return
                val into = IntArray(5)
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val pastVisibleItems = layoutManager.findFirstCompletelyVisibleItemPositions(into)[0]
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

    fun getData(isFirst: Boolean){

        isErrorShowing = false

        if(isFirst){
            page = 1
            array.clear()
            swipeRefreshLayout.isRefreshing = true
        }

        val params: List<Pair<String, Any?>> = listOf("func" to "get_products", "page" to page, "filterType" to filterType, "catId" to 7)
        webserviceUrl
                .httpPost(params)
                .liveDataObject(Product.ListDeserializer())
                .observeForever { it ->
                    Log.d("ergegewgv", it.toString())
                    it?.success {
                        swipeRefreshLayout.isRefreshing = false
                        progressBar.visibility = View.INVISIBLE
                        if (it.isNotEmpty()){
                            page += 1
                            array + it.toCollection(array)
                            adapter?.notifyDataSetChanged()
                            mIsLoading = false
                            if (isFirst){
                                YoYo.with(Techniques.ZoomIn).duration(500).playOn(recyclerview)
                            }
                        }
                    }
                    it?.failure {
                        Log.d("errorFound", it.message)
                        progressBar.visibility = View.INVISIBLE
                        swipeRefreshLayout.isRefreshing = false
                        mIsLoading = false
                        if (!isErrorShowing){
                            isErrorShowing = true
                            val d = PrettyDialog(activity)
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


}
