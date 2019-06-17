package ir.elevin.azhand.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import ir.elevin.azhand.Address
import ir.elevin.azhand.R
import kotlinx.android.synthetic.main.address_row.view.*

class AddressAdapter(private val activity: FragmentActivity, private val items: List<Address>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
        return AddressHolder(LayoutInflater.from(activity).inflate(R.layout.address_row, p0, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    @SuppressLint("SetTextI18n", "RestrictedApi", "CheckResult")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = items[position]
        (holder as AddressHolder).addressTv.text = data.address

        if (data.isSelected){
            holder.selectedIv.visibility = View.VISIBLE
            holder.addressTv.setTextColor(AppCompatResources.getColorStateList(activity, R.color.colorGreen))
        }else{
            holder.selectedIv.visibility = View.INVISIBLE
            holder.addressTv.setTextColor(AppCompatResources.getColorStateList(activity, R.color.colorDisableText))
        }

        holder.itemView.setOnClickListener {
            for (i in items){
                i.isSelected = false
            }
            items[position].isSelected = true
            notifyDataSetChanged()
        }

        holder.editButton.setOnClickListener {

        }
        holder.deleteButton.setOnClickListener {

        }
    }
}

private class AddressHolder(view: View) : RecyclerView.ViewHolder(view) {
    val addressTv = view.addressTv!!
    val deleteButton = view.deleteButton!!
    val editButton = view.editButton!!
    val selectedIv = view.selectedIv!!
}