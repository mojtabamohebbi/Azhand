package ir.elevin.mykotlinapplication

import android.annotation.SuppressLint
import android.content.Intent

import android.os.Build

import android.os.Bundle
import android.os.Handler

import android.transition.TransitionInflater

import android.util.Log
import android.view.View

import androidx.appcompat.content.res.AppCompatResources
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.fuel.livedata.liveDataObject
import com.github.kittinunf.fuel.livedata.liveDataResponse
import com.simmorsal.recolor_project.ReColor
import com.smarteist.autoimageslider.IndicatorAnimations
import com.smarteist.autoimageslider.SliderAnimations
import com.squareup.picasso.Picasso
import ir.elevin.mykotlinapplication.Adapters.SliderAdapterExample
import kotlinx.android.synthetic.main.activity_product_detail.*
import kotlinx.android.synthetic.main.activity_product_detail.imageSlider
import kotlinx.android.synthetic.main.activity_product_detail.iv
import kotlinx.android.synthetic.main.activity_product_detail.productToolbar
import libs.mjn.prettydialog.PrettyDialog
import android.util.TypedValue
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.github.kittinunf.result.failure
import com.github.kittinunf.result.success
import ir.elevin.mykotlinapplication.Adapters.CommentsAdapter


class ProductDetailActivity : CustomActivity() {

    lateinit var data: Product
    var numProduct = 1
    var page = 1
    lateinit var commentsAdapter: CommentsAdapter
    lateinit var layoutManager: androidx.recyclerview.widget.LinearLayoutManager
    var mIsLoading = true
    val comments = ArrayList<Comment>()

    @SuppressLint("SetTextI18n")
    @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS", "NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail)

        data = intent.extras.getParcelable("data")

        Picasso.get()
                .load(data.image)
                .into(iv)
        productPriceTv.text = "${data.price} هزارتومان"
        desTv.setText(data.description, true)
        maintenanceTv.setText(data.maintenance, true)

        setSupportActionBar(productToolbar)
        title = data.name

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val explode = TransitionInflater.from(this).inflateTransition(R.transition.activity_slide)
            explode.excludeTarget(android.R.id.statusBarBackground, true)
            explode.excludeTarget(android.R.id.navigationBarBackground, true)
            window.enterTransition = explode
            window.returnTransition = explode
            window.reenterTransition = explode
            window.exitTransition = explode
            window.allowReturnTransitionOverlap = true
            window.allowEnterTransitionOverlap = true

            val ex : android.transition.Transition = window.enterTransition
            ex.addListener(object : android.transition.Transition.TransitionListener {
                override fun onTransitionEnd(transition: android.transition.Transition?) {
                    imageSlider.animate().alpha(1f).duration = 300
                    iv.visibility = View.INVISIBLE
                    imageSlider.visibility = View.VISIBLE
                    productPriceTv.animate().alpha(1f).scaleY(1f).duration = 300
                    cardview.cardElevation = 10f
                }

                override fun onTransitionResume(transition: android.transition.Transition?) {

                }

                override fun onTransitionPause(transition: android.transition.Transition?) {

                }

                override fun onTransitionCancel(transition: android.transition.Transition?) {

                }

                override fun onTransitionStart(transition: android.transition.Transition?) {

                }
            })

        }

        imageSlider.setIndicatorAnimation(IndicatorAnimations.DROP) //set indicator animation by using 	  SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        imageSlider.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION)
        imageSlider.scrollTimeInSec = 7 //set scroll delay in seconds :

        val images = listOf(data.image, data.macroImage, data.potImage)
        imageSlider.sliderAdapter = SliderAdapterExample(this, images)

