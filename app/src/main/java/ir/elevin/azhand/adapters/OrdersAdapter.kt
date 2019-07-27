package ir.elevin.azhand.adapters

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ir.elevin.azhand.*
import kotlinx.android.synthetic.main.order_row.view.*

class OrdersAdapter(private val activity: FragmentActivity, private val items: List<OrderToShow>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
        return OrderViewHolder(LayoutInflater.from(activity).inflate(R.layout.order_row, p0, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    @SuppressLint("SetTextI18n", "RestrictedApi")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = items[position]
        (holder as OrderViewHolder).orderNumberTv.text = "شماره سفارش: "+data.id
        holder.orderAmountTv.text = "مبلغ کل: "+decimalFormatCommafy("${data.total}")
        val date = parsDateAndTimeWooo(data.date_created)
        holder.orderDateCreateTv.text = "${date.first} - ${date.second}"

        when (data.status){
            "processing" -> {holder.orderStatusTv.text = "در حال انجام"}
            "on-hold" -> {holder.orderStatusTv.text = "در انتظار تایید"}
            "completed" -> {holder.orderStatusTv.text = "تحویل شده"}
            "cancelled" -> {holder.orderStatusTv.text = "لغو شده"}
        }

        holder.orderAddressTv.text = data.shipping.address_1

//        Picasso.get().load(data.cardPoster).placeholder(R.drawable.no_image_wide).into(holder.giftCardIv)
        holder.ordersRecyclerView.layoutManager = LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false)
//        holder.ordersRecyclerView.adapter = OrderProductsAdapter(activity, mapper.readValue<List<line_items>>(data.line_items))
        holder.ordersRecyclerView.adapter = OrderProductsAdapter(activity, data.line_items)

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