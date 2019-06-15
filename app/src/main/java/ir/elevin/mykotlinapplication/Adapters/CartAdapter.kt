package ir.elevin.mykotlinapplication.Adapters

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.core.util.Pair
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.fuel.livedata.liveDataObject
import com.github.kittinunf.result.failure
import com.github.kittinunf.result.success
import com.squareup.picasso.Picasso
import ir.elevin.mykotlinapplication.*
import kotlinx.android.synthetic.main.cart_row.view.*
import libs.mjn.prettydialog.PrettyDialog

class CartAdapter (private val activity: FragmentActivity, private val items: List<Cart>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface ChangeTotalPrice{
        fun setTotalPrice()
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
        return CartHolder(LayoutInflater.from(activity).inflate(R.layout.cart_row, p0, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    @SuppressLint("SetTextI18n", "RestrictedApi")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = items[position]
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
            (activity as ChangeTotalPrice).setTotalPrice()
        }

        holder.downNumButton.setOnClickListener {
            YoYo.with(Techniques.BounceInUp).duration(300).playOn(holder.numProductTv)
            YoYo.with(Techniques.FadeIn).duration(300).playOn(holder.downNumButton)
            if (data.num > 1){
                data.num -= 1
                totalPRICE -= data.price
                (activity as ChangeTotalPrice).setTotalPrice()
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