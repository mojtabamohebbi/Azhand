package ir.elevin.azhand.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

import androidx.core.content.ContextCompat.startActivity
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.Glide
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.fuel.livedata.liveDataObject
import com.github.kittinunf.result.failure
import com.github.kittinunf.result.success
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import ir.elevin.azhand.*
import ir.mjmim.woocommercehelper.enums.RequestMethod
import ir.mjmim.woocommercehelper.enums.SigningMethod
import ir.mjmim.woocommercehelper.helpers.OAuthSigner
import ir.mjmim.woocommercehelper.main.WooBuilder
import kotlinx.android.synthetic.main.product_row.view.*
import libs.mjn.prettydialog.PrettyDialog

class ProductAdapter(private val activity: FragmentActivity, private val items: List<Product>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
        return ViewHolderLawyer(LayoutInflater.from(activity).inflate(R.layout.product_row, p0, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    @SuppressLint("SetTextI18n", "RestrictedApi", "CheckResult")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = items[position]
        (holder as ViewHolderLawyer).nameTv.text = data.name
        holder.priceTv.text = decimalFormatCommafy(data.price)

        Picasso.get()
                .load(data.images[0].src)
                .placeholder(R.drawable.loading)
                .error(R.drawable.loading)
                .into(holder.imageIv)

        holder.itemView.setOnClickListener {
            getData(data.id, holder.imageIv, holder.progressBar, holder.priceTv, holder.cardView)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun getData(pid: Int, view: View, progressBar: ProgressBar, priceTv: TextView, cardView: CardView){
//        val progressDialog = progressDialog(activity)
        progressBar.visibility = View.VISIBLE
        priceTv.visibility = View.GONE
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
                priceTv.visibility = View.VISIBLE
                val intent = Intent(activity, ProductDetailActivity::class.java)
                intent.putExtra("data", it)
                intent.putExtra("images", it.images)
                var des = it.description
                des = des.replace("<p>", "", true)
                des = des.replace("</p>", "", true)
                intent.putExtra("description", des)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                    val pair1 = Pair.create<View, String>(view, view.transitionName)
                    val pair2 = Pair.create<View, String>(cardView, view.transitionName)
                    val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                            activity, pair1, pair2)
                    startActivity(activity, intent, options.toBundle())
                } else {
                    startActivity(activity, intent, null)
                }
            }
            it.failure {
                progressBar.visibility = View.GONE
                priceTv.visibility = View.VISIBLE
                val d = PrettyDialog(activity)
                d.setTitle(activity.getString(R.string.error))
                        .setIcon(R.drawable.error)
                        .setMessage(activity.getString(R.string.network_error))
                        .addButton(activity.getString(R.string.try_again), R.color.colorWhite, R.color.colorRed) {
                            d.dismiss()
                            getData(pid, view, progressBar, priceTv, cardView)
                        }
                        .show()
            }
        }
    }

}

class ViewHolderLawyer(view: View) : RecyclerView.ViewHolder(view) {
    val nameTv = view.nameTv!!
    val priceTv = view.priceTv!!
    val imageIv = view.imageIv!!
    val progressBar = view.progressBar!!
    val cardView = view.cardview!!
}