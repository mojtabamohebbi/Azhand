package ir.elevin.azhand.adapters

import android.annotation.SuppressLint
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.kittinunf.fuel.jackson.mapper
import com.squareup.picasso.Picasso
import ir.elevin.azhand.*
import kotlinx.android.synthetic.main.order_row.view.*

class OrdersAdapter(private val activity: FragmentActivity, private val items: List<Order>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
        return OrderViewHolder(LayoutInflater.from(activity).inflate(R.layout.order_row, p0, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    @SuppressLint("SetTextI18n", "RestrictedApi")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = items[position]
        (holder as OrderViewHolder).orderNumberTv.text = "شماره سفارش: "+data.transCode
        holder.orderAmountTv.text = "مبلغ کل: "+decimalFormatCommafy("${data.amount}")
        val date = parsDateAndTime(data.dateCreate)
        holder.orderDateCreateTv.text = "${date.first} - ${date.second}"

        when (data.status){
            0 -> {holder.orderStatusTv.text = "تایید پرداخت"}
            1 -> {holder.orderStatusTv.text = "در حال آماده سازی"}
            2 -> {holder.orderStatusTv.text = "ارسال شده"}
            3 -> {holder.orderStatusTv.text = "تحویل شده"}
        }

        holder.orderAddressTv.text = data.address

        Picasso.get().load(data.cardPoster).placeholder(R.drawable.no_image_wide).into(holder.giftCardIv)
        holder.ordersRecyclerView.layoutManager = LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false)
        holder.ordersRecyclerView.adapter = OrderProductsAdapter(activity, mapper.readValue<List<Cart>>(data.products))

        if (data.isSelected){
            holder.detailsButton.setBackgroundColor(AppCompatResources.getColorStateList(activity, R.color.colorAccentDark).defaultColor)
            holder.detailsButton.text = "بستن"
            holder.detailLayout.visibility = View.VISIBLE
        }else{
            holder.detailsButton.text = "جزئیات"
            holder.detailsButton.setBackgroundColor(Color.parseColor("#6EAD67"))
            holder.detailLayout.visibility = View.GONE
        }

        holder.detailsButton.setOnClickListener {
            if (data.isSelected){
                data.isSelected = false
                holder.detailLayout.visibility = View.GONE
                holder.detailsButton.text = "جزئیات"
                holder.detailsButton.setBackgroundColor(Color.parseColor("#6EAD67"))
            }else{
                data.isSelected = true
                holder.detailLayout.visibility = View.VISIBLE
                holder.detailsButton.setBackgroundColor(AppCompatResources.getColorStateList(activity, R.color.colorAccentDark).defaultColor)
                holder.detailsButton.text = "بستن"
            }
            notifyItemChanged(position)
        }
    }
}

private class OrderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val orderAmountTv = view.orderAmountTv!!
    val orderDateCreateTv = view.orderDateCreateTv!!
    val orderNumberTv = view.orderNumberTv!!
    val orderStatusTv = view.orderStatusTv!!
    val ordersRecyclerView = view.ordersRecyclerView!!
    val orderAddressTv = view.orderAddressTv!!
    val detailsButton = view.detailsButton!!
    val giftCardIv = view.giftCardIv!!
    val detailLayout = view.detailLayout!!

}