//        imageSlider.postDelayed({imageSlider.startAutoCycle()}, 3000)

        plusButton.setOnClickListener {
            if (account.id > 0){
                if (numProduct < 100){
                    numProduct += 1
                }
                numProductTv.text = ""+numProduct
                YoYo.with(Techniques.ZoomIn).duration(300).playOn(numProductTv)
                YoYo.with(Techniques.FadeIn).duration(300).playOn(plusButton)
            }else{
                startActivity(Intent(this, LoginActivity::class.java))
            }

        }

        minusButton.setOnClickListener {
            if (numProduct > 1){
                numProduct -= 1
            }
            numProductTv.text = ""+numProduct
            YoYo.with(Techniques.ZoomIn).duration(300).playOn(numProductTv)
            YoYo.with(Techniques.FadeIn).duration(300).playOn(minusButton)
        }

        addToCartButton.setOnClickListener {
            if (account.id > 0){
                if (addToCartState == 1){
                    addToCartState = 2
                    showLoadingState()
                    val handler = Handler()
                    handler.postDelayed({addToCart()},1000)
                }
            }else{
                startActivity(Intent(this, LoginActivity::class.java))
            }
        }

        layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        commentsRecyclerView.layoutManager = layoutManager
        commentsAdapter = CommentsAdapter(this, comments)
        commentsRecyclerView.adapter = commentsAdapter
        val mScrollListener = object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (mIsLoading)
                    return
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val pastVisibleItems = layoutManager.findFirstVisibleItemPosition()
                if (pastVisibleItems + visibleItemCount >= totalItemCount) {
                    //End of list
                    progressBar.visibility = View.VISIBLE
                    mIsLoading = true
                    getComments()
                }
            }
        }
        commentsRecyclerView.addOnScrollListener(mScrollListener)
        val handler = Handler()
        handler.postDelayed({
            getComments()
        }, 3000)

    }

    var addToCartState = 1 //1=normal, 2=loading, 3=done
    private fun addToCart(){
        webserviceUrl.httpPost(listOf("func" to "add_to_cart", "uid" to account.id, "pid" to data.id, "num" to numProduct)).liveDataResponse().observeForever {
            Log.d("WEGweg", it.first.data.toString(Charsets.UTF_8))
            if (it.first.data.toString(Charsets.UTF_8) == "1"){
                showDoneState()
                val handler = Handler()
                handler.postDelayed({
                    showNormalState()
                }, 3000)
            }else{
                showNormalState()
                val d = PrettyDialog(this)
                d.setTitle(this.getString(R.string.error))
                        .setIcon(R.drawable.error)
                        .setMessage(this.getString(R.string.network_error))
                        .addButton(this.getString(R.string.try_again), R.color.colorWhite, R.color.colorRed) {
                            d.dismiss()
                            addToCartState = 2
                            showLoadingState()
                            addToCart()
                        }
                        .show()
            }
        }
    }

    private fun getComments(){
        webserviceUrl.httpPost(listOf("func" to "get_comments", "page" to page, "pid" to data.id))
                .liveDataObject(Comment.ListDeserializer()).observeForever { it ->
                    Log.d("wegwegvev", it.toString())
                    it.success{
                        commentsProgressBar.visibility = View.GONE
                        if (it.isNotEmpty()){
                            page += 1
                            commentsTitleTv.text = "نظرات"
                            comments + it.toCollection(comments)
                            commentsAdapter.notifyDataSetChanged()
                            mIsLoading = false
                        }else if(comments.size == 0){
                            commentsTitleTv.text = "نظری درباره این محصول ثبت نشده است."
                        }
                    }
                    it.failure {
                        getComments()
                    }
        }
    }

    private fun showLoadingState(){
        val progressBar40dpMargin = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 48f,
                resources.displayMetrics
        )

        minusButton.animate().translationX(-500f).alpha(0f).duration = 300
        numProductTv.animate().translationX(-500f).alpha(0f).duration = 250
        plusButton.animate().translationX(-500f).alpha(0f).duration = 200
        progressBar.animate().translationX(progressBar40dpMargin).alpha(1f).duration = 200
        ReColor(this).setViewBackgroundColor(footerBar, "#579C50", "#ffffff", 500)
        ReColor(this).setTextViewColor(addToCartButton, "FFFFFF", "356B30", 500)
        addToCartButton.text = "در حال پردازش..."
    }

    private fun showNormalState(){
        addToCartState = 1
        minusButton.animate().translationX(0f).alpha(1f).duration = 300
        numProductTv.animate().translationX(0f).alpha(1f).duration = 250
        plusButton.animate().translationX(0f).alpha(1f).duration = 200
        progressBar.animate().translationX(-200f).alpha(0f).duration = 200
        ReColor(this).setViewBackgroundColor(footerBar, "#ffffff", "#579C50", 500)
        ReColor(this).setTextViewColor(addToCartButton, "356B30", "ffffff", 500)
        addToCartButton.text = "افزودن به سبد خرید"
    }

    private fun showDoneState(){
        progressBar.animate().translationX(-200f).alpha(0f).duration = 200
        ReColor(this).setViewBackgroundColor(footerBar, "#FFFFFF", "#FF30497A", 500)
        ReColor(this).setTextViewColor(addToCartButton, "356B30", "ffffff", 500)
        addToCartButton.text = "با موفقیت ثبت شد"
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        cardview.cardElevation = 0f
        productPriceTv.animate().alpha(0f).translationY(500f).duration = 400
        iv.visibility = View.VISIBLE
        imageSlider.visibility = View.INVISIBLE
        super.onBackPressed()
    }


}